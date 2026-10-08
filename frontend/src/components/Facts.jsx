/** A two-column list of label/value pairs. items: [[label, value], ...] */
export default function Facts({ items }) {
  return (
    <dl className="facts">
      {items.map(([label, value]) => (
        <div key={label} className="fact">
          <dt>{label}</dt>
          <dd>{value}</dd>
        </div>
      ))}
    </dl>
  );
}
