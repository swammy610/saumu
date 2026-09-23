const BASE = '/api';

async function handle(res) {
  if (!res.ok) {
    let message = `Request failed (${res.status})`;
    try {
      const body = await res.json();
      if (body?.message) message = body.message;
    } catch { /* keep default */ }
    throw new Error(message);
  }
  return res.status === 204 ? null : res.json();
}

export const api = {
  ask: (payload) =>
    fetch(`${BASE}/ask`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    }).then(handle),

  subjects: () => fetch(`${BASE}/subjects`).then(handle),

  conversations: () => fetch(`${BASE}/conversations`).then(handle),

  conversation: (id) => fetch(`${BASE}/conversations/${id}`).then(handle),

  remove: (id) => fetch(`${BASE}/conversations/${id}`, { method: 'DELETE' }).then(handle),

  health: () => fetch(`${BASE}/health`).then(handle)
};
