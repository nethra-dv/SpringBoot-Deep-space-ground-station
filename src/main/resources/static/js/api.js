(() => {
  const paths = {
    customers: '/api/customers', vendors: '/api/vendors', dishes: '/api/space/dishes',
    products: '/api/products', passes: '/api/space/passes', telemetry: '/api/space/telemetry'
  };

  async function request(path, options = {}) {
    let response;
    try {
      response = await fetch(path, {
        ...options,
        headers: { ...(options.body ? { 'Content-Type': 'application/json' } : {}), ...options.headers }
      });
    } catch {
      const error = new Error('Backend unavailable. Check that the ground station service is running.');
      error.status = 0;
      throw error;
    }
    if (!response.ok) {
      let detail = `Request failed (${response.status})`;
      try {
        const body = await response.json();
        detail = body.message || detail;
      } catch {}
      const error = new Error(detail);
      error.status = response.status;
      throw error;
    }
    if (response.status === 204) return null;
    return response.json();
  }

  const resource = (path) => ({
    list: () => request(path),
    get: (id) => request(`${path}/${encodeURIComponent(id)}`),
    create: (data) => request(path, { method: 'POST', body: JSON.stringify(data) }),
    update: (id, data) => request(`${path}/${encodeURIComponent(id)}`, { method: 'PUT', body: JSON.stringify(data) }),
    delete: (id) => request(`${path}/${encodeURIComponent(id)}`, { method: 'DELETE' })
  });

  window.GSApi = {
    customers: resource(paths.customers), vendors: resource(paths.vendors), dishes: resource(paths.dishes),
    products: resource(paths.products), passes: resource(paths.passes), telemetry: resource(paths.telemetry),
    calculate: (passId) => request(`/api/billing/pass/${encodeURIComponent(passId)}/calculate`),
    generateInvoice: (passId) => request(`/api/billing/pass/${encodeURIComponent(passId)}/invoice`, { method: 'POST' }),
    async health() {
      try { await request(paths.dishes); return true; } catch { return false; }
    }
  };
})();
