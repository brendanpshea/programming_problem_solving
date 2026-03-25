/* ═══════════════════════════════════════════════════════════════════
   lecture-init.js
   Shared Reveal.js + Mermaid initialisation for COMP 2243 lectures.
   Load AFTER all reveal.js plugin scripts and mermaid.min.js.
   ═══════════════════════════════════════════════════════════════════ */

Reveal.initialize({
  hash: true,
  slideNumber: 'c/t',
  transition: 'slide',
  transitionSpeed: 'default',
  backgroundTransition: 'fade',
  controls: true,
  progress: true,
  center: false,
  width: 1200,
  height: 800,
  margin: 0.03,
  minScale: 0.2,
  maxScale: 1.6,
  plugins: [RevealMarkdown, RevealHighlight, RevealNotes]
});

/* Keep slides pinned to the top regardless of content height */
function pinSlidesToTop() {
  document.querySelectorAll(
    '.reveal .slides > section, .reveal .slides > section > section'
  ).forEach(s => { s.style.top = '0px'; s.style.marginTop = '0px'; });
}

document.querySelector('.reveal')?.classList.remove('center');
Reveal.configure({ center: false });
Reveal.layout();
pinSlidesToTop();

Reveal.on('ready',        ()      => { Reveal.configure({ center: false }); pinSlidesToTop(); });
Reveal.on('slidechanged', ()      => { pinSlidesToTop(); });
window.addEventListener('resize', () => { setTimeout(pinSlidesToTop, 0); });

/* Mermaid — render only the diagram(s) on the visible slide */
mermaid.initialize({
  startOnLoad: false,
  theme: 'dark',
  themeVariables: {
    fontFamily: 'Segoe UI, Arial, sans-serif',
    fontSize: '10px',
    primaryColor: '#2f7dbe',
    primaryTextColor: '#ffffff',
    primaryBorderColor: '#63f0ff',
    lineColor: '#ffd84d',
    secondaryColor: '#1fa86a',
    tertiaryColor: '#ff9f43',
    background: '#0d1321',
    mainBkg: '#11192c',
    clusterBkg: '#11192c',
    edgeLabelBackground: '#1a2438',
    secondaryBorderColor: '#49e29c',
    tertiaryBorderColor: '#ffd84d',
    cScale0: '#2f7dbe',
    cScale1: '#1fa86a',
    cScale2: '#ff9f43',
    cScale3: '#ff6f91',
    cScale4: '#7b68ee',
    cScale5: '#63f0ff',
    nodeBorder: '#63f0ff',
    clusterBorder: '#95a8ff',
    defaultLinkColor: '#ffd84d',
    labelTextColor: '#f4f7fc'
  },
  flowchart: {
    htmlLabels: false,
    curve: 'linear',
    nodeSpacing: 28,
    rankSpacing: 32,
    padding: 10
  }
});

function renderMermaidInSlide(slideEl) {
  if (!slideEl) return;
  const pending = slideEl.querySelectorAll('.mermaid:not([data-processed="true"])');
  if (pending.length > 0) mermaid.run({ nodes: pending });
}

Reveal.on('ready',        e => renderMermaidInSlide(e.currentSlide));
Reveal.on('slidechanged', e => renderMermaidInSlide(e.currentSlide));