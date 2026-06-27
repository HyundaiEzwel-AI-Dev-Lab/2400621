# Worklog

## 2026-06-27

### 프로젝트 초기 구성 생성

- `frontend/` Vite 기반 Vue3 + TypeScript 프로젝트 골격 생성
- Nuxt 미사용 원칙 확인
- `backend/` Spring Boot 3.x + Java 21 + Gradle 프로젝트 골격 생성
- `backend/gradlew`는 표준 wrapper jar 없이 로컬 Gradle 또는 Docker Gradle 이미지에 위임하는 프로토타입용 스크립트로 생성
- Docker Gradle 이미지의 `linux/arm64` native library 오류를 회피하기 위해 Gradle 컨테이너 실행 플랫폼을 `linux/amd64`로 지정
- PostgreSQL 로컬 실행을 `docker-compose.yml`로 구성
- DB 초기화 SQL을 `db/init/001_schema.sql`에 작성
- 샘플 데이터를 `db/init/002_seed.sql`에 작성
- 실행 스크립트 생성
  - `scripts/dev-start.sh`
  - `scripts/test.sh`
  - `scripts/build.sh`
- 실행, 테스트, 빌드 방법을 `README.md`에 정리
- 문서 기준과 충돌하는 사항은 발견되지 않았으며, 적용 전제를 `assumptions.md`에 기록

### 범위

- 현재 작업은 프로젝트 초기 구성이다.
- 내 업무 상세 API, Backend Layer 구현, Frontend 상세 화면 구현은 후속 Phase에서 진행한다.

### 초기 구성 검증

요청된 검증 명령을 실제 실행했다.

#### 1. frontend 의존성 설치

- 실행 명령: `npm install`
- 위치: `frontend/`
- 결과: 성공
- 비고:
  - 52 packages 설치
  - npm audit 기준 취약점 2건 표시
    - moderate 1건
    - high 1건
  - 프로토타입 초기 구성 검증 단계이므로 임의 의존성 업그레이드나 `npm audit fix --force`는 수행하지 않음

#### 2. frontend build

- 최초 실행 명령: `npm run build`
- 위치: `frontend/`
- 최초 결과: 실패
- 실패 원인:
  - `src/router/index.ts`에서 `@/views/MyWorkView.vue` alias를 TypeScript가 해석하지 못함
  - Vite alias는 있었지만 `tsconfig.json`의 `baseUrl`, `paths` 설정이 누락됨
- 수정:
  - `frontend/tsconfig.json`에 `baseUrl`, `paths` 추가
  - `frontend/src/env.d.ts` 생성
- 재실행 결과: 성공
- 성공 확인:
  - `vue-tsc --noEmit` 통과
  - `vite build` 통과
  - `dist/` 산출물 생성

#### 3. backend test

- 실행 명령: `./gradlew test`
- 위치: `backend/`
- 최초 결과: 실패
- 실패 원인 1:
  - 로컬에 `gradle` 명령이 없음
  - `backend/gradlew`가 Docker Gradle 이미지를 사용하려 했으나 Docker CLI가 `/var/run/docker.sock`을 바라보고 있었고 해당 소켓이 없음
- 수정 1:
  - Docker Desktop 실행
  - `backend/gradlew`에서 `$HOME/.docker/run/docker.sock`이 있으면 `DOCKER_HOST`를 자동 지정하도록 수정
  - `scripts/dev-start.sh`에도 동일한 Docker socket 자동 지정 추가
- 실패 원인 2:
  - `gradle:8.10.2-jdk21`의 `linux/arm64` 실행에서 `libnative-platform.so` 초기화 실패
- 수정 2:
  - `backend/gradlew`에서 Gradle Docker 컨테이너를 `--platform linux/amd64`로 실행
  - `backend/Dockerfile`의 Gradle build stage도 `linux/amd64`로 지정
- 실패 원인 3:
  - Gradle native library 캐시 위치 권한 문제
- 수정 3:
  - `backend/gradlew`에서 `GRADLE_USER_HOME=/workspace/.gradle` 지정
- 재실행 결과: 성공
- 비고:
  - Gradle file watcher 경고가 출력되었으나 `BUILD SUCCESSFUL`

#### 4. backend build

- 실행 명령: `./gradlew build`
- 위치: `backend/`
- 결과: 성공
- 성공 확인:
  - `compileJava`
  - `bootJar`
  - `test`
  - `build`
  - 전체 `BUILD SUCCESSFUL`

#### 5. docker compose up -d

- 실행 명령: `DOCKER_HOST=unix:///Users/user/.docker/run/docker.sock docker compose up -d --build`
- 위치: repository root
- 결과: 성공
- 수행 내용:
  - `postgres:16` image pull
  - backend image build
  - frontend image build
  - Docker network 생성
  - Docker volume 생성
  - `h-pms-postgres` 기동
  - `h-pms-backend` 기동
  - `h-pms-frontend` 기동

#### 6. PostgreSQL 컨테이너 기동 확인

- 실행 명령: `docker compose ps`
- 결과:
  - `h-pms-postgres`: `Up`, `healthy`
  - `h-pms-backend`: `Up`
  - `h-pms-frontend`: `Up`
- 실행 명령: `docker compose exec -T postgres pg_isready -U hpms -d hpms`
- 결과: `accepting connections`
- 샘플 데이터 확인:
  - `project` 4건
  - `wbs_task` 6건
  - 접속 DB: `hpms`
  - 접속 사용자: `hpms`

#### 7. backend와 DB 연결 확인

- Backend 로그 확인 결과:
  - `HikariPool-1 - Starting...`
  - `HikariPool-1 - Added connection org.postgresql.jdbc.PgConnection`
  - `HikariPool-1 - Start completed.`
  - `Initialized JPA EntityManagerFactory`
  - `Tomcat started on port 8080`
- Backend 컨테이너 내부 health 확인:
  - 실행 명령: `docker compose exec -T backend curl -sS http://localhost:8080/actuator/health`
  - 결과: `{"status":"UP"}`
- Backend 컨테이너에서 `postgres` 서비스명 해석 확인:
  - 결과: `172.18.0.2 postgres`

#### 추가 관찰

- Codex 실행 환경에서 host의 `127.0.0.1:8080`, `127.0.0.1:5173`으로 직접 `curl`은 실패했다.
- Docker Compose 기준 포트 publish는 정상 확인됨:
  - backend: `0.0.0.0:8080->8080/tcp`
  - frontend: `0.0.0.0:5173->5173/tcp`
- 컨테이너 내부 health와 Docker Compose 상태가 정상이라 애플리케이션 기동 및 DB 연결은 정상으로 판단한다.

#### 최종 재검증

- `backend/Dockerfile`의 Gradle build stage 플랫폼 지정 방식을 build arg 형태로 정리한 뒤 재검증했다.
- 실행 명령: `DOCKER_HOST=unix:///Users/user/.docker/run/docker.sock docker compose up -d --build`
- 결과: 성공
- 최종 상태:
  - `h-pms-postgres`: `Up`, `healthy`
  - `h-pms-backend`: `Up`
  - `h-pms-frontend`: `Up`
- Backend health:
  - 실행 명령: `docker compose exec -T backend curl -sS http://localhost:8080/actuator/health`
  - 결과: `{"status":"UP"}`
- PostgreSQL readiness:
  - 실행 명령: `docker compose exec -T postgres pg_isready -U hpms -d hpms`
  - 결과: `accepting connections`

## 2026-06-27 Phase1 내 업무 구현

### 작업 범위

- 사용자 승인에 따라 Phase1 범위에서 내 업무 프로토타입의 최소 동작을 구현했다.
- Backend API를 먼저 구현한 뒤 Frontend를 API에 연동했다.
- 실제 로그인/권한, 일정 저장, WBS 상세 화면, 고도화된 컴포넌트 분리는 구현하지 않았다.

### Backend 구현

- 내 업무 REST API 구현
  - `GET /api/v1/my-work/summary`
  - `GET /api/v1/my-work/card`
  - `GET /api/v1/my-work/calendar`
  - `GET /api/v1/my-work/calendar/daily`
  - `GET /api/v1/my-work/unscheduled`
  - `GET /api/v1/my-work/tasks/{taskId}/actions`
- 공통 응답 DTO와 오류 응답 DTO 추가
- `X-User-Id` 헤더 기반 임시 사용자 조회 적용
- 기준일 미지정 시 `Asia/Seoul` 기준 서버 오늘을 사용하도록 처리
- 샘플 데이터 기반 조회 Repository 구현
- D-day, 금주 마감, 지연 여부 계산 로직 구현
- Backend 테스트 작성
  - D-day 계산 테스트
  - 내 업무 서비스 집계 테스트
  - REST Controller 응답 테스트

### Frontend 구현

- 내 업무 API client 추가
- API 응답 TypeScript 타입 추가
- Pinia store 추가
- 카드형 화면 API 연동
  - 요약 지표
  - 진행 프로젝트
  - 내 할 일
  - 대기 프로젝트
- 캘린더형 화면 API 연동
  - 월간 캘린더
  - 월 이동
  - 오늘 이동
  - 날짜별 업무 팝업
  - 일정 미등록 업무 패널
- 날짜, D-day, 공정률 표시 유틸 추가
- 프로토타입 검증 기준일은 샘플 데이터와 맞추기 위해 Frontend API 호출에서 `2026-03-22`로 고정했다.

### 관찰 이슈 기록

- `docs/issues.md` 생성
- 다음 항목은 Phase1에서 수정하지 않고 기록만 수행했다.
  - frontend npm audit 취약점 2건
  - Codex 실행 환경 host curl 실패 관찰 사항

### 실행 및 검증 결과

#### 1. backend 테스트 실행

- 실행 명령: `./gradlew test`
- 위치: `backend/`
- 결과: 성공
- 비고:
  - Gradle file watcher 경고가 출력되었으나 `BUILD SUCCESSFUL`

#### 2. backend 빌드 실행

- 실행 명령: `./gradlew build`
- 위치: `backend/`
- 결과: 성공
- 확인:
  - `compileJava`
  - `bootJar`
  - `test`
  - `build`

#### 3. frontend 테스트 또는 타입 체크 실행

- 실행 명령: `npm run type-check`
- 위치: `frontend/`
- 결과: 성공
- 확인:
  - `vue-tsc --noEmit` 통과

#### 4. frontend 빌드 실행

- 실행 명령: `npm run build`
- 위치: `frontend/`
- 결과: 성공
- 확인:
  - `vue-tsc --noEmit`
  - `vite build`
  - `dist/` 산출물 생성

#### 5. docker compose up -d --build 실행

- 실행 명령: `DOCKER_HOST=unix:///Users/user/.docker/run/docker.sock docker compose up -d --build`
- 위치: repository root
- 결과: 성공
- 확인:
  - backend image build 성공
  - frontend image build 성공
  - `h-pms-postgres` healthy
  - `h-pms-backend` started
  - `h-pms-frontend` started

#### 6. 컨테이너 상태 확인

- 실행 명령: `docker compose ps`
- 결과:
  - `h-pms-postgres`: `Up`, `healthy`, `5432` publish
  - `h-pms-backend`: `Up`, `8080` publish
  - `h-pms-frontend`: `Up`, `5173` publish
- Backend 로그 확인:
  - HikariPool connection 생성
  - JPA EntityManagerFactory 초기화
  - Tomcat 8080 기동

#### 7. API 동작 확인

- 실행 명령: `curl http://127.0.0.1:8080/actuator/health`
- 결과: `{"status":"UP"}`

- 실행 명령: `curl 'http://127.0.0.1:8080/api/v1/my-work/summary?baseDate=2026-03-22'`
- 결과 요약:
  - 진행 프로젝트 2건
  - 내 할 일 6건
  - 금주 마감 3건
  - 지연 3건
  - 대기 프로젝트 2건

- 실행 명령: `curl 'http://127.0.0.1:8080/api/v1/my-work/card?baseDate=2026-03-22'`
- 결과: 진행 프로젝트, 내 할 일, 대기 프로젝트 데이터 정상 반환

- 실행 명령: `curl 'http://127.0.0.1:8080/api/v1/my-work/calendar?year=2026&month=3&baseDate=2026-03-22'`
- 결과: 2026년 3월 일정 4건, 일정 미등록 업무 2건 정상 반환

- 실행 명령: `curl 'http://127.0.0.1:8080/api/v1/my-work/calendar/daily?date=2026-03-20&baseDate=2026-03-22'`
- 결과: 2026-03-20 업무 2건 정상 반환

- 실행 명령: `curl 'http://127.0.0.1:8080/api/v1/my-work/tasks/1/actions'`
- 결과: 일정관리, WBS 상세 액션 가능 응답 정상 반환

### 변경 파일 목록

Backend:

- `backend/src/main/java/com/hpms/prototype/api/GlobalExceptionHandler.java`
- `backend/src/main/java/com/hpms/prototype/api/MyWorkController.java`
- `backend/src/main/java/com/hpms/prototype/api/dto/ApiResponse.java`
- `backend/src/main/java/com/hpms/prototype/api/dto/CalendarEventResponse.java`
- `backend/src/main/java/com/hpms/prototype/api/dto/DailyTaskItemResponse.java`
- `backend/src/main/java/com/hpms/prototype/api/dto/DailyTaskResponse.java`
- `backend/src/main/java/com/hpms/prototype/api/dto/ErrorResponse.java`
- `backend/src/main/java/com/hpms/prototype/api/dto/MyTaskResponse.java`
- `backend/src/main/java/com/hpms/prototype/api/dto/MyWorkCalendarResponse.java`
- `backend/src/main/java/com/hpms/prototype/api/dto/MyWorkCardResponse.java`
- `backend/src/main/java/com/hpms/prototype/api/dto/MyWorkSummaryResponse.java`
- `backend/src/main/java/com/hpms/prototype/api/dto/ProjectCardResponse.java`
- `backend/src/main/java/com/hpms/prototype/api/dto/TaskActionResponse.java`
- `backend/src/main/java/com/hpms/prototype/api/dto/UnscheduledTaskItemResponse.java`
- `backend/src/main/java/com/hpms/prototype/api/dto/UnscheduledTaskResponse.java`
- `backend/src/main/java/com/hpms/prototype/api/dto/WaitingProjectResponse.java`
- `backend/src/main/java/com/hpms/prototype/application/DdayCalculator.java`
- `backend/src/main/java/com/hpms/prototype/application/DisplayStatus.java`
- `backend/src/main/java/com/hpms/prototype/application/MyWorkService.java`
- `backend/src/main/java/com/hpms/prototype/domain/ProjectStatus.java`
- `backend/src/main/java/com/hpms/prototype/domain/TaskStatus.java`
- `backend/src/main/java/com/hpms/prototype/repository/JdbcMyWorkRepository.java`
- `backend/src/main/java/com/hpms/prototype/repository/MyWorkRepository.java`
- `backend/src/main/java/com/hpms/prototype/repository/ProjectRow.java`
- `backend/src/main/java/com/hpms/prototype/repository/TaskRow.java`
- `backend/src/test/java/com/hpms/prototype/HpmsApplicationTests.java`
- `backend/src/test/java/com/hpms/prototype/api/MyWorkControllerTest.java`
- `backend/src/test/java/com/hpms/prototype/application/DdayCalculatorTest.java`
- `backend/src/test/java/com/hpms/prototype/application/MyWorkServiceTest.java`

Frontend:

- `frontend/src/api/httpClient.ts`
- `frontend/src/api/myWorkApi.ts`
- `frontend/src/stores/myWorkStore.ts`
- `frontend/src/types/myWork.ts`
- `frontend/src/utils/calendarUtils.ts`
- `frontend/src/utils/dateFormatter.ts`
- `frontend/src/views/MyWorkView.vue`
- `frontend/src/styles.css`

Docs:

- `docs/issues.md`
- `docs/worklog.md`

## 2026-06-27 Phase1 검증 리포트 작성

### 작업 범위

- Phase1 구현 완료 상태를 기준으로 검증 리포트를 작성했다.
- 코드 수정 없이 문서 검증과 문서화만 수행했다.
- 검증 기준 문서:
  - `docs/01_requirements.md`
  - `docs/02_screen_spec.md`
  - `docs/03_api_spec.md`
  - `docs/04_db_design.md`

### 생성/수정 문서

- 생성:
  - `docs/07_phase1_review.md`
- 수정:
  - `docs/issues.md`
  - `docs/worklog.md`

### 검증 요약

- 요구사항 충족 여부:
  - 핵심 내 업무 조회, 카드형/캘린더형 전환, 요약 지표, 주요 목록 표시는 가능
  - 좌우 이동, 더보기 메뉴, 안내 문구, 일부 지연 배지와 상세 이동은 미구현 또는 축소 구현
- 화면 요구사항 충족 여부:
  - 카드형/캘린더형/일별 팝업/일정 미등록 패널은 동작
  - 접근성 세부 정책과 PPTX 수준의 세부 UX는 남은 과제로 분류
- API 명세 충족 여부:
  - 6개 내 업무 API는 구현 및 호출 검증 완료
  - `scope`, paging, `/unscheduled` 전체 건수 등은 명세와 구현 차이가 있음
- DB 설계 충족 여부:
  - 테이블, 주요 컬럼, FK, 인덱스, 샘플 데이터는 설계에 부합
  - Repository 구현은 Spring Data JPA가 아니라 `JdbcTemplate` 기반이라 Phase2 전 결정 필요
- 테스트/빌드:
  - Phase1 완료 시점의 backend test/build, frontend type-check/build, Docker Compose, API 호출 검증 성공 결과를 리포트에 반영

### 추가 기록 이슈

- `ISSUE-003` API query parameter 일부 미구현
- `ISSUE-004` 화면 세부 UX 일부 미구현
- `ISSUE-005` Repository 구현 방식과 Spring Data JPA 설계 차이
- `ISSUE-006` 접근성 및 팝업 세부 정책 미구현

### Phase2 전 보완 필요 사항

- API 명세와 구현 차이 정합화
- Repository 전략 결정
- 화면 UX 우선순위 결정
- Frontend 기준일 고정값 처리 방식 결정
- 접근성 및 팝업 정책 보완 범위 결정

## 2026-06-27 Phase2 구현

### 작업 범위

- Phase1 검증 결과 기준으로 Phase2 범위만 구현했다.
- 작업 순서는 백엔드/API, 프론트, 테스트, 빌드 검증 순서로 진행했다.
- 실제 로그인/권한, 일정 저장/수정/삭제, WBS 상세 화면 본구현, 일정관리 화면 본구현, npm audit 임의 업그레이드는 수행하지 않았다.

### Phase1 잔여 이슈 반영 판단

- `ISSUE-001` frontend npm audit 취약점:
  - Phase2 기능 구현 직접 영향 없음
  - 사용자 지시에 따라 임의 업그레이드하지 않음
- `ISSUE-002` Codex 실행 환경 host curl 실패 관찰:
  - Phase2 검증에서 host API curl과 브라우저 화면 확인을 함께 수행
  - 이번 검증에서는 host API 호출 및 화면 접속 정상
- `ISSUE-003` API query parameter 일부 미구현:
  - Phase2 핵심 보완 대상으로 반영
- `ISSUE-004` 화면 세부 UX 일부 미구현:
  - Phase2에서 안내 문구, 더보기 메뉴, 지연 표시, 팝업 건수 등 최소 UX 보완
- `ISSUE-005` Repository 구현 방식과 Spring Data JPA 설계 차이:
  - Phase2에서 JPA Entity/Repository 기반으로 전환
- `ISSUE-006` 접근성 및 팝업 세부 정책 미구현:
  - ESC 닫기와 지연 텍스트 표시 일부 보완
  - 포커스 트랩 등 고도화 항목은 후속 범위로 유지

### Backend/API 구현

- DB 설계 기준 JPA Entity 추가
  - `Department`
  - `UserAccount`
  - `Project`
  - `ProjectAssignment`
  - `Requirement`
  - `WbsTask`
  - `TaskAssignment`
  - `TaskSchedule`
- Spring Data JPA Repository 및 projection 추가
  - 진행 프로젝트 조회/건수
  - 대기 프로젝트 조회/건수
  - 내 할 일 조회
  - 캘린더 일정 조회
  - 날짜별 업무 조회
  - 일정 미등록 업무 조회/전체 건수
  - 배정 업무 존재 여부
- 기존 `JdbcMyWorkRepository` 제거
- API query parameter 보완
  - `scope`
  - `projectPage`
  - `projectSize`
  - `waitingPage`
  - `waitingSize`
  - `page`
  - `size`
- `scope=INTEGRATED`만 허용하고 미지원 scope는 `400 MY_WORK_BAD_REQUEST`로 응답하도록 처리
- page, size 검증 추가
- `/unscheduled.totalCount`를 전체 일정 미등록 업무 수로 보정
- `user_account.active=true` 조회 조건 반영

### Frontend 구현

- API 호출 시 `scope`와 paging parameter 전달
- 기준일은 `VITE_MY_WORK_BASE_DATE` 환경값으로 override 가능하게 하고, 기본값은 샘플 데이터 검증용 `2026-03-22`로 유지
- `통합` 라벨 표시
- 카드형 하단 안내 문구 추가
- 업무 행 `...` 더보기 메뉴 추가
- 일정관리/WBS 상세 메뉴 링크 추가
- 캘린더 일정 항목에 기간, 프로젝트명, 지연 텍스트 표시 보완
- 날짜별 팝업 제목에 업무 건수 표시
- 날짜별 팝업 ESC 닫기 구현
- Docker 환경에서 Frontend Vite proxy가 `backend:8080`을 바라보도록 `VITE_API_BASE_URL` 연동

### 실패 및 수정

- 화면 검증 중 `/my-work` 카드형 화면에서 `API 요청 실패 (500)` 표시
- 원인:
  - Docker Compose 환경의 Frontend 컨테이너 Vite proxy가 `http://localhost:8080`을 바라봄
  - 컨테이너 내부에서 `localhost`는 Frontend 컨테이너 자신이므로 Backend로 연결되지 않음
- 수정:
  - `frontend/vite.config.ts`에서 `process.env.VITE_API_BASE_URL`을 proxy target으로 사용
  - `docker-compose.yml`의 Frontend 환경값을 `http://backend:8080`으로 변경
- 재검증:
  - Frontend type-check 성공
  - Frontend build 성공
  - Docker Compose 재기동 성공
  - 브라우저 화면 API 오류 해소 확인

### 테스트 및 빌드 검증

#### 1. backend test

- 실행 명령: `./gradlew test`
- 위치: `backend/`
- 결과: 성공
- 확인:
  - Service 테스트 통과
  - Controller 테스트 통과
  - scope 오류 응답 테스트 추가 통과
  - 일정 미등록 전체 건수 테스트 추가 통과

#### 2. backend build

- 실행 명령: `./gradlew build`
- 위치: `backend/`
- 결과: 성공

#### 3. frontend test 또는 typecheck

- 실행 명령: `npm run type-check`
- 위치: `frontend/`
- 결과: 성공

#### 4. frontend build

- 실행 명령: `npm run build`
- 위치: `frontend/`
- 결과: 성공
- 확인:
  - `vue-tsc --noEmit`
  - `vite build`

#### 5. docker compose up -d --build

- 실행 명령: `DOCKER_HOST=unix:///Users/user/.docker/run/docker.sock docker compose up -d --build`
- 위치: repository root
- 결과: 성공

#### 6. API 동작 확인

- 컨테이너 상태:
  - `h-pms-postgres`: `Up`, `healthy`
  - `h-pms-backend`: `Up`, `8080` publish
  - `h-pms-frontend`: `Up`, `5173` publish
- 확인 API:
  - `GET /api/v1/my-work/summary?scope=INTEGRATED&baseDate=2026-03-22`
    - 진행 프로젝트 2건
    - 내 할 일 6건
    - 금주 마감 3건
    - 지연 3건
    - 대기 프로젝트 2건
  - `GET /api/v1/my-work/card?scope=INTEGRATED&baseDate=2026-03-22&projectPage=0&projectSize=1&waitingPage=0&waitingSize=1`
    - 진행 프로젝트 1건, 대기 프로젝트 1건으로 paging 반영 확인
  - `GET /api/v1/my-work/unscheduled?scope=INTEGRATED&page=0&size=1`
    - `totalCount=2`
    - `items` 1건으로 전체 건수와 페이지 크기 분리 확인
  - `GET /api/v1/my-work/calendar?scope=INTEGRATED&year=2026&month=3&baseDate=2026-03-22`
    - 일정 4건, 일정 미등록 업무 2건 반환
  - `GET /api/v1/my-work/calendar/daily?scope=INTEGRATED&date=2026-03-20&baseDate=2026-03-22`
    - 2026-03-20 업무 2건 반환
  - `GET /api/v1/my-work/tasks/1/actions`
    - 일정관리, WBS 상세 액션 가능 반환
  - `GET /api/v1/my-work/summary?scope=DEPARTMENT&baseDate=2026-03-22`
    - `400 MY_WORK_BAD_REQUEST` 반환

#### 7. 화면 동작 확인

- 브라우저로 `http://127.0.0.1:5173/my-work` 접속 확인
- 카드형 화면 확인:
  - API 오류 없음
  - 요약 지표 표시
  - 진행 프로젝트 표시
  - 내 할 일 표시
  - 일정 미등록 업무 `일정 미등록 / -%` 표시
  - 대기 프로젝트 표시
  - 안내 문구 표시
  - 업무 행 `...` 더보기 버튼 표시
- 더보기 메뉴 확인:
  - `일정관리` 메뉴 표시
  - `WBS 상세` 메뉴 표시
- 캘린더형 화면 확인:
  - `view=calendar` 전환
  - `2026년 3월` 표시
  - 일정 항목 표시
  - 지연 텍스트 표시
  - 일정 미등록 업무 패널 표시
- 날짜별 팝업 확인:
  - 2026-03-20 날짜 선택
  - `2026-03-20 내 업무 (2건)` 표시
  - `단위 테스트`, `DEV 테스트` 표시
  - ESC 키로 팝업 닫힘 확인

### 변경 파일 목록

Backend:

- `backend/src/main/java/com/hpms/prototype/api/GlobalExceptionHandler.java`
- `backend/src/main/java/com/hpms/prototype/api/MyWorkController.java`
- `backend/src/main/java/com/hpms/prototype/application/InvalidMyWorkRequestException.java`
- `backend/src/main/java/com/hpms/prototype/application/MyWorkService.java`
- `backend/src/main/java/com/hpms/prototype/domain/Department.java`
- `backend/src/main/java/com/hpms/prototype/domain/UserAccount.java`
- `backend/src/main/java/com/hpms/prototype/domain/Project.java`
- `backend/src/main/java/com/hpms/prototype/domain/ProjectAssignment.java`
- `backend/src/main/java/com/hpms/prototype/domain/Requirement.java`
- `backend/src/main/java/com/hpms/prototype/domain/WbsTask.java`
- `backend/src/main/java/com/hpms/prototype/domain/TaskAssignment.java`
- `backend/src/main/java/com/hpms/prototype/domain/TaskSchedule.java`
- `backend/src/main/java/com/hpms/prototype/repository/JpaMyWorkRepository.java`
- `backend/src/main/java/com/hpms/prototype/repository/MyWorkRepository.java`
- `backend/src/main/java/com/hpms/prototype/repository/jpa/ProjectJpaRepository.java`
- `backend/src/main/java/com/hpms/prototype/repository/jpa/ProjectProjection.java`
- `backend/src/main/java/com/hpms/prototype/repository/jpa/TaskJpaRepository.java`
- `backend/src/main/java/com/hpms/prototype/repository/jpa/TaskProjection.java`
- `backend/src/main/java/com/hpms/prototype/repository/JdbcMyWorkRepository.java` 제거
- `backend/src/test/java/com/hpms/prototype/api/MyWorkControllerTest.java`
- `backend/src/test/java/com/hpms/prototype/application/MyWorkServiceTest.java`

Frontend:

- `frontend/src/api/myWorkApi.ts`
- `frontend/src/views/MyWorkView.vue`
- `frontend/src/styles.css`
- `frontend/vite.config.ts`

Infra:

- `docker-compose.yml`

Docs:

- `docs/issues.md`
- `docs/worklog.md`

### 남은 이슈

- `ISSUE-001` npm audit 취약점은 여전히 미조치
- `ISSUE-002` host curl 관찰 이슈는 이번 검증에서는 재현되지 않았지만 환경 관찰 대상으로 유지
- `ISSUE-004` 중 진행/대기 프로젝트 좌우 이동은 아직 미구현
- `ISSUE-006` 중 포커스 트랩과 긴 텍스트 전체 확인 수단은 아직 미구현

## 2026-06-27 Phase2 검증 리포트 작성

### 작업 범위

- Phase2 구현 완료 상태 기준 검증 리포트를 작성했다.
- 코드 수정 없이 문서 검증과 문서화만 수행했다.
- 검증 항목:
  - Phase2 요구사항 충족 여부
  - Backend API 변경 내역
  - Frontend 화면 변경 내역
  - DB/Repository 변경 내역
  - 테스트/빌드 결과
  - Phase2 범위 외 변경 여부
  - Phase3 진행 전 해결 필요 이슈
  - 남은 이슈의 우선순위

### 생성/수정 문서

- 생성:
  - `docs/08_phase2_review.md`
- 수정:
  - `docs/issues.md`
  - `docs/worklog.md`

### 검증 결론

- Phase2 핵심 목표는 충족했다.
  - API query/paging/totalCount 정합화
  - JPA Repository 기반 전환
  - active 사용자 조건 반영
  - Docker Frontend proxy 오류 수정
  - 카드형/캘린더형 최소 UX 보완
- Phase2 범위 외 변경은 발견되지 않았다.
- 테스트/빌드/통합 검증은 Phase2 완료 시점 결과를 기준으로 모두 성공으로 기록했다.

### 이슈 업데이트

- `ISSUE-001`: P2, 미해결
- `ISSUE-002`: P3, 관찰 유지
- `ISSUE-003`: 완료
- `ISSUE-004`: P1/P2, 부분 해결
- `ISSUE-005`: 완료
- `ISSUE-006`: P2, 부분 해결
- `ISSUE-007`: P1, 신규. Frontend 테스트 자동화 부재

### Phase3 전 우선 검토 사항

- 일정관리/WBS 상세/일정 등록 대상 화면을 placeholder로 둘지 실제 화면으로 구현할지 결정
- Frontend 테스트 도구 도입 여부 승인
- 진행/대기 프로젝트 좌우 이동 구현 여부 결정
- 팝업 포커스 트랩 및 긴 텍스트 표시 정책 결정
- npm audit 취약점 처리 여부와 시점 결정

## 2026-06-27 Phase3 구현

### 작업 범위

- Phase3 계획 승인에 따라 Phase3 범위만 구현했다.
- 사용자 추가 지시에 따라 Frontend 테스트 도구를 도입했다.
- Backend API는 기존 명세를 유지하고, 액션 조회 성공 케이스 테스트를 보강했다.
- Frontend는 Phase2 잔여 UX 항목 중 프로토타입 검증에 필요한 화면 흐름, 캐러셀, 접근성 동작을 보완했다.

### Backend/API 작업

- `GET /api/v1/my-work/tasks/{taskId}/actions` 성공 응답 Controller 테스트를 추가했다.
- 신규 API 또는 DB 스키마 변경은 수행하지 않았다.

### Frontend 작업

- Vitest를 Frontend 테스트 도구로 도입했다.
- `npm run test` 스크립트를 추가했다.
- 날짜 포맷 유틸 테스트를 추가했다.
- 캘린더 유틸 테스트를 추가했다.
- 일정관리 placeholder 화면을 추가했다.
- WBS 업무 상세 placeholder 화면을 추가했다.
- 내 업무 화면의 업무 액션 메뉴와 일정 미등록 업무 링크가 placeholder 화면으로 이동하도록 연결했다.
- 진행 프로젝트와 대기 프로젝트 좌우 이동 버튼을 추가했다.
- 긴 프로젝트명/업무명의 기본 확인 수단으로 주요 텍스트에 `title` 속성을 추가했다.
- 날짜별 팝업에 포커스 트랩을 추가했다.
- ESC 닫기와 팝업 닫힘 후 이전 포커스 복귀 동작을 재확인했다.

### 테스트 및 빌드 검증

#### 1. backend test

- 실행 명령: `./gradlew test`
- 위치: `backend/`
- 결과: 성공
- 확인:
  - Service 테스트 통과
  - Controller 테스트 통과
  - 업무 액션 조회 성공 케이스 통과

#### 2. backend build

- 실행 명령: `./gradlew build`
- 위치: `backend/`
- 결과: 성공

#### 3. frontend type-check

- 실행 명령: `npm run type-check`
- 위치: `frontend/`
- 결과: 성공

#### 4. frontend test

- 실행 명령: `npm run test`
- 위치: `frontend/`
- 결과: 성공
- 확인:
  - `dateFormatter` 테스트 통과
  - `calendarUtils` 테스트 통과
  - 총 2개 파일, 6개 테스트 통과

#### 5. frontend build

- 실행 명령: `npm run build`
- 위치: `frontend/`
- 결과: 성공
- 확인:
  - `vue-tsc --noEmit`
  - `vite build`

#### 6. docker compose up -d --build

- 실행 명령: `DOCKER_HOST=unix:///Users/user/.docker/run/docker.sock docker compose up -d --build`
- 위치: repository root
- 결과: 성공
- 컨테이너 상태:
  - `h-pms-postgres`: `Up`, `healthy`
  - `h-pms-backend`: `Up`, `8080` publish
  - `h-pms-frontend`: `Up`, `5173` publish

#### 7. API 동작 확인

- `GET /api/v1/my-work/summary?scope=INTEGRATED&baseDate=2026-03-22`
  - 진행 프로젝트 2건
  - 내 할 일 6건
  - 금주 마감 3건
  - 지연 3건
  - 대기 프로젝트 2건
- `GET /api/v1/my-work/tasks/1/actions`
  - `scheduleManagement=true`
  - `wbsDetail=true`
- `GET /api/v1/my-work/calendar?scope=INTEGRATED&year=2026&month=3&baseDate=2026-03-22`
  - 일정 4건
  - 일정 미등록 업무 2건

#### 8. 화면 동작 확인

- 브라우저로 `http://127.0.0.1:5173/my-work?view=calendar` 접속 확인
- 캘린더형 화면:
  - API 오류 없음
  - 캘린더 표시 확인
  - 일정 미등록 업무 패널 표시 확인
  - 일정 미등록 업무 `일정 등록` 링크가 `/schedule/tasks/{taskId}`로 연결됨을 확인
- 카드형 화면:
  - `카드형` 전환 시 URL이 `/my-work?view=card`로 변경됨을 확인
  - 진행 프로젝트 다음 버튼으로 두 번째 프로젝트 표시 확인
  - 대기 프로젝트 다음 버튼으로 두 번째 대기 프로젝트 표시 확인
  - 업무 행 더보기 메뉴의 `일정관리` 링크가 `/schedule/tasks/{taskId}`로 연결됨을 확인
  - 업무 행 더보기 메뉴의 `WBS 상세` 링크가 `/projects/{projectId}/wbs/tasks/{taskId}`로 연결됨을 확인
- Placeholder 화면:
  - 일정관리 placeholder 화면 진입 확인
  - WBS 업무 상세 placeholder 화면 진입 확인

#### 9. 접근성 관련 동작 확인

- 날짜별 팝업 열림 확인:
  - 2026-03-20 날짜 선택
  - `2026-03-20 내 업무 (2건)` 표시
  - 팝업이 `role="dialog"`로 표시됨을 확인
- ESC 닫기:
  - ESC 입력 시 날짜별 팝업 닫힘 확인
- 포커스 트랩:
  - 팝업 열린 직후 포커스가 팝업으로 이동함을 확인
  - Tab 입력 시 포커스가 팝업 내부에 머무름을 확인
  - Shift+Tab 입력 시 포커스가 팝업 내부에 머무름을 확인
- 키보드 이동:
  - 팝업 닫힘 후 이전 날짜 셀로 포커스가 복귀함을 확인

### 관찰 및 이슈 업데이트

- Vitest 도입 후 `npm install` 기준 Frontend audit 취약점이 3건으로 관찰됐다.
  - moderate 1건
  - high 1건
  - critical 1건
- 사용자 지시에 따라 임의 업그레이드나 `npm audit fix --force`는 수행하지 않았다.
- `docs/issues.md`의 `ISSUE-001`을 3건 기준으로 업데이트했다.
- Phase2 잔여 항목이었던 화면 흐름, 캐러셀, 포커스 트랩, 긴 텍스트 확인 수단은 Phase3 해결로 정리했다.

### 변경 파일 목록

Backend:

- `backend/src/test/java/com/hpms/prototype/api/MyWorkControllerTest.java`

Frontend:

- `frontend/package.json`
- `frontend/package-lock.json`
- `frontend/src/router/index.ts`
- `frontend/src/styles.css`
- `frontend/src/utils/calendarUtils.test.ts`
- `frontend/src/utils/dateFormatter.test.ts`
- `frontend/src/views/MyWorkView.vue`
- `frontend/src/views/ScheduleTaskView.vue`
- `frontend/src/views/WbsTaskDetailView.vue`

Docs:

- `docs/issues.md`
- `docs/worklog.md`

### 남은 이슈

- `ISSUE-001` Frontend npm audit 취약점 3건은 미해결 상태다.
- `ISSUE-002` Codex 실행 환경 host curl 실패 관찰 이슈는 현재 검증에서는 재현되지 않았지만 관찰 대상으로 유지한다.
- 실제 일정 저장/수정/삭제와 WBS 상세 본기능은 프로토타입 Phase3 범위 밖으로 유지한다.

## 2026-06-27 최종 기능 검증 리포트 작성

### 작업 범위

- Phase3 완료 상태를 기준으로 최종 기능 검증 리포트를 작성했다.
- 코드 수정 없이 검증 결과 문서화만 수행했다.
- Phase1~3 전체 요구사항, 화면, API, DB 연동, 테스트/빌드 결과, 남은 이슈, PoC 완료/미완료 항목, 본 개발 전 보완 사항을 정리했다.

### 생성/수정 문서

- 생성:
  - `docs/09_final_function_review.md`
- 수정:
  - `docs/issues.md`
  - `docs/worklog.md`

### 최종 검증 결론

- Phase1~3에서 정의한 PoC 핵심 범위는 충족했다.
- Backend, Frontend, PostgreSQL, Docker Compose 기반 통합 실행이 가능하다.
- 카드형/캘린더형 내 업무 주요 조회 흐름은 API 연동 상태로 동작한다.
- 날짜별 팝업의 ESC 닫기, 포커스 트랩, 키보드 이동은 검증 완료했다.
- 일정관리/WBS 상세는 placeholder 화면 이동까지 PoC 범위로 완료했다.

### 남은 이슈 정리

- `ISSUE-001`: Frontend npm audit 취약점 3건은 미해결. 본 개발 또는 외부 배포 전 보안 검토 필요
- `ISSUE-002`: Codex 실행 환경 host curl 실패 관찰 이슈는 Phase2~3에서는 재현되지 않았으나 관찰 유지
- `ISSUE-003`~`ISSUE-007`: Phase1~3 범위에서 해결 완료

### 본 개발 전 보완 필요 사항

- Frontend 의존성 보안 점검
- 인증/권한 정책 확정 및 Spring Security 전환
- 일정관리 등록/수정/삭제 본기능 설계
- WBS 상세 본기능 설계
- Frontend Store/컴포넌트/E2E 테스트 확대
- 운영 배포, DB index, query plan, 장애 대응 정책 보강

## 2026-06-27 최종 기능 검증 리포트 갱신

### 작업 범위

- Phase3 이후 추가 보완까지 포함한 현재 완료 상태 기준으로 최종 기능 검증 리포트를 갱신했다.
- 코드 수정 없이 검증과 문서화만 수행했다.
- 갱신 대상:
  - `docs/09_final_function_review.md`
  - `docs/issues.md`
  - `docs/worklog.md`

### 현재 상태 기준 주요 보완 반영

- 캘린더형:
  - 동일 프로젝트 연속 일정의 기간 막대 표시
  - 같은 날짜 다른 프로젝트의 별도 라인 표시
  - 프로젝트 막대 전용 상세 팝업
  - 날짜 숫자 버튼 포커스 범위 보정
- 카드형:
  - 내 할 일 영역 반응형 카드 레이아웃
  - 넓은 화면/좁은 화면 업무 액션 메뉴 표시 보정
- Placeholder:
  - 일정관리/WBS 상세 화면 상단 버튼을 `뒤로가기`로 변경
  - 내 업무/일정관리/WBS 상세 breadcrumb 앞쪽 `>` 기호 제거

### 검증 결과

Backend:

- 실행 명령: `./gradlew test`
- 위치: `backend/`
- 결과: 성공
- 실행 명령: `./gradlew build`
- 위치: `backend/`
- 결과: 성공

Frontend:

- 실행 명령: `npm run test`
- 위치: `frontend/`
- 결과: 성공
- 확인:
  - `dateFormatter` 테스트 통과
  - `calendarUtils` 테스트 통과
  - 총 2개 파일, 10개 테스트 통과
- 실행 명령: `npm run build`
- 위치: `frontend/`
- 결과: 성공
- 확인:
  - `vue-tsc --noEmit`
  - `vite build`

Docker/DB:

- 실행 명령: `DOCKER_HOST=unix:///Users/user/.docker/run/docker.sock docker compose ps`
- 결과:
  - `h-pms-postgres`: `Up`, `healthy`
  - `h-pms-backend`: `Up`, `8080` publish
  - `h-pms-frontend`: `Up`, `5173` publish

API:

- `GET /api/v1/my-work/summary?scope=INTEGRATED&baseDate=2026-03-22`
  - 진행 프로젝트 2건
  - 내 할 일 6건
  - 금주 마감 3건
  - 지연 3건
  - 대기 프로젝트 2건
- `GET /api/v1/my-work/card?scope=INTEGRATED&baseDate=2026-03-22&projectPage=0&projectSize=1&waitingPage=0&waitingSize=1`
  - 진행 프로젝트 page size 1 반영
  - 내 할 일 6건 반환
  - 대기 프로젝트 page size 1 반영
- `GET /api/v1/my-work/calendar?scope=INTEGRATED&year=2026&month=3&baseDate=2026-03-22`
  - 2026년 3월 일정 4건 반환
  - 일정 미등록 업무 2건 반환
  - 2026-03-20에 프로젝트 1/2 일정이 함께 반환됨
- `GET /api/v1/my-work/calendar/daily?scope=INTEGRATED&date=2026-03-20&baseDate=2026-03-22`
  - 2026-03-20 업무 3건 반환
- `GET /api/v1/my-work/unscheduled?scope=INTEGRATED&page=0&size=1`
  - `totalCount=2`
  - `items` 1건 반환
- `GET /api/v1/my-work/tasks/1/actions`
  - 일정관리, WBS 상세 액션 가능 반환
- `GET /api/v1/my-work/summary?scope=DEPARTMENT&baseDate=2026-03-22`
  - `400 MY_WORK_BAD_REQUEST` 반환

### 이슈 업데이트

- `ISSUE-001`: 미해결 유지. Frontend npm audit 취약점 3건
- `ISSUE-002`: 관찰 유지. 최종 검증에서는 host API/화면 접속 정상
- `ISSUE-003`~`ISSUE-007`: 해결 유지
- `ISSUE-008`: 신규 정리. Phase3 이후 카드형/캘린더형 UI 보완 해결

### 최종 판정

- Phase1~3 및 후속 UI 보완 범위의 PoC 핵심 기능은 완료 상태로 판단한다.
- 본 개발 전 보완 필요 사항은 보안 취약점 정비, 인증/권한 전환, 일정관리/WBS 상세 본기능 설계, 테스트 자동화 확대다.
