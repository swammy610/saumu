import { useRef, useEffect } from 'react';

export default function Composer({ value, onChange, onSend, busy }) {
  const ref = useRef(null);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    el.style.height = 'auto';
    el.style.height = Math.min(el.scrollHeight, 180) + 'px';
  }, [value]);

  const handleKey = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      onSend();
    }
  };

  return (
    <div className="composer">
      <div className="composer-inner">
        <div className="composer-box">
          <textarea
            ref={ref}
            rows={1}
            value={value}
            disabled={busy}
            placeholder="Ask anything — Uliza chochote…"
            onChange={(e) => onChange(e.target.value)}
            onKeyDown={handleKey}
          />
          <button className="send" onClick={onSend} disabled={busy || !value.trim()} aria-label="Send question">
            ↑
          </button>
        </div>
        <div className="hint">Enter to send · Shift+Enter for a new line</div>
      </div>
    </div>
  );
}
