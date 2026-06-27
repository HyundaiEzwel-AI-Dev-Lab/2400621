# H-PMS 내 업무 프로토타입 바이브코딩 개발 방법론

## 1. 전체 진행 흐름

이번 H-PMS 내 업무 프로토타입은 "문서화된 기준을 먼저 만들고, Codex와 짧은 검증 루프를 반복하면서 구현을 확장하는 방식"으로 진행했다. 단순히 코드를 먼저 작성한 것이 아니라, PPTX 화면 분석에서 출발해 요구사항, 설계, 구현 계획, Phase별 구현, 검증 리포트, 후속 UI 보완까지 이어지는 흐름을 만들었다.

### 1.1 기획

시작점은 업로드된 PPTX의 `내 업무` 화면 22~26페이지 분석이었다. 이 단계에서 구현하지 않고 요구사항 문서만 작성하도록 범위를 제한했다.

작성 산출물:

- `docs/01_requirements.md`

정리한 항목:

- 기능 목록
- 사용자 시나리오
- 화면 정의
- 데이터 정의
- 정책 정의
- 가정 사항
- 미확정 사항

이 단계의 핵심은 사용자가 "구현하지 말고 분석만 수행"하도록 명확히 통제했다는 점이다. Codex는 PPTX 화면을 기준으로 기능과 화면, 데이터, 정책을 분리했고, 이후 모든 설계와 구현은 이 요구사항 문서를 기준으로 진행했다.

### 1.2 설계

요구사항 문서를 기준으로 구현 전 설계 문서를 생성했다.

작성 산출물:

- `docs/02_screen_spec.md`
- `docs/03_api_spec.md`
- `docs/04_db_design.md`
- `docs/05_component_design.md`

설계 후에는 즉시 구현하지 않고 별도 검토 단계를 거쳤다. 검토 항목은 요구사항 누락, API 설계 오류, 상태값 정의 오류, 화면 간 데이터 전달 문제, 테스트 난이도, 확장 포인트였다.

이 검토 결과를 바탕으로 프로토타입 범위에서 필요한 개선만 반영했다. 확장성이나 누락 기능을 모두 구현 대상으로 삼지 않고, PoC 검증에 필요한 기준을 임의로 정해 좁혔다.

### 1.3 초기 구성

초기 구성 전에는 반드시 작업 계획을 먼저 제시하고, 사용자 승인 후 파일을 생성했다. 기술 스택은 `docs/00_project_context.md`와 사용자 지시를 기준으로 고정했다.

적용 기술 스택:

- Frontend: Vue3 + Vite + TypeScript
- Backend: Spring Boot 3 + Java 21 + Gradle
- Database: PostgreSQL
- Local DB: Docker Compose
- API: REST API
- Architecture: Layered Architecture

생성 산출물:

- `frontend/`
- `backend/`
- `db/init/001_schema.sql`
- `db/init/002_seed.sql`
- `docker-compose.yml`
- `scripts/dev-start.sh`
- `scripts/test.sh`
- `scripts/build.sh`
- `README.md`
- `assumptions.md`

초기 구성 후 실제 검증 명령을 실행했다.

- frontend 의존성 설치
- frontend build
- backend test
- backend build
- docker compose up -d
- PostgreSQL 컨테이너 기동 확인
- backend와 DB 연결 확인

이 단계에서 발생한 실패도 문서화했다. 예를 들어 Frontend alias 설정 누락으로 `npm run build`가 실패했고, `tsconfig.json`의 `baseUrl`, `paths`와 `env.d.ts`를 보완해 재검증했다. Backend는 로컬 Gradle 부재, Docker socket 경로, Gradle Docker 이미지의 arm64 native library 문제, Gradle cache 권한 문제를 순차적으로 해결했다.

### 1.4 개발

개발은 Phase1, Phase2, Phase3으로 나누어 진행했다.

Phase1:

- Backend API 최소 구현
- Frontend 카드형/캘린더형 API 연동
- 요약 지표, 진행 프로젝트, 내 할 일, 대기 프로젝트, 캘린더, 일정 미등록 업무 표시
- `docs/issues.md` 생성

Phase2:

- API query parameter 보완
- paging, `scope`, `totalCount` 정합화
- JDBC Repository에서 JPA Entity/Repository 기반으로 전환
- Frontend 안내 문구, 더보기 메뉴, 지연 표시, 날짜별 팝업 건수 보완
- Docker 환경의 Frontend proxy 오류 수정

Phase3:

- Frontend 테스트 도구 Vitest 도입
- 일정관리/WBS 상세 placeholder 화면 추가
- 진행/대기 프로젝트 좌우 이동
- 긴 텍스트 `title` 보완
- 날짜별 팝업 ESC 닫기, 포커스 트랩, 이전 포커스 복귀 확인
- Backend 업무 액션 조회 테스트 보강

Phase3 이후 후속 UI 보완:

- 캘린더형 동일 프로젝트 기간 막대 표시
- 같은 날짜 다른 프로젝트 별도 라인 표시
- 프로젝트 막대 클릭 시 프로젝트 상세 팝업 표시
- 날짜 숫자 포커스 범위 보정
- 카드형 반응형 레이아웃 개선
- 넓은 화면에서 내 할 일 액션 메뉴가 표시되지 않던 문제 수정
- 일정관리/WBS 상세 placeholder의 `내업무` 버튼을 `뒤로가기`로 변경
- breadcrumb 앞쪽 `>` 기호 제거

### 1.5 테스트

테스트는 각 Phase 완료 후 실제 명령을 실행하고 결과를 `docs/worklog.md`에 기록하는 방식으로 수행했다.

반복 검증 명령:

- `backend`에서 `./gradlew test`
- `backend`에서 `./gradlew build`
- `frontend`에서 `npm run type-check`
- `frontend`에서 `npm run test`
- `frontend`에서 `npm run build`
- repository root에서 `docker compose up -d --build`
- API `curl` 확인
- 브라우저 화면 확인

Phase3 완료 후에는 접근성 관련 동작도 확인했다.

- ESC 닫기
- 포커스 트랩
- 키보드 이동
- 팝업 닫힘 후 이전 포커스 복귀

### 1.6 배포

이번 프로젝트의 배포는 운영 배포가 아니라 Docker Compose 기반 로컬 통합 실행 검증으로 정의했다.

검증한 실행 단위:

- `h-pms-postgres`
- `h-pms-backend`
- `h-pms-frontend`

검증 기준:

- PostgreSQL 컨테이너 `healthy`
- Backend 8080 publish
- Frontend 5173 publish
- Backend health `UP`
- Backend와 PostgreSQL 연결 로그 확인
- API 정상 응답
- 브라우저 화면 정상 표시

이 배포 단계는 실제 운영 배포 자동화가 아니라, 본 개발 전 "로컬에서 전체 사이클을 재현할 수 있는가"를 확인하는 PoC 목적이었다.

### 1.7 회고

최종 검증 리포트는 Phase3 이후 추가 UI 보완까지 포함해 갱신했다.

작성 산출물:

- `docs/09_final_function_review.md`
- `docs/issues.md`
- `docs/worklog.md`

최종 판단:

- Phase1~3 및 후속 UI 보완 범위의 PoC 핵심 기능은 완료
- Backend, Frontend, PostgreSQL, Docker Compose 통합 실행 가능
- 카드형/캘린더형 내 업무 조회 흐름은 API 연동 상태로 동작
- 남은 이슈는 보안 취약점 정비, 인증/권한 전환, 일정관리/WBS 상세 본기능, 테스트 자동화 확대

## 2. Codex 활용 방식

이번 프로젝트에서 Codex는 단순 코드 생성기가 아니라 다음 역할을 수행했다.

### 2.1 요구사항 분석자

PPTX 화면을 기준으로 내 업무 기능을 기능, 시나리오, 화면, 데이터, 정책으로 분리했다. 이 단계에서는 구현을 금지하고 분석 산출물만 만들었다.

### 2.2 설계 보조자

요구사항 문서를 기준으로 화면, API, DB, 컴포넌트 설계를 작성했다. 이후 설계 검토를 통해 누락, 오류, 상태값, 화면 간 데이터 전달, 테스트 난이도, 확장 포인트를 점검했다.

### 2.3 구현 계획자

`docs/06_implementation_plan.md`에 Phase1, Phase2, Phase3 단위의 수정 파일, 생성 파일, 예상 작업 시간, 테스트 방법, 위험 요소를 정리했다. 사용자는 계획을 승인하거나 조정했고, Codex는 승인 전 구현하지 않았다.

### 2.4 구현 에이전트

승인된 범위 안에서 Backend API, Frontend 화면, DB 초기 데이터, Docker 설정, 테스트 코드를 구현했다. Phase 범위 외 작업은 하지 않고, 임의 의존성 업그레이드나 기술 스택 변경도 하지 않았다.

### 2.5 검증 실행자

테스트와 빌드 명령을 실제로 실행하고 실패 원인을 분석했다. 실패가 발생하면 수정 후 재검증했다.

### 2.6 문서화 담당자

각 단계의 결과, 실패 원인, 수정 내용, 검증 결과, 남은 이슈를 `docs/worklog.md`, `docs/issues.md`, Phase별 review 문서에 기록했다.

### 2.7 UI 피드백 반영자

사용자가 브라우저 화면을 보며 제기한 UI 문제를 기준으로 수정 계획을 먼저 정리하고, 승인 후 구현했다. 예를 들어 캘린더 기간 막대, 같은 날짜 다른 프로젝트 라인 분리, 카드형 반응형, 액션 메뉴 위치, breadcrumb 표시 문제가 이 방식으로 처리됐다.

## 3. 단계별 프롬프트 예시

아래 프롬프트는 이번 프로젝트에서 실제 수행한 흐름을 재사용할 수 있도록 정리한 예시다.

### 3.1 기획 단계

```text
업로드한 PPTX의 '내 업무' 화면을 분석하여 docs/01_requirements.md를 작성해라.

반드시 아래 형식으로 정리한다.

# 기능 목록
# 사용자 시나리오
# 화면 정의
# 데이터 정의
# 정책 정의
# 가정 사항
# 미확정 사항

구현하지 말고 분석만 수행한다.
```

### 3.2 프로젝트 기준 고정

```text
앞으로 모든 설계와 구현은 docs/00_project_context.md를 기준으로 수행한다.

기술 스택을 임의로 변경하거나 추가하지 않는다.

- Backend : Spring Boot 3 + Java 21
- Frontend : Vue3
- Database : PostgreSQL
- REST API 기반
- Layered Architecture
- Docker 기반 실행 환경
```

### 3.3 설계 문서 생성

```text
docs/01_requirements.md를 기준으로 구현을 위한 설계를 수행한다.

다음 문서를 생성해라.

docs/02_screen_spec.md
docs/03_api_spec.md
docs/04_db_design.md
docs/05_component_design.md

구현은 하지 않는다.
```

### 3.4 설계 검토

```text
생성한 설계를 검토하라.

다음을 점검해라.

- 요구사항 누락 여부
- API 설계 오류
- 상태값 정의 오류
- 화면 간 데이터 전달 문제
- 테스트 난이도가 높은 영역
- 향후 확장 포인트

문제점과 개선안을 제시하라.
```

### 3.5 구현 계획 수립

```text
설계를 기반으로 구현 계획을 작성해라.

작업을 다음 단위로 분리한다.

Phase1
Phase2
Phase3

각 Phase마다
- 수정 파일
- 생성 파일
- 예상 작업 시간
- 테스트 방법
- 위험 요소

를 작성한다.

docs/06_implementation_plan.md
```

### 3.6 초기 구성 전 승인 요청

```text
현재 프로젝트는 docs 산출물만 존재하고 frontend/backend/database 구성은 아직 없다.

문서를 기준으로 프로젝트 초기 구성을 생성하라.

규칙:
- 먼저 작업 계획을 제시하라.
- 계획 승인 전에는 파일을 생성하거나 수정하지 마라.
- 임의 기술 스택을 추가하지 마라.
```

### 3.7 Phase 구현 승인

```text
Phase1 계획을 승인한다.

계획대로 구현하라.

구현 완료 후 다음을 반드시 수행하라.

1. backend 테스트 실행
2. backend 빌드 실행
3. frontend 테스트 또는 타입 체크 실행
4. frontend 빌드 실행
5. docker compose up -d --build 실행
6. 컨테이너 상태 확인
7. API 동작 확인
8. docs/worklog.md 기록

실패 항목이 있으면 원인을 분석하고 수정한 뒤 재검증하라.
```

### 3.8 검증 리포트 작성

```text
Phase 완료 상태를 기준으로 검증 리포트를 작성하라.

검증 항목:
1. 요구사항 충족 여부
2. 화면 요구사항 충족 여부
3. API 명세 충족 여부
4. DB 설계 충족 여부
5. 테스트/빌드 결과
6. 범위 외 변경 여부
7. 남은 이슈
8. 다음 Phase 전 보완 필요 사항

규칙:
- 코드는 수정하지 마라.
- 검증과 문서화만 수행하라.
```

### 3.9 UI 수정 전 계획 요청

```text
캘린더형 UI 수정이 필요해.
동일 프로젝트는 일 별로 표시하지 말고, 쭉 이어서 보이도록 UI를 변경해.
우선 수정 방향을 계획하고.
실제 코드 수정은 하지마.
```

### 3.10 UI 수정 승인 및 검증

```text
계획대로 구현하라.

구현 완료 후 다음을 수행하라.
- frontend build
- frontend test

브라우저 화면으로 실제 요구사항이 잘 반영됐는지 확인해.
```

## 4. 산출물 목록

### 4.1 기획/설계 산출물

- `docs/00_project_context.md`
- `docs/01_requirements.md`
- `docs/02_screen_spec.md`
- `docs/03_api_spec.md`
- `docs/04_db_design.md`
- `docs/05_component_design.md`
- `docs/06_implementation_plan.md`

### 4.2 구현 산출물

- `frontend/`
- `backend/`
- `db/init/001_schema.sql`
- `db/init/002_seed.sql`
- `docker-compose.yml`
- `scripts/dev-start.sh`
- `scripts/test.sh`
- `scripts/build.sh`
- `README.md`
- `assumptions.md`

### 4.3 검증/관리 산출물

- `docs/07_phase1_review.md`
- `docs/08_phase2_review.md`
- `docs/09_final_function_review.md`
- `docs/issues.md`
- `docs/worklog.md`
- `docs/11_vibecoding_methodology.md`

### 4.4 테스트 산출물

- Backend Controller 테스트
- Backend Service 테스트
- Backend D-day 계산 테스트
- Frontend date formatter 테스트
- Frontend calendar util 테스트
- Docker Compose 통합 실행 결과
- API 수동 검증 결과
- 브라우저 화면 검증 결과

## 5. 사람이 결정해야 하는 영역

바이브코딩에서 Codex가 많은 작업을 수행할 수 있지만, 다음 항목은 사람이 결정해야 한다.

### 5.1 제품 범위 결정

이번 프로젝트에서도 사용자가 명확히 "Phase1 범위 외 작업 금지", "Phase2 범위만", "코드 수정하지 말고 계획만" 같은 제약을 줬다. 이 결정이 없으면 Codex가 너무 넓은 범위를 구현할 수 있다.

사람이 결정할 항목:

- PoC에서 어디까지 구현할지
- 본기능과 placeholder의 경계
- 일정관리/WBS 상세를 실제 구현할지 여부
- 보안 취약점 업그레이드 시점
- UI 상세 기준의 우선순위

### 5.2 기술 스택과 아키텍처

이번 프로젝트에서는 사용자가 Nuxt 금지, Vue3 + Vite, Spring Boot 3, Java 21, PostgreSQL, REST API, Layered Architecture, Docker Compose를 명시했다. Codex는 이 기준 안에서만 움직였다.

사람이 결정할 항목:

- 기술 스택 변경 여부
- 라이브러리 추가 승인 여부
- 운영 배포 방식
- 인증/권한 방식
- 테스트 도구 도입 여부

### 5.3 UX 판단

캘린더형 UI에서 "동일 프로젝트는 일별로 반복하지 말고 이어서 보여야 한다", "날칸 안에 그려져야 한다", "같은 날 다른 프로젝트는 묶지 말고 각각 라인으로 표시해야 한다" 같은 판단은 사용자가 화면을 보고 결정했다.

사람이 결정할 항목:

- 화면이 기획 의도에 맞는지
- 좁은 화면과 넓은 화면에서 허용 가능한 UI 수준
- 클릭 대상과 포커스 범위
- breadcrumb 문구
- 버튼명과 이동 방식

### 5.4 리스크 수용 여부

`npm audit` 취약점은 발견됐지만 사용자가 임의 업그레이드를 금지했다. Codex는 이를 수정하지 않고 `docs/issues.md`에 기록했다.

사람이 결정할 항목:

- 취약점 즉시 조치 여부
- 임의 업그레이드 허용 여부
- PoC에서 남겨둘 이슈
- 본 개발 전 반드시 해소할 이슈

## 6. Codex에게 위임 가능한 영역

### 6.1 문서 초안 작성

Codex는 PPTX나 기존 문서를 기준으로 요구사항, 화면 정의, API 설계, DB 설계, 컴포넌트 설계 초안을 빠르게 만들 수 있다.

### 6.2 설계 점검

요구사항 누락, API parameter 누락, 상태값 불일치, 화면 간 데이터 전달 문제처럼 문서 간 정합성 점검을 위임할 수 있다.

### 6.3 반복 구현

승인된 Phase 범위 안에서 Backend API, Frontend 연동, DB 스키마, 테스트 코드를 구현할 수 있다.

### 6.4 실패 원인 분석

이번 프로젝트에서는 다음 문제를 Codex가 분석하고 수정했다.

- Frontend TypeScript alias 해석 실패
- 로컬 Gradle 부재
- Docker socket 경로 문제
- Gradle Docker image arm64 native library 문제
- Gradle cache 권한 문제
- Docker Compose 환경에서 Frontend proxy가 Backend를 찾지 못한 문제
- 넓은 화면에서 카드형 액션 메뉴가 열리지 않는 문제
- 캘린더 기간 막대가 날칸 밖으로 깨지는 문제
- 날짜 클릭 포커스 범위가 달력 전체로 잡히는 문제

### 6.5 테스트와 검증 실행

Codex는 명령을 실제 실행하고 결과를 기록할 수 있다. 특히 `test`, `build`, `docker compose`, `curl`, 브라우저 화면 확인을 반복적으로 수행하는 데 적합했다.

### 6.6 작업 로그와 이슈 관리

`docs/worklog.md`와 `docs/issues.md`처럼 작업 이력을 누적하는 문서는 Codex에게 위임하기 좋다. 단, 이슈 우선순위나 수용 여부는 사람이 판단해야 한다.

## 7. 실패/수정 사례

### 7.1 Frontend build 실패

현상:

- `npm run build` 실패
- `@/views/MyWorkView.vue` alias를 TypeScript가 해석하지 못함

원인:

- Vite alias는 있었지만 `tsconfig.json`의 `baseUrl`, `paths` 설정 누락
- Vue 타입 선언 파일 누락

수정:

- `frontend/tsconfig.json`에 `baseUrl`, `paths` 추가
- `frontend/src/env.d.ts` 생성

결과:

- `vue-tsc --noEmit` 통과
- `vite build` 통과

### 7.2 Backend test 실패

현상:

- `./gradlew test` 실패

원인:

- 로컬에 `gradle` 명령 없음
- Docker CLI가 잘못된 socket을 바라봄
- `gradle:8.10.2-jdk21`의 `linux/arm64` native library 초기화 실패
- Gradle native library cache 권한 문제

수정:

- Docker Desktop socket 자동 지정
- Gradle Docker 컨테이너 `--platform linux/amd64` 적용
- `GRADLE_USER_HOME=/workspace/.gradle` 지정

결과:

- `./gradlew test` 성공
- `./gradlew build` 성공

### 7.3 Docker Compose Frontend proxy 오류

현상:

- 브라우저 `/my-work` 카드형 화면에서 `API 요청 실패 (500)` 표시

원인:

- Frontend 컨테이너 내부 Vite proxy가 `http://localhost:8080`을 바라봄
- 컨테이너 내부 `localhost`는 Backend가 아니라 Frontend 컨테이너 자신

수정:

- `frontend/vite.config.ts`에서 `VITE_API_BASE_URL`을 proxy target으로 사용
- `docker-compose.yml`의 Frontend 환경값을 `http://backend:8080`으로 변경

결과:

- Docker Compose 재기동 성공
- 브라우저 API 오류 해소

### 7.4 API 명세와 구현 차이

현상:

- Phase1 검증에서 `scope`, paging, `/unscheduled.totalCount` 등 명세와 구현 차이 발견

수정:

- Phase2에서 `scope=INTEGRATED` 검증 추가
- page, size 검증 추가
- project/waiting paging 반영
- `/unscheduled.totalCount`를 전체 일정 미등록 업무 수로 보정
- 미지원 scope는 `400 MY_WORK_BAD_REQUEST`로 응답

결과:

- Phase2 API 검증 통과
- 관련 이슈 해결 처리

### 7.5 Repository 전략 차이

현상:

- 초기 구현은 `JdbcTemplate` 기반이었으나 설계 문서는 JPA Repository 기준

수정:

- Phase2에서 JPA Entity와 Spring Data JPA Repository 기반으로 전환
- 기존 `JdbcMyWorkRepository` 제거

결과:

- DB 설계와 구현 방식 정합화

### 7.6 접근성 미구현

현상:

- Phase2까지 ESC 닫기는 일부 보완됐지만 포커스 트랩과 키보드 이동은 미완성

수정:

- Phase3에서 날짜별 팝업 포커스 트랩 추가
- ESC 닫기 확인
- 팝업 닫힘 후 이전 포커스 복귀 확인

결과:

- 접근성 관련 동작 검증 완료

### 7.7 캘린더 UI 기간 표시 문제

현상:

- 동일 프로젝트 일정이 날짜별로 반복 표시됨
- 기간 막대가 캘린더 날칸 밖으로 그려지며 UI가 깨짐
- 프로젝트 클릭 시 날짜 팝업과 섞여 부자연스럽게 동작

수정:

- 동일 프로젝트 연속 일정은 기간 막대로 표시
- 막대는 캘린더 날칸 내부에서만 그려지도록 조정
- 프로젝트 막대 클릭 시 프로젝트 상세 팝업 표시
- 날짜 숫자 클릭 시 날짜별 팝업 표시

결과:

- 캘린더형 UI가 기획 의도에 가깝게 개선됨

### 7.8 같은 날짜 프로젝트 묶음 문제

현상:

- 같은 날짜에 있는 다른 프로젝트가 하나로 묶여 표시됨

수정:

- 같은 날짜라도 프로젝트가 다르면 각각 별도 라인으로 표시
- 샘플 데이터에 같은 날짜 다른 프로젝트 케이스 추가
- Frontend calendar util 테스트 보강

결과:

- 같은 날짜 다른 프로젝트 라인 분리 확인
- Frontend 테스트 10개 통과

### 7.9 카드형 반응형 문제

현상:

- 캘린더형은 좁은 화면에서 반응형이 유지됐지만 카드형 내 할 일 영역은 깨짐

수정:

- 일정 폭 이하에서 내 할 일 테이블을 카드형 목록으로 전환
- 요약/헤더/업무 액션 영역의 반응형 CSS 보완

결과:

- 좁은 화면에서도 카드형 화면이 사용할 수 있는 형태로 표시됨

### 7.10 카드형 액션 메뉴 문제

현상:

- 좁은 화면에서는 내 할 일 액션 버튼 메뉴가 열리지만, 넓은 화면에서는 메뉴가 보이지 않음

원인:

- 테이블 행/셀 overflow와 메뉴 위치 계산이 충돌

수정:

- 액션 영역 overflow와 메뉴 위치 조정
- 넓은 화면에서는 액션 컬럼 왼쪽으로 메뉴가 열리도록 보정

결과:

- 넓은 화면과 좁은 화면 모두 액션 메뉴 표시 확인

### 7.11 Placeholder 이동 UX 문제

현상:

- 캘린더 일정 미등록 업무에서 `일정등록`으로 상세 페이지 진입 후 상단 `내업무` 버튼을 누르면 카드형 페이지로 이동
- 사용자는 이전 캘린더 화면으로 돌아가기를 기대

수정:

- 버튼명을 `뒤로가기`로 변경
- 동작을 `router.back()` 기반으로 변경
- fallback은 `/my-work`로 유지

결과:

- placeholder 화면의 이동 의도가 명확해짐

### 7.12 Breadcrumb 표시 문제

현상:

- 상단 breadcrumb가 `> 내업무 > 일정관리`처럼 가장 앞에 불필요한 `>`를 표시
- 내 업무 메인 화면에서도 동일 문제 발생

수정:

- 내 업무, 일정관리, WBS 상세 화면 breadcrumb 앞쪽 `>` 제거

결과:

- breadcrumb 표시 정리 완료

## 8. 장점

### 8.1 문서와 구현의 왕복이 빠르다

요구사항 문서, 설계 문서, 구현 계획, Phase별 검증 리포트가 모두 같은 흐름 안에서 작성됐다. 구현 중 발견된 차이는 `docs/issues.md`와 `docs/worklog.md`에 남기고, 다음 Phase에서 반영했다.

### 8.2 승인 기반으로 범위를 통제할 수 있다

사용자가 "계획 승인 전에는 수정하지 마라", "Phase1 범위 외 작업 금지", "코드는 수정하지 마라" 같은 규칙을 주면 Codex가 그 범위를 지키며 작업했다. 바이브코딩이 무작정 빠르게 코드를 늘리는 방식이 아니라, 승인 단위로 확장되는 방식이 됐다.

### 8.3 실패가 개발 자산으로 남는다

빌드 실패, Docker 오류, API 명세 차이, UI 깨짐 같은 문제가 단순히 지나가지 않고 worklog와 issues에 누적됐다. 본 개발에서는 이 기록이 환경 구성 가이드와 위험 목록으로 재사용될 수 있다.

### 8.4 화면 피드백 반영 속도가 빠르다

캘린더형 기간 막대, 같은 날짜 프로젝트 라인 분리, 카드형 반응형, 액션 메뉴 문제처럼 브라우저 화면을 보며 나온 피드백을 빠르게 수정하고 재검증했다.

### 8.5 테스트 루프를 강제하기 좋다

사용자가 매번 `backend test`, `backend build`, `frontend test/type-check`, `frontend build`, `docker compose up -d --build`, API 확인을 요구했기 때문에 Codex가 구현 후 검증을 생략하지 않았다.

## 9. 한계

### 9.1 사람이 화면 품질을 최종 판단해야 한다

Codex가 브라우저로 화면을 확인할 수 있어도 "기획 의도에 맞는가"는 사용자가 더 정확히 판단했다. 이번 프로젝트에서도 캘린더 막대가 날칸 안에 있어야 한다거나, 같은 날짜 프로젝트를 묶지 말아야 한다는 판단은 사용자가 제공했다.

### 9.2 초기 산출물이 완벽하지 않다

Phase1에서는 API parameter, paging, totalCount, Repository 전략에서 설계와 구현 차이가 있었다. 이후 Phase 검증을 통해 발견하고 수정했지만, 첫 구현이 항상 설계와 완전히 일치하지는 않았다.

### 9.3 환경 문제는 반복 확인이 필요하다

Docker socket, Gradle platform, 컨테이너 proxy, Codex 실행 환경 host curl 같은 문제는 로컬 환경에 따라 달라진다. 자동화만 믿기보다 실제 명령 실행과 로그 확인이 필요했다.

### 9.4 보안과 운영 판단은 별도 절차가 필요하다

`npm audit` 취약점은 발견됐지만 임의 업그레이드하지 않았다. 바이브코딩 과정에서 취약점을 바로 고치는 것이 항상 정답은 아니며, 운영 영향과 호환성을 사람이 판단해야 한다.

### 9.5 Placeholder와 본기능의 경계가 흐려질 수 있다

일정관리/WBS 상세는 placeholder로 구현했지만, 사용자가 화면을 테스트하면서 실제 이동 UX까지 수정해야 했다. 본 개발에서는 placeholder도 사용자 흐름 안에서는 실제 기능처럼 다뤄야 한다.

## 10. 향후 H-PMS 본 개발 적용 방안

### 10.1 Phase 게이트를 공식화한다

본 개발에서도 다음 게이트를 유지한다.

1. 요구사항 문서 승인
2. 화면/API/DB/컴포넌트 설계 승인
3. 구현 계획 승인
4. Phase 구현
5. 테스트/빌드/API/화면 검증
6. 이슈 정리
7. 다음 Phase 착수 판단

각 게이트에서는 Codex가 산출물을 만들고, 사람이 범위와 우선순위를 승인한다.

### 10.2 worklog와 issues를 필수 산출물로 둔다

이번 프로젝트에서 `docs/worklog.md`는 실제 수행 이력의 기준이 됐고, `docs/issues.md`는 미해결/해결 이슈의 기준이 됐다. 본 개발에서도 모든 기능 단위에 이 두 문서를 유지하는 것이 좋다.

권장 운영:

- 구현 전 계획 기록
- 구현 후 명령 실행 결과 기록
- 실패 원인과 수정 내용 기록
- 남은 이슈와 우선순위 기록
- 해결 완료 시 이슈 상태 변경

### 10.3 Codex 프롬프트는 역할과 금지사항을 함께 준다

효과적이었던 패턴은 다음과 같다.

- "분석만 수행, 구현하지 마라"
- "계획을 먼저 제시, 승인 전 수정하지 마라"
- "Phase 범위 외 작업 금지"
- "코드는 수정하지 말고 문서화만 수행"
- "실패 항목은 원인 분석 후 수정하고 재검증하라"
- "임의 기술 스택을 추가하지 마라"

본 개발에서도 프롬프트에 작업 범위, 산출물, 금지사항, 검증 명령을 함께 넣어야 한다.

### 10.4 본 개발 전 보완할 기준을 먼저 정한다

PoC에서 남은 항목은 본 개발 착수 전에 기준을 확정해야 한다.

- 인증/권한 정책
- 실제 사용자 식별 방식
- 일정 등록/수정/삭제 정책
- WBS 상세 본기능 범위
- 프로젝트/업무 상태값 최종 정의
- Frontend 의존성 보안 조치
- API 오류 응답 표준
- E2E 테스트 도입 여부
- 운영 배포 방식
- DB index와 query plan 검증 기준

### 10.5 UI는 브라우저 확인 루프를 포함한다

이번 프로젝트에서 UI 품질은 "구현 후 빌드 성공"만으로는 충분하지 않았다. 캘린더 막대 위치, 포커스 범위, 메뉴 표시, 좁은 화면 레이아웃은 실제 브라우저 확인을 통해 발견됐다.

본 개발 권장 루프:

1. UI 변경 계획 작성
2. 코드 수정
3. frontend test/build
4. 브라우저 화면 확인
5. 사용자가 의도와 차이를 피드백
6. 수정 후 재확인

### 10.6 테스트 자동화를 확대한다

PoC에서는 Backend 단위/Controller 테스트와 Frontend util 테스트 중심이었다. 본 개발에서는 다음을 확대해야 한다.

- Frontend component 테스트
- 사용자 흐름 E2E 테스트
- 접근성 테스트
- API contract 테스트
- Repository query 테스트
- DB migration 테스트
- Docker Compose smoke test 자동화

### 10.7 PoC 산출물을 본 개발의 초안으로 재사용한다

이번 산출물은 폐기용이 아니라 본 개발의 출발점으로 사용할 수 있다.

재사용 가능한 항목:

- 요구사항 분류 체계
- API endpoint 구조
- DB 기본 모델
- 화면 목록과 컴포넌트 구조
- Phase별 구현 계획 방식
- 테스트/빌드 검증 루프
- 이슈 관리 방식
- 실패 사례와 대응 방안

다만 본 개발에서는 placeholder 화면, 임시 사용자 식별, 샘플 기준일, 제한된 scope, 보안 취약점 미조치 상태를 그대로 가져가면 안 된다. 이 부분은 착수 전 정식 설계로 전환해야 한다.

