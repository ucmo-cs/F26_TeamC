import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { api } from "../../api.js";
import Facts from "../../components/Facts.jsx";
import RepaymentBar from "../../components/RepaymentBar.jsx";
import { describeSchedule, formatDate, FREQUENCY_LABELS, money, percent } from "../../format.js";

export default function AdminLoanDetail() {
  const { id } = useParams();
  const [loan, setLoan] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    api.getLoan(id).then(setLoan).catch((err) => setError(err.message));
  }, [id]);

  if (error) return <p className="error" role="alert">{error}</p>;
  if (!loan) return <p className="muted">Loading loan…</p>;

  return (
    <>
      <p className="back"><Link to="/admin">Back to open loans</Link></p>
      <h1>{loan.customerName}</h1>

      <RepaymentBar original={loan.originalAmount} remaining={loan.remainingBalance} />

      <div className="columns">
        <section className="panel">
          <h2>Loan</h2>
          <Facts
            items={[
              ["Taken out", formatDate(loan.startDate)],
              ["Original amount", money(loan.originalAmount)],
              ["Still owed", money(loan.remainingBalance)],
              ["Interest rate", percent(loan.interestRate)],
              ["Term", `${loan.termMonths} months`],
              ["Minimum monthly payment", money(loan.minimumMonthlyPayment)],
              ["Pay-off date", loan.payoffDate ? formatDate(loan.payoffDate) : "No automatic payments set up"],
            ]}
          />
        </section>

        <section className="panel">
          <h2>Customer</h2>
          <Facts
            items={[
              ["Name", loan.customerName],
              ["Email", <a key="e" href={`mailto:${loan.email}`}>{loan.email}</a>],
              ["Phone", loan.phone],
            ]}
          />
          <h3>Bank account for automatic payments</h3>
          {loan.accountNumber ? (
            <Facts
              items={[
                ["Bank", loan.bankName || "—"],
                ["Routing number", loan.routingNumber || "—"],
                ["Account number", loan.accountNumber],
              ]}
            />
          ) : (
            <p className="muted">The customer hasn't added bank details yet.</p>
          )}
        </section>
      </div>

      <section className="panel">
        <h2>Scheduled automatic payments</h2>
        {loan.schedules.length === 0 ? (
          <p className="muted">The customer hasn't scheduled any payments.</p>
        ) : (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th scope="col">Frequency</th>
                  <th scope="col">When</th>
                  <th scope="col">First payment</th>
                  <th scope="col" className="num">Amount</th>
                  <th scope="col">Status</th>
                </tr>
              </thead>
              <tbody>
                {loan.schedules.map((s) => (
                  <tr key={s.id} className={s.active ? "" : "inactive"}>
                    <td>{FREQUENCY_LABELS[s.frequency]}</td>
                    <td>{describeSchedule(s.frequency, s.startDate)}</td>
                    <td>{formatDate(s.startDate)}</td>
                    <td className="num">{money(s.amount)}</td>
                    <td>{s.active ? <span className="tag">Active</span> : "Replaced or cancelled"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </>
  );
}
