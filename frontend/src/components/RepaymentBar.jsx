import { money } from "../format.js";

/** The balance still owed, with a bar showing how much of the original loan has been repaid. */
export default function RepaymentBar({ original, remaining }) {
  const orig = Number(original) || 0;
  const left = Number(remaining) || 0;
  const repaid = orig > 0 ? Math.min(100, Math.max(0, (1 - left / orig) * 100)) : 0;

  return (
    <section className="repayment" aria-label="Repayment progress">
      <p className="balance">{money(left)}</p>
      <p className="balance-caption">left to pay of {money(orig)}</p>
      <div
        className="bar"
        role="progressbar"
        aria-valuemin={0}
        aria-valuemax={100}
        aria-valuenow={Math.round(repaid)}
        aria-label="Percent of loan repaid"
      >
        <div className="bar-fill" style={{ width: `${repaid}%` }} />
      </div>
      <p className="bar-caption">{Math.round(repaid)}% repaid</p>
    </section>
  );
}
