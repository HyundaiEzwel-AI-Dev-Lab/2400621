# Phase2 검증 리포트

## 1. 검증 개요

- 검증 기준일: 2026-06-27
- 검증 대상: Phase2 구현 완료 상태
- 검증 기준:
  - Phase2 승인 계획
  - `docs/07_phase1_review.md`
  - `docs/issues.md`
  - `docs/worklog.md`
- 검증 방식:
  - Phase2 구현 내역과 잔여 이슈 대조
  - Backend/API/Frontend/DB Repository 변경 범위 검토
  - 테스트, 빌드, Docker, API, 화면 검증 결과 확인
  - 코드 수정 없이 문서화만 수행

검증 상태 기준:

| 상태 | 의미 |
| --- | --- |
| 충족 | Phase2 목표에 맞게 구현 및 검증 완료 |
| 부분 충족 | 핵심은 반영했으나 일부 후속 과제 존재 |
| 미충족 | Phase2에서 반영하지 못함 |
| Phase2 제외 | 승인된 Phase2 범위 밖으로 유지 |

## 2. Phase2 요구사항 충족 여부

| 요구사항 | 결과 | 검증 내용 |
| --- | --- | --- |
| Phase1 잔여 이슈 영향 판단 | 충족 | `ISSUE-003`, `ISSUE-005`는 해결, `ISSUE-004`, `ISSUE-006`은 부분 해결, `ISSUE-001`, `ISSUE-002`는 관찰 유지 |
| Backend/API 우선 구현 | 충족 | API 파라미터, JPA Repository, active 사용자 조건, 오류 응답 보완 |
| Frontend 연동 | 충족 | 변경된 API 파라미터 반영, Docker proxy 오류 수정, 화면 동작 확인 |
| 테스트 코드 보강 | 부분 충족 | Backend Service/Controller 테스트 보강. Frontend는 type-check/build 중심이며 단위 테스트 도구는 미도입 |
| 테스트/빌드 실행 | 충족 | backend test/build, frontend type-check/build 성공 |
| Docker 통합 검증 | 충족 | `docker compose up -d --build` 성공, 세 컨테이너 정상 |
| API 동작 확인 | 충족 | summary/card/calendar/daily/unscheduled/actions/bad scope 호출 검증 |
| 화면 동작 확인 | 충족 | 카드형, 더보기 메뉴, 캘린더형, 일정 미등록 패널, 날짜별 팝업, ESC 닫기 확인 |
| Phase2 범위 준수 | 충족 | 로그인/권한, 일정 저장, WBS 상세 본화면, 일정관리 본화면, npm audit 업그레이드 미수행 |

종합 판단:

- Phase2의 핵심 목표였던 API 명세 정합화, Repository 전략 정리, 최소 UX 보완, 통합 검증은 충족했다.
- Phase3 진입 전 남은 주요 위험은 화면 완성도, 접근성 세부, Frontend 테스트 자동화, npm audit 취약점이다.

## 3. Backend API 변경 내역

### 3.1 요청 파라미터 보완

| API | 변경 내용 | 검증 결과 |
| --- | --- | --- |
| `GET /api/v1/my-work/summary` | `scope` query 수신 | `scope=INTEGRATED` 정상, 미지원 scope 400 확인 |
| `GET /api/v1/my-work/card` | `scope`, `projectPage`, `projectSize`, `waitingPage`, `waitingSize` 반영 | page size 1 요청 시 진행/대기 프로젝트 각 1건 반환 확인 |
| `GET /api/v1/my-work/calendar` | `scope` query 수신 | 2026년 3월 일정 4건, 일정 미등록 2건 확인 |
| `GET /api/v1/my-work/calendar/daily` | `scope` query 수신 | 2026-03-20 업무 2건 확인 |
| `GET /api/v1/my-work/unscheduled` | `scope`, `page`, `size` 반영 | `totalCount=2`, `items=1`로 전체 건수/페이지 크기 분리 확인 |
| `GET /api/v1/my-work/tasks/{taskId}/actions` | 변경 없음 | 일정관리/WBS 상세 액션 true 확인 |

### 3.2 오류 응답 보완

- `InvalidMyWorkRequestException` 추가
- 미지원 scope 또는 잘못된 page/size 요청 시 `400 MY_WORK_BAD_REQUEST` 반환
- 존재하지 않는 업무 액션 조회는 기존처럼 `404 MY_WORK_NOT_FOUND` 유지

### 3.3 집계/조회 정책 보완

- `scope=INTEGRATED`만 허용
- page는 0 이상, size는 1 이상 100 이하로 검증
- `/unscheduled.totalCount`를 현재 페이지 항목 수가 아니라 전체 일정 미등록 업무 수로 보정
- `user_account.active=true` 조건을 프로젝트/업무 조회에 반영

## 4. Frontend 화면 변경 내역

| 영역 | 변경 내용 | 검증 결과 |
| --- | --- | --- |
| API 호출 | `scope`, paging parameter 전달 | 화면 API 호출 정상 |
| 기준일 | `VITE_MY_WORK_BASE_DATE` override 가능, 기본 `2026-03-22` 유지 | 샘플 데이터 기준 화면 정상 |
| Docker proxy | `VITE_API_BASE_URL` 기반 proxy target 적용 | Frontend 컨테이너에서 Backend API 연결 정상 |
| 공통 헤더 | `통합` 라벨 표시 | 카드형 화면에서 확인 |
| 카드형 안내 | WBS/일정 안내 문구 추가 | 카드형 화면에서 확인 |
| 업무 액션 | `...` 더보기 메뉴, 일정관리/WBS 상세 링크 표시 | 메뉴 표시 확인 |
| 캘린더 일정 | 기간, 프로젝트명, 지연 텍스트 표시 보완 | 캘린더형 화면에서 확인 |
| 날짜별 팝업 | 업무 건수 표시, ESC 닫기 구현 | `2026-03-20 내 업무 (2건)` 및 ESC 닫기 확인 |

남은 화면 과제:

- 진행 프로젝트/대기 프로젝트 좌우 이동
- 일정관리/WBS 상세/일정 등록 대상 화면 본구현
- 팝업 포커스 트랩
- 긴 프로젝트명/업무명 전체 확인 수단
- Frontend 컴포넌트 분리 및 테스트 자동화

## 5. DB/Repository 변경 내역

### 5.1 JPA Entity 추가

- `Department`
- `UserAccount`
- `Project`
- `ProjectAssignment`
- `Requirement`
- `WbsTask`
- `TaskAssignment`
- `TaskSchedule`

### 5.2 Repository 변경

- 기존 `JdbcMyWorkRepository` 제거
- `JpaMyWorkRepository` 추가
- Spring Data JPA Repository 추가
  - `ProjectJpaRepository`
  - `TaskJpaRepository`
- Native projection 추가
  - `ProjectProjection`
  - `TaskProjection`

### 5.3 조회 조건 변경

- 프로젝트 조회 시 active 사용자 조건 반영
- 업무 조회 시 active 사용자 조건 반영
- 진행/대기 프로젝트 count 분리
- 일정 미등록 업무 total count 분리
- 캘린더 기간 겹침 조회 유지

검증 판단:

- `docs/04_db_design.md`의 Spring Data JPA 전제와 Repository 구현 방식 차이는 Phase2에서 해소되었다.
- DB DDL 자체는 변경하지 않았고, 기존 스키마 위에서 JPA Entity/Repository를 매핑했다.

## 6. 테스트/빌드 결과

| 항목 | 명령 | 결과 |
| --- | --- | --- |
| Backend test | `./gradlew test` | 성공 |
| Backend build | `./gradlew build` | 성공 |
| Frontend type-check | `npm run type-check` | 성공 |
| Frontend build | `npm run build` | 성공 |
| Docker Compose | `docker compose up -d --build` | 성공 |
| 컨테이너 상태 | `docker compose ps` | postgres healthy, backend/frontend Up |
| API 확인 | curl 호출 | 정상 |
| 화면 확인 | 브라우저 검증 | 정상 |

API 검증 항목:

- summary: 통합 요약 지표 정상
- card: paging 반영 정상
- unscheduled: `totalCount`와 `items` 분리 정상
- calendar: 월간 일정 및 일정 미등록 패널 데이터 정상
- daily: 특정 날짜 업무 목록 정상
- actions: 업무 액션 정상
- bad scope: `400 MY_WORK_BAD_REQUEST` 정상

화면 검증 항목:

- 카드형 API 오류 없음
- 요약 지표 표시
- 진행 프로젝트/내 할 일/대기 프로젝트 표시
- 일정 미등록 업무 표시
- 안내 문구 표시
- 업무 행 `...` 더보기 메뉴 표시
- 캘린더형 전환
- 일정 항목 및 지연 텍스트 표시
- 일정 미등록 업무 패널 표시
- 날짜별 팝업 표시 및 ESC 닫기

## 7. Phase2 범위 외 변경 여부

판단: 승인된 Phase2 범위 외 변경 없음.

Phase2에서 의도적으로 제외한 항목:

- 실제 로그인/권한 시스템
- 일정 저장/수정/삭제 API
- 일정관리 화면 본구현
- WBS 상세 화면 본구현
- 진행/대기 프로젝트 캐러셀 본구현
- npm audit 취약점 임의 업그레이드
- 운영 수준 CI/CD
- 고도화된 모바일 전용 UI

비고:

- `docker-compose.yml`과 `frontend/vite.config.ts`의 proxy 수정은 화면 검증 실패 원인 수정으로 Phase2 통합 검증 범위에 포함된다.
- Frontend 테스트 도구 추가는 기술 스택 확장 가능성이 있어 Phase2에서 수행하지 않았다.

## 8. Phase3 진행 전 해결 필요 이슈

| 이슈 | 필요성 | Phase3 영향 |
| --- | --- | --- |
| 진행/대기 프로젝트 좌우 이동 | PPTX 화면 재현도 향상 | 카드형 UX 완성도 영향 |
| 일정관리/WBS 상세/일정 등록 대상 화면 | 현재 링크만 존재 | 버튼 클릭 후 사용자 흐름 단절 |
| 포커스 트랩 | 팝업 접근성 | 키보드 사용자 경험 영향 |
| 긴 텍스트 전체 확인 수단 | 프로젝트명/업무명 확인 | 실제 업무 데이터에서 정보 누락 가능 |
| Frontend 테스트 자동화 | 화면 회귀 방지 | Phase3 UI 변경 시 안정성 저하 |
| npm audit 취약점 | 보안 리스크 | 배포 전 별도 판단 필요 |
| host curl 관찰 이슈 | 환경 재현성 | 현재 미재현이나 개발자별 환경에서 확인 필요 |

## 9. 남은 이슈 우선순위

| 우선순위 | 이슈 ID | 이슈 | 이유 | 권장 처리 |
| --- | --- | --- | --- | --- |
| P1 | ISSUE-004 | 일정관리/WBS 상세/일정 등록 대상 화면 미구현 | 현재 메뉴/버튼 클릭 후 실제 업무 흐름이 끊김 | Phase3 첫 작업으로 placeholder 또는 실제 화면 연결 |
| P1 | 신규 | Frontend 테스트 자동화 부재 | Phase3 화면 변경이 늘어나면 회귀 위험 증가 | 기술 스택 승인 후 Vitest 등 도입 여부 결정 |
| P2 | ISSUE-004 | 진행/대기 프로젝트 좌우 이동 미구현 | PPTX 재현도와 카드형 탐색성 영향 | Phase3 카드형 UX 보완에 포함 |
| P2 | ISSUE-006 | 포커스 트랩 미구현 | 접근성/키보드 사용성 영향 | 팝업 컴포넌트 정리 시 반영 |
| P2 | ISSUE-006 | 긴 텍스트 전체 확인 수단 미구현 | 실제 프로젝트명/업무명 확인성 저하 | title/tooltip 또는 상세 보기 정책 추가 |
| P2 | ISSUE-001 | npm audit 취약점 2건 | 보안 리스크 | 의존성 영향 분석 후 업그레이드 여부 결정 |
| P3 | ISSUE-002 | host curl 실패 관찰 | 이번 검증에서는 미재현 | 환경별 재현 시에만 대응 |

## 10. 결론

Phase2는 Phase1 검증에서 드러난 API/Repository 정합성 문제를 대부분 해소했고, 화면도 Phase3에 진입 가능한 수준으로 보완했다.

현재 상태는 Backend API, JPA Repository, Docker 통합, 카드형/캘린더형 주요 화면이 연결된 프로토타입이다. Phase3에서는 화면 흐름 완성도와 회귀 테스트 체계를 중심으로 작업하는 것이 적절하다.
