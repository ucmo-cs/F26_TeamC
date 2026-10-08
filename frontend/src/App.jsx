import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import AppShell from "./components/AppShell.jsx";
import RequireRole from "./components/RequireRole.jsx";
import Login from "./pages/Login.jsx";
import LoanList from "./pages/admin/LoanList.jsx";
import CreateLoan from "./pages/admin/CreateLoan.jsx";
import AdminLoanDetail from "./pages/admin/LoanDetail.jsx";
import Dashboard from "./pages/customer/Dashboard.jsx";
import Schedule from "./pages/customer/Schedule.jsx";
import { getSession } from "./session.js";

function HomeRedirect() {
  const session = getSession();
  if (!session) return <Navigate to="/login" replace />;
  return <Navigate to={session.role === "ADMIN" ? "/admin" : "/me"} replace />;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />

        {/* Admin portal */}
        <Route path="/admin" element={<RequireRole role="ADMIN"><AppShell /></RequireRole>}>
          <Route index element={<LoanList />} />
          <Route path="loans/new" element={<CreateLoan />} />
          <Route path="loans/:id" element={<AdminLoanDetail />} />
        </Route>

        {/* Customer portal */}
        <Route path="/me" element={<RequireRole role="CUSTOMER"><AppShell /></RequireRole>}>
          <Route index element={<Dashboard />} />
          <Route path="schedule" element={<Schedule />} />
        </Route>

        <Route path="*" element={<HomeRedirect />} />
      </Routes>
    </BrowserRouter>
  );
}
