# 내 업무 구현 계획

## 1. 계획 기준

본 구현 계획은 다음 문서를 기준으로 한다.

- `docs/00_project_context.md`
- `docs/01_requirements.md`
- `docs/02_screen_spec.md`
- `docs/03_api_spec.md`
- `docs/04_db_design.md`
- `docs/05_component_design.md`

기술 스택은 임의로 변경하거나 추가하지 않는다.

- Backend: Spring Boot 3.x, Java 21, Gradle, Spring Data JPA
- Frontend: Vue3, Vue Router, Pinia, Vite, TypeScript
- Database: PostgreSQL 16
- Infra: Docker, Docker Compose
- API: REST API
- Architecture: Layered Architecture

## 2. Phase 구성

| Phase | 목표 | 산출물 |
| --- | --- | --- |
| Phase1 | 프로젝트 실행 골격과 DB/API 기반 구성 | Backend/Frontend/Docker 기본 실행 환경, DB 스키마, 샘플 데이터 |
| Phase2 | Backend API 구현 | 내 업무 REST API, Service 집계 로직, Repository 조회, Backend 테스트 |
| Phase3 | Frontend 화면 구현 및 통합 | 카드형/캘린더형 화면, Pinia Store, API 연동, 통합 검증 |

## 3. Phase1

### 3.1 목표

프로토타입 구현을 시작할 수 있는 Backend, Frontend, Database, Docker 실행 기반을 만든다. 이 단계에서는 화면 기능을 완성하지 않고, 프로젝트 골격과 샘플 데이터 기반을 준비한다.

### 3.2 수정 파일

| 파일 | 작업 내용 |
| --- | --- |
| `README.md` | 로컬 실행 방법, Docker 실행 방법, 테스트 명령 추가 |
| `docs/00_project_context.md` | 실제 생성된 디렉터리 구조가 달라질 경우 구조 설명 보정 |

### 3.3 생성 파일

Backend:

| 파일 | 작업 내용 |
| --- | --- |
| `backend/settings.gradle` | Gradle 프로젝트 이름 설정 |
| `backend/build.gradle` | Spring Boot 3.x, Java 21, JPA, PostgreSQL, Test, Swagger 의존성 설정 |
| `backend/src/main/java/.../HpmsApplication.java` | Spring Boot 진입점 |
| `backend/src/main/resources/application.yml` | 공통 설정 |
| `backend/src/main/resources/application-local.yml` | 로컬 DB 연결 설정 |
| `backend/src/main/resources/db/schema.sql` | 프로토타입 DB 스키마 |
| `backend/src/main/resources/db/data.sql` | 내 업무 화면 검증용 샘플 데이터 |
| `backend/src/test/java/.../HpmsApplicationTests.java` | Spring context load 테스트 |

Frontend:

| 파일 | 작업 내용 |
| --- | --- |
| `frontend/package.json` | Vue3, Vite, TypeScript, Vue Router, Pinia 스크립트 설정 |
| `frontend/index.html` | Vite 진입 HTML |
| `frontend/tsconfig.json` | TypeScript strict 설정 |
| `frontend/vite.config.ts` | Vite 설정 및 API proxy 설정 |
| `frontend/src/main.ts` | Vue 앱 진입점 |
| `frontend/src/App.vue` | 기본 앱 레이아웃 |
| `frontend/src/router/index.ts` | `/my-work` 라우트 등록 |
| `frontend/src/stores/index.ts` | Pinia 초기 설정 |

Infra:

| 파일 | 작업 내용 |
| --- | --- |
| `docker-compose.yml` | PostgreSQL, Backend, Frontend 실행 구성 |
| `backend/Dockerfile` | Spring Boot 실행 이미지 |
| `frontend/Dockerfile` | Vue 앱 개발 또는 정적 서빙 이미지 |
| `.gitignore` | Java, Node, Gradle, IDE, 빌드 산출물 제외 |

### 3.4 예상 작업 시간

| 작업 | 예상 시간 |
| --- | --- |
| Backend 프로젝트 골격 생성 | 1.5h |
| Frontend 프로젝트 골격 생성 | 1.5h |
| PostgreSQL/Docker Compose 구성 | 1h |
| DB 스키마 및 샘플 데이터 작성 | 2h |
| 기본 실행 및 문서 보정 | 1h |
| 합계 | 7h |

### 3.5 테스트 방법

- `./gradlew test`로 Backend 기본 테스트 실행
- `npm run type-check`로 Frontend TypeScript 설정 확인
- `npm run build`로 Frontend 빌드 확인
- `docker compose up`으로 PostgreSQL, Backend, Frontend 기동 확인
- Backend health 또는 Swagger URL 접속 확인
- PostgreSQL 컨테이너에서 샘플 데이터 적재 확인

### 3.6 위험 요소

| 위험 요소 | 영향 | 대응 |
| --- | --- | --- |
| 초기 의존성 다운로드에 네트워크가 필요함 | 개발 환경 구성 지연 | 최초 1회 의존성 설치 필요성을 명시 |
| Docker Compose 포트 충돌 | 로컬 실행 실패 | 기본 포트와 대체 포트 문서화 |
| DB 스키마가 이후 API 조회와 맞지 않음 | Phase2 재작업 | Phase2 시작 전 API 응답 DTO와 스키마 재대조 |
| 샘플 데이터가 화면 케이스를 충분히 담지 못함 | 화면 검증 부실 | 정상, 빈 상태, 지연, 일정 미등록 데이터를 모두 포함 |

## 4. Phase2

### 4.1 목표

`docs/03_api_spec.md`와 `docs/04_db_design.md`에 맞춰 내 업무 Backend REST API를 구현한다. 화면 구현 전에도 API 응답만으로 카드형/캘린더형 데이터를 검증할 수 있어야 한다.

### 4.2 수정 파일

| 파일 | 작업 내용 |
| --- | --- |
| `backend/build.gradle` | 테스트 또는 Swagger 설정 보완이 필요한 경우 수정 |
| `backend/src/main/resources/application-local.yml` | SQL 초기화, 로깅 설정 보완 |
| `backend/src/main/resources/db/data.sql` | API 테스트 케이스 보강용 샘플 데이터 추가 |

### 4.3 생성 파일

Domain/Entity:

| 파일 | 작업 내용 |
| --- | --- |
| `backend/src/main/java/.../domain/Department.java` | 부서 Entity |
| `backend/src/main/java/.../domain/UserAccount.java` | 사용자 Entity |
| `backend/src/main/java/.../domain/Project.java` | 프로젝트 Entity |
| `backend/src/main/java/.../domain/ProjectAssignment.java` | 프로젝트 배정 Entity |
| `backend/src/main/java/.../domain/Requirement.java` | 요구사항 Entity |
| `backend/src/main/java/.../domain/WbsTask.java` | WBS 업무 Entity |
| `backend/src/main/java/.../domain/TaskAssignment.java` | 업무 배정 Entity |
| `backend/src/main/java/.../domain/TaskSchedule.java` | 업무 일정 Entity |

Enum:

| 파일 | 작업 내용 |
| --- | --- |
| `backend/src/main/java/.../domain/ProjectStatus.java` | 프로젝트 상태 |
| `backend/src/main/java/.../domain/TaskStatus.java` | 업무 상태 |
| `backend/src/main/java/.../application/DisplayStatus.java` | 화면 파생 상태 |

Repository:

| 파일 | 작업 내용 |
| --- | --- |
| `backend/src/main/java/.../repository/ProjectRepository.java` | 진행/대기 프로젝트 조회 |
| `backend/src/main/java/.../repository/WbsTaskRepository.java` | 내 할 일, 캘린더, 일정 미등록 업무 조회 |
| `backend/src/main/java/.../repository/UserAccountRepository.java` | 임시 사용자 조회 |

Service:

| 파일 | 작업 내용 |
| --- | --- |
| `backend/src/main/java/.../application/MyWorkService.java` | 내 업무 집계 및 조회 로직 |
| `backend/src/main/java/.../application/DdayCalculator.java` | D-day, 지연, 금주 마감 계산 |
| `backend/src/main/java/.../application/MyWorkQueryCondition.java` | 조회 조건 모델 |

Controller/DTO:

| 파일 | 작업 내용 |
| --- | --- |
| `backend/src/main/java/.../api/MyWorkController.java` | REST API Controller |
| `backend/src/main/java/.../api/dto/MyWorkSummaryResponse.java` | 요약 지표 응답 |
| `backend/src/main/java/.../api/dto/MyWorkCardResponse.java` | 카드형 응답 |
| `backend/src/main/java/.../api/dto/MyWorkCalendarResponse.java` | 캘린더형 응답 |
| `backend/src/main/java/.../api/dto/DailyTaskResponse.java` | 날짜별 업무 응답 |
| `backend/src/main/java/.../api/dto/UnscheduledTaskResponse.java` | 일정 미등록 업무 응답 |
| `backend/src/main/java/.../api/dto/TaskActionResponse.java` | 업무 가능 액션 응답 |
| `backend/src/main/java/.../api/dto/ApiResponse.java` | 공통 응답 래퍼 |
| `backend/src/main/java/.../api/dto/ErrorResponse.java` | 공통 오류 응답 |
| `backend/src/main/java/.../api/GlobalExceptionHandler.java` | API 예외 처리 |

Test:

| 파일 | 작업 내용 |
| --- | --- |
| `backend/src/test/java/.../application/DdayCalculatorTest.java` | D-day, 지연, 금주 마감 단위 테스트 |
| `backend/src/test/java/.../application/MyWorkServiceTest.java` | 집계/조회 서비스 테스트 |
| `backend/src/test/java/.../api/MyWorkControllerTest.java` | REST API 응답 테스트 |

### 4.4 예상 작업 시간

| 작업 | 예상 시간 |
| --- | --- |
| Entity/Enum 구현 | 2h |
| Repository 조회 구현 | 2h |
| Service 집계/조회 로직 구현 | 3h |
| Controller/DTO/예외 처리 구현 | 2.5h |
| Backend 테스트 작성 | 3h |
| Swagger 확인 및 API 수동 검증 | 1h |
| 합계 | 13.5h |

### 4.5 테스트 방법

- `./gradlew test`로 Backend 전체 테스트 실행
- `DdayCalculatorTest`에서 다음 케이스 검증
  - `D-14`
  - `D-Day`
  - `D+2`
  - 월요일~일요일 금주 마감
  - 완료 업무 제외
  - 일정 미등록 업무 제외
- `MyWorkServiceTest`에서 다음 집계 검증
  - 진행 프로젝트 수
  - 내 할 일 수
  - 금주 마감 수
  - 지연 수
  - 대기 수
- `MyWorkControllerTest`에서 다음 API 검증
  - `GET /api/v1/my-work/summary`
  - `GET /api/v1/my-work/card`
  - `GET /api/v1/my-work/calendar`
  - `GET /api/v1/my-work/calendar/daily`
  - `GET /api/v1/my-work/unscheduled`
  - `GET /api/v1/my-work/tasks/{taskId}/actions`
- Swagger UI에서 응답 DTO 확인
- Docker PostgreSQL 연결 상태에서 API 수동 호출 확인

### 4.6 위험 요소

| 위험 요소 | 영향 | 대응 |
| --- | --- | --- |
| D-day 부호 규칙 혼동 | 화면 표시 오류 | `DdayCalculator` 단위 테스트를 먼저 작성 |
| 기간 일정의 월간 캘린더 조회 조건이 복잡함 | 일정 누락 | 조회 월과 일정 기간 겹침 테스트 추가 |
| Entity 연관관계가 과도하게 복잡해짐 | 조회 성능 및 테스트 난이도 증가 | 화면 조회는 DTO projection 또는 명시적 query 사용 |
| API 응답 DTO가 Frontend 표시 모델과 어긋남 | Phase3 재작업 | Phase2 종료 전 `docs/05_component_design.md` 타입과 대조 |
| 인증이 아직 실제 구현되지 않음 | 사용자별 조회 정확도 제한 | 프로토타입에서는 `X-User-Id` 기준으로 고정 |

## 5. Phase3

### 5.1 목표

Vue3 기반 내 업무 화면을 구현하고 Backend API와 연동한다. 카드형, 캘린더형, 날짜별 팝업, 일정 미등록 업무 패널을 프로토타입 수준으로 동작하게 만든다.

### 5.2 수정 파일

| 파일 | 작업 내용 |
| --- | --- |
| `frontend/src/App.vue` | 공통 레이아웃 적용 |
| `frontend/src/router/index.ts` | `/my-work` 라우트 및 query 처리 보완 |
| `frontend/vite.config.ts` | Backend API proxy 보완 |
| `README.md` | Frontend 실행 및 통합 테스트 방법 보완 |

### 5.3 생성 파일

API/Store/Types:

| 파일 | 작업 내용 |
| --- | --- |
| `frontend/src/api/httpClient.ts` | REST API 공통 client |
| `frontend/src/api/myWorkApi.ts` | 내 업무 API 호출 함수 |
| `frontend/src/stores/myWorkStore.ts` | Pinia Store |
| `frontend/src/types/myWork.ts` | 내 업무 TypeScript 타입 |
| `frontend/src/utils/dateFormatter.ts` | 날짜, D-day 표시 포맷 |
| `frontend/src/utils/calendarUtils.ts` | 월간 캘린더 그리드 생성 |

Views/Components:

| 파일 | 작업 내용 |
| --- | --- |
| `frontend/src/views/MyWorkView.vue` | 내 업무 최상위 화면 |
| `frontend/src/components/layout/AppSidebar.vue` | 좌측 메뉴 |
| `frontend/src/components/my-work/MyWorkHeader.vue` | 화면 제목, 브레드크럼, 보기 전환 |
| `frontend/src/components/my-work/ViewModeTabs.vue` | 카드형/캘린더형 전환 |
| `frontend/src/components/my-work/MyWorkSummaryBar.vue` | 요약 지표 |
| `frontend/src/components/my-work/SummaryMetricItem.vue` | 요약 지표 단일 항목 |
| `frontend/src/components/my-work/card/MyWorkCardView.vue` | 카드형 섹션 조합 |
| `frontend/src/components/my-work/card/ProgressProjectSection.vue` | 진행 프로젝트 영역 |
| `frontend/src/components/my-work/card/ProjectCardCarousel.vue` | 진행 프로젝트 좌우 이동 |
| `frontend/src/components/my-work/card/ProjectCard.vue` | 진행 프로젝트 카드 |
| `frontend/src/components/my-work/card/MyTaskSection.vue` | 내 할 일 영역 |
| `frontend/src/components/my-work/card/MyTaskTable.vue` | 내 할 일 테이블 |
| `frontend/src/components/my-work/card/MyTaskRow.vue` | 내 할 일 행 |
| `frontend/src/components/my-work/card/TaskActionMenu.vue` | 업무 행 더보기 메뉴 |
| `frontend/src/components/my-work/card/WaitingProjectSection.vue` | 대기 프로젝트 영역 |
| `frontend/src/components/my-work/card/WaitingProjectCarousel.vue` | 대기 프로젝트 좌우 이동 |
| `frontend/src/components/my-work/card/WaitingProjectCard.vue` | 대기 프로젝트 카드 |
| `frontend/src/components/my-work/card/MyWorkNotice.vue` | 안내 문구 |
| `frontend/src/components/my-work/calendar/MyWorkCalendarView.vue` | 캘린더형 섹션 조합 |
| `frontend/src/components/my-work/calendar/CalendarToolbar.vue` | 월 이동, 오늘 버튼 |
| `frontend/src/components/my-work/calendar/MonthlyCalendar.vue` | 월간 캘린더 |
| `frontend/src/components/my-work/calendar/CalendarDayCell.vue` | 날짜 셀 |
| `frontend/src/components/my-work/calendar/CalendarEventItem.vue` | 일정 항목 |
| `frontend/src/components/my-work/calendar/CalendarMoreButton.vue` | 더보기 버튼 |
| `frontend/src/components/my-work/calendar/DailyTaskPopup.vue` | 날짜별 내 업무 팝업 |
| `frontend/src/components/my-work/calendar/DailyTaskList.vue` | 팝업 업무 목록 |
| `frontend/src/components/my-work/calendar/UnscheduledTaskPanel.vue` | 일정 미등록 업무 패널 |
| `frontend/src/components/my-work/calendar/UnscheduledTaskItem.vue` | 일정 미등록 업무 항목 |
| `frontend/src/components/common/EmptyState.vue` | 빈 상태 표시 |
| `frontend/src/components/common/LoadingState.vue` | 로딩 상태 표시 |
| `frontend/src/components/common/ErrorState.vue` | 오류 상태 표시 |

Test:

| 파일 | 작업 내용 |
| --- | --- |
| `frontend/src/utils/dateFormatter.test.ts` | 날짜/D-day 표시 테스트 |
| `frontend/src/utils/calendarUtils.test.ts` | 캘린더 그리드 테스트 |
| `frontend/src/stores/myWorkStore.test.ts` | Store API 호출 상태 테스트 |
| `frontend/src/components/my-work/card/MyTaskRow.test.ts` | 일정 등록/미등록 표시 테스트 |
| `frontend/src/components/my-work/calendar/MonthlyCalendar.test.ts` | 일정 표시와 더보기 테스트 |

### 5.4 예상 작업 시간

| 작업 | 예상 시간 |
| --- | --- |
| API client, 타입, Pinia Store 구현 | 2.5h |
| 공통 레이아웃 및 헤더/요약 지표 구현 | 2h |
| 카드형 화면 구현 | 4h |
| 캘린더형 화면 구현 | 5h |
| 날짜별 팝업 및 일정 미등록 패널 구현 | 3h |
| Frontend 테스트 작성 | 3h |
| Backend 연동 및 Docker 통합 검증 | 2h |
| 합계 | 21.5h |

### 5.5 테스트 방법

- `npm run type-check`로 TypeScript 타입 검증
- `npm run test`로 Frontend 단위 테스트 실행
- `npm run build`로 Frontend 빌드 검증
- 카드형 화면 테스트
  - 요약 지표 표시
  - 진행 프로젝트 카드 표시
  - 내 할 일 목록 표시
  - 일정 미등록 업무의 `일정 미등록`, `-%` 표시
  - 대기 프로젝트 표시
  - 빈 상태 문구 표시
- 캘린더형 화면 테스트
  - 월 이동
  - 오늘 버튼
  - 일정 항목 표시
  - 지연 배지 표시
  - `+N 더보기` 표시
  - 날짜별 팝업 열기/닫기
  - 일정 미등록 업무 패널 표시
- 통합 테스트
  - `docker compose up`으로 전체 실행
  - `/my-work?view=card` 접속 확인
  - `/my-work?view=calendar&year=2026&month=3` 접속 확인
  - Backend API 응답과 화면 표시값 비교

### 5.6 위험 요소

| 위험 요소 | 영향 | 대응 |
| --- | --- | --- |
| 캘린더 UI가 예상보다 복잡함 | 일정 표시/더보기 구현 지연 | 프로토타입에서는 고정 행 높이와 최대 표시 개수를 단순화 |
| 반응형 화면 범위가 커짐 | 화면 구현 시간 증가 | 1차는 데스크톱 중심, 모바일은 깨지지 않는 수준으로 제한 |
| API 응답과 화면 타입 불일치 | 런타임 오류 | `frontend/src/types/myWork.ts`를 API Spec 기준으로 먼저 정의 |
| 팝업 접근성 구현 난이도 | 테스트 증가 | ESC 닫기와 기본 포커스 이동만 우선 구현 |
| 일정관리/WBS 상세 화면이 아직 없음 | 메뉴 클릭 시 목적지 부재 | 프로토타입에서는 라우트 생성 또는 placeholder 이동으로 처리 |

## 6. 전체 일정 요약

| Phase | 예상 시간 |
| --- | --- |
| Phase1 | 7h |
| Phase2 | 13.5h |
| Phase3 | 21.5h |
| 총합 | 42h |

## 7. Phase 간 완료 기준

| Phase | 완료 기준 |
| --- | --- |
| Phase1 | Docker 기반으로 PostgreSQL, Backend, Frontend가 기동되고 샘플 데이터가 준비됨 |
| Phase2 | 내 업무 REST API가 문서 기준 응답을 반환하고 Backend 테스트가 통과함 |
| Phase3 | 카드형/캘린더형 화면이 API와 연동되고 주요 사용자 시나리오가 화면에서 검증됨 |

## 8. 구현 범위 제한

프로토타입 개발 범위를 유지하기 위해 다음은 구현하지 않는다.

- 실제 로그인/권한 시스템
- 일정 저장 API
- 일정 수정/삭제
- 업무 생성/삭제
- 댓글/첨부파일/알림
- 운영 수준의 CI/CD
- 고도화된 모바일 전용 UI
