import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../../api.js";
import { describeSchedule, formatDate, FREQUENCY_LABELS, money, todayIso } from "../../format.js";
import { getSession } from "../../session.js";

const FREQUENCIES = ["MONTHLY", "BIWEEKLY", "WEEKLY"];

export default function Schedule() {
  const { customerId } = getSession();
  const [loan, setLoan] = useState(null);
  const [frequency, setFrequency] = useState("MONTHLY");
  const [startDate, setStartDate] = useState(todayIso(1));
  const [amount, setAmount] = useState("");
  const [minimum, setMinimum] = useState(null);
  const [error, setError] = useState("");
  const [formError, setFormError] = useState("");
  const [message, setMessage] = useState("");
  const [busy, setBusy] = useState(false);

  const loadLoan = () =>
    api.getCustomerLoan(customerId).then(setLoan).catch((err) => setError(err.message));

  useEffect(() => {
    loadLoan();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [customerId]);

  // The minimum depends on the schedule type, so refetch it whenever that changes.
  useEffect(() => {
    if (!loan) return;
    setMinimum(null);
    api.getMinimumPayment(loan.id, frequency).then(setMinimum).catch((err) => setFormError(err.message));
  }, [loan?.id, frequency]);

  const active = loan?.schedules.find((s) => s.active);
  const hasBank = Boolean(loan?.accountNumber);
  const belowMinimum = minimum && amount !== "" && Number(amount) < Number(minimum.minimumPayment);

  async function handleSchedule(e) {
    e.preventDefault();
    setFormError("");
    setMessage("");
    if (belowMinimum) {
      setFormError(`Payments must be at least ${money(minimum.minimumPayment)} for this schedule.`);
      return;
    }
    setBusy(true);
    try {
      await api.createSchedule(loan.id, { frequency, amount: Number(amount), startDate });
      await loadLoan();
      setAmount("");
      setMessage("Automatic payments scheduled.");
    } catch (err) {
      setFormError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function handleCancel() {
    setFormError("");
    setMessage("");
    try {
      await api.cancelSchedule(loan.id, active.id);
      await loadLoan();
      setMessage("Automatic payments cancelled.");
    } catch (err) {
      setFormError(err.message);
    }
  }

  if (error) return <p className="error" role="alert">{error}</p>;
  if (!loan) return <p className="muted">Loading…</p>;

  return (
    <>
      <h1>Automatic payments</h1>

      <section className="panel">
        <h2>Current schedule</h2>
        {active ? (
          <div className="current">
            <p>
              <strong>{money(active.amount)}</strong> {describeSchedule(active.frequency, active.startDate).toLowerCase()},
              starting {formatDate(active.startDate)}.
            </p>
            <p>
              At this rate your loan will be paid off on <strong>{formatDate(loan.payoffDate)}</strong>.
            </p>
            <button type="button" className="button secondary" onClick={handleCancel}>
              Cancel automatic payments
            </button>
          </div>
        ) : (
          <p className="muted">You don't have automatic payments set up yet.</p>
        )}
      </section>

      <form className="panel form" onSubmit={handleSchedule}>
        <h2>{active ? "Change your schedule" : "Set up a schedule"}</h2>

        {!hasBank && (
          <p className="warning">
            Add your bank account on the <Link to="/me">overview page</Link> before scheduling payments.
          </p>
        )}

        <fieldset className="choices">
          <legend>How often</legend>
          {FREQUENCIES.map((f) => (
            <label key={f} className={`choice ${frequency === f ? "selected" : ""}`}>
              <input
                type="radio"
                name="frequency"
                value={f}
                checked={frequency === f}
                onChange={() => setFrequency(f)}
              />
              <span>{FREQUENCY_LABELS[f]}</span>
            </label>
          ))}
        </fieldset>

        <div className="row">
          <label className="field">
            <span>First payment</span>
            <input type="date" value={startDate} min={todayIso()} onChange={(e) => setStartDate(e.target.value)} required />
            <small className="muted">{describeSchedule(frequency, startDate)}</small>
          </label>
          <label className="field">
            <span>Amount each payment ($)</span>
            <input
              type="number"
              min={minimum?.minimumPayment ?? 0.01}
              step="0.01"
              value={amount}
              placeholder={minimum ? String(minimum.minimumPayment) : ""}
              onChange={(e) => setAmount(e.target.value)}
              aria-invalid={belowMinimum || undefined}
              required
            />
            <small className={belowMinimum ? "error" : "muted"}>
              {minimum
                ? `Minimum ${money(minimum.minimumPayment)} (${minimum.paymentsRemaining} payments left in your term)`
                : "Calculating minimum…"}
            </small>
          </label>
        </div>

        {formError && <p className="error" role="alert">{formError}</p>}
        {message && <p className="success" role="status">{message}</p>}

        <button type="submit" className="button" disabled={busy || !hasBank || !minimum}>
          {busy ? "Scheduling…" : active ? "Replace schedule" : "Schedule payments"}
        </button>
      </form>
    </>
  );
}
