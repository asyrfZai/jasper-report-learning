-- Sample data loaded on every startup (spring.sql.init.mode=always).
-- Deliberately small and readable so report output is easy to eyeball while learning.

INSERT INTO company (name, address, founded_year) VALUES
  ('Acme Technologies', '1 Innovation Way, Austin, TX', 2005),
  ('Globex Manufacturing', '88 Industrial Rd, Dayton, OH', 1998);

INSERT INTO department (company_id, name) VALUES
  (1, 'Engineering'), (1, 'Sales'), (1, 'Human Resources'),
  (2, 'Production'), (2, 'Logistics');

INSERT INTO employee (department_id, first_name, last_name, email, hire_date, salary) VALUES
  (1, 'Acap',    'Zai',      'acap.zai@testmail.com',        '2018-03-01', 9000.00),
  (1, 'Hazim',   'Marzuki',  'hazim.marzuki@testmail.com',  '2019-07-15', 9600.00),
  (1, 'Epul',    'Dudin',    'epul.dudin@testmail.com',     '2020-01-10', 9200.00),
  (2, 'Farid',   'Dudin',    'farid.dudin@testmail.com',    '2017-11-20', 7200.00),
  (2, 'Sapikah', 'Azman',    'sapikah.azman@testmail.com',  '2021-05-05', 6800.00),
  (3, 'Zaim',    'Kelate',   'zaim.kelate@testmail.com',    '2016-09-12', 6100.00),
  (4, 'Amin',    'Lajja',    'amin.lajja@testmail.com',     '2015-02-18', 5900.00),
  (4, 'Lala',    'Bakar',    'lala.bakar@testmail.com',     '2022-04-01', 5600.00),
  (5, 'Syahrul', 'Nik',      'syahrul.nik@testmail.com',    '2019-08-23', 6200.00);

INSERT INTO salary_history (employee_id, effective_date, amount, reason) VALUES
  (1, '2018-03-01', 8500.00, 'Hire'),
  (1, '2020-03-01', 9200.00, 'Annual review'),
  (1, '2022-03-01', 9000.00, 'Promotion'),
  (2, '2019-07-15', 8800.00, 'Hire'),
  (2, '2021-07-15', 9600.00, 'Annual review'),
  (3, '2020-01-10', 8700.00, 'Hire'),
  (3, '2022-01-10', 9200.00, 'Annual review'),
  (4, '2017-11-20', 6500.00, 'Hire'),
  (4, '2020-11-20', 7200.00, 'Promotion'),
  (5, '2021-05-05', 6800.00, 'Hire'),
  (6, '2016-09-12', 6100.00, 'Hire'),
  (7, '2015-02-18', 5900.00, 'Hire'),
  (8, '2022-04-01', 5600.00, 'Hire'),
  (9, '2019-08-23', 6200.00, 'Hire');

INSERT INTO product (name, category, unit_price) VALUES
  ('Wireless Mouse',              'Computer Accessories', 25.00),
  ('27" Monitor',                 'Computer Hardware',    220.00),
  ('External SSD 1TB',            'Storage Device',       90.00),
  ('Microsoft 365 Personal',      'Software Subscription',69.99),
  ('Windows 11 Pro License',      'Software License',     199.00),
  ('Google Workspace Subscription','Online Service',      12.00),
  ('Cloud Storage 2TB Plan',      'Cloud Service',        19.99);

INSERT INTO customer_order (customer_name, order_date) VALUES
  ('Northwind Traders', '2025-01-10'),
  ('Contoso Ltd',       '2025-02-14'),
  ('Fabrikam Inc',      '2025-03-03'),
  ('Northwind Traders', '2025-03-20');

INSERT INTO order_item (order_id, product_id, quantity, unit_price) VALUES
  (1, 1, 4, 25.00),
  (1, 2, 2, 220.00),
  (1, 4, 1, 69.99),
  (2, 3, 3, 90.00),
  (2, 6, 10, 12.00),
  (3, 5, 1, 199.00),
  (3, 7, 5, 19.99),
  (4, 1, 2, 25.00),
  (4, 3, 1, 90.00);
