export default function Sidebar({ open, conversations, activeId, engine, onNew, onOpen, onDelete }) {
  return (
    <aside className={`sidebar ${open ? 'open' : ''}`}>
      <div className="brand">
        <h1>Ask<span>Bot</span></h1>
        <small>DIT</small>
      </div>

      <button className="new-chat" onClick={onNew}>+ New question</button>

      <div>
        <div className="side-label" style={{ marginBottom: 8 }}>History</div>
        <div className="history">
          {conversations.length === 0 && (
            <div style={{ fontSize: 12, color: 'var(--chalk-dim)', padding: '6px 10px' }}>
              Nothing yet.
            </div>
          )}
          {conversations.map((c) => (
            <div
              key={c.id}
              className={`history-item ${c.id === activeId ? 'active' : ''}`}
              onClick={() => onOpen(c.id)}
            >
              <span className="title">{c.title}</span>
              <button
                className="del"
                aria-label="Delete conversation"
                onClick={(e) => { e.stopPropagation(); onDelete(c.id); }}
              >×</button>
            </div>
          ))}
        </div>
      </div>

      <div className="engine-badge">
        <span className={`dot ${engine ? '' : 'down'}`} />
        {engine || 'Backend offline'}
      </div>
    </aside>
  );
}
