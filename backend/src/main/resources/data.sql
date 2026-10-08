-- Demo data. Reloaded on every startup while ddl-auto=create.
-- Several loans are partially repaid, as the spec requires.

INSERT INTO customers (id, name, email, phone, bank_name, routing_number, account_number) VALUES
 (1, 'Jordan Miller', 'jordan.miller@example.com', '816-555-0142', 'Central Bank', '101000019', '4401002231'),
 (2, 'Priya Shah',    'priya.shah@example.com',    '660-555-0187', 'First Missouri Credit Union', '281081479', '9020117784'),
 (3, 'Marcus Lee',    'marcus.lee@example.com',    '660-555-0110', NULL, NULL, NULL),
 (4, 'Elena Garcia',  'elena.garcia@example.com',  '913-555-0164', 'Central Bank', '101000019', '4401009975'),
 (5, 'Sam Okafor',    'sam.okafor@example.com',    '816-555-0199', 'Heartland Savings', '101100029', '7731554020');

INSERT INTO users (id, username, password, role, customer_id) VALUES
 (1, 'admin',   'admin123',  'ADMIN',    NULL),
 (2, 'jmiller', 'password1', 'CUSTOMER', 1),
 (3, 'pshah',   'password2', 'CUSTOMER', 2),
 (4, 'mlee',    'password3', 'CUSTOMER', 3),
 (5, 'egarcia', 'password4', 'CUSTOMER', 4),
 (6, 'sokafor', 'password5', 'CUSTOMER', 5);

INSERT INTO loans (id, customer_id, start_date, original_amount, remaining_balance, interest_rate, term_months) VALUES
 (1, 1, '2024-03-15', 25000.00, 18420.55, 6.500,  60),
 (2, 2, '2023-08-01', 12000.00,  5310.00, 4.250,  48),
 (3, 3, '2026-06-20', 40000.00, 39100.00, 7.900, 120),
 (4, 4, '2021-01-10',  8000.00,     0.00, 5.000,  36),
 (5, 5, '2025-01-05', 15000.00,  9875.40, 5.750,  36);

INSERT INTO payment_schedules (id, loan_id, frequency, amount, start_date, active) VALUES
 (1, 1, 'MONTHLY',  700.00, '2026-10-15', TRUE),
 (2, 2, 'BIWEEKLY', 260.00, '2026-10-09', TRUE),
 (3, 5, 'WEEKLY',   150.00, '2026-10-06', TRUE);
