# This tools works the same as the "Python Practice Tool" from COMP 1150
from IPython.display import display, Javascript, HTML
import json, random, itertools, io, urllib.request, contextlib, os, tempfile, subprocess
from copy import deepcopy
import numpy as np, pandas as pd, html
import ipywidgets as widgets

# --- Java execution utilities ------------------------------------------------
def compile_and_run_java(code, class_name, inputs=None):
    """Compile and run Java code, returning the output and any errors"""
    # Create a temporary directory for our files
    with tempfile.TemporaryDirectory() as tmpdir:
        # Write the Java code to a file
        java_file = os.path.join(tmpdir, f"{class_name}.java")
        with open(java_file, 'w') as f:
            f.write(code)
        
        # Compile the Java code
        compile_process = subprocess.run(
            ['javac', java_file], 
            capture_output=True, 
            text=True
        )
        
        if compile_process.returncode != 0:
            # If compilation failed, return the error
            return None, compile_process.stderr
        
        # Run the compiled code
        cmd = ['java', '-cp', tmpdir, class_name]
        
        # If inputs are provided, convert them to command-line args
        if inputs is not None:
            if not isinstance(inputs, (list, tuple)):
                inputs = [inputs]  # Convert single input to list
            
            # Convert all inputs to strings for command line
            str_inputs = [str(i) for i in inputs]
            cmd.extend(str_inputs)
        
        # Run the program
        run_process = subprocess.run(
            cmd,
            capture_output=True,
            text=True
        )
        
        if run_process.returncode != 0:
            # If execution failed, return the error
            return None, run_process.stderr
        
        # Return the output
        return run_process.stdout.strip(), None

def wrap_java_function(code, function_name, function_sig):
    """
    Wrap a Java function in a class with a main method to test it
    
    Args:
        code: The Java function code
        function_name: The name of the function
        function_sig: The function signature (return type and parameters)
    
    Returns:
        A complete Java class with a main method that calls the function
    """
    class_name = "Solution"
    
    # Extract return type and parameters from signature
    parts = function_sig.split('(')
    return_type = parts[0].split()[-1]  # Get the return type
    params_str = parts[1].split(')')[0]
    
    # Build parameter declaration for main method
    params = []
    if params_str:
        for p in params_str.split(','):
            p = p.strip()
            if p:
                typ, name = p.split()
                params.append((typ, name))
    
    # Generate code to call the function with test inputs
    main_code = []
    main_code.append(f"public static void main(String[] args) {{")
    
    # Create test cases
    if not params:
        # No parameters, simply call the function
        main_code.append(f"    System.out.println({function_name}());")
    else:
        # Generate code to initialize parameters from args
        for i, (typ, name) in enumerate(params):
            # Add check for args length to prevent ArrayIndexOutOfBoundsException
            main_code.append(f"    if (args.length <= {i}) {{")
            main_code.append(f"        System.err.println(\"Error: Not enough arguments\");")
            main_code.append(f"        System.exit(1);")
            main_code.append(f"    }}")
            
            # Convert string arg to appropriate type
            if typ == "int":
                main_code.append(f"    {typ} {name} = Integer.parseInt(args[{i}]);")
            elif typ == "double":
                main_code.append(f"    {typ} {name} = Double.parseDouble(args[{i}]);")
            elif typ == "String":
                main_code.append(f"    {typ} {name} = args[{i}];")
            elif typ == "boolean":
                main_code.append(f"    {typ} {name} = Boolean.parseBoolean(args[{i}]);")
        
        # Generate code to call the function
        main_code.append(f"    System.out.println({function_name}({', '.join(p[1] for p in params)}));")
    
    main_code.append("}")
    
    # Wrap everything in a class
    wrapped_code = f"""
public class {class_name} {{
    // Student's function
    {code}
    
    // Main method for testing
    {chr(10).join('    ' + line for line in main_code)}
}}
"""
    return wrapped_code, class_name

# --- Sample inputs for Java -------------------------------------------------
_INPUT_SAMPLES = {
    'int': [0, 1, -1, 2, -2, 5, -5, 10, -10, 100, -100, -50, 2147483647],
    'double': [0.0, 1.5, -1.5, 3.14, -3.14, float('inf'), float('-inf'), float('nan')],
    'String': ["", "hello", "world", "Java", "AI", "The Matrix", "HAL 9000", "42", 
             "Dune", "Blade Runner", "I, Robot", "Ender's Game", "Neuromancer"],
    'boolean': [True, False]
}
# Add special types for Java arrays and lists later

def _clone(obj):
    return obj.copy() if isinstance(obj, np.ndarray) else deepcopy(obj)

# Convert Python input types to Java types
def _python_to_java_type(py_type):
    type_map = {
        'int': 'int',
        'float': 'double',
        'string': 'String',
        'bool': 'boolean'
    }
    return type_map.get(py_type, py_type)

# Convert Python values to Java-friendly string representations
def _python_to_java_value(value, java_type):
    if java_type == 'int' or java_type == 'double':
        return str(value)
    elif java_type == 'String':
        return f'"{value}"'  # Quoted string
    elif java_type == 'boolean':
        return str(value).lower()  # true or false
    return str(value)

# --- Java Question class ----------------------------------------------------
class JavaQuestion:
    def __init__(self, function_name, return_type, parameters, param_types, 
                 description, answer_code, hint='', test_inputs=None):
        self.function_name = function_name
        self.return_type = return_type
        self.parameters = parameters
        self.param_types = param_types  # Java types as strings
        self.description = description
        self.answer_code = answer_code
        self.hint = hint
        self.test_inputs = test_inputs or self.generate_inputs()
        self.sample_inputs = random.sample(self.test_inputs,
                                         min(3, len(self.test_inputs)))
        
    def generate_signature(self):
        """Generate a Java function signature"""
        param_list = []
        for i, param in enumerate(self.parameters):
            param_list.append(f"{self.param_types[i]} {param}")
        
        return f"public static {self.return_type} {self.function_name}({', '.join(param_list)})"
        
    def generate_inputs(self, max_tests=10):
        """Generate test inputs for this function"""
        # Map the Java types to our sample input types
        java_to_py = {
            'int': 'int',
            'double': 'float',
            'String': 'string',
            'boolean': 'bool'
        }
        
        sample_keys = [java_to_py.get(t, 'any') for t in self.param_types]
        samples = [_INPUT_SAMPLES.get(k, []) for k in sample_keys]
        
        # Generate combinations of inputs
        combos = list(itertools.product(*samples)) if samples else [[]]
        chosen = random.sample(combos, min(max_tests, len(combos)))
        
        # Return as either single values or tuples depending on parameter count
        return [args[0] if len(args) == 1 else args for args in chosen]

# --- JavaPracticeTool class ------------------------------------------------
class JavaPracticeTool:
    def __init__(self, questions=None, json_url=None):
        self.questions = (self._load_questions(json_url)
                         if json_url else questions or [])
        self.current_index = 0
        self.student_output = io.StringIO()
        self._setup_widgets()
        self._show_directions()
        self._show_question()
        display(self.main)
        self._enable_tab()

    def _load_questions(self, url):
        try:
            raw = (urllib.request.urlopen(url).read().decode()
                  if url.startswith('http') else open(url).read())
            data = json.loads(raw)
            return [JavaQuestion(**q) for q in data.get('questions', [])]
        except Exception as e:
            print(f"Error loading questions: {e}")
            return []

    def _setup_widgets(self):
        w = widgets
        self.progress = w.IntProgress(min=0, max=len(self.questions),
                                    description='Progress:',
                                    layout=w.Layout(width='100%'))
        self.question_output = w.Output()
        self.code_input = w.Textarea(layout=w.Layout(width='100%', height='200px'))
        self.code_input.add_class('code-input')

        self.run_btn = w.Button(description='Run', button_style='success', icon='check')
        self.retry_btn = w.Button(description='Retry', button_style='warning', icon='refresh')
        self.next_btn = w.Button(description='Next', button_style='info', icon='arrow-right', disabled=True)
        self.hint_btn = w.Button(description='Show Hint', button_style='primary', icon='lightbulb-o')
        self.skip_dd = w.Dropdown(options=[], description='Skip to:', layout=w.Layout(width='200px'))
        self.hint_output = w.Output()

        left = [
            self.progress,
            self.question_output,
            self.code_input,
            w.HBox([self.run_btn, self.retry_btn, self.next_btn, self.hint_btn, self.skip_dd]),
            self.hint_output
        ]
        self.left_panel = w.VBox(left, layout=w.Layout(width='50%'))

        self.sample_output = w.Output()
        self.results_output = w.Output()
        self.program_output = w.Output()
        self.results_retry = w.Button(description='Retry', button_style='warning', icon='refresh')
        self.results_next = w.Button(description='Next', button_style='info', icon='arrow-right', disabled=True)

        right = [
            w.HTML("<h3>Sample Inputs and Outputs</h3>"),
            self.sample_output,
            w.HTML("<h3>Results</h3>"),
            self.results_output,
            w.HTML("<h3>Output</h3>"),
            self.program_output
        ]
        self.right_panel = w.VBox(right, layout=w.Layout(width='50%'))
        self.main = w.HBox([self.left_panel, self.right_panel],
                          layout=w.Layout(width='100%'))

        self.run_btn.on_click(self._run)
        self.retry_btn.on_click(self._reset)
        self.next_btn.on_click(self._next)
        self.hint_btn.on_click(self._hint)
        self.results_retry.on_click(self._reset)
        self.results_next.on_click(self._next)
        self.skip_dd.observe(self._skip, names='value')

    def _enable_tab(self):
        js = """
        (function() {
          function handler(e) {
            if (e.keyCode===9) {
              e.preventDefault();
              let start=this.selectionStart, end=this.selectionEnd;
              this.value=this.value.slice(0,start)+"    "+this.value.slice(end);
              this.selectionStart=this.selectionEnd=start+4;
            }
          }
          function attach() {
            document.querySelectorAll('.code-input textarea')
              .forEach(function(ta) {
                if (!ta._tabHandler) {
                  ta._tabHandler = true;
                  ta.addEventListener('keydown', handler, false);
                }
              });
          }
          attach();
          new MutationObserver(attach)
            .observe(document.body, {childList:true, subtree:true});
        })();
        """
        display(Javascript(js))

    def _show_directions(self):
        with self.question_output:
            self.question_output.clear_output()
            display(HTML("""
<h2>Java Practice Tool</h2>
<p>Write your Java function in the left pane. Use <code>Tab</code> to indent.</p>
<ul>
  <li>Run to test</li>
  <li>Retry to reset</li>
  <li>Next after all tests pass</li>
  <li>Show Hint for guidance</li>
</ul>"""))

    def _show_question(self):
        if self.current_index >= len(self.questions):
            self.left_panel.children = [widgets.HTML("<h3>All done!</h3>")]
            return
        q = self.questions[self.current_index]
        sig = f"{q.generate_signature()} {{\n    // Your code here\n    return {self._default_return_value(q.return_type)};\n}}"
        self.code_input.value = sig
        self.progress.value = self.current_index
        self.next_btn.disabled = self.results_next.disabled = True
        self.skip_dd.options = [(f"Problem {i+1}", i) for i in range(len(self.questions))]
        self.skip_dd.value = self.current_index

        with self.question_output:
            self.question_output.clear_output()
            display(HTML(f"<h3>Question {self.current_index+1}/{len(self.questions)}</h3>"
                        f"<p>{q.description}</p>"))

        self._render_sample_table(q)
        self.results_output.clear_output()
        self.program_output.clear_output()

    def _default_return_value(self, return_type):
        """Provide a default return value based on the Java type"""
        if return_type == 'int':
            return '0'
        elif return_type == 'double':
            return '0.0'
        elif return_type == 'String':
            return '""'
        elif return_type == 'boolean':
            return 'false'
        else:
            return 'null'

    def _render_sample_table(self, q):
        """Display sample inputs and outputs"""
        rows = []
        
        for inp in q.sample_inputs:
            # Prepare input for display
            if isinstance(inp, (tuple, list)):
                display_input = repr(inp)
            else:
                display_input = repr((inp,)) if q.parameters and inp is not None else "()"
            
            # Execute correct solution to get expected output
            try:
                # Wrap the answer code in a test class
                func_sig = q.generate_signature()
                wrapped_code, class_name = wrap_java_function(q.answer_code, q.function_name, func_sig)
                
                # Prepare the inputs for Java
                args = []
                if isinstance(inp, (tuple, list)):
                    args = list(inp)  # Convert tuple to list for args
                elif q.parameters and inp is not None:  # Single parameter
                    args = [inp]
                
                # Run the Java code
                output, error = compile_and_run_java(wrapped_code, class_name, args)
                if error:
                    out = f"Error: {error}"
                else:
                    out = output
            except Exception as e:
                out = f"Error: {e}"
            
            rows.append((display_input, html.escape(str(out))))
            
        df = pd.DataFrame(rows, columns=['Input', 'Output'])
        with self.sample_output:
            self.sample_output.clear_output()
            display(HTML(df.to_html(index=False, escape=False)))

    def _run(self, _):
        """Run the student's code"""
        q = self.questions[self.current_index]
        self.program_output.clear_output()
        self.student_output = io.StringIO()
        
        try:
            # Prepare student's code
            func_sig = q.generate_signature()
            student_wrapped, student_class = wrap_java_function(self.code_input.value, q.function_name, func_sig)
            answer_wrapped, answer_class = wrap_java_function(q.answer_code, q.function_name, func_sig)
            
            # Test with all test inputs
            results = []
            for inp in q.test_inputs:
                # Prepare the arguments
                args = []
                if isinstance(inp, tuple) or isinstance(inp, list):
                    args = list(inp)  # Convert tuple to list for command-line args
                elif q.parameters:  # Single parameter
                    args = [inp]
                
                # Run the correct solution
                exp_output, exp_error = compile_and_run_java(answer_wrapped, answer_class, args)
                if exp_error:
                    results.append({
                        'Result': 'Error',
                        'Input': repr(inp) if isinstance(inp, (tuple, list)) else repr((inp,)) if inp is not None else "()",
                        'Expected': 'Error in answer code',
                        'Your Output': 'N/A'
                    })
                    continue
                
                # Run the student's solution
                std_output, std_error = compile_and_run_java(student_wrapped, student_class, args)
                
                if std_error:
                    results.append({
                        'Result': 'Error',
                        'Input': repr(inp) if isinstance(inp, (tuple, list)) else repr((inp,)) if inp is not None else "()",
                        'Expected': exp_output,
                        'Your Output': f"Error: {std_error}"
                    })
                else:
                    # Compare outputs
                    result = 'Pass' if std_output.strip() == exp_output.strip() else 'Fail'
                    results.append({
                        'Result': result,
                        'Input': repr(inp) if isinstance(inp, (tuple, list)) else repr((inp,)) if inp is not None else "()",
                        'Expected': exp_output,
                        'Your Output': std_output
                    })
                
            self._show_results(results)
            
        except Exception as e:
            # Handle any other errors
            results = [{'Result': 'Error', 'Input': '', 'Expected': '', 'Your Output': str(e)}]
            self._show_results(results)
        
        self._show_program_output()

    def _show_results(self, results):
        """Display test results"""
        total = len(results)
        passed = sum(1 for r in results if r['Result'] == 'Pass')
        status = "All tests passed!" if passed == total else f"{passed}/{total} tests passed"
        color = "green" if passed == total else "red"
        self.next_btn.disabled = self.results_next.disabled = (passed != total)

        with self.results_output:
            self.results_output.clear_output()
            display(HTML(f"<h4 style='color:{color}'>{status}</h4>"))
            df = pd.DataFrame(results)
            display(HTML(df.to_html(index=False, escape=False)))
            display(widgets.HBox([self.results_retry, self.results_next]))

    def _show_program_output(self):
        """Display any program output"""
        with self.program_output:
            self.program_output.clear_output()
            out = self.student_output.getvalue().strip()
            display(HTML(f"<pre>{out}</pre>") if out else HTML("<i>No output.</i>"))

    def _reset(self, _):
        """Reset the current question"""
        q = self.questions[self.current_index]
        sig = f"{q.generate_signature()} {{\n    // Your code here\n    return {self._default_return_value(q.return_type)};\n}}"
        self.code_input.value = sig
        for o in (self.results_output, self.program_output, self.hint_output):
            o.clear_output()
        self.next_btn.disabled = self.results_next.disabled = True

    def _next(self, _):
        """Go to the next question"""
        self.current_index += 1
        self._show_question()

    def _hint(self, _):
        """Show a hint for the current question"""
        q = self.questions[self.current_index]
        with self.hint_output:
            self.hint_output.clear_output()
            display(HTML(f"<b>Hint:</b> {q.hint or 'No hint available.'}"))

    def _skip(self, change):
        """Skip to a specific question"""
        idx = change['new']
        if idx != self.current_index:
            self.current_index = idx
            self._show_question()

# --- Java-specific imports for use in examples -----------------------------
# Remove this class since we're using Python's float equivalents
# class Double:
#     """Mock Java Double class with constants"""
#     POSITIVE_INFINITY = float('inf')
#     NEGATIVE_INFINITY = float('-inf')
#     NaN = float('nan')

# --- Example questions -----------------------------------------------------
def create_java_questions():
    """Create a list of Java practice questions"""
    return [
        JavaQuestion(
            function_name="helloWorld",
            return_type="String",
            parameters=[],
            param_types=[],
            description="Write a function that returns the string 'Hello, World!'. This is your first function, so just return the exact text inside the quotes.",
            answer_code="public static String helloWorld() {\n    return \"Hello, World!\";\n}",
            hint="You don't need any parameters for this function. Just use the return keyword with a String.",
            test_inputs=[[]]
        ),
        JavaQuestion(
            function_name="addNumbers",
            return_type="int",
            parameters=["a", "b"],
            param_types=["int", "int"],
            description="Write a function that takes two integers and returns their sum. For example, addNumbers(3, 4) should return 7.",
            answer_code="public static int addNumbers(int a, int b) {\n    return a + b;\n}",
            hint="Use the + operator to add the two parameters together.",
            test_inputs=[[3, 4], [10, 5], [0, 0]]
        ),
        JavaQuestion(
            function_name="alienGreeting",
            return_type="String",
            parameters=["name"],
            param_types=["String"],
            description="Return a greeting message to an alien named 'name'. Format should be 'Welcome to Earth, [name]!'",
            answer_code="public static String alienGreeting(String name) {\n    return \"Welcome to Earth, \" + name + \"!\";\n}",
            hint="Use string concatenation with the + operator to create the greeting.",
            test_inputs=["Zorg", "E.T.", "Spock", "Klaatu", "Yoda"]
        ),
        JavaQuestion(
            function_name="calculateHyperspaceDistance",
            return_type="double",
            parameters=["distance1", "distance2"],
            param_types=["double", "double"],
            description="Calculate the total distance of two hyperspace jumps by adding them together.",
            answer_code="public static double calculateHyperspaceDistance(double distance1, double distance2) {\n    return distance1 + distance2;\n}",
            hint="Add the two distances together using the + operator.",
            test_inputs=[[12.5, 7.8], [3.4, 5.6], [100.0, 200.0]]
        ),
        JavaQuestion(
            function_name="doubleNumber",
            return_type="int",
            parameters=["number"],
            param_types=["int"],
            description="Write a function that takes an integer and returns double its value. For example, doubleNumber(5) should return 10.",
            answer_code="public static int doubleNumber(int number) {\n    return number * 2;\n}",
            hint="Multiply the input by 2 using the * operator.",
            test_inputs=[[5], [0], [100]]
        ),
        JavaQuestion(
            function_name="calculateEnergy",
            return_type="double",
            parameters=["mass"],
            param_types=["double"],
            description="Calculate the energy equivalent of a mass using E=mc^2. c = 299792458.",
            answer_code="public static double calculateEnergy(double mass) {\n    double c = 299792458.0;\n    return mass * c * c;\n}",
            hint="Use the formula E = mass * c squared.",
            test_inputs=[1.0, 0.5, 2.0, 0.1, 0.001]
        ),
        JavaQuestion(
            function_name="isPositive",
            return_type="boolean",
            parameters=["number"],
            param_types=["int"],
            description="Write a function that takes an integer and returns true if it's greater than zero, or false otherwise.",
            answer_code="public static boolean isPositive(int number) {\n    return number > 0;\n}",
            hint="Use the > operator to check if the number is greater than zero.",
            test_inputs=[[5], [0], [-3]]
        ),
        JavaQuestion(
            function_name="joinPlanetNames",
            return_type="String",
            parameters=["planet1", "planet2"],
            param_types=["String", "String"],
            description="Return the two planet names joined with ' and ' in between.",
            answer_code="public static String joinPlanetNames(String planet1, String planet2) {\n    return planet1 + \" and \" + planet2;\n}",
            hint="Concatenate the strings with ' and ' in between using the + operator.",
            test_inputs=[["Earth", "Mars"], ["Vulcan", "Krypton"], ["Tatooine", "Naboo"]]
        ),
        JavaQuestion(
            function_name="calculateArea",
            return_type="int",
            parameters=["length", "width"],
            param_types=["int", "int"],
            description="Write a function that calculates the area of a rectangle. It should take the length and width as parameters and return their product.",
            answer_code="public static int calculateArea(int length, int width) {\n    return length * width;\n}",
            hint="Multiply the length by the width using the * operator.",
            test_inputs=[[4, 5], [10, 2], [7, 7]]
        ),
        JavaQuestion(
            function_name="convertToCelsius",
            return_type="double",
            parameters=["fahrenheit"],
            param_types=["double"],
            description="Write a function that converts a temperature from Fahrenheit to Celsius. The formula is: Celsius = (Fahrenheit - 32) * 5/9.",
            answer_code="public static double convertToCelsius(double fahrenheit) {\n    return (fahrenheit - 32) * 5 / 9;\n}",
            hint="First subtract 32 from the Fahrenheit temperature, then multiply by 5/9.",
            test_inputs=[[32], [212], [68]]
        ),
        JavaQuestion(
            function_name="isAdult",
            return_type="boolean",
            parameters=["age"],
            param_types=["int"],
            description="Write a function that checks if someone is an adult (18 or older). Return true if they are an adult, or false otherwise.",
            answer_code="public static boolean isAdult(int age) {\n    return age >= 18;\n}",
            hint="Use the >= operator to check if the age is greater than or equal to 18.",
            test_inputs=[[20], [18], [15]]
        ),
        JavaQuestion(
            function_name="maxNumber",
            return_type="int",
            parameters=["a", "b"],
            param_types=["int", "int"],
            description="Write a function that returns the larger of two integers. Use Java's Math.max() method.",
            answer_code="public static int maxNumber(int a, int b) {\n    return Math.max(a, b);\n}",
            hint="Use Java's Math.max() method which returns the largest of the given arguments.",
            test_inputs=[[5, 10], [10, 5], [7, 7]]
        ),
        JavaQuestion(
            function_name="absoluteValue",
            return_type="int",
            parameters=["number"],
            param_types=["int"],
            description="Write a function that returns the absolute value of an integer. Use Java's Math.abs() method.",
            answer_code="public static int absoluteValue(int number) {\n    return Math.abs(number);\n}",
            hint="Use Java's Math.abs() method which returns the absolute value of a number.",
            test_inputs=[[-5], [5], [0]]
        ),
        JavaQuestion(
            function_name="combineStrings",
            return_type="String",
            parameters=["firstName", "lastName"],
            param_types=["String", "String"],
            description="Write a function that takes a person's first and last name, and returns their full name with a space in between.",
            answer_code="public static String combineStrings(String firstName, String lastName) {\n    return firstName + \" \" + lastName;\n}",
            hint="Concatenate the first name, a space, and the last name using the + operator.",
            test_inputs=[["John", "Doe"], ["Jane", "Smith"], ["Albert", "Einstein"]]
        ),
    ]

# Example usage:
# java_tool = JavaPracticeTool(questions=create_java_questions())
