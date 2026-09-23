# AskBot — Ask Anything, Any Subject

A full-stack Q&A robot that answers questions from **any subject**, in **English or Kiswahili**.

**Stack:** React (Vite) · Spring Boot 3.3 · PostgreSQL · Claude API

---

## Architecture

```
React (5173)  ──HTTP/JSON──▶  Spring Boot (8080)  ──JDBC──▶  PostgreSQL
                                     │
                                     └──HTTPS──▶  Anthropic Claude API
```

| Layer | Responsibility |
|---|---|
| `controller/` | REST endpoints only — no business logic |
| `service/` | Business logic + the `AnswerEngine` abstraction |
| `repository/` | Spring Data JPA interfaces |
| `model/` | JPA entities + enums |
| `dto/` | Java `record` request/response carriers |
| `config/` | CORS + global exception handling |

---

## Where each OOP principle lives

| Principle | Where | What to look at |
|---|---|---|
| **Abstraction** | `BaseEntity`, `AnswerEngine` | `BaseEntity` is `abstract` with an abstract `describe()`. `AnswerEngine` is an interface — the contract, no implementation. |
| **Encapsulation** | `Conversation` | `messages` is `private`. `getMessages()` returns an **unmodifiable** list, so the only way in is `addMessage()`, which keeps both sides of the JPA relationship in sync. |
| **Inheritance** | `Conversation extends BaseEntity`, `Message extends BaseEntity` | Both inherit `id` + `createdAt` for free via `@MappedSuperclass`. |
| **Polymorphism** | `describe()`, `AnswerEngine` | Loop over `List<BaseEntity>` and each object runs its *own* `describe()`. `QaService` holds an `AnswerEngine` — it never knows if it's Claude or the offline stub. |

**Java basics also shown:** enums with fields *and* behaviour (`Subject.promptHint()`), `record` DTOs, switch expressions, text blocks (`"""`), streams, constructor injection, checked-exception handling.

### The polymorphism payoff

`QaService` does this:

```java
String answer = answerEngine.answer(question, subject, language);
```

It has **no idea** which engine runs. Swap `ClaudeAnswerEngine` for `OfflineAnswerEngine` and not one line of `QaService` changes. That's the whole point of programming to an interface.

---

## Setup

### 1. PostgreSQL

```sql
CREATE DATABASE askbot;
```

Tables are created automatically (`ddl-auto=update`). `schema.sql` is provided if you'd rather do it by hand.

Update credentials in `backend/src/main/resources/application.properties` if yours differ.

### 2. API key

Get one at https://console.anthropic.com

**Windows (PowerShell):**
```powershell
$env:ANTHROPIC_API_KEY="sk-ant-your-key-here"
```

**Linux/macOS:**
```bash
export ANTHROPIC_API_KEY="sk-ant-your-key-here"
```

In IntelliJ: *Run → Edit Configurations → Environment variables*.

> No key? The app still runs — inject `OfflineAnswerEngine` (see below) to demo the architecture with zero internet.

### 3. Backend

```bash
cd backend
mvn spring-boot:run
```
→ http://localhost:8080

### 4. Frontend

```bash
cd frontend
npm install
npm run dev
```
→ http://localhost:5173

---

## API

| Method | Endpoint | Purpose |
|---|---|---|
| `POST` | `/api/ask` | Ask a question |
| `GET` | `/api/subjects` | List the 12 subjects |
| `GET` | `/api/conversations` | History list |
| `GET` | `/api/conversations/{id}` | One thread with messages |
| `DELETE` | `/api/conversations/{id}` | Delete a thread |
| `GET` | `/api/health` | Status + active engine |

**Example**

```bash
curl -X POST http://localhost:8080/api/ask \
  -H "Content-Type: application/json" \
  -d '{"question":"Eleza sheria ya Ohm","subject":"PHYSICS","language":"SWAHILI"}'
```

---

## Swapping the engine (the demo that sells the design)

In `QaService`, change the constructor parameter:

```java
public QaService(@Qualifier("offlineAnswerEngine") AnswerEngine answerEngine,
                 ConversationRepository conversationRepository) {
```

Restart. Everything still works — the controller, the service body, the frontend, the database all untouched. `/api/health` now reports `Offline (demo)`.

---

## Adding a new subject

Add one line to the `Subject` enum:

```java
AGRICULTURE("Agriculture", "Kilimo"),
```

Add a `case` to `promptHint()` if it needs special guidance. **That's it** — the API, the chips in the UI, and the prompt all pick it up automatically. No other file changes.

---

## Verified

- All 20 Java files compile clean on JDK 17+
- Frontend builds clean (288 modules, Vite 5)
- OOP behaviour tested at runtime: polymorphic `describe()` dispatch confirmed, and `getMessages().add()` correctly throws `UnsupportedOperationException`

## Notes

- PostgreSQL only — no H2
- No Lombok — every getter/constructor is written out so the OOP is visible
- `open-in-view=false`; the repository uses `JOIN FETCH` to avoid `LazyInitializationException`
