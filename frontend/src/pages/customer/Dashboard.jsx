import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../../api.js";
import Facts from "../../components/Facts.jsx";
import RepaymentBar from "../../components/RepaymentBar.jsx";
import { formatDate, money, percent } from "../../format.js";
import { getSession } from "../../session.js";

const toForm = (loan) => ({
  name: loan.customerName ?? "",
  email: loan.email ?? "",
  phone: loan.phone ?? "",
  bankName: loan.bankName ?? "",
  routingNumber: loan.routingNumber ?? "",
  accountNumber: loan.accountNumber ?? "",
});

export default function Dashboard() {
  const { customerId } = getSession();
  const [loan, setLoan] = useState(null);
  const [form, setForm] = useState(null);
  const [error, setError] = useState("");
  const [saveError, setSaveError] = useState("");
  const [saved, setSaved] = useState(false);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    api
      .getCustomerLoan(customerId)
      .then((data) => {
        setLoan(data);
        setForm(toForm(data));
      })
      .catch((err) => setError(err.message));
  }, [customerId]);

  const update = (field) => (e) => {
    setSaved(false);
    setForm({ ...form, [field]: e.target.value });
  };

  async function handleSave(e) {
    e.preventDefault();
    setSaveError("");
    setBusy(true);
    try {
      const data = await api.updateCustomer(customerId, form);
      setLoan(data);
      setForm(toForm(data));
      setSaved(true);
    } catch (err) {
      setSaveError(err.message);
    } finally {
      setBusy(false);
    }
  }

  if (error) return <p className="error" role="alert">{error}</p>;
  if (!loan) return <p className="muted">Loading your loan…</p>;

  return (
    <>
      <h1>Hi, {loan.customerName.split(" ")[0]}</h1>

      <RepaymentBar original={loan.originalAmount} remaining={loan.remainingBalance} />

      <div className="columns">
        <section className="panel">
          <h2>Your loan</h2>
          <Facts
            items={[
              ["Taken out", formatDate(loan.startDate)],
              ["Original amount", money(loan.originalAmount)],
              ["Still owed", money(loan.remainingBalance)],
              ["Interest rate", percent(loan.interestRate)],
              ["Minimum monthly payment", money(loan.minimumMonthlyPayment)],
              [
                "Pay-off date",
                loan.payoffDate ? (
                  formatDate(loan.payoffDate)
                ) : (
                  <Link key="p" to="/me/schedule">Set up automatic payments to see it</Link>
                ),
              ],
            ]}
          />
        </section>

        <form className="panel form" onSubmit={handleSave}>
          <h2>Your details</h2>
          <label className="field">
            <span>Full name</span>
            <input value={form.name} onChange={update("name")} required />
          </label>
          <label className="field">
            <span>Email</span>
            <input type="email" value={form.email} onChange={update("email")} required />
          </label>
          <label className="field">
            <span>Phone</span>
            <input type="tel" value={form.phone} onChange={update("phone")} required />
          </label>

          <h3>Bank account for automatic payments</h3>
          <label className="field">
            <span>Bank name</span>
            <input value={form.bankName} onChange={update("bankName")} />
          </label>
          <div className="row">
            <label className="field">
              <span>Routing number</span>
              <input inputMode="numeric" value={form.routingNumber} onChange={update("routingNumber")} />
            </label>
            <label className="field">
              <span>Account number</span>
              <input inputMode="numeric" value={form.accountNumber} onChange={update("accountNumber")} />
            </label>
          </div>

          {saveError && <p className="error" role="alert">{saveError}</p>}
          {saved && <p className="success" role="status">Details saved.</p>}

          <button type="submit" className="button" disabled={busy}>
            {busy ? "Saving…" : "Save details"}
          </button>
        </form>
      </div>
    </>
  );
}
