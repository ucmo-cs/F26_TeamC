import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../../api.js";
import { formatDate, money, percent } from "../../format.js";

export default function LoanList() {
  const navigate = useNavigate();
  const [loans, setLoans] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    api.getOpenLoans().then(setLoans).catch((err) => setError(err.message));
  }, []);

  if (error) return <p className="error" role="alert">{error}</p>;
  if (!loans) return <p className="muted">Loading loans…</p>;

  const totalOwed = loans.reduce((sum, l) => sum + Number(l.remainingBalance), 0);

  return (
    <>
      <div className="page-head">
        <div>
          <h1>Open loans</h1>
          <p className="muted">
            {loans.length} {loans.length === 1 ? "loan" : "loans"} not yet repaid, {money(totalOwed)} outstanding
          </p>
        </div>
        <Link to="/admin/loans/new" className="button">Add a loan</Link>
      </div>

      {loans.length === 0 ? (
        <p className="empty">Every loan is fully repaid. Add a loan to get started.</p>
      ) : (
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th scope="col">Customer</th>
                <th scope="col">Taken out</th>
                <th scope="col" className="num">Still owed</th>
                <th scope="col" className="num">Original amount</th>
                <th scope="col" className="num">Interest rate</th>
              </tr>
            </thead>
            <tbody>
              {loans.map((loan) => (
                <tr key={loan.id} className="clickable" onClick={() => navigate(`/admin/loans/${loan.id}`)}>
                  <td>
                    <Link to={`/admin/loans/${loan.id}`} onClick={(e) => e.stopPropagation()}>
                      {loan.customerName}
                    </Link>
                  </td>
                  <td>{formatDate(loan.startDate)}</td>
                  <td className="num">{money(loan.remainingBalance)}</td>
                  <td className="num">{money(loan.originalAmount)}</td>
                  <td className="num">{percent(loan.interestRate)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  );
}
