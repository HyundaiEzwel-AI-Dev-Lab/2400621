INSERT INTO department (id, name)
VALUES
  (1, '테크담당'),
  (2, '상품사업부')
ON CONFLICT DO NOTHING;

INSERT INTO user_account (id, department_id, username, display_name, active)
VALUES
  (1, 1, 'demo.user', '데모 사용자', true)
ON CONFLICT DO NOTHING;

INSERT INTO project (id, department_id, name, status, target_date, open_date, open_date_text, progress_rate)
VALUES
  (1, 1, '프로모션 운영 프로세스 및 기능 개발', 'TESTING', DATE '2026-03-20', DATE '2026-03-20', NULL, 80),
  (2, 1, 'DL이앤씨 바우처 정책 변경 개발', 'DISCUSSING', DATE '2026-03-30', DATE '2026-03-30', NULL, 55),
  (3, 1, '전사 프로젝트 관리 시스템 구축', 'RECEIVED', NULL, DATE '2026-04-10', NULL, 0),
  (4, 2, '농협카드 인앱 쇼핑몰', 'RECEIVED', NULL, NULL, '오픈일 미정', 0)
ON CONFLICT DO NOTHING;

INSERT INTO project_assignment (project_id, user_id, role_type)
VALUES
  (1, 1, 'MEMBER'),
  (2, 1, 'MEMBER'),
  (3, 1, 'MEMBER'),
  (4, 1, 'MEMBER')
ON CONFLICT DO NOTHING;

INSERT INTO requirement (id, project_id, title, status, confirmed_at)
VALUES
  (1, 1, '프로모션 운영 기능 요구사항', 'CONFIRMED', now()),
  (2, 2, '바우처 정책 변경 요구사항', 'CONFIRMED', now())
ON CONFLICT DO NOTHING;

INSERT INTO wbs_task (id, project_id, requirement_id, name, status, progress_rate, sort_order)
VALUES
  (1, 1, 1, '단위 테스트', 'IN_PROGRESS', 73, 1),
  (2, 2, 2, '바우처 특복 배정 개발', 'IN_PROGRESS', 45, 2),
  (3, 2, 2, '바우처 특복 회수 개발', 'IN_PROGRESS', 50, 3),
  (4, 1, 1, 'DEV 테스트', 'TODO', 0, 4),
  (5, 2, 2, 'HIMS 바우처 강제회수', 'TODO', 0, 5),
  (6, 2, 2, 'HCAS 바우처 강제회수', 'TODO', 0, 6)
ON CONFLICT DO NOTHING;

INSERT INTO task_assignment (task_id, user_id, assignee_type)
VALUES
  (1, 1, 'OWNER'),
  (2, 1, 'OWNER'),
  (3, 1, 'OWNER'),
  (4, 1, 'OWNER'),
  (5, 1, 'OWNER'),
  (6, 1, 'OWNER')
ON CONFLICT DO NOTHING;

INSERT INTO task_schedule (task_id, start_date, due_date)
VALUES
  (1, DATE '2026-03-18', DATE '2026-03-20'),
  (2, DATE '2026-03-20', DATE '2026-03-20'),
  (3, DATE '2026-03-29', DATE '2026-03-29'),
  (4, DATE '2026-03-20', DATE '2026-03-20')
ON CONFLICT DO NOTHING;

SELECT setval('department_id_seq', (SELECT max(id) FROM department));
SELECT setval('user_account_id_seq', (SELECT max(id) FROM user_account));
SELECT setval('project_id_seq', (SELECT max(id) FROM project));
SELECT setval('requirement_id_seq', (SELECT max(id) FROM requirement));
SELECT setval('wbs_task_id_seq', (SELECT max(id) FROM wbs_task));
