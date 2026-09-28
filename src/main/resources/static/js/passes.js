document.addEventListener('DOMContentLoaded', async () => {
  const root = document.querySelector('#page-content'), ui = GSUI; let passes = [], customers = [], dishes = [];
  root.innerHTML = `<div class="page-head"><div><div class="eyebrow">MISSION SCHEDULING / CONTACT WINDOWS</div><h1>Satellite passes</h1><p>Schedule and track station contact windows for commercial operators.</p></div><button class="btn btn-primary" id="add-pass">＋ Schedule pass</button></div><section class="panel panel-pad"><div class="toolbar" style="margin-bottom:15px"><input class="input search" id="pass-search" type="search" placeholder="Search satellite, operator or dish…"><select class="select status-select" id="pass-status"><option value="">All statuses</option><option>SCHEDULED</option><option>ACTIVE</option><option>COMPLETED</option><option>CANCELLED</option></select><span class="muted" id="pass-count"></span></div><div id="pass-table"></div></section>`;
  function render() {
    const term = document.querySelector('#pass-search').value.toLowerCase(), status = document.querySelector('#pass-status').value;
    const rows = passes.filter(p => `${p.satelliteName} ${p.customerName} ${p.dishCode}`.toLowerCase().includes(term) && (!status || p.status === status)).sort((a,b) => new Date(a.passStart) - new Date(b.passStart));
    document.querySelector('#pass-count').textContent = `${rows.length} pass windows`;
    document.querySelector('#pass-table').innerHTML = rows.length ? `<div class="table-wrap"><table class="data-table"><thead><tr><th>Satellite / Customer</th><th>Dish</th><th>Start</th><th>End</th><th>Duration</th><th>Band</th><th>Status</th><th></th></tr></thead><tbody>${rows.map(p => { const duration = Math.max(0, (new Date(p.passEnd)-new Date(p.passStart))/3600000); return `<tr><td><strong>${ui.esc(p.satelliteName)}</strong><div class="muted">${ui.esc(p.customerName)}</div></td><td class="mono">${ui.esc(p.dishCode)}</td><td>${ui.dateTime(p.passStart)}</td><td>${ui.dateTime(p.passEnd)}</td><td>${ui.number(duration,2)} h</td><td>${ui.esc(p.frequency || '—')}</td><td>${ui.badge(p.status)}</td><td><div class="toolbar">${p.status === 'COMPLETED' ? `<a class="btn btn-sm" href="/billing.html?passId=${encodeURIComponent(p.id)}">Bill preview</a>` : ''}<button class="btn btn-sm" data-edit="${p.id}">Edit</button><button class="btn btn-sm btn-danger" data-delete="${p.id}">Delete</button></div></td></tr>`; }).join('')}</tbody></table></div>` : ui.empty('No satellite passes scheduled yet.', 'Create a contact window and assign an operator and dish.', '<button class="btn btn-primary btn-sm" id="empty-add">＋ Schedule satellite pass</button>');
    document.querySelectorAll('[data-edit]').forEach(b => b.onclick = () => openForm(passes.find(p => p.id == b.dataset.edit)));
    document.querySelectorAll('[data-delete]').forEach(b => b.onclick = () => remove(b.dataset.delete));
    document.querySelector('#empty-add')?.addEventListener('click', () => openForm());
  }
  function openForm(pass = {}) {
    const editing = Boolean(pass.id);
    const customerOptions = customers.map(c => `<option value="${c.id}" ${pass.customerId === c.id ? 'selected' : ''}>${ui.esc(c.operatorName)}</option>`).join('');
    const dishOptions = dishes.map(d => `<option value="${d.id}" ${pass.dishId === d.id ? 'selected' : ''}>${ui.esc(d.dishCode)} · ${ui.esc(d.dishName)}</option>`).join('');
    const body = `<form id="record-form" class="form-grid"><div class="field"><label>Satellite name</label><input class="input" name="satelliteName" required value="${ui.esc(pass.satelliteName)}" placeholder="ORBIT-7"></div><div class="field"><label>Status</label><select class="select" name="status">${['SCHEDULED','ACTIVE','COMPLETED','CANCELLED'].map(s => `<option ${pass.status === s ? 'selected' : ''}>${s}</option>`).join('')}</select></div><div class="field"><label>Customer</label><select class="select" name="customerId" required><option value="">Select operator</option>${customerOptions}</select></div><div class="field"><label>Dish</label><select class="select" name="dishId" required><option value="">Select dish</option>${dishOptions}</select></div><div class="field"><label>Pass start</label><input class="input" name="passStart" type="datetime-local" required value="${String(pass.passStart || '').slice(0,16)}"></div><div class="field"><label>Pass end</label><input class="input" name="passEnd" type="datetime-local" required value="${String(pass.passEnd || '').slice(0,16)}"></div><div class="field"><label>Frequency</label><input class="input" name="frequency" value="${ui.esc(pass.frequency)}" placeholder="X-BAND"></div><div class="field"><label>Expected duration (minutes)</label><input class="input" name="expectedDurationMinutes" type="number" min="0" step="1" value="${pass.expectedDurationMinutes ?? ''}"></div></form>`;
    ui.modal(editing ? `Update ${pass.satelliteName}` : 'Schedule satellite pass', body, async form => {
      const values = Object.fromEntries(form.entries());
      const start = new Date(values.passStart), end = new Date(values.passEnd);
      if (!(end > start)) throw new Error('Pass end must be after pass start.');
      const payload = { satelliteName: values.satelliteName.trim(), customerId: Number(values.customerId), dishId: Number(values.dishId), passStart: values.passStart, passEnd: values.passEnd, frequency: values.frequency || null, expectedDurationMinutes: values.expectedDurationMinutes ? Number(values.expectedDurationMinutes) : null, status: values.status };
      await (editing ? GSApi.passes.update(pass.id, payload) : GSApi.passes.create(payload));
      ui.toast(editing ? 'Pass updated.' : 'Pass scheduled.', 'success'); await load();
    }, editing ? 'Save pass' : 'Schedule pass');
  }
  async function remove(id) { if (!confirm('Delete this satellite pass? Existing telemetry may prevent deletion.')) return; try { await GSApi.passes.delete(id); ui.toast('Pass deleted.', 'success'); await load(); } catch (error) { ui.showError(error); } }
  async function load() {
    try { [passes,customers,dishes] = await Promise.all([GSApi.passes.list(),GSApi.customers.list(),GSApi.dishes.list()]); render(); }
    catch (error) { ui.showError(error); document.querySelector('#pass-table').innerHTML = ui.empty('Pass schedule unavailable', 'Could not load satellite pass records.'); }
  }
  document.querySelector('#add-pass').addEventListener('click', () => openForm());
  document.querySelector('#pass-search').addEventListener('input', render);
  document.querySelector('#pass-status').addEventListener('change', render);
  load();
});
