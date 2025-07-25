# Optimized Java Practice Tool for Google Colab - Fixed Version
from IPython.display import display, Javascript, HTML
import json, random, itertools, io, urllib.request, contextlib, os, tempfile, subprocess
from copy import deepcopy
import numpy as np, pandas as pd, html
import ipywidgets as widgets
import concurrent.futures
import hashlib
import re

# --- Sample inputs for Java -------------------------------------------------
_INPUT_SAMPLES = {
    'int': [0, 1, -1, 2, -2, 5, -5, 10, -10, 100, -100, -50, 2147483647],
    'double': [0.0, 1.5, -1.5, 3.14, -3.14, float('inf'), float('-inf'), float('nan')],
    'String': ["", "hello", "world", "Java", "AI", "The Matrix", "HAL 9000", "42", 
             "Dune", "Blade Runner", "I, Robot", "Ender's Game", "Neuromancer"],
    'boolean': [True, False]
}

def _clone(obj):
    return obj.copy() if isinstance(obj, np.ndarray) else deepcopy(obj)

# --- Optimized Java execution utilities -------------------------------------
class CompiledJavaCache:
    """Cache for compiled Java code to avoid recompilation"""
    def __init__(self):
        self.cache = {}
    
    def get_key(self, code):
        """Generate a cache key from code"""
        return hashlib.md5(code.encode()).hexdigest()
    
    def get(self, code):
        """Get compiled class path if exists"""
        return self.cache.get(self.get_key(code))
    
    def set(self, code, class_path):
        """Store compiled class path"""
        self.cache[self.get_key(code)] = class_path

# Global cache instance
_compiled_cache = CompiledJavaCache()

def parse_java_signature(signature):
    """Parse a Java method signature to extract return type, method name, and parameters"""
    # Remove public static modifiers
    clean_sig = signature.replace('public', '').replace('static', '').strip()
    
    # Find the opening parenthesis
    paren_index = clean_sig.find('(')
    if paren_index == -1:
        raise ValueError(f"Invalid signature: {signature}")
    
    # Everything before the parenthesis is return type + method name
    before_paren = clean_sig[:paren_index].strip()
    
    # Split by spaces to separate return type from method name
    parts = before_paren.split()
    
    if len(parts) == 0:
        raise ValueError(f"Invalid signature: {signature}")
    elif len(parts) == 1:
        # Only method name, assume void return type
        return_type = 'void'
        method_name = parts[0]
    else:
        # Last part is method name, everything else is return type
        method_name = parts[-1]
        return_type = ' '.join(parts[:-1])
    
    # Extract parameters
    params_start = clean_sig.find('(')
    params_end = clean_sig.find(')')
    if params_start != -1 and params_end != -1:
        params_str = clean_sig[params_start+1:params_end]
    else:
        params_str = ""
    
    return return_type, method_name, params_str

def create_test_harness(code, function_name, function_sig, test_cases, class_suffix=""):
    """Create a Java test harness that runs all test cases in one execution"""
    class_name = f"TestHarness{class_suffix}"
    
    # Parse the signature using our robust parser
    return_type, parsed_name, params_str = parse_java_signature(function_sig)
    
    # print(f"DEBUG: Parsed signature - Return type: '{return_type}', Method: '{parsed_name}', Params: '{params_str}'")
    
    params = []
    if params_str.strip():
        for p in params_str.split(','):
            p = p.strip()
            if p:
                parts = p.split()
                if len(parts) >= 2:
                    typ = ' '.join(parts[:-1])
                    name = parts[-1]
                    params.append((typ, name))
    
    # Generate test harness code
    test_code = []
    test_code.append("import java.util.*;")
    test_code.append("")
    test_code.append(f"public class {class_name} {{")
    test_code.append("")
    test_code.append("    // Function to test")
    
    # Add the function code
    if code.strip().startswith("public static"):
        for line in code.strip().split('\n'):
            test_code.append(f"    {line}")
    else:
        test_code.append(f"    {function_sig} {{")
        for line in code.strip().split('\n'):
            test_code.append(f"        {line}")
        test_code.append("    }")
    
    test_code.append("")
    test_code.append("    public static void main(String[] args) {")
    test_code.append("        // Run all test cases")
    
    for i, test_input in enumerate(test_cases):
        test_code.append(f"        // Test case {i}")
        test_code.append("        try {")
        
        # Prepare parameters
        param_values = []
        if params:
            if not isinstance(test_input, (list, tuple)):
                test_input = [test_input]
            
            for j, (typ, name) in enumerate(params):
                if j < len(test_input):
                    value = test_input[j]
                    var_name = f"{name}_{i}"
                    
                    if typ == "int":
                        if value is None:
                            test_code.append(f"            int {var_name} = 0;")  # Or handle differently
                        else:
                            test_code.append(f"            int {var_name} = {value};")
                    elif typ == "double":
                        if value is None:
                            test_code.append(f"            double {var_name} = 0.0;")  # Or handle differently
                        elif value == float('inf'):
                            test_code.append(f"            double {var_name} = Double.POSITIVE_INFINITY;")
                        elif value == float('-inf'):
                            test_code.append(f"            double {var_name} = Double.NEGATIVE_INFINITY;")
                        elif value != value:  # NaN check
                            test_code.append(f"            double {var_name} = Double.NaN;")
                        else:
                            test_code.append(f"            double {var_name} = {value};")
                    elif typ == "String":
                        if value is None:
                            test_code.append(f'            String {var_name} = null;')
                        else:
                            escaped_value = value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t")
                            test_code.append(f'            String {var_name} = "{escaped_value}";')
                    elif typ == "boolean":
                        if value is None:
                            test_code.append(f"            boolean {var_name} = false;")  # Or handle differently
                        else:
                            test_code.append(f"            boolean {var_name} = {str(value).lower()};")
                    
                    param_values.append(var_name)
        
        # Generate function call with CORRECT return type
        if param_values:
            call = f"{function_name}({', '.join(param_values)})"
        else:
            call = f"{function_name}()"
        
        # Handle different return types - USE THE PARSED RETURN TYPE
        if return_type == "void":
            test_code.append(f"            {call};")
            test_code.append(f'            System.out.println("TEST_{i}_RESULT:void");')
        else:
            # THIS IS THE KEY FIX - use return_type, not function_name!
            test_code.append(f"            {return_type} result = {call};")
            test_code.append(f'            System.out.println("TEST_{i}_RESULT:" + result);')
        
        test_code.append("        } catch (Exception e) {")
        test_code.append(f'            System.out.println("TEST_{i}_ERROR:" + e.getClass().getName() + ": " + e.getMessage());')
        test_code.append("        }")
        test_code.append("")
    
    test_code.append("    }")
    test_code.append("}")
    
    return '\n'.join(test_code), class_name

def compile_and_run_harness(harness_code, class_name):
    """Compile and run a test harness"""
    with tempfile.TemporaryDirectory() as tmpdir:
        java_file = os.path.join(tmpdir, f"{class_name}.java")
        with open(java_file, 'w') as f:
            f.write(harness_code)
        
        # Debug: print generated code for Answer harness
        # if "Answer" in class_name:
        #    print(f"\nGenerated {class_name}.java:")
        #    lines = harness_code.split('\n')
        #    for i, line in enumerate(lines[:25]):  # Show first 25 lines
        #        print(f"{i+1}: {line}")
        #    if len(lines) > 25:
        #        print("...")
        
        # Compile
        compile_process = subprocess.run(
            ['javac', java_file],
            capture_output=True,
            text=True
        )
        
        if compile_process.returncode != 0:
            return None, compile_process.stderr
        
        # Run
        try:
            run_process = subprocess.run(
                ['java', '-cp', tmpdir, class_name],
                capture_output=True,
                text=True,
                timeout=5
            )
            
            if run_process.returncode != 0:
                return None, run_process.stderr
            
            return run_process.stdout, None
        except subprocess.TimeoutExpired:
            return None, "Timeout: Code took too long to execute"

def parse_harness_output(output):
    """Parse the output from a test harness"""
    results = {}
    if output:
        lines = output.strip().split('\n')
        for line in lines:
            if '_RESULT:' in line:
                parts = line.split('_RESULT:', 1)
                test_num = int(parts[0].split('_')[1])
                results[test_num] = ('result', parts[1])
            elif '_ERROR:' in line:
                parts = line.split('_ERROR:', 1)
                test_num = int(parts[0].split('_')[1])
                results[test_num] = ('error', parts[1])
    return results

# --- Java Question class ----------------------------------------------------
class JavaQuestion:
    def __init__(self, function_name, return_type, parameters, param_types, 
                 description, answer_code, hint='', test_inputs=None):
        self.function_name = function_name
        self.return_type = return_type
        self.parameters = parameters
        self.param_types = param_types
        self.description = description
        self.answer_code = answer_code
        self.hint = hint
        self.test_inputs = test_inputs or self.generate_inputs()
        self.sample_inputs = random.sample(self.test_inputs, min(3, len(self.test_inputs)))
        
    def generate_signature(self):
        """Generate a Java function signature"""
        param_list = []
        for i, param in enumerate(self.parameters):
            param_list.append(f"{self.param_types[i]} {param}")
        
        return f"public static {self.return_type} {self.function_name}({', '.join(param_list)})"
        
    def generate_inputs(self, max_tests=10):
        """Generate test inputs for this function"""
        if not self.param_types:
            return [[]]
            
        samples = []
        for typ in self.param_types:
            if typ in _INPUT_SAMPLES:
                samples.append(_INPUT_SAMPLES[typ])
            else:
                samples.append([None])
        
        if len(samples) == 1:
            return random.sample(samples[0], min(max_tests, len(samples[0])))
        else:
            combos = list(itertools.product(*samples))
            return random.sample(combos, min(max_tests, len(combos)))

# --- Optimized JavaPracticeTool class --------------------------------------
class JavaPracticeTool:
    def __init__(self, questions=None, json_url=None):
        self.questions = (self._load_questions(json_url)
                         if json_url else questions or [])
        self.current_index = 0
        self.student_output = io.StringIO()
        self._answer_results_cache = {}
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

    def _get_answer_results(self, q):
        """Get cached answer results or compute them"""
        # Convert test inputs to hashable format
        hashable_inputs = []
        for inp in q.test_inputs:
            if isinstance(inp, (list, tuple)):
                hashable_inputs.append(tuple(inp))
            else:
                hashable_inputs.append(inp)
        cache_key = (q.function_name, tuple(hashable_inputs))
        
        if cache_key not in self._answer_results_cache:
            # Create and run answer harness
            func_sig = q.generate_signature()
            answer_harness, answer_class = create_test_harness(
                q.answer_code, q.function_name, func_sig, q.test_inputs, "Answer"
            )
            
            output, error = compile_and_run_harness(answer_harness, answer_class)
            if error:
                print(f"Error compiling/running answer code for {q.function_name}:")
                print(error)
                self._answer_results_cache[cache_key] = None
            else:
                self._answer_results_cache[cache_key] = parse_harness_output(output)
        
        return self._answer_results_cache[cache_key]

    def _render_sample_table(self, q):
        """Display sample inputs and outputs"""
        # Get answer results
        answer_results = self._get_answer_results(q)
        
        rows = []
        for i, inp in enumerate(q.sample_inputs):
            # Find the index of this input in test_inputs
            test_index = None
            for idx, test_inp in enumerate(q.test_inputs):
                # Compare inputs handling both single values and tuples/lists
                if isinstance(inp, (tuple, list)) and isinstance(test_inp, (tuple, list)):
                    if len(inp) == len(test_inp) and all(a == b for a, b in zip(inp, test_inp)):
                        test_index = idx
                        break
                elif inp == test_inp:
                    test_index = idx
                    break
            
            if test_index is None:
                test_index = i
            
            # Format input for display
            if isinstance(inp, (tuple, list)):
                display_input = repr(inp)
            else:
                display_input = repr((inp,)) if q.parameters else "()"
            
            # Get expected output
            if answer_results and test_index in answer_results:
                result_type, result_value = answer_results[test_index]
                if result_type == 'error':
                    out = f"Error: {result_value}"
                else:
                    out = result_value
            else:
                out = "Error computing expected output"
            
            rows.append((display_input, html.escape(str(out))))
            
        df = pd.DataFrame(rows, columns=['Input', 'Output'])
        with self.sample_output:
            self.sample_output.clear_output()
            display(HTML(df.to_html(index=False, escape=False)))

    def _run(self, _):
        """Run the student's code - optimized version"""
        q = self.questions[self.current_index]
        self.program_output.clear_output()
        
        try:
            # Create test harnesses
            func_sig = q.generate_signature()
            
            # Student harness
            student_harness, student_class = create_test_harness(
                self.code_input.value, q.function_name, func_sig, q.test_inputs, "Student"
            )
            
            # Get cached answer results
            answer_results = self._get_answer_results(q)
            
            if answer_results is None:
                # Error in answer code
                results = [{
                    'Result': 'Error',
                    'Input': 'N/A',
                    'Expected': 'Error in answer code',
                    'Your Output': 'Cannot run tests'
                }]
                self._show_results(results)
                return
            
            # Compile and run student code
            student_output, student_error = compile_and_run_harness(student_harness, student_class)
            
            if student_error:
                # Compilation or runtime error
                error_msg = student_error.strip()
                if 'error:' in error_msg.lower():
                    # Extract just the relevant error message
                    lines = error_msg.split('\n')
                    error_lines = [l for l in lines if 'error:' in l.lower()]
                    if error_lines:
                        error_msg = '\n'.join(error_lines[:3])  # Show first 3 errors
                
                results = [{
                    'Result': 'Compilation Error',
                    'Input': 'N/A',
                    'Expected': 'N/A',
                    'Your Output': error_msg
                }]
                self._show_results(results)
                return
            
            # Parse student results
            student_results = parse_harness_output(student_output)
            
            # Compare results
            results = []
            for i, inp in enumerate(q.test_inputs):
                input_str = repr(inp) if isinstance(inp, (tuple, list)) else repr((inp,)) if q.parameters else "()"
                
                # Get expected output
                if i in answer_results:
                    exp_type, exp_value = answer_results[i]
                    if exp_type == 'error':
                        expected = f"Error: {exp_value}"
                    else:
                        expected = exp_value
                else:
                    expected = "Missing output"
                
                # Get student output
                if i in student_results:
                    std_type, std_value = student_results[i]
                    if std_type == 'error':
                        result = 'Runtime Error'
                        your_output = f"Error: {std_value}"
                    else:
                        your_output = std_value
                        # Compare outputs
                        result = 'Pass' if std_value == exp_value else 'Fail'
                else:
                    result = 'Missing Output'
                    your_output = "No output produced"
                
                results.append({
                    'Result': result,
                    'Input': input_str,
                    'Expected': expected,
                    'Your Output': your_output
                })
            
            self._show_results(results)
            
            # Show any additional output
            with self.program_output:
                self.program_output.clear_output()
                # Filter out test result lines
                other_output = []
                if student_output:
                    for line in student_output.split('\n'):
                        if not ('_RESULT:' in line or '_ERROR:' in line):
                            other_output.append(line)
                
                if other_output and any(line.strip() for line in other_output):
                    display(HTML(f"<pre>{chr(10).join(other_output)}</pre>"))
                else:
                    display(HTML("<i>No additional output.</i>"))
                    
        except subprocess.TimeoutExpired:
            results = [{
                'Result': 'Timeout',
                'Input': 'N/A',
                'Expected': 'N/A',
                'Your Output': 'Code took too long to execute (possible infinite loop)'
            }]
            self._show_results(results)
        except Exception as e:
            results = [{
                'Result': 'Error',
                'Input': 'N/A',
                'Expected': 'N/A',
                'Your Output': str(e)
            }]
            self._show_results(results)

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
            
            # Color code the results
            df = pd.DataFrame(results)
            styled_results = []
            for _, row in df.iterrows():
                if row['Result'] == 'Pass':
                    styled_results.append({
                        'Result': f"<span style='color:green'>✓ Pass</span>",
                        'Input': row['Input'],
                        'Expected': row['Expected'],
                        'Your Output': row['Your Output']
                    })
                elif row['Result'] in ['Compilation Error', 'Runtime Error', 'Timeout']:
                    styled_results.append({
                        'Result': f"<span style='color:red'>✗ {row['Result']}</span>",
                        'Input': row['Input'],
                        'Expected': row['Expected'],
                        'Your Output': f"<span style='color:red'>{html.escape(row['Your Output'])}</span>"
                    })
                else:
                    styled_results.append({
                        'Result': f"<span style='color:red'>✗ {row['Result']}</span>",
                        'Input': row['Input'],
                        'Expected': row['Expected'],
                        'Your Output': row['Your Output']
                    })
            
            styled_df = pd.DataFrame(styled_results)
            display(HTML(styled_df.to_html(index=False, escape=False)))
            display(widgets.HBox([self.results_retry, self.results_next]))

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
            answer_code='public static String helloWorld() {\n    return "Hello, World!";\n}',
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
            test_inputs=[(3, 4), (10, 5), (0, 0), (-5, 5), (100, -50)]
        ),
        JavaQuestion(
            function_name="alienGreeting",
            return_type="String",
            parameters=["name"],
            param_types=["String"],
            description="Return a greeting message to an alien named 'name'. Format should be 'Welcome to Earth, [name]!'",
            answer_code='public static String alienGreeting(String name) {\n    return "Welcome to Earth, " + name + "!";\n}',
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
            test_inputs=[(12.5, 7.8), (3.4, 5.6), (100.0, 200.0), (0.0, 0.0)]
        ),
        JavaQuestion(
            function_name="doubleNumber",
            return_type="int",
            parameters=["number"],
            param_types=["int"],
            description="Write a function that takes an integer and returns double its value. For example, doubleNumber(5) should return 10.",
            answer_code="public static int doubleNumber(int number) {\n    return number * 2;\n}",
            hint="Multiply the input by 2 using the * operator.",
            test_inputs=[5, 0, 100, -10, 1]
        ),
        JavaQuestion(
            function_name="calculateEnergy",
            return_type="double",
            parameters=["mass"],
            param_types=["double"],
            description="Calculate the energy equivalent of a mass using E=mc^2. Use c = 299792458.0 (speed of light in m/s).",
            answer_code="public static double calculateEnergy(double mass) {\n    double c = 299792458.0;\n    return mass * c * c;\n}",
            hint="Use the formula E = mass * c * c, where c is the speed of light.",
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
            test_inputs=[5, 0, -3, 100, -100]
        ),
        JavaQuestion(
            function_name="joinPlanetNames",
            return_type="String",
            parameters=["planet1", "planet2"],
            param_types=["String", "String"],
            description="Return the two planet names joined with ' and ' in between.",
            answer_code='public static String joinPlanetNames(String planet1, String planet2) {\n    return planet1 + " and " + planet2;\n}',
            hint="Concatenate the strings with ' and ' in between using the + operator.",
            test_inputs=[("Earth", "Mars"), ("Vulcan", "Krypton"), ("Tatooine", "Naboo")]
        ),
        JavaQuestion(
            function_name="calculateArea",
            return_type="int",
            parameters=["length", "width"],
            param_types=["int", "int"],
            description="Write a function that calculates the area of a rectangle. It should take the length and width as parameters and return their product.",
            answer_code="public static int calculateArea(int length, int width) {\n    return length * width;\n}",
            hint="Multiply the length by the width using the * operator.",
            test_inputs=[(4, 5), (10, 2), (7, 7), (0, 5), (1, 1)]
        ),
        JavaQuestion(
            function_name="convertToCelsius",
            return_type="double",
            parameters=["fahrenheit"],
            param_types=["double"],
            description="Write a function that converts a temperature from Fahrenheit to Celsius. The formula is: Celsius = (Fahrenheit - 32) * 5/9.",
            answer_code="public static double convertToCelsius(double fahrenheit) {\n    return (fahrenheit - 32) * 5.0 / 9.0;\n}",
            hint="First subtract 32 from the Fahrenheit temperature, then multiply by 5/9. Use 5.0 and 9.0 to ensure floating point division.",
            test_inputs=[32.0, 212.0, 68.0, 0.0, -40.0]
        ),
        JavaQuestion(
            function_name="isAdult",
            return_type="boolean",
            parameters=["age"],
            param_types=["int"],
            description="Write a function that checks if someone is an adult (18 or older). Return true if they are an adult, or false otherwise.",
            answer_code="public static boolean isAdult(int age) {\n    return age >= 18;\n}",
            hint="Use the >= operator to check if the age is greater than or equal to 18.",
            test_inputs=[20, 18, 15, 0, 100]
        ),
        JavaQuestion(
            function_name="maxNumber",
            return_type="int",
            parameters=["a", "b"],
            param_types=["int", "int"],
            description="Write a function that returns the larger of two integers. Use Java's Math.max() method.",
            answer_code="public static int maxNumber(int a, int b) {\n    return Math.max(a, b);\n}",
            hint="Use Java's Math.max() method which returns the largest of the given arguments.",
            test_inputs=[(5, 10), (10, 5), (7, 7), (-5, -10), (0, 0)]
        ),
        JavaQuestion(
            function_name="absoluteValue",
            return_type="int",
            parameters=["number"],
            param_types=["int"],
            description="Write a function that returns the absolute value of an integer. Use Java's Math.abs() method.",
            answer_code="public static int absoluteValue(int number) {\n    return Math.abs(number);\n}",
            hint="Use Java's Math.abs() method which returns the absolute value of a number.",
            test_inputs=[-5, 5, 0, -100, 100]
        ),
        JavaQuestion(
            function_name="combineStrings",
            return_type="String",
            parameters=["firstName", "lastName"],
            param_types=["String", "String"],
            description="Write a function that takes a person's first and last name, and returns their full name with a space in between.",
            answer_code='public static String combineStrings(String firstName, String lastName) {\n    return firstName + " " + lastName;\n}',
            hint="Concatenate the first name, a space, and the last name using the + operator.",
            test_inputs=[("John", "Doe"), ("Jane", "Smith"), ("Albert", "Einstein"), ("Marie", "Curie")]
        ),
        JavaQuestion(
            function_name="getStringLength",
            return_type="int",
            parameters=["text"],
            param_types=["String"],
            description="Write a function that returns the length of a string. If the string is null, return 0.",
            answer_code='public static int getStringLength(String text) {\n    if (text == null) {\n        return 0;\n    }\n    return text.length();\n}',
            hint="Check if the string is null before calling .length(). Use == null to check for null.",
            test_inputs=["hello", "", "Java Programming", None, "a", None]
        ),
    ]

# Example usage:
# java_tool = JavaPracticeTool(questions=create_java_questions())
