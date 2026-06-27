CREATE TABLE department (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE user_account (
  id BIGSERIAL PRIMARY KEY,
  department_id BIGINT REFERENCES department(id),
  username VARCHAR(100) NOT NULL UNIQUE,
  display_name VARCHAR(100) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE project (
  id BIGSERIAL PRIMARY KEY,
  department_id BIGINT REFERENCES department(id),
  name VARCHAR(200) NOT NULL,
  status VARCHAR(30) NOT NULL,
  target_date DATE,
  open_date DATE,
  open_date_text VARCHAR(50),
  progress_rate NUMERIC(5, 2) NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE project_assignment (
  id BIGSERIAL PRIMARY KEY,
  project_id BIGINT NOT NULL REFERENCES project(id),
  user_id BIGINT NOT NULL REFERENCES user_account(id),
  role_type VARCHAR(30) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uq_project_assignment UNIQUE (project_id, user_id, role_type)
);

CREATE TABLE requirement (
  id BIGSERIAL PRIMARY KEY,
  project_id BIGINT NOT NULL REFERENCES project(id),
  title VARCHAR(200) NOT NULL,
  status VARCHAR(30) NOT NULL,
  confirmed_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE wbs_task (
  id BIGSERIAL PRIMARY KEY,
  project_id BIGINT NOT NULL REFERENCES project(id),
  requirement_id BIGINT REFERENCES requirement(id),
  parent_task_id BIGINT REFERENCES wbs_task(id),
  name VARCHAR(200) NOT NULL,
  status VARCHAR(30) NOT NULL,
  progress_rate NUMERIC(5, 2) NOT NULL DEFAULT 0,
  sort_order INTEGER NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE task_assignment (
  id BIGSERIAL PRIMARY KEY,
  task_id BIGINT NOT NULL REFERENCES wbs_task(id),
  user_id BIGINT NOT NULL REFERENCES user_account(id),
  assignee_type VARCHAR(30) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uq_task_assignment UNIQUE (task_id, user_id, assignee_type)
);

CREATE TABLE task_schedule (
  id BIGSERIAL PRIMARY KEY,
  task_id BIGINT NOT NULL UNIQUE REFERENCES wbs_task(id),
  start_date DATE,
  due_date DATE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_project_assignment_user_project ON project_assignment (user_id, project_id);
CREATE INDEX idx_task_assignment_user_task ON task_assignment (user_id, task_id);
CREATE INDEX idx_task_schedule_due_date ON task_schedule (due_date);
CREATE INDEX idx_task_schedule_start_due_date ON task_schedule (start_date, due_date);
CREATE INDEX idx_task_schedule_task ON task_schedule (task_id);
CREATE INDEX idx_project_status ON project (status);
CREATE INDEX idx_wbs_task_project_status ON wbs_task (project_id, status);
