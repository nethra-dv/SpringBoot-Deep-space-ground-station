document.addEventListener('DOMContentLoaded', async () => {
  const root = document.querySelector('#page-content');
  const ui = window.GSUI;
  root.innerHTML = `
    <div class="earth-strip"><img src="https://images.unsplash.com/photo-1614730321146-b6fa6a46bcb4?auto=format&fit=crop&w=1500&q=85" alt="Earth viewed from orbit"><div class="earth-overlay"><div><div class="eyebrow">NETWORK CONTROL / LIVE OPERATIONS</div><h2>Deep-space tracking network</h2><p>Telemetry acquisition, antenna readiness and pass activity across the ground station.</p></div></div></div>
    <div class="page-head"><div><div class="eyebrow">MISSION OVERVIEW</div><h1>Station status</h1><p>Live operational snapshot from connected ground assets.</p></div><div class="toolbar"><span class="badge badge-online">API LINK ACTIVE</span><button class="btn btn-quiet" id="refresh-dashboard">↻ Refresh</button></div></div>
    <section class="grid grid-4" id="metrics"><div class="panel metric"><div class="metric-label">Ground dishes</div><div class="metric-value">—</div><div class="metric-note">Loading assets</div></div><div class="panel metric"><div class="metric-label">Available arrays</div><div class="metric-value">—</div><div class="metric-note">Awaiting API data</div></div><div class="panel metric"><div class="metric-label">Active tracking</div><div class="metric-value">—</div><div class="metric-note">Current pass status</div></div><div class="panel metric"><div class="metric-label">Scheduled passes</div><div class="metric-value">—</div><div class="metric-note">Upcoming operations</div></div><div class="panel metric"><div class="metric-label">Data received</div><div class="metric-value">—</div><div class="metric-note">Telemetry sum</div></div><div class="panel metric"><div class="metric-label">Pending invoices</div><div class="metric-value">N/A</div><div class="metric-note">Invoice list API unavailable</div></div><div class="panel metric"><div class="metric-label">Total revenue</div><div class="metric-value">N/A</div><div class="metric-note">Financial report API unavailable</div></div><div class="panel metric"><div class="metric-label">Connected vendors</div><div class="metric-value">—</div><div class="metric-note">Servo manufacturers</div></div></section>
    <div class="dashboard-grid"><section><div class="section-heading"><h2>Antenna array status</h2><a class="btn btn-sm btn-quiet" href="/dishes.html">Manage arrays →</a></div><div class="panel panel-pad"><div class="dish-list" id="dish-summary"></div></div></section><section><div class="section-heading"><h2>Telemetry reception</h2><span>ALL RECORDS / GB</span></div><div class="panel panel-pad"><div class="telemetry-split"><div><div class="chart-box" id="telemetry-chart"></div><div class="chart-legend"><span><i class="legend-dot"></i>Received GB</span></div></div><div><div class="gauge" id="tracking-gauge" style="--gauge:0deg"><div class="gauge-value">—<div class="gauge-label">TRACK HOURS</div></div></div><div class="gauge-label" id="telemetry-count">No records loaded</div></div></div></div></section></div>
    <div class="dashboard-bottom"><section><div class="section-heading"><h2>Recent satellite passes</h2><a class="btn btn-sm btn-quiet" href="/passes.html">Pass schedule →</a></div><div class="panel" id="recent-passes"></div></section><section><div class="section-heading"><h2>Financial connectivity</h2><span>PHASED DATA</span></div><div class="panel panel-pad"><div class="activity-list"><div class="activity-row"><span>Billing preview</span><span class="badge badge-online">CONNECTED</span></div><div class="activity-row"><span>Invoice generation</span><span class="badge badge-online">CONNECTED</span></div><div class="activity-row"><span>Invoice list / payment status</span><span class="badge badge-scheduled">NOT AVAILABLE</span></div><div class="activity-row"><span>Balance sheet / P&amp;L API</span><span class="badge badge-scheduled">NOT AVAILABLE</span></div><div class="activity-row"><span>Budget actuals API</span><span class="badge badge-scheduled">NOT AVAILABLE</span></div></div></div></section></div>`;

  const chart = (records) => {
    const host = document.querySelector('#telemetry-chart');
    const data = [...records].sort((a,b) => new Date(a.recordedAt) - new Date(b.recordedAt)).slice(-8);
    if (!data.length) { host.innerHTML = ui.empty('No telemetry received', 'Telemetry will appear here after the first record is ingested.'); return; }
    const values = data.map(record => Number(record.dataReceivedGB || 0));
    const max = Math.max(...values, 1);
    const points = values.map((value, index) => `${18 + index * (264 / Math.max(values.length - 1, 1))},${176 - (value / max) * 145}`).join(' ');
    const circles = values.map((value, index) => `<circle cx="${18 + index * (264 / Math.max(values.length - 1, 1))}" cy="${176 - (value / max) * 145}" r="3.5" fill="#72e2df"><title>${ui.esc(value)} GB</title></circle>`).join('');
    host.innerHTML = `<svg class="chart-svg" viewBox="0 0 300 205" role="img" aria-label="Recent received data by telemetry record"><defs><linearGradient id="chartFill" x1="0" x2="0" y1="0" y2="1"><stop offset="0" stop-color="#72e2df" stop-opacity=".28"/><stop offset="1" stop-color="#72e2df" stop-opacity="0"/></linearGradient></defs><line class="chart-grid" x1="10" y1="30" x2="290" y2="30"/><line class="chart-grid" x1="10" y1="80" x2="290" y2="80"/><line class="chart-grid" x1="10" y1="130" x2="290" y2="130"/><line class="chart-grid" x1="10" y1="180" x2="290" y2="180"/><polygon class="chart-fill" points="${points} 282,180 18,180"/><polyline class="chart-line" points="${points}"/>${circles}<text class="chart-label" x="12" y="198">OLDER</text><text class="chart-label" x="254" y="198">LATEST</text></svg>`;
  };

  async function load() {
    ui.setLoading('#dish-summary'); ui.setLoading('#recent-passes');
    try {
      const [dishes, passes, telemetry, vendors] = await Promise.all([GSApi.dishes.list(), GSApi.passes.list(), GSApi.telemetry.list(), GSApi.vendors.list()]);
      const active = passes.filter(pass => pass.status === 'ACTIVE').length;
      const scheduled = passes.filter(pass => pass.status === 'SCHEDULED').length;
      const received = telemetry.reduce((sum, row) => sum + Number(row.dataReceivedGB || 0), 0);
      const hours = telemetry.reduce((sum, row) => sum + Number(row.trackingDurationHours || 0), 0);
      const available = dishes.filter(dish => dish.status === 'AVAILABLE').length;
      const metrics = [
        ['Ground dishes', dishes.length, 'Registered antenna arrays', ''], ['Available arrays', available, `${dishes.length ? Math.round(available / dishes.length * 100) : 0}% of registered assets`, 'lime'],
        ['Active tracking', active, 'Current pass status', 'accent'], ['Scheduled passes', scheduled, 'Awaiting station contact', 'amber'],
        ['Data received', `${ui.number(received)} GB`, `${telemetry.length} telemetry records`, 'accent'], ['Pending invoices', 'N/A', 'Invoice list API unavailable', 'amber'],
        ['Total revenue', 'N/A', 'Financial report API unavailable', 'lime'], ['Connected vendors', vendors.length, 'Servo manufacturers', '']
      ];
      document.querySelector('#metrics').innerHTML = metrics.map(([label, value, note, style]) => `<article class="panel metric"><div class="metric-label">${ui.esc(label)}</div><div class="metric-value ${style ? `metric-${style}` : ''}">${ui.esc(value)}</div><div class="metric-note">${ui.esc(note)}</div></article>`).join('');
      document.querySelector('#dish-summary').innerHTML = dishes.length ? dishes.map(dish => `<article class="dish-row"><div class="dish-orbit">${ui.esc(dish.dishCode)}</div><div><div class="dish-name">${ui.esc(dish.dishName)}</div><div class="dish-meta">${ui.esc(dish.frequencyBand || 'Band n/a')} · ${ui.number(dish.diameterMeters)} m · ${ui.esc(dish.location || 'Location n/a')}</div><div class="dish-meta">Elevation ${ui.number(dish.minimumElevation)}°–${ui.number(dish.maximumElevation)}°</div></div>${ui.badge(dish.status)}</article>`).join('') : ui.empty('No antenna arrays registered', 'Add a dish to begin tracking operations.', '<a class="btn btn-primary btn-sm" href="/dishes.html">＋ Add dish</a>');
      const sorted = [...passes].sort((a,b) => new Date(b.passStart) - new Date(a.passStart)).slice(0,5);
      document.querySelector('#recent-passes').innerHTML = sorted.length ? `<div class="table-wrap"><table class="data-table"><thead><tr><th>Satellite</th><th>Customer</th><th>Dish</th><th>Pass window</th><th>Status</th><th>Telemetry</th><th>Billing</th></tr></thead><tbody>${sorted.map(pass => { const count = telemetry.filter(record => record.satellitePassId === pass.id).length; return `<tr><td><strong>${ui.esc(pass.satelliteName)}</strong></td><td>${ui.esc(pass.customerName)}</td><td class="mono">${ui.esc(pass.dishCode)}</td><td>${ui.dateTime(pass.passStart)}</td><td>${ui.badge(pass.status)}</td><td>${count} record${count === 1 ? '' : 's'}</td><td>${pass.status === 'COMPLETED' ? `<a class="btn btn-sm" href="/billing.html?passId=${encodeURIComponent(pass.id)}">Ready to bill</a>` : '<span class="muted">Not billable</span>'}</td></tr>`; }).join('')}</tbody></table></div>` : ui.empty('No satellite passes scheduled yet.', 'Pass schedules will appear here when entered.', '<a class="btn btn-primary btn-sm" href="/passes.html">＋ Schedule pass</a>');
      chart(telemetry);
      const gauge = document.querySelector('#tracking-gauge');
      gauge.style.setProperty('--gauge', `${Math.min(hours / Math.max(hours, 1) * 360, 360)}deg`);
      gauge.querySelector('.gauge-value').firstChild.textContent = ui.number(hours);
      document.querySelector('#telemetry-count').textContent = `${telemetry.length} telemetry records received`;
    } catch (error) {
      ui.showError(error);
      document.querySelector('#dish-summary').innerHTML = ui.empty('Station data unavailable', 'Could not load dish records from the backend.');
      document.querySelector('#recent-passes').innerHTML = ui.empty('Pass data unavailable', 'Check that the application and database are online.');
      document.querySelector('#telemetry-chart').innerHTML = ui.empty('Telemetry unavailable', 'Could not reach the telemetry API.');
    }
  }
  document.querySelector('#refresh-dashboard').addEventListener('click', load);
  load();
});
