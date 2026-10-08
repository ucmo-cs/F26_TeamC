const currency = new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" });

export const money = (n) => (n == null ? "—" : currency.format(Number(n)));

/** Parses "2026-10-15" as a local date (avoids the off-by-one-day UTC bug). */
export const parseDate = (s) => new Date(`${s}T00:00:00`);

export const formatDate = (s) =>
  s ? parseDate(s).toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" }) : "—";

export const percent = (r) => `${Number(r).toLocaleString("en-US", { maximumFractionDigits: 3 })}%`;

export const FREQUENCY_LABELS = {
  MONTHLY: "Monthly",
  BIWEEKLY: "Every two weeks",
  WEEKLY: "Weekly",
};

/** "On the 15th of every month", "Every other Friday", "Every Tuesday" */
export function describeSchedule(frequency, startDate) {
  if (!startDate) return "";
  const d = parseDate(startDate);
  const weekday = d.toLocaleDateString("en-US", { weekday: "long" });
  if (frequency === "MONTHLY") return `On the ${ordinal(d.getDate())} of every month`;
  if (frequency === "BIWEEKLY") return `Every other ${weekday}`;
  return `Every ${weekday}`;
}

function ordinal(n) {
  const s = ["th", "st", "nd", "rd"];
  const v = n % 100;
  return n + (s[(v - 20) % 10] || s[v] || s[0]);
}

/** Today's date as "YYYY-MM-DD" in local time, for <input type="date"> */
export function todayIso(offsetDays = 0) {
  const d = new Date();
  d.setDate(d.getDate() + offsetDays);
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${mm}-${dd}`;
}
