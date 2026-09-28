document.addEventListener('DOMContentLoaded', async () => {
  const root = document.querySelector('#page-content'), ui = GSUI; let vendors = [], dishes = [];
  root.innerHTML = `<div class="page-head"><div><div class="eyebrow">SUPPLY NETWORK / SERVO SYSTEMS</div><h1>Servo manufacturers</h1><p>Vendor directory and antenna equipment relationships.</p></div><button class="btn btn-primary" id="add-vendor">＋ Add manufacturer</button></div><section class="panel panel-pad"><div class="toolbar" style="margin-bottom:15px"><input class="input search" id="vendor-search" type="search" placeholder="Search manufacturers…" aria-label="Search vendors"><span class="muted" id="vendor-count"></span></div><div id="vendor-list"></div></section>`;
  function render() {
    const term = document.querySelector('#vendor-search').value.toLowerCase();
    const visible = vendors.filter(v => `${v.vendorName} ${v.contactPerson || ''} ${v.email || ''} ${v.phone || ''}`.toLowerCase().includes(term));
    document.querySelector('#vendor-count').textContent = `${visible.length} manufacturers`;
    document.querySelector('#vendor-list').innerHTML = visible.length ? `<div class="table-wrap"><table class="data-table"><thead><tr><th>Manufacturer</th><th>Contact</th><th>Email / Phone</th><th>Servo arrays</th><th>State</th><th>Actions</th></tr></thead><tbody>${visible.map(v => { const attached = dishes.filter(d => d.servoManufacturerId === v.id); return `<tr><td><strong>${ui.esc(v.vendorName)}</strong><div class="muted">${ui.esc(v.address || 'Address not supplied')}</div></td><td>${ui.esc(v.contactPerson || '—')}</td><td>${ui.esc(v.email || '—')}<div class="muted">${ui.esc(v.phone || '')}</div></td><td>${attached.length ? attached.map(d => `<span class="mono">${ui.esc(d.dishCode)}</span>`).join(', ') : '<span class="muted">No linked dishes</span>'}</td><td>${ui.badge(v.active ? 'ACTIVE' : 'OFFLINE')}</td><td><div class="toolbar"><button class="btn btn-sm" data-edit="${v.id}">Edit</button><button class="btn btn-sm btn-danger" data-deactivate="${v.id}">${v.active ? 'Deactivate' : 'Activate'}</button></div></td></tr>`; }).join('')}</tbody></table></div>` : ui.empty('No manufacturers found', 'Register a servo manufacturer to link it to an antenna array.', '<button class="btn btn-primary btn-sm" id="empty-add">＋ Add manufacturer</button>');
    document.querySelectorAll('[data-edit]').forEach(b => b.onclick = () => openForm(vendors.find(v => v.id == b.dataset.edit)));
    document.querySelectorAll('[data-deactivate]').forEach(b => b.onclick = () => toggleVendor(vendors.find(v => v.id == b.dataset.deactivate)));
    document.querySelector('#empty-add')?.addEventListener('click', () => openForm());
  }
  function openForm(vendor = {}) {
    const editing = Boolean(vendor.id);
    const body = `<form id="record-form" class="form-grid"><div class="field span-2"><label>Manufacturer name</label><input class="input" name="vendorName" required value="${ui.esc(vendor.vendorName)}"></div><div class="field"><label>Contact person</label><input class="input" name="contactPerson" value="${ui.esc(vendor.contactPerson)}"></div><div class="field"><label>Email</label><input class="input" type="email" name="email" value="${ui.esc(vendor.email)}"></div><div class="field"><label>Phone</label><input class="input" name="phone" value="${ui.esc(vendor.phone)}"></div><div class="field"><label>Address</label><input class="input" name="address" value="${ui.esc(vendor.address)}"></div><label class="check-row span-2"><input type="checkbox" name="active" ${vendor.active !== false ? 'checked' : ''}> Active manufacturer</label></form>`;
    ui.modal(editing ? 'Edit manufacturer' : 'Add servo manufacturer', body, async form => {
      const data = Object.fromEntries(form.entries()); data.active = form.has('active');
      await (editing ? GSApi.vendors.update(vendor.id, data) : GSApi.vendors.create(data));
      ui.toast(editing ? 'Manufacturer updated.' : 'Manufacturer added.', 'success'); await load();
    }, editing ? 'Save vendor' : 'Add vendor');
  }
  async function toggleVendor(vendor) {
    try { await GSApi.vendors.update(vendor.id, { vendorName: vendor.vendorName, contactPerson: vendor.contactPerson, email: vendor.email, phone: vendor.phone, address: vendor.address, active: !vendor.active }); ui.toast(vendor.active ? 'Manufacturer deactivated.' : 'Manufacturer activated.', 'success'); await load(); } catch (error) { ui.showError(error); }
  }
  async function load() {
    try { [vendors,dishes] = await Promise.all([GSApi.vendors.list(),GSApi.dishes.list()]); render(); }
    catch (error) { ui.showError(error); document.querySelector('#vendor-list').innerHTML = ui.empty('Vendor directory unavailable', 'Could not load manufacturer records.'); }
  }
  document.querySelector('#add-vendor').addEventListener('click', () => openForm());
  document.querySelector('#vendor-search').addEventListener('input', render);
  load();
});
