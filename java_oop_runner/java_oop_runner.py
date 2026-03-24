"""
Java OOP Problem Runner for Jupyter / Google Colab
===================================================
Drop this into a Colab cell (or import it) alongside a JSON problem file.
Students see a problem description, type Java code in a widget, hit Submit,
and get instant compile + test feedback.

Requirements:
  - JDK installed on the underlying OS  (Colab default: ✓)
  - ipywidgets  (Colab default: ✓)

Usage (in a Colab cell):
  # --- Cell 1: paste or %run this file ---
  # --- Cell 2: load from local file, URL, or inline dict ---
  load_problem("problems/animal_hierarchy.json")
  load_problem("https://raw.githubusercontent.com/you/repo/main/problems/animal.json")
"""

import json
import os
import subprocess
import tempfile
import textwrap
import urllib.request
from pathlib import Path

import ipywidgets as widgets
from IPython.display import display, HTML, clear_output

# ──────────────────────────────────────────────
#  Styling helpers
# ──────────────────────────────────────────────

_CSS = """
<style>
.oop-runner-title   { font-size:1.3em; font-weight:700; color:#1a73e8;
                      margin-bottom:4px; }
.oop-runner-desc    { background:#f8f9fa; border-left:4px solid #1a73e8;
                      padding:12px 16px; margin:0; white-space:pre-wrap;
                      font-family: monospace; font-size: 0.9em; line-height:1.5;
                      overflow-y:auto; }
.oop-runner-pass    { color:#0d7d2c; font-weight:600; }
.oop-runner-fail    { color:#c62828; font-weight:600; }
.oop-runner-compile { color:#c62828; }
.oop-runner-info    { color:#555; font-style:italic; }
</style>
"""

def _html(cls, text):
    return HTML(f'{_CSS}<div class="{cls}">{text}</div>')


# ──────────────────────────────────────────────
#  Core: compile & test
# ──────────────────────────────────────────────

def _run_java_tests(student_code: str, problem: dict) -> str:
    """
    Write student code + test harness to temp files, compile, run.
    Returns an HTML-formatted result string.
    """
    # We use a fresh temp dir each run so nothing leaks between submissions.
    with tempfile.TemporaryDirectory(prefix="java_oop_") as tmp:
        # ---- Write student file(s) ----
        # The problem specifies which filename(s) to expect.
        # student_code goes into the *primary* file; any support files
        # (provided by the problem) are written alongside it.
        primary_file = problem.get("student_filename", "Student.java")
        student_path = os.path.join(tmp, primary_file)
        with open(student_path, "w") as f:
            f.write(student_code)

        # Optional support files that ship with the problem
        # (e.g. an abstract base class the student must extend).
        for sf in problem.get("support_files", []):
            sp = os.path.join(tmp, sf["filename"])
            with open(sp, "w") as f:
                f.write(sf["code"])

        # ---- Write test harness ----
        test_code = problem["test_code"]
        test_file = problem.get("test_filename", "TestRunner.java")
        test_path = os.path.join(tmp, test_file)
        with open(test_path, "w") as f:
            f.write(test_code)

        # ---- Gather all .java files to compile ----
        java_files = sorted(str(p) for p in Path(tmp).glob("*.java"))

        # ---- Compile ----
        compile_result = subprocess.run(
            ["javac"] + java_files,
            capture_output=True, text=True, timeout=15
        )
        if compile_result.returncode != 0:
            err = compile_result.stderr.replace(tmp + "/", "")  # tidy paths
            err_html = (
                '<span class="oop-runner-compile"><b>⛔ Compilation failed</b></span>'
                f'<pre style="margin-top:6px;font-size:0.92em;">{_escape(err)}</pre>'
            )
            return err_html

        # ---- Run ----
        # Convention: the test harness has a main() that prints lines like
        #   PASS: description
        #   FAIL: description — expected X but got Y
        run_result = subprocess.run(
            ["java", "-cp", tmp, test_file.replace(".java", "")],
            capture_output=True, text=True, timeout=10
        )

        output = run_result.stdout.strip()
        stderr = run_result.stderr.strip()

        if run_result.returncode != 0 and not output:
            # Runtime crash with no structured output
            err_html = (
                '<span class="oop-runner-fail"><b>💥 Runtime error</b></span>'
                f'<pre style="margin-top:6px;font-size:0.92em;">{_escape(stderr or "Unknown error")}</pre>'
            )
            return err_html

        # ---- Format results ----
        return _format_test_output(output, stderr)


def _format_test_output(stdout: str, stderr: str) -> str:
    """Turn PASS/FAIL lines into nicely formatted HTML."""
    lines = stdout.splitlines()
    parts = []
    total = 0
    passed = 0
    for line in lines:
        if line.startswith("PASS:"):
            parts.append(f'<div class="oop-runner-pass">✅ {_escape(line)}</div>')
            total += 1
            passed += 1
        elif line.startswith("FAIL:"):
            parts.append(f'<div class="oop-runner-fail">❌ {_escape(line)}</div>')
            total += 1
        else:
            # Informational lines from the test harness
            parts.append(f'<div class="oop-runner-info">{_escape(line)}</div>')

    if total > 0:
        summary_cls = "oop-runner-pass" if passed == total else "oop-runner-fail"
        emoji = "🎉" if passed == total else "📊"
        parts.insert(0, f'<div class="{summary_cls}" style="font-size:1.1em;margin-bottom:6px;">'
                        f'{emoji} <b>{passed}/{total} tests passed</b></div>')

    if stderr:
        parts.append(f'<pre style="color:#888;font-size:0.85em;margin-top:8px;">{_escape(stderr)}</pre>')

    return "\n".join(parts)


def _escape(text: str) -> str:
    """Minimal HTML escaping."""
    return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")


# ──────────────────────────────────────────────
#  UI: build the interactive widget
# ──────────────────────────────────────────────

def load_problem(source):
    """
    Load a problem and display the interactive widget.

    Parameters
    ----------
    source : str or dict
        A URL (http/https), a local file path to a JSON file,
        or an already-parsed dict.

    Examples
    --------
    load_problem("problems/animal_hierarchy.json")
    load_problem("https://raw.githubusercontent.com/you/repo/main/problems/animal.json")
    load_problem({"title": "Inline problem", ...})
    """
    if isinstance(source, dict):
        problem = source
    elif isinstance(source, str) and source.startswith(("http://", "https://")):
        try:
            with urllib.request.urlopen(source) as resp:
                problem = json.loads(resp.read().decode("utf-8"))
        except Exception as e:
            display(HTML(
                f'{_CSS}<span class="oop-runner-fail">'
                f'<b>Failed to fetch problem:</b> {_escape(str(e))}</span>'
            ))
            return
    else:
        with open(source) as f:
            problem = json.load(f)

    # ---- Title (full width above everything) ----
    title_w = widgets.HTML(
        f'{_CSS}'
        f'<div class="oop-runner-title">{problem["title"]}</div>'
    )

    # ---- Left panel: scrollable instructions ----
    desc_w = widgets.HTML(
        f'{_CSS}'
        f'<div class="oop-runner-desc">{problem["description"]}</div>',
        layout=widgets.Layout(
            width="100%",
            height="500px",
            overflow_y="auto",
            border="1px solid #ddd",
            border_radius="4px",
        ),
    )
    left_panel = widgets.VBox(
        [widgets.HTML('<b style="font-size:0.95em;">📋 Instructions</b>'), desc_w],
        layout=widgets.Layout(width="48%", min_width="300px"),
    )

    # ---- Right panel: code editor + button + results ----
    starter = problem.get("starter_code", "// Write your code here\n")
    code_area = widgets.Textarea(
        value=starter,
        placeholder="Type your Java code here...",
        layout=widgets.Layout(width="100%", height="500px"),
    )

    submit_btn = widgets.Button(
        description="▶  Submit & Test",
        button_style="primary",
        layout=widgets.Layout(width="180px", height="36px"),
    )

    output_area = widgets.Output()

    def _on_submit(_):
        with output_area:
            clear_output(wait=True)
            display(HTML('<div class="oop-runner-info">⏳ Compiling & running tests…</div>'))
        try:
            result_html = _run_java_tests(code_area.value, problem)
        except subprocess.TimeoutExpired:
            result_html = '<span class="oop-runner-fail"><b>⏰ Timed out</b> — your code took too long (possible infinite loop).</span>'
        except Exception as e:
            result_html = f'<span class="oop-runner-fail"><b>Error:</b> {_escape(str(e))}</span>'
        with output_area:
            clear_output(wait=True)
            display(HTML(_CSS + result_html))

    submit_btn.on_click(_on_submit)

    right_panel = widgets.VBox(
        [
            widgets.HTML('<b style="font-size:0.95em;">✏️ Your Java Code</b>'),
            code_area,
            submit_btn,
            output_area,
        ],
        layout=widgets.Layout(width="50%", min_width="300px"),
    )

    # ---- Assemble: title on top, panels side-by-side ----
    side_by_side = widgets.HBox(
        [left_panel, right_panel],
        layout=widgets.Layout(
            width="100%",
            justify_content="space-between",
            gap="16px",
        ),
    )

    display(widgets.VBox([title_w, side_by_side]))


# ──────────────────────────────────────────────
#  Convenience: load all problems in a folder
# ──────────────────────────────────────────────

def list_problems(folder="problems"):
    """Print available problem files in a folder."""
    p = Path(folder)
    if not p.is_dir():
        print(f"Folder '{folder}' not found.")
        return []
    files = sorted(p.glob("*.json"))
    for i, f in enumerate(files, 1):
        with open(f) as fh:
            title = json.load(fh).get("title", f.stem)
        print(f"  {i}. {title}  ({f})")
    return files
