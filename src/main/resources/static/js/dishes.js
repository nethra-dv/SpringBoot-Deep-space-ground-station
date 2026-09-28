document.addEventListener('DOMContentLoaded', async () => {
  const root = document.querySelector('#page-content'), ui = GSUI;
  let dishes = [], vendors = [];
  root.innerHTML = `<div class="page-head"><div><div class="eyebrow">GROUND ASSETS / ANTENNA CONTROL</div><h1>Antenna arrays</h1><p>Dish register, maintenance state and servo manufacturer assignment.</p></div><button class="btn btn-primary" id="add-dish">＋ Add new dish</button></div><section class="panel panel-pad"><div class="toolbar" style="margin-bottom:15px"><input class="input search" id="dish-search" type="search" placeholder="Search code, name, location…" aria-label="Search dishes"><select class="select status-select" id="dish-status" aria-label="Filter by status"><option value="">All statuses</option><option>AVAILABLE</option><option>TRACKING</option><option>MAINTENANCE</option><option>OFFLINE</option></select><span class="muted" id="dish-count"></span></div><div id="dish-table"></div></section>`;
  const render = () => {
    const term = document.querySelector('#dish-search').value.toLowerCase(), status = document.querySelector('#dish-status').value;
    const rows = dishes.filter(d => `${d.dishCode} ${d.dishName} ${d.location || ''} ${d.frequencyBand || ''}`.toLowerCase().includes(term) && (!status || d.status === status));
    document.querySelector('#dish-count').textContent = `${rows.length} arrays`;
    document.querySelector('#dish-table').innerHTML = rows.length ? `<div class="table-wrap"><table class="data-table"><thead><tr><th>Dish</th><th>Location</th><th>Band / Diameter</th><th>Elevation range</th><th>Servo maker</th><th>Status</th><th>Active</th><th></th></tr></thead><tbody>${rows.map(d => `<tr><td><strong>${ui.esc(d.dishCode)}</strong><div class="muted">${ui.esc(d.dishName)}</div></td><td>${ui.esc(d.location || '—')}</td><td>${ui.esc(d.frequencyBand || '—')}<div class="muted">${ui.number(d.diameterMeters)} m</div></td><td>${ui.number(d.minimumElevation)}° – ${ui.number(d.maximumElevation)}°</td><td>${ui.esc(d.servoManufacturerName || '—')}</td><td>${ui.badge(d.status)}</td><td>${d.active ? 'Yes' : 'No'}</td><td><div class="toolbar"><button class="btn btn-sm" data-edit="${d.id}">Edit</button><button class="btn btn-sm btn-danger" data-delete="${d.id}">Delete</button></div></td></tr>`).join('')}</tbody></table></div>` : ui.empty('No dishes match this view', 'Adjust the search or add a ground station array.', '<button class="btn btn-primary btn-sm" id="empty-add">＋ Add dish</button>');
    document.querySelectorAll('[data-edit]').forEach(button => button.onclick = () => openForm(dishes.find(d => d.id == button.dataset.edit)));
    document.querySelectorAll('[data-delete]').forEach(button => button.onclick = () => removeDish(button.dataset.delete));
    document.querySelector('#empty-add')?.addEventListener('click', () => openForm());
  };
  function openForm(dish = {}) {
    const editing = Boolean(dish.id);
    const vendorOptions = vendors.map(v => `<option value="${v.id}" ${dish.servoManufacturerId === v.id ? 'selected' : ''}>${ui.esc(v.vendorName)}</option>`).join('');
    const fields = `<form id="record-form" class="form-grid"><div class="field"><label>Dish code</label><input class="input" name="dishCode" required value="${ui.esc(dish.dishCode)}" placeholder="DS-03"></div><div class="field"><label>Dish name</label><input class="input" name="dishName" required value="${ui.esc(dish.dishName)}"></div><div class="field"><label>Diameter (m)</label><input class="input" name="diameterMeters" type="number" min="0.01" step="0.01" required value="${dish.diameterMeters ?? ''}"></div><div class="field"><label>Frequency band</label><input class="input" name="frequencyBand" value="${ui.esc(dish.frequencyBand)}" placeholder="X-BAND"></div><div class="field span-2"><label>Location</label><input class="input" name="location" value="${ui.esc(dish.location)}"></div><div class="field"><label>Minimum elevation (°)</label><input class="input" name="minimumElevation" type="number" min="0" max="90" step="0.1" value="${dish.minimumElevation ?? 0}"></div><div class="field"><label>Maximum elevation (°)</label><input class="input" name="maximumElevation" type="number" min="0" max="90" step="0.1" value="${dish.maximumElevation ?? 90}"></div><div class="field"><label>Servo manufacturer</label><select class="select" name="servoManufacturerId"><option value="">Unassigned</option>${vendorOptions}</select></div><div class="field"><label>Status</label><select class="select" name="status">${['AVAILABLE','TRACKING','MAINTENANCE','OFFLINE'].map(s => `<option ${dish.status === s ? 'selected' : ''}>${s}</option>`).join('')}</select></div><label class="check-row span-2"><input type="checkbox" name="active" ${dish.active !== false ? 'checked' : ''}> Active array</label></form>`;
    ui.modal(editing ? `Edit ${dish.dishCode}` : 'Register antenna array', fields, async form => {
      const payload = Object.fromEntries(form.entries());
      payload.diameterMeters = Number(payload.diameterMeters);
      payload.minimumElevation = payload.minimumElevation === '' ? null : Number(payload.minimumElevation);
      payload.maximumElevation = payload.maximumElevation === '' ? null : Number(payload.maximumElevation);
      payload.servoManufacturerId = payload.servoManufacturerId ? Number(payload.servoManufacturerId) : null;
      payload.active = form.has('active');
      if (payload.minimumElevation > payload.maximumElevation) throw new Error('Minimum elevation must not exceed maximum elevation.');
      await (editing ? GSApi.dishes.update(dish.id, payload) : GSApi.dishes.create(payload));
      ui.toast(editing ? 'Dish updated.' : 'Dish registered.', 'success'); await load();
    }, editing ? 'Save dish' : 'Register dish');
  }
  async function removeDish(id) {
    if (!confirm('Delete this dish? Existing satellite passes or telemetry may prevent deletion.')) return;
    try { await GSApi.dishes.delete(id); ui.toast('Dish deleted.', 'success'); await load(); } catch (error) { ui.showError(error); }
  }
  async function load() {
    try { [dishes, vendors] = await Promise.all([GSApi.dishes.list(), GSApi.vendors.list()]); render(); }
    catch (error) { ui.showError(error); document.querySelector('#dish-table').innerHTML = ui.empty('Dish register unavailable', 'The backend could not return dish records.'); }
  }
  document.querySelector('#add-dish').addEventListener('click', () => openForm());
  document.querySelector('#dish-search').addEventListener('input', render);
  document.querySelector('#dish-status').addEventListener('change', render);
  load();
});
