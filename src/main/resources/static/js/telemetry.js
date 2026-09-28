document.addEventListener('DOMContentLoaded', async () => {
  const root = document.querySelector('#page-content'), ui = GSUI; let telemetry = [], passes = [], dishes = [];
  root.innerHTML = `<div class="page-head"><div><div class="eyebrow">DOWNLINK ACQUISITION / SENSOR FEED</div><h1>Telemetry stream</h1><p>Received data, antenna position and equipment telemetry.</p></div><button class="btn btn-primary" id="add-record">＋ Record telemetry</button></div><section class="grid grid-3" id="telemetry-metrics"><article class="panel metric"><div class="metric-label">Total data received</div><div class="metric-value">—</div><div class="metric-note">Across all records</div></article><article class="panel metric"><div class="metric-label">Tracking time</div><div class="metric-value">—</div><div class="metric-note">Aggregated hours</div></article><article class="panel metric"><div class="metric-label">Telemetry packets</div><div class="metric-value">—</div><div class="metric-note">Stored records</div></article></section><div class="section-heading"><h2>Telemetry records</h2><div class="toolbar"><select class="select" id="pass-filter"><option value="">All satellite passes</option></select></div></div><section class="panel panel-pad"><div id="telemetry-table"></div></section><div class="grid grid-2" style="margin-top:16px"><section><div class="section-heading"><h2>Data received over time</h2><span>GB / RECORD</span></div><div class="panel panel-pad"><div id="data-chart" class="chart-box"></div></div></section><section><div class="section-heading"><h2>Dish elevation</h2><span>DEGREES / RECORD</span></div><div class="panel panel-pad"><div id="elevation-chart" class="chart-box"></div></div></section></div>`;
  function svgChart(host, records, field, unit, color = '#72e2df') {
    const data = [...records].filter(r => r[field] != null).sort((a,b) => new Date(a.recordedAt)-new Date(b.recordedAt)).slice(-12);
    if (!data.length) { host.innerHTML = ui.empty('No chart data', `Records with ${field} values will appear here.`); return; }
    const values = data.map(r => Number(r[field])), max = Math.max(...values,1), min = Math.min(...values,0), span = Math.max(max-min,1);
    const points = values.map((v,i) => `${16+i*268/Math.max(1,values.length-1)},${173-(v-min)/span*140}`).join(' ');
    host.innerHTML = `<svg class="chart-svg" viewBox="0 0 300 205" role="img" aria-label="${ui.esc(field)} by telemetry record"><line class="chart-grid" x1="10" y1="30" x2="290" y2="30"/><line class="chart-grid" x1="10" y1="80" x2="290" y2="80"/><line class="chart-grid" x1="10" y1="130" x2="290" y2="130"/><line class="chart-grid" x1="10" y1="180" x2="290" y2="180"/><polyline points="${points}" fill="none" stroke="${color}" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>${values.map((v,i)=>`<circle cx="${16+i*268/Math.max(1,values.length-1)}" cy="${173-(v-min)/span*140}" r="3" fill="${color}"><title>${ui.esc(v)} ${unit}</title></circle>`).join('')}<text class="chart-label" x="12" y="198">${ui.esc(data[0]?.recordedAt ? ui.date(data[0].recordedAt.slice(0,10)) : '')}</text><text class="chart-label" x="246" y="198">LATEST</text></svg>`;
  }
  function render() {
    const passId = document.querySelector('#pass-filter').value;
    const records = telemetry.filter(t => !passId || String(t.satellitePassId) === passId).sort((a,b) => new Date(b.recordedAt)-new Date(a.recordedAt));
    const gb = records.reduce((sum,t)=>sum+Number(t.dataReceivedGB || 0),0), hours = records.reduce((sum,t)=>sum+Number(t.trackingDurationHours || 0),0);
    document.querySelector('#telemetry-metrics').innerHTML = `<article class="panel metric"><div class="metric-label">Total data received</div><div class="metric-value metric-accent">${ui.number(gb)} GB</div><div class="metric-note">Across current selection</div></article><article class="panel metric"><div class="metric-label">Tracking time</div><div class="metric-value metric-lime">${ui.number(hours,2)} h</div><div class="metric-note">Aggregated duration</div></article><article class="panel metric"><div class="metric-label">Telemetry packets</div><div class="metric-value">${records.length}</div><div class="metric-note">Stored records</div></article>`;
    document.querySelector('#telemetry-table').innerHTML = records.length ? `<div class="table-wrap"><table class="data-table"><thead><tr><th>Satellite / pass</th><th>Dish</th><th>Recorded</th><th>Data</th><th>Tracking</th><th>Elevation</th><th>Azimuth</th><th>Signal</th><th>Motor temp</th><th>Actions</th></tr></thead><tbody>${records.map(t=>`<tr><td><strong>${ui.esc(t.satelliteName || `Pass ${t.satellitePassId}`)}</strong></td><td class="mono">${ui.esc(t.dishCode || `#${t.dishId}`)}</td><td>${ui.dateTime(t.recordedAt)}</td><td>${ui.number(t.dataReceivedGB)} GB</td><td>${ui.number(t.trackingDurationHours,2)} h</td><td>${ui.number(t.elevation)}°</td><td>${ui.number(t.azimuth)}°</td><td>${ui.number(t.signalStrength)}%</td><td>${ui.number(t.motorTemperature)}°C</td><td><div class="toolbar"><button class="btn btn-sm" data-edit-telemetry="${t.id}">Edit</button><button class="btn btn-sm btn-danger" data-delete-telemetry="${t.id}">Delete</button></div></td></tr>`).join('')}</tbody></table></div>` : ui.empty('No telemetry records found', 'Select another pass or record the first telemetry packet.', '<button class="btn btn-primary btn-sm" id="empty-add">＋ Record telemetry</button>');
    document.querySelector('#empty-add')?.addEventListener('click', openForm);
    document.querySelectorAll('[data-edit-telemetry]').forEach(button => {
      button.addEventListener('click', () => openForm(telemetry.find(record => String(record.id) === button.dataset.editTelemetry)));
    });
    document.querySelectorAll('[data-delete-telemetry]').forEach(button => {
      button.addEventListener('click', () => deleteRecord(button.dataset.deleteTelemetry));
    });
    svgChart(document.querySelector('#data-chart'), records, 'dataReceivedGB', 'GB');
    svgChart(document.querySelector('#elevation-chart'), records, 'elevation', '°', '#c6ef88');
  }
  function openForm(record = null) {
    const editing = record !== null;
    const passOptions = passes.map(p=>`<option value="${p.id}">${ui.esc(p.satelliteName)} · ${ui.esc(p.customerName)} · ${ui.esc(p.dishCode)}</option>`).join('');
    const dishOptions = dishes.map(d=>`<option value="${d.id}">${ui.esc(d.dishCode)} · ${ui.esc(d.dishName)}</option>`).join('');
    const passSelect = editing
      ? `<div class="field"><label>Satellite pass (fixed)</label><input class="input" value="${ui.esc(record.satelliteName || `Pass ${record.satellitePassId}`)}" readonly></div>`
      : `<div class="field"><label>Satellite pass</label><select class="select" name="satellitePassId" required><option value="">Select pass</option>${passOptions}</select></div>`;
    const dishSelect = editing
      ? `<div class="field"><label>Dish (fixed)</label><input class="input" value="${ui.esc(record.dishCode || `Dish ${record.dishId}`)}" readonly></div>`
      : `<div class="field"><label>Dish</label><select class="select" name="dishId" required><option value="">Select dish</option>${dishOptions}</select></div>`;
    const inputValue = (value) => value == null ? '' : ui.esc(value);
    const recordedAt = editing ? String(record.recordedAt || '').slice(0, 16) : new Date().toISOString().slice(0, 16);
    const body = `<form id="record-form" class="form-grid">${passSelect}${dishSelect}<div class="field"><label>Data received (GB)</label><input class="input" type="number" name="dataReceivedGB" min="0" step="any" required value="${editing ? inputValue(record.dataReceivedGB) : ''}"></div><div class="field"><label>Tracking duration (hours)</label><input class="input" type="number" name="trackingDurationHours" min="0" step="any" required value="${editing ? inputValue(record.trackingDurationHours) : ''}"></div><div class="field"><label>Elevation (°)</label><input class="input" type="number" name="elevation" min="0" max="90" step="any" value="${editing ? inputValue(record.elevation) : ''}"></div><div class="field"><label>Azimuth (°)</label><input class="input" type="number" name="azimuth" min="0" max="360" step="any" value="${editing ? inputValue(record.azimuth) : ''}"></div><div class="field"><label>Signal strength</label><input class="input" type="number" name="signalStrength" step="any" value="${editing ? inputValue(record.signalStrength) : ''}"></div><div class="field"><label>Motor temperature (°C)</label><input class="input" type="number" name="motorTemperature" step="any" value="${editing ? inputValue(record.motorTemperature) : ''}"></div><div class="field span-2"><label>Recorded at</label><input class="input" type="datetime-local" name="recordedAt" required value="${ui.esc(recordedAt)}"></div></form>`;
    ui.modal(editing ? `Edit telemetry · ${record.satelliteName || `Pass ${record.satellitePassId}`}` : 'Record telemetry', body, async form=>{
      const f=Object.fromEntries(form.entries()); const n=(v)=>v===''?null:Number(v);
      const payload={satellitePassId:editing?record.satellitePassId:Number(f.satellitePassId),dishId:editing?record.dishId:Number(f.dishId),dataReceivedGB:Number(f.dataReceivedGB),trackingDurationHours:Number(f.trackingDurationHours),elevation:n(f.elevation),azimuth:n(f.azimuth),signalStrength:n(f.signalStrength),motorTemperature:n(f.motorTemperature),recordedAt:f.recordedAt};
      if (editing) {
        await GSApi.telemetry.update(record.id, payload);
        ui.toast('Telemetry record updated.', 'success');
      } else {
        await GSApi.telemetry.create(payload);
        ui.toast('Telemetry record stored.', 'success');
      }
      await load();
    }, editing ? 'Save telemetry' : 'Record telemetry');
  }
  async function deleteRecord(id) {
    const record = telemetry.find(item => String(item.id) === String(id));
    if (!confirm(`Delete telemetry for ${record?.satelliteName || 'this pass'} (${record?.dishCode || `dish ${record?.dishId}`})? This cannot be undone.`)) return;
    try {
      await GSApi.telemetry.delete(id);
      ui.toast('Telemetry record deleted.', 'success');
      await load();
    } catch (error) {
      ui.showError(error);
    }
  }
  async function load() {
    try { const filter = document.querySelector('#pass-filter'); const selectedPass = filter.value; [telemetry,passes,dishes]=await Promise.all([GSApi.telemetry.list(),GSApi.passes.list(),GSApi.dishes.list()]); filter.innerHTML='<option value="">All satellite passes</option>'+passes.map(p=>`<option value="${p.id}">${ui.esc(p.satelliteName)} · ${ui.esc(p.dishCode)}</option>`).join(''); if (passes.some(pass => String(pass.id) === selectedPass)) filter.value = selectedPass; render(); }
    catch(error){ui.showError(error);document.querySelector('#telemetry-table').innerHTML=ui.empty('Telemetry unavailable','The telemetry API could not be reached.');}
  }
  document.querySelector('#add-record').addEventListener('click',()=>openForm());
  document.querySelector('#pass-filter').addEventListener('change',render);
  load();
});
