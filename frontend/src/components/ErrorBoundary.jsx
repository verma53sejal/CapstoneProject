import React from 'react';

const isDevelopment = process.env.NODE_ENV !== 'production';

export default class ErrorBoundary extends React.Component {
  state = {
    error: null,
    componentStack: '',
    detailsDismissed: false,
    retryKey: 0
  };

  static getDerivedStateFromError(error) {
    return { error, detailsDismissed: false };
  }

  componentDidCatch(error, errorInfo) {
    // Keep full diagnostics available to developers without exposing them in production UI.
    console.error('PlacementPro React runtime error:', error);
    console.error('PlacementPro component stack:', errorInfo.componentStack);
    this.setState({ componentStack: errorInfo.componentStack || '' });
  }

  retry = () => {
    // A full reload clears corrupted component state and retries the current route.
    window.location.reload();
  };

  dismissDetails = () => {
    this.setState({ detailsDismissed: true });
  };

  reopenDetails = () => {
    this.setState({ detailsDismissed: false });
  };

  render() {
    const { error, componentStack, detailsDismissed } = this.state;
    if (!error) return this.props.children;

    const path = window.location.pathname;
    const message = isDevelopment
      ? error.message || String(error)
      : 'The application encountered an unexpected error.';

    if (detailsDismissed) {
      return (
        <div style={styles.dismissedBar} role="alert">
          <span>PlacementPro stopped rendering because of an unexpected error.</span>
          <button type="button" onClick={this.reopenDetails} style={styles.linkButton}>View error</button>
          <button type="button" onClick={this.retry} style={styles.retryButton}>Retry</button>
        </div>
      );
    }

    return (
      <main style={styles.shell} role="alert" aria-live="assertive">
        <section style={styles.panel}>
          <div style={styles.badge}>Runtime error</div>
          <h1 style={styles.heading}>PlacementPro could not render this page</h1>
          <p style={styles.message}>{message}</p>
          <p style={styles.page}>Page: <code>{path}</code></p>

          {isDevelopment && componentStack && (
            <details open style={styles.details}>
              <summary>Component stack</summary>
              <pre style={styles.stack}>{componentStack}</pre>
            </details>
          )}

          <div style={styles.actions}>
            <button type="button" onClick={this.retry} style={styles.retryButton}>Retry</button>
            <button type="button" onClick={this.dismissDetails} style={styles.dismissButton}>Dismiss</button>
          </div>
          {!isDevelopment && <p style={styles.hint}>Try reloading the page. If the problem continues, contact support.</p>}
        </section>
      </main>
    );
  }
}

const styles = {
  shell: {
    minHeight: '100vh', background: '#0f1117', color: '#e2e8f0',
    display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 24
  },
  panel: {
    width: '100%', maxWidth: 760, background: '#1a2035', border: '1px solid #ef4444',
    borderRadius: 14, padding: 28, boxShadow: '0 18px 60px rgba(0,0,0,0.45)'
  },
  badge: { color: '#fca5a5', fontSize: 12, fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.08em' },
  heading: { margin: '8px 0 12px', fontSize: 24, fontFamily: 'Syne, sans-serif' },
  message: { color: '#fecaca', fontFamily: 'JetBrains Mono, monospace', whiteSpace: 'pre-wrap', wordBreak: 'break-word' },
  page: { color: '#94a3b8', fontSize: 13, marginTop: 14 },
  details: { marginTop: 20, color: '#cbd5e1' },
  stack: { marginTop: 10, padding: 14, maxHeight: 260, overflow: 'auto', background: '#0d1117', color: '#94a3b8', fontSize: 12, whiteSpace: 'pre-wrap' },
  actions: { display: 'flex', gap: 10, marginTop: 24 },
  retryButton: { background: 'linear-gradient(135deg, #4f8ef7, #7c3aed)', color: 'white', border: 0, borderRadius: 8, padding: '10px 18px', fontWeight: 700 },
  dismissButton: { background: '#141824', color: '#cbd5e1', border: '1px solid #3d4f7c', borderRadius: 8, padding: '10px 18px', fontWeight: 600 },
  linkButton: { background: 'none', color: '#93c5fd', border: 0, textDecoration: 'underline', marginLeft: 12 },
  hint: { color: '#64748b', fontSize: 13, marginTop: 16 },
  dismissedBar: { position: 'fixed', zIndex: 9999, top: 0, left: 0, right: 0, padding: '12px 18px', background: '#3f1d2e', borderBottom: '1px solid #ef4444', color: '#fecaca', display: 'flex', alignItems: 'center', gap: 8, fontSize: 13 }
};
