-- ============================================================================
-- Schema for the JasperReports learning project.
--
-- Two independent domains on purpose, so reports can demonstrate "multiple
-- data sources" (advanced section):
--   1. Company -> Department -> Employee -> SalaryHistory  (org-chart style,
--      used for grouping, master-detail, and multi-level nested datasets)
--   2. CustomerOrder -> OrderItem -> Product                (invoice style,
--      used for subreports and crosstabs)
-- ============================================================================

CREATE TABLE IF NOT EXISTS company (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(150) NOT NULL,
    address       VARCHAR(255),
    founded_year  INT
);

CREATE TABLE IF NOT EXISTS department (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id  BIGINT NOT NULL REFERENCES company(id),
    name        VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS employee (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_id  BIGINT NOT NULL REFERENCES department(id),
    first_name     VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    email          VARCHAR(150),
    hire_date      DATE,
    salary         DECIMAL(12,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS salary_history (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id    BIGINT NOT NULL REFERENCES employee(id),
    effective_date DATE NOT NULL,
    amount         DECIMAL(12,2) NOT NULL,
    reason         VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS product (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150) NOT NULL,
    category    VARCHAR(100) NOT NULL,
    unit_price  DECIMAL(12,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS customer_order (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name  VARCHAR(150) NOT NULL,
    order_date     DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS order_item (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT NOT NULL REFERENCES customer_order(id),
    product_id  BIGINT NOT NULL REFERENCES product(id),
    quantity    INT NOT NULL,
    unit_price  DECIMAL(12,2) NOT NULL
);
