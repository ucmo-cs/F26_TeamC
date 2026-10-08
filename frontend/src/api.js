// All calls from the web tier to the application tier go through here.
const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080";

async function request(path, { method = "GET", body } = {}) {
  let res;
  try {
    res = await fetch(BASE_URL + path, {
      method,
      headers: body ? { "Content-Type": "application/json" } : undefined,
      body: body ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new Error(`Can't reach the server at ${BASE_URL}. Check that the Spring Boot app is running.`);
  }
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  if (!res.ok) {
    throw new Error(data?.error || `Request failed (${res.status})`);
  }
  return data;
}

export const api = {
  login: (username, password) => request("/api/auth/login", { method: "POST", body: { username, password } }),

  // Admin
  getOpenLoans: () => request("/api/admin/loans"),
  getLoan: (loanId) => request(`/api/admin/loans/${loanId}`),
  createLoan: (loan) => request("/api/admin/loans", { method: "POST", body: loan }),

  // Customer
  getCustomerLoan: (customerId) => request(`/api/customers/${customerId}/loan`),
  updateCustomer: (customerId, details) => request(`/api/customers/${customerId}`, { method: "PUT", body: details }),

  // Scheduling
  getMinimumPayment: (loanId, frequency) => request(`/api/loans/${loanId}/minimum-payment?frequency=${frequency}`),
  createSchedule: (loanId, schedule) => request(`/api/loans/${loanId}/schedules`, { method: "POST", body: schedule }),
  cancelSchedule: (loanId, scheduleId) => request(`/api/loans/${loanId}/schedules/${scheduleId}`, { method: "DELETE" }),
};
