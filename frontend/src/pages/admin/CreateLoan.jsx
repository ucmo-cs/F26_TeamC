import { useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../../api.js";
import { todayIso } from "../../format.js";

const EMPTY = {
  customerName: "",
  email: "",
  phone: "",
  startDate: todayIso(),
  originalAmount: "",
  interestRate: "",
  termMonths: "",
};

export default function CreateLoan() {
  const [form, setForm] = useState(EMPTY);
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const [created, setCreated] = useState(null);

  const update = (field) => (e) => setForm({ ...form, [field]: e.target.value });

  async function handleCreate(e) {
    e.preventDefault();
    setError("");
    setBusy(true);
    try {
      const result = await api.createLoan({
        ...form,
        originalAmount: Number(form.originalAmount),
        interestRate: Number(form.interestRate),
        termMonths: form.termMonths ? Number(form.termMonths) : null,
      });
      setCreated(result);
      setForm({ ...EMPTY, startDate: todayIso() });
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <>
      <h1>Add a loan</h1>
      <p className="muted">
        Adding a loan also creates the customer's account and emails them their sign-in details.
      </p>

      {created && (
        <div className="notice" role="status">
          <p>
            <strong>Loan added for {created.loan.customerName}.</strong>{" "}
            {created.emailSent
              ? `Sign-in details were emailed to ${created.loan.email}.`
              : "Email is turned off, so share these sign-in details with the customer:"}
          </p>
          <p>
            Username <code>{created.username}</code>, temporary password <code>{created.temporaryPassword}</code>
          </p>
          <p><Link to={`/admin/loans/${created.loan.id}`}>View this loan</Link></p>
        </div>
      )}

      <form className="panel form" onSubmit={handleCreate}>
        <h2>Customer</h2>
        <label className="field">
          <span>Full name</span>
          <input value={form.customerName} onChange={update("customerName")} required />
        </label>
        <div className="row">
          <label className="field">
            <span>Email</span>
            <input type="email" value={form.email} onChange={update("email")} required />
          </label>
          <label className="field">
            <span>Phone</span>
            <input type="tel" value={form.phone} onChange={update("phone")} required />
          </label>
        </div>

        <h2>Loan</h2>
        <div className="row">
          <label className="field">
            <span>Date taken out</span>
            <input type="date" value={form.startDate} max={todayIso()} onChange={update("startDate")} required />
          </label>
          <label className="field">
            <span>Original amount ($)</span>
            <input type="number" min="0.01" step="0.01" value={form.originalAmount} onChange={update("originalAmount")} required />
          </label>
        </div>
        <div className="row">
          <label className="field">
            <span>Interest rate (% per year)</span>
            <input type="number" min="0" max="100" step="0.001" value={form.interestRate} onChange={update("interestRate")} required />
          </label>
          <label className="field">
            <span>Term in months (optional)</span>
            <input type="number" min="1" step="1" placeholder="120" value={form.termMonths} onChange={update("termMonths")} />
          </label>
        </div>

        {error && <p className="error" role="alert">{error}</p>}

        <button type="submit" className="button" disabled={busy}>
          {busy ? "Adding loan…" : "Add loan"}
        </button>
      </form>
    </>
  );
}
