import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { clearSession, getSession } from "../session.js";

export default function AppShell() {
  const session = getSession();
  const navigate = useNavigate();
  const isAdmin = session?.role === "ADMIN";

  function signOut() {
    clearSession();
    navigate("/login");
  }

  return (
    <div className="shell">
      <header className="topbar">
        <div className="topbar-inner">
          <span className="brand">{isAdmin ? "Loan administration" : "My loan"}</span>
          <nav className="nav">
            {isAdmin ? (
              <>
                <NavLink to="/admin" end>Open loans</NavLink>
                <NavLink to="/admin/loans/new">Add a loan</NavLink>
              </>
            ) : (
              <>
                <NavLink to="/me" end>Overview</NavLink>
                <NavLink to="/me/schedule">Automatic payments</NavLink>
              </>
            )}
          </nav>
          <div className="user">
            <span>{session?.username}</span>
            <button type="button" className="link-button" onClick={signOut}>Sign out</button>
          </div>
        </div>
      </header>
      <main className="page">
        <Outlet />
      </main>
    </div>
  );
}
