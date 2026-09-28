(() => {
  const page = document.body.dataset.page || 'dashboard';
  const pages = [
    ['dashboard', 'Mission overview', '/', '◈'], ['dishes', 'Antenna arrays', '/dishes.html', '⌖'],
    ['passes', 'Satellite passes', '/passes.html', '◌'], ['telemetry', 'Telemetry stream', '/telemetry.html', '⌁'],
    ['billing', 'Billing preview', '/billing.html', '◇'], ['invoices', 'Customer invoices', '/invoices.html', '▤'],
    ['vendors', 'Servo manufacturers', '/vendors.html', '⎔'], ['reports', 'Financial reports', '/reports.html', '▥'],
    ['settings', 'Settings', '/settings.html', '⚙']
  ];
  const titles = Object.fromEntries(pages.map(([key, title]) => [key, title]));
  const shell = document.querySelector('#app-shell');
  shell.innerHTML = `
    <div class="app-shell">
      <aside class="sidebar" id="sidebar">
        <a class="brand" href="/" aria-label="Deep Space Ground Station home"><span class="brand-mark">◈</span><span class="brand-name">DEEP SPACE GS<span class="brand-sub">GROUND NETWORK / 01</span></span></a>
        <div class="nav-label">Operations</div>
        <nav class="nav-list" aria-label="Main navigation">${pages.map(([key, title, href, icon]) => `<a class="nav-link ${key === page ? 'active' : ''}" href="${href}" ${key === page ? 'aria-current="page"' : ''}><span class="nav-icon" aria-hidden="true">${icon}</span>${title}</a>`).join('')}</nav>
        <div class="nav-label">System</div>
        <div class="sidebar-foot"><span class="connection-dot"></span>NODE / GS-CHN-01<br><span style="padding-left:15px">UTC LINK SYNCHRONIZED</span></div>
      </aside>
      <div class="workspace">
        <header class="topbar">
          <button class="menu-toggle" id="menu-toggle" aria-label="Open navigation">☰</button>
          <div class="top-title-wrap"><div class="top-eyebrow">DEEP-SPACE GROUND STATION</div><div class="top-title">${titles[page] || 'Mission overview'}</div></div>
          <div class="top-actions"><div class="system-state"><span class="connection-dot"></span><span class="state-copy">SYSTEM ONLINE</span></div><button class="notification-button" id="notifications" type="button" aria-label="Notifications" title="Notifications">&#128276;</button><div class="admin-chip"><span class="admin-avatar">AD</span><span class="admin-label">Station Admin</span></div></div>
        </header>
        <main class="main page-appear" id="page-content" aria-live="polite"></main>
      </div>
      <div class="toast-region" id="toast-region" aria-live="polite"></div>
      <div id="modal-root"></div>
    </div>`;

  document.querySelector('#menu-toggle').addEventListener('click', () => document.querySelector('#sidebar').classList.toggle('open'));
  document.querySelector('#notifications').addEventListener('click', () => toast('No new station notifications.', 'success'));
  GSApi.health().then(online => {
    const state = document.querySelector('.system-state');
    state.classList.toggle('offline', !online);
    state.querySelector('.state-copy').textContent = online ? 'SYSTEM ONLINE' : 'API OFFLINE';
  });
  document.addEventListener('click', (event) => {
    if (innerWidth <= 760 && !event.target.closest('#sidebar') && !event.target.closest('#menu-toggle')) {
      document.querySelector('#sidebar').classList.remove('open');
    }
  });

  const esc = (value) => String(value ?? '').replace(/[&<>"']/g, (char) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char]);
  const toast = (message, type = '') => {
    const region = document.querySelector('#toast-region');
    const item = document.createElement('div');
    item.className = `toast ${type}`;
    item.textContent = message;
    region.append(item);
    setTimeout(() => item.remove(), 4500);
  };
  const showError = (error) => {
    const prefix = error?.status === 0 ? 'Backend is unavailable. ' : '';
    toast(prefix + (error?.message || 'Unable to complete the request.'), 'error');
  };
  const modal = (title, body, onSubmit, submitLabel = 'Save changes') => {
    const root = document.querySelector('#modal-root');
    root.innerHTML = `<div class="modal-backdrop" role="presentation"><section class="modal" role="dialog" aria-modal="true" aria-labelledby="modal-title"><div class="modal-head"><h2 id="modal-title">${esc(title)}</h2><button class="icon-button" type="button" data-close aria-label="Close dialog">×</button></div><div class="modal-body">${body}</div><div class="modal-foot"><button class="btn btn-quiet" type="button" data-close>Cancel</button><button class="btn btn-primary" type="submit" form="record-form">${esc(submitLabel)}</button></div></section></div>`;
    root.querySelectorAll('[data-close]').forEach(button => button.addEventListener('click', () => root.replaceChildren()));
    root.querySelector('.modal-backdrop').addEventListener('click', (event) => { if (event.target.classList.contains('modal-backdrop')) root.replaceChildren(); });
    const form = root.querySelector('#record-form');
    if (form) form.addEventListener('submit', async (event) => {
      event.preventDefault();
      const button = root.querySelector('[type="submit"]');
      button.disabled = true;
      try { await onSubmit(new FormData(form)); root.replaceChildren(); }
      catch (error) { showError(error); button.disabled = false; }
    });
    root.querySelector('.modal').addEventListener('keydown', event => { if (event.key === 'Escape') root.replaceChildren(); });
  };
  const money = (value, currency = 'INR') => value == null ? '—' : new Intl.NumberFormat('en-IN', { style: 'currency', currency, maximumFractionDigits: 2 }).format(Number(value));
  const number = (value, digits = 1) => value == null ? '—' : new Intl.NumberFormat('en-IN', { maximumFractionDigits: digits }).format(Number(value));
  const dateTime = (value) => value ? new Date(value).toLocaleString([], { dateStyle: 'medium', timeStyle: 'short' }) : '—';
  const date = (value) => value ? new Date(value + (value.length === 10 ? 'T00:00:00' : '')).toLocaleDateString([], { dateStyle: 'medium' }) : '—';
  const badge = (value) => `<span class="badge badge-${esc(String(value || 'unknown').toLowerCase())}">${esc(value || 'UNKNOWN')}</span>`;
  const empty = (title, detail, action = '') => `<div class="empty-state"><div class="empty-mark">⌁</div><strong>${esc(title)}</strong><span>${esc(detail)}</span>${action ? `<div style="margin-top:15px">${action}</div>` : ''}</div>`;
  const setLoading = (target) => { const element = typeof target === 'string' ? document.querySelector(target) : target; if (element) element.innerHTML = '<div class="panel-pad"><div class="loading-line"></div><div class="skeleton" style="width:58%;margin:18px 0 10px"></div><div class="skeleton" style="width:82%"></div></div>'; };
  window.GSUI = { esc, toast, showError, modal, money, number, dateTime, date, badge, empty, setLoading };
})();
