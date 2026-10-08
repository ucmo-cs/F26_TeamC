import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../api.js";
import { setSession } from "../session.js";

export default function Login() {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  async function handleSignIn(e) {
    e.preventDefault();
    setError("");
    setBusy(true);
    try {
      const session = await api.login(username, password);
      setSession(session);
      navigate(session.role === "ADMIN" ? "/admin" : "/me");
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="login">
      <form className="login-card" onSubmit={handleSignIn}>
        <h1>Sign in</h1>
        <p className="muted">Customers and bank staff both sign in here.</p>

        <label className="field">
          <span>Username</span>
          <input value={username} onChange={(e) => setUsername(e.target.value)} autoComplete="username" required />
        </label>
        <label className="field">
          <span>Password</span>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} autoComplete="current-password" required />
        </label>

        {error && <p className="error" role="alert">{error}</p>}

        <button type="submit" className="button" disabled={busy}>
          {busy ? "Signing in…" : "Sign in"}
        </button>
      </form>
    </div>
  );
}
