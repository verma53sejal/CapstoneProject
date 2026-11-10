import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';

const isDevelopment = process.env.NODE_ENV !== 'production';

function showGlobalRuntimeError(error, source = 'Browser runtime error') {
  let overlay = document.getElementById('placementpro-runtime-error');
  if (!overlay) {
    overlay = document.createElement('div');
    overlay.id = 'placementpro-runtime-error';
    overlay.style.cssText = 'position:fixed;inset:0;z-index:2147483647;background:#0f1117;color:#e2e8f0;display:flex;align-items:center;justify-content:center;padding:24px;font-family:Inter,Arial,sans-serif;overflow:auto';
    document.body.appendChild(overlay);
  }

  const message = isDevelopment && error?.message ? error.message : 'The application encountered an unexpected error.';
  const stack = isDevelopment && (error?.stack || error?.reason?.stack) ? (error.stack || error.reason.stack) : '';
  overlay.innerHTML = '';

  const panel = document.createElement('section');
  panel.style.cssText = 'width:100%;max-width:760px;background:#1a2035;border:1px solid #ef4444;border-radius:14px;padding:28px;box-shadow:0 18px 60px rgba(0,0,0,.45)';
  const title = document.createElement('h1');
  title.textContent = 'PlacementPro encountered a runtime error';
  title.style.cssText = 'font-size:24px;margin:0 0 12px;font-family:Syne,Arial,sans-serif';
  const sourceText = document.createElement('p');
  sourceText.textContent = `${source} • ${window.location.pathname}`;
  sourceText.style.cssText = 'color:#94a3b8;font-size:13px;margin:0 0 12px';
  const messageText = document.createElement('p');
  messageText.textContent = message;
  messageText.style.cssText = 'color:#fecaca;font-family:monospace;white-space:pre-wrap;word-break:break-word';
  panel.append(title, sourceText, messageText);

  if (stack) {
    const details = document.createElement('details');
    details.open = true;
    const summary = document.createElement('summary');
    summary.textContent = 'Debug details';
    const pre = document.createElement('pre');
    pre.textContent = stack;
    pre.style.cssText = 'max-height:260px;overflow:auto;background:#0d1117;color:#94a3b8;padding:14px;font-size:12px;white-space:pre-wrap';
    details.append(summary, pre);
    panel.appendChild(details);
  }

  const actions = document.createElement('div');
  actions.style.cssText = 'display:flex;gap:10px;margin-top:24px';
  const retry = document.createElement('button');
  retry.textContent = 'Retry';
  retry.style.cssText = 'background:linear-gradient(135deg,#4f8ef7,#7c3aed);color:white;border:0;border-radius:8px;padding:10px 18px;font-weight:700;cursor:pointer';
  retry.onclick = () => window.location.reload();
  const dismiss = document.createElement('button');
  dismiss.textContent = 'Dismiss';
  dismiss.style.cssText = 'background:#141824;color:#cbd5e1;border:1px solid #3d4f7c;border-radius:8px;padding:10px 18px;font-weight:600;cursor:pointer';
  dismiss.onclick = () => { overlay.style.display = 'none'; };
  actions.append(retry, dismiss);
  panel.appendChild(actions);
  overlay.appendChild(panel);
}

window.addEventListener('error', event => {
  // Ignore resource-loading notifications; show JavaScript errors only.
  if (event.error || event.message) {
    console.error('PlacementPro global runtime error:', event.error || event.message);
    showGlobalRuntimeError(event.error || new Error(event.message), 'Uncaught runtime error');
  }
});

window.addEventListener('unhandledrejection', event => {
  console.error('PlacementPro unhandled promise rejection:', event.reason);
  showGlobalRuntimeError(event.reason instanceof Error ? event.reason : new Error(String(event.reason)), 'Unhandled promise rejection');
});

const root = ReactDOM.createRoot(document.getElementById('root'));

// Load the application only after the global handlers exist. This also catches
// module-evaluation/import errors that happen before React can mount an ErrorBoundary.
import('./App')
  .then(({ default: App }) => {
    root.render(<React.StrictMode><App /></React.StrictMode>);
  })
  .catch(error => {
    console.error('PlacementPro failed to load the application:', error);
    showGlobalRuntimeError(error, 'Application startup error');
  });
