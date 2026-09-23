import { useEffect, useRef, useState } from 'react';
import { api } from './api/client';
import Sidebar from './components/Sidebar';
import Composer from './components/Composer';
import Markdown from './components/Markdown';

const LANGS = [
  { code: 'AUTO', label: 'Auto' },
  { code: 'ENGLISH', label: 'EN' },
  { code: 'SWAHILI', label: 'SW' }
];

const SAMPLES = [
  { s: 'PHYSICS', q: 'A car accelerates from 0 to 25 m/s in 8 seconds. Find its acceleration and the distance covered.' },
  { s: 'COMPUTER_SCIENCE', q: 'Explain the four pillars of OOP with a Java example for each.' },
  { s: 'HISTORY', q: 'Eleza kwa kifupi historia ya Azimio la Arusha na athari zake Tanzania.' },
  { s: 'MATHEMATICS', q: 'Solve the quadratic 2x² − 7x + 3 = 0 and show every step.' }
];

export default function App() {
  const [subjects, setSubjects] = useState([]);
  const [subject, setSubject] = useState('GENERAL');
  const [language, setLanguage] = useState('AUTO');
  const [conversations, setConversations] = useState([]);
  const [activeId, setActiveId] = useState(null);
  const [messages, setMessages] = useState([]);
  const [draft, setDraft] = useState('');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const [engine, setEngine] = useState('');
  const [menuOpen, setMenuOpen] = useState(false);
  const bottomRef = useRef(null);

  useEffect(() => {
    api.subjects().then(setSubjects).catch(() => {});
    api.conversations().then(setConversations).catch(() => {});
    api.health().then((h) => setEngine(h.engine)).catch(() => setEngine(''));
  }, []);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, busy]);

  const startNew = () => {
    setActiveId(null); setMessages([]); setError(''); setMenuOpen(false);
  };

  const openConversation = async (id) => {
    setError(''); setMenuOpen(false);
    try {
      const c = await api.conversation(id);
      setActiveId(c.id);
      setMessages(c.messages);
    } catch (e) { setError(e.message); }
  };

  const removeConversation = async (id) => {
    try {
      await api.remove(id);
      setConversations((prev) => prev.filter((c) => c.id !== id));
      if (id === activeId) startNew();
    } catch (e) { setError(e.message); }
  };

  const send = async (text) => {
    const question = (text ?? draft).trim();
    if (!question || busy) return;

    setError('');
    setDraft('');
    setBusy(true);
    setMessages((prev) => [...prev, { id: `tmp-${Date.now()}`, role: 'USER', content: question }]);

    try {
      const res = await api.ask({ question, subject, language, conversationId: activeId });
      setMessages((prev) => [...prev, { id: `bot-${Date.now()}`, role: 'BOT', content: res.answer }]);
      if (!activeId) {
        setActiveId(res.conversationId);
        const list = await api.conversations();
        setConversations(list);
      }
    } catch (e) {
      setError(e.message);
    } finally {
      setBusy(false);
    }
  };

  const useSample = (sample) => { setSubject(sample.s); send(sample.q); };

  return (
    <div className="shell">
      <Sidebar
        open={menuOpen}
        conversations={conversations}
        activeId={activeId}
        engine={engine}
        onNew={startNew}
        onOpen={openConversation}
        onDelete={removeConversation}
      />

      <main className="main">
        <div className="controls">
          <button className="menu-btn" onClick={() => setMenuOpen((o) => !o)} aria-label="Toggle menu">☰</button>

          <div className="chips">
            {subjects.map((s) => (
              <button
                key={s.code}
                className={`chip ${subject === s.code ? 'on' : ''}`}
                onClick={() => setSubject(s.code)}
                title={s.swahiliLabel}
              >
                {s.label}
              </button>
            ))}
          </div>

          <div className="lang-toggle">
            {LANGS.map((l) => (
              <button
                key={l.code}
                className={language === l.code ? 'on' : ''}
                onClick={() => setLanguage(l.code)}
              >{l.label}</button>
            ))}
          </div>
        </div>

        <div className="thread">
          <div className="thread-inner">
            {messages.length === 0 && !busy && (
              <div className="empty">
                <h2>Ask anything. <em>Any subject.</em></h2>
                <p>Pick a subject above so the answer is tuned to it, then ask in English or Kiswahili.</p>
                <div className="suggestions">
                  {SAMPLES.map((s, i) => (
                    <button key={i} className="suggestion" onClick={() => useSample(s)}>
                      <b>{s.s.replace('_', ' ')}</b>
                      {s.q}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {messages.map((m) => (
              <div key={m.id} className={`msg ${m.role === 'USER' ? 'user' : 'bot'}`}>
                <div className="msg-role">{m.role === 'USER' ? 'You' : 'AskBot'}</div>
                <div className="bubble">
                  {m.role === 'USER' ? m.content : <Markdown>{m.content}</Markdown>}
                </div>
              </div>
            ))}

            {busy && (
              <div className="msg bot">
                <div className="msg-role">AskBot</div>
                <div className="thinking"><i /><i /><i /></div>
              </div>
            )}

            {error && <div className="error">{error}</div>}
            <div ref={bottomRef} />
          </div>
        </div>

        <Composer value={draft} onChange={setDraft} onSend={() => send()} busy={busy} />
      </main>
    </div>
  );
}
