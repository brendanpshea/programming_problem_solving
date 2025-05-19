import os
import subprocess
import uuid
from IPython.display import display, HTML
from google.colab import output

# — SETUP WORKSPACE —
WORKDIR   = "java_autograder"
SRC_DIR   = os.path.join(WORKDIR, "src")
TEST_DIR  = os.path.join(WORKDIR, "tests")
BUILD_DIR = os.path.join(WORKDIR, "build")
for d in (SRC_DIR, TEST_DIR, BUILD_DIR):
    os.makedirs(d, exist_ok=True)

# — FETCH JAR DEPENDENCIES —
def _ensure_jars():
    JARS = [
        ("junit.jar",    "https://search.maven.org/remotecontent?filepath=junit/junit/4.13.2/junit-4.13.2.jar"),
        ("hamcrest.jar", "https://search.maven.org/remotecontent?filepath=org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar")
    ]
    for fname, url in JARS:
        if not os.path.exists(fname):
            subprocess.run(["wget","-q","-O",fname,url])
_ensure_jars()
JUNIT_JAR    = os.path.abspath("junit.jar")
HAMCREST_JAR = os.path.abspath("hamcrest.jar")

# — GLOBAL STATE & CALLBACKS —
_CURRENT_CLASS = None
_CURRENT_TESTS = None

def _compile_js(code: str):
    if not _CURRENT_CLASS:
        return "Error: no problem registered."
    with open(os.path.join(SRC_DIR, f"{_CURRENT_CLASS}.java"), 'w') as f:
        f.write(code)
    cp = os.pathsep.join([JUNIT_JAR, HAMCREST_JAR])
    cmd = ['javac','-cp',cp,'-d',BUILD_DIR]
    for d in (SRC_DIR, TEST_DIR):
        cmd += [os.path.join(d, fn) for fn in os.listdir(d) if fn.endswith(".java")]
    proc = subprocess.run(cmd, capture_output=True, text=True)
    if proc.returncode:
        return f"**Compile Error:**\n{proc.stderr}"
    return "**Compiled successfully.**"

def _test_js(_):
    if not _CURRENT_CLASS:
        return "Error: no problem registered."
    cp = os.pathsep.join([BUILD_DIR, JUNIT_JAR, HAMCREST_JAR])
    cmd = ['java','-cp',cp,'org.junit.runner.JUnitCore'] + list(_CURRENT_TESTS)
    try:
        proc = subprocess.run(cmd, capture_output=True, text=True, timeout=10)
        out = proc.stdout or ""
        err = proc.stderr or ""
        return out + (("\n"+err) if err else "")
    except subprocess.TimeoutExpired:
        return "**Error:** test execution timed out."

output.register_callback('java_grader.compile', _compile_js)
output.register_callback('java_grader.test',    _test_js)

# — THE WIDGET —
class JavaAutoGraderWidget:
    def __init__(self, class_name: str, description: str, tests: dict[str,str]):
        global _CURRENT_CLASS, _CURRENT_TESTS
        _CURRENT_CLASS = class_name
        _CURRENT_TESTS = tests.keys()
        for name, code in tests.items():
            with open(os.path.join(TEST_DIR, f"{name}.java"), 'w') as f:
                f.write(code)

        eid = f"ace-{uuid.uuid4().hex}"
        html = f"""
<div style="border:1px solid #ddd;padding:10px;border-radius:6px;margin-bottom:8px;">
  <strong style="font-size:16px;">Problem:</strong><br>{description}
</div>
<div id="{eid}" style="height:300px;width:100%;">
public class {class_name} {{
    // TODO: implement methods
}}
</div>
<button id="{eid}-c">Compile</button>
<button id="{eid}-t">Run Tests</button>
<pre id="{eid}-o" style="border:1px solid #ccc;padding:10px;white-space:pre-wrap;"></pre>
<script src="https://cdnjs.cloudflare.com/ajax/libs/ace/1.4.12/ace.js"></script>
<script>
  let ed = ace.edit("{eid}");
  ed.session.setMode("ace/mode/java");
  ed.setOptions({{enableBasicAutocompletion:true, tabSize:4}});
  document.getElementById("{eid}-c").onclick = () =>
    google.colab.kernel.invokeFunction(
      'java_grader.compile', [ed.getValue()], {{}}  // now literal {{}} 
    ).then(res => {{
      document.getElementById("{eid}-o").textContent =
        res.data['application/json'];
    }});
  document.getElementById("{eid}-t").onclick = () =>
    google.colab.kernel.invokeFunction(
      'java_grader.test', [], {{}}  // now literal {{}} 
    ).then(res => {{
      document.getElementById("{eid}-o").textContent =
        res.data['application/json'];
    }});
</script>
"""
        display(HTML(html))

# — EXAMPLE USAGE —
hello_test = """
import org.junit.Test;
import static org.junit.Assert.*;

public class HelloWorldTest {
    @Test public void testGreet() {
        assertEquals("Hello, world!", HelloWorld.greet());
    }
}
"""
widget = JavaAutoGraderWidget(
    class_name="HelloWorld",
    description=(
        "Implement a class **HelloWorld** with a static method\n"
        "`public static String greet()` that returns exactly\n"
        "`\"Hello, world!\"`."
    ),
    tests={"HelloWorldTest": hello_test}
)
