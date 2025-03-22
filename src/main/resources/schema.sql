CREATE TABLE departments (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    description VARCHAR(500) NOT NULL,
    established_date TIMESTAMPTZ NOT NULL,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NULL
);

CREATE TABLE binary_contents (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100) NOT NULL,
    file_size BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NULL
);


CREATE TABLE employees (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    department_id BIGINT NOT NULL,
    binary_content_id BIGINT NULL,
    employee_number VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    job_title VARCHAR(50) NOT NULL,
    hire_date TIMESTAMPTZ NOT NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('ACTIVE', 'ON_LEAVE', 'RESIGNED')),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NULL,
    CONSTRAINT fk_employees_department FOREIGN KEY (department_id) REFERENCES departments (id),
    CONSTRAINT fk_employees_binary_content FOREIGN KEY (binary_content_id) REFERENCES binary_contents (id) ON DELETE SET NULL
);


CREATE TABLE change_log (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    employee_number VARCHAR(50) NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('CREATED', 'UPDATED', 'DELETED')),
    details JSONB NULL,
    memo TEXT NULL,
    ip_address VARCHAR(50) NOT NULL,
    capture_date DATE NULL,
    created_at TIMESTAMPTZ NOT NULL
);


CREATE TABLE backup_history (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    worker VARCHAR(50) NOT NULL,
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NULL,
    status VARCHAR(50) NOT NULL CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'FAILED', 'SKIPPED')),
    file_id BIGINT NULL,
    CONSTRAINT fk_backup_history_file FOREIGN KEY (file_id) REFERENCES binary_contents (id) ON DELETE SET NULL
);

CREATE TABLE employee_statistics (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    employee_count BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL CHECK(type IN ('YEAR', 'QUARTER', 'MONTH', 'WEEK', 'DAY')),
    diff BIGINT NOT NULL DEFAULT 0,
    rate DECIMAL NOT NULL DEFAULT 0,
    capture_date DATE NOT NULL
)

ALTER TABLE employee_statistics
    ADD CONSTRAINT uq_capture_date_type UNIQUE (capture_date, type);
