# H-PMS 내 업무 프로토타입 개발 PoC 결과 보고서

## 1. PoC 목적

본 PoC의 목적은 H-PMS 내 `내 업무` 기능을 대상으로 기획, 설계, 개발, 테스트, Docker 기반 실행 검증까지의 전체 개발 사이클을 검증하는 것이다.

검증 목표는 다음과 같다.

- PPTX 화면 기획 자료를 요구사항 문서로 전환할 수 있는지 확인
- 요구사항을 화면, API, DB, 컴포넌트 설계로 분해할 수 있는지 확인
- 정해진 기술 스택 안에서 Frontend, Backend, Database 초기 구성을 생성할 수 있는지 확인
- Phase 단위로 기능을 구현하고 검증할 수 있는지 확인
- Codex를 활용한 바이브코딩 방식이 H-PMS 본 개발에 적용 가능한지 확인
- 화면 피드백, 실패 수정, 테스트, 문서화가 하나의 반복 루프로 동작하는지 확인

최종적으로 본 PoC는 H-PMS `내 업무` 기능의 핵심 조회 흐름과 개발 프로세스 검증을 목표로 수행했다.

## 2. 개발 범위

### 2.1 포함 범위

- PPTX `내 업무` 화면 분석
- 요구사항 문서 작성
- 화면/API/DB/컴포넌트 설계
- 구현 계획 수립
- Frontend 프로젝트 초기 구성
- Backend 프로젝트 초기 구성
- PostgreSQL Docker Compose 구성
- DB schema/seed SQL 작성
- 내 업무 Backend REST API 구현
- 내 업무 카드형 화면 구현
- 내 업무 캘린더형 화면 구현
- 카드형/캘린더형 API 연동
- 일정 미등록 업무 표시
- 날짜별 업무 팝업
- 프로젝트 기간 막대 표시
- 같은 날짜 다른 프로젝트 라인 분리
- 일정관리/WBS 상세 placeholder 화면 이동
- 접근성 기본 동작 확인
- 테스트/빌드/Docker Compose 검증
- Phase별 검증 리포트 및 최종 검증 리포트 작성

### 2.2 제외 범위

- 실제 로그인/인증/권한 본구현
- 실제 일정 등록/수정/삭제 저장 기능
- WBS 상세 본기능
- 운영 배포 파이프라인
- 운영 수준의 접근성 전체 검수
- 대용량 데이터 성능 검증
- Frontend 의존성 보안 취약점 정비

제외 범위는 PoC 기능 검증에는 직접 영향이 없지만, 본 개발 전에는 별도 설계와 구현이 필요하다.

## 3. 기술 스택

| 영역 | 기술 |
| --- | --- |
| Frontend | Vue3, Vite, TypeScript, Vue Router, Pinia |
| Frontend Test | Vitest |
| Backend | Spring Boot 3, Java 21, Gradle |
| Backend Persistence | Spring Data JPA |
| Database | PostgreSQL |
| API | REST API |
| Architecture | Layered Architecture |
| Local Runtime | Docker, Docker Compose |
| DB 초기화 | `db/init/001_schema.sql`, `db/init/002_seed.sql` |

기술 스택은 사용자 지시에 따라 임의 변경하지 않았다. Nuxt는 사용하지 않았고, Frontend는 Vue3 + Vite 기반으로 구성했다.

## 4. 진행 절차

### 4.1 기획

PPTX의 `내 업무` 화면을 분석해 `docs/01_requirements.md`를 작성했다.

정리 항목:

- 기능 목록
- 사용자 시나리오
- 화면 정의
- 데이터 정의
- 정책 정의
- 가정 사항
- 미확정 사항

이 단계에서는 구현을 수행하지 않고 분석 산출물만 생성했다.

### 4.2 설계

요구사항 문서를 기준으로 다음 설계 문서를 작성했다.

- `docs/02_screen_spec.md`
- `docs/03_api_spec.md`
- `docs/04_db_design.md`
- `docs/05_component_design.md`

이후 설계 검토를 통해 요구사항 누락, API 설계 오류, 상태값 정의 오류, 화면 간 데이터 전달 문제, 테스트 난이도, 확장 포인트를 점검하고 개선 사항을 반영했다.

### 4.3 구현 계획

`docs/06_implementation_plan.md`에 Phase1, Phase2, Phase3 단위의 구현 계획을 작성했다.

각 Phase는 다음 항목으로 분리했다.

- 수정 파일
- 생성 파일
- 예상 작업 시간
- 테스트 방법
- 위험 요소

### 4.4 초기 구성

문서 산출물만 존재하던 상태에서 프로젝트 초기 구성을 생성했다.

생성 대상:

- `frontend/`
- `backend/`
- `db/init/`
- `docker-compose.yml`
- `scripts/dev-start.sh`
- `scripts/test.sh`
- `scripts/build.sh`
- `README.md`

초기 구성 후 Frontend 의존성 설치, Frontend build, Backend test/build, Docker Compose 기동, PostgreSQL 컨테이너 기동, Backend-DB 연결을 확인했다.

### 4.5 Phase1 개발

Phase1에서는 내 업무 기능의 최소 동작을 구현했다.

- Backend 내 업무 API 기본 구현
- Frontend 카드형/캘린더형 API 연동
- 요약 지표 표시
- 진행 프로젝트, 내 할 일, 대기 프로젝트 표시
- 월간 캘린더와 일정 미등록 업무 패널 표시
- `docs/issues.md` 생성

### 4.6 Phase2 개발

Phase2에서는 Phase1 검증 결과에서 발견된 명세 차이와 구조 차이를 보완했다.

- API query parameter 보완
- paging 정책 반영
- `/unscheduled.totalCount` 정합화
- 미지원 `scope` 오류 응답 처리
- Repository를 Spring Data JPA 기반으로 전환
- Docker Compose 환경의 Frontend proxy 오류 수정
- 화면 세부 UX 일부 보완

### 4.7 Phase3 개발

Phase3에서는 화면 흐름, 테스트 자동화, 접근성 기본 동작을 보완했다.

- Vitest 도입
- Frontend util 테스트 추가
- 일정관리 placeholder 화면 추가
- WBS 상세 placeholder 화면 추가
- 진행/대기 프로젝트 좌우 이동
- 날짜별 팝업 포커스 트랩
- ESC 닫기 및 포커스 복귀 확인
- Backend 업무 액션 API 테스트 보강

### 4.8 Phase3 이후 UI 보완

사용자 화면 확인 과정에서 발견된 UI 이슈를 추가로 보완했다.

- 캘린더형 동일 프로젝트 기간 막대 표시
- 캘린더 날칸 내부에 막대가 그려지도록 조정
- 같은 날짜 다른 프로젝트를 각각 라인으로 표시
- 프로젝트 막대 클릭 시 프로젝트 상세 팝업 표시
- 날짜 숫자 포커스 범위 보정
- 카드형 내 할 일 영역 반응형 보완
- 넓은 화면에서 액션 메뉴가 보이지 않는 문제 수정
- placeholder 상단 버튼을 `뒤로가기`로 변경
- breadcrumb 앞쪽 `>` 제거

### 4.9 최종 검증

최종 완료 상태 기준으로 `docs/09_final_function_review.md`, `docs/issues.md`, `docs/worklog.md`를 갱신했다.

## 5. 주요 산출물

### 5.1 문서 산출물

| 문서 | 내용 |
| --- | --- |
| `docs/00_project_context.md` | 프로젝트 기준 및 기술 스택 |
| `docs/01_requirements.md` | 요구사항 분석 |
| `docs/02_screen_spec.md` | 화면 설계 |
| `docs/03_api_spec.md` | API 설계 |
| `docs/04_db_design.md` | DB 설계 |
| `docs/05_component_design.md` | 컴포넌트 설계 |
| `docs/06_implementation_plan.md` | Phase별 구현 계획 |
| `docs/07_phase1_review.md` | Phase1 검증 리포트 |
| `docs/08_phase2_review.md` | Phase2 검증 리포트 |
| `docs/09_final_function_review.md` | 최종 기능 검증 리포트 |
| `docs/11_vibecoding_methodology.md` | 바이브코딩 개발 방법론 |
| `docs/issues.md` | 이슈 목록 |
| `docs/worklog.md` | 작업 이력 |

### 5.2 구현 산출물

| 영역 | 산출물 |
| --- | --- |
| Frontend | `frontend/` |
| Backend | `backend/` |
| Database | `db/init/001_schema.sql`, `db/init/002_seed.sql` |
| Docker | `docker-compose.yml`, Backend/Frontend Dockerfile |
| Scripts | `scripts/dev-start.sh`, `scripts/test.sh`, `scripts/build.sh` |
| 실행 문서 | `README.md` |
| 전제 기록 | `assumptions.md` |

## 6. 구현 기능

### 6.1 Backend API

| API | 구현 내용 |
| --- | --- |
| `GET /api/v1/my-work/summary` | 내 업무 요약 지표 조회 |
| `GET /api/v1/my-work/card` | 카드형 화면 데이터 조회 |
| `GET /api/v1/my-work/calendar` | 월간 캘린더 데이터 조회 |
| `GET /api/v1/my-work/calendar/daily` | 날짜별 업무 목록 조회 |
| `GET /api/v1/my-work/unscheduled` | 일정 미등록 업무 조회 |
| `GET /api/v1/my-work/tasks/{taskId}/actions` | 업무별 가능 액션 조회 |

Backend 구현 특징:

- `scope=INTEGRATED` 지원
- 미지원 scope는 `400 MY_WORK_BAD_REQUEST` 반환
- project/waiting paging 지원
- 일정 미등록 업무 `page`, `size`, `totalCount` 지원
- 기준일 기반 D-day, 지연, 금주 마감 계산
- Spring Data JPA Repository 및 projection 기반 조회
- `user_account.active=true` 조건 반영

### 6.2 Frontend 화면

| 화면 | 구현 내용 |
| --- | --- |
| 내 업무 카드형 | 요약 지표, 진행 프로젝트, 내 할 일, 대기 프로젝트 표시 |
| 내 할 일 영역 | 업무명, 마감일, D-day, 공정률, 프로젝트명, 액션 메뉴 표시 |
| 카드형 반응형 | 좁은 화면에서 내 할 일 영역을 카드형 레이아웃으로 표시 |
| 내 업무 캘린더형 | 월간 캘린더, 일정 막대, 일정 미등록 업무 패널 표시 |
| 캘린더 기간 막대 | 동일 프로젝트 연속 일정을 하나의 막대로 표시 |
| 같은 날짜 다른 프로젝트 | 프로젝트별 별도 라인 표시 |
| 날짜별 업무 팝업 | 날짜 클릭 시 해당 날짜 업무 목록 표시 |
| 프로젝트 상세 팝업 | 프로젝트 막대 클릭 시 기간과 포함 업무 표시 |
| 일정관리 placeholder | `/schedule/tasks/{taskId}` 라우팅 |
| WBS 상세 placeholder | `/projects/{projectId}/wbs/tasks/{taskId}` 라우팅 |

### 6.3 접근성 및 UX

- 날짜별 팝업 ESC 닫기
- 포커스 트랩
- 팝업 닫힘 후 이전 포커스 복귀
- 긴 텍스트 `title` 속성 보완
- breadcrumb 앞쪽 `>` 제거
- placeholder 화면 `뒤로가기` 버튼 적용

## 7. 테스트/검증 결과

### 7.1 명령 기반 검증

| 구분 | 명령 | 결과 |
| --- | --- | --- |
| Backend test | `./gradlew test` | 성공 |
| Backend build | `./gradlew build` | 성공 |
| Frontend test | `npm run test` | 성공, 2개 파일 10개 테스트 통과 |
| Frontend build | `npm run build` | 성공 |
| Docker Compose | `docker compose up -d --build` | 성공 |
| Docker 상태 | `docker compose ps` | PostgreSQL healthy, Backend/Frontend up |

### 7.2 API 검증

| API | 검증 결과 |
| --- | --- |
| summary | 진행 프로젝트 2건, 내 할 일 6건, 금주 마감 3건, 지연 3건, 대기 프로젝트 2건 반환 |
| card | 요약, 진행 프로젝트, 내 할 일 6건, 대기 프로젝트 반환 및 paging 확인 |
| calendar | 2026년 3월 일정 4건, 일정 미등록 업무 2건 반환 |
| daily | 2026-03-20 기준 업무 3건 반환 |
| unscheduled | `totalCount=2`, `items` page size 반영 확인 |
| actions | 일정관리, WBS 상세 액션 가능 반환 |
| bad scope | `400 MY_WORK_BAD_REQUEST` 반환 |

### 7.3 화면 검증

| 화면 | 검증 결과 |
| --- | --- |
| 카드형 | 요약, 진행 프로젝트, 내 할 일, 대기 프로젝트, 더보기 메뉴 정상 표시 |
| 카드형 반응형 | 좁은 화면에서 UI 깨짐 없이 표시 |
| 카드형 액션 메뉴 | 넓은 화면/좁은 화면 모두 메뉴 표시 |
| 캘린더형 | 월간 캘린더, 기간 막대, 지연 표시, 일정 미등록 패널 표시 |
| 같은 날짜 프로젝트 | 프로젝트별 별도 라인 표시 |
| 프로젝트 팝업 | 프로젝트 막대 클릭 시 상세 팝업 표시 |
| 날짜 팝업 | 날짜 숫자 클릭 시 날짜별 업무 팝업 표시 |
| placeholder | 일정관리/WBS 상세 이동 및 뒤로가기 동작 확인 |
| 접근성 | ESC 닫기, 포커스 트랩, 키보드 이동 확인 |

## 8. 자동화 구성

### 8.1 실행 자동화

| 파일 | 역할 |
| --- | --- |
| `scripts/dev-start.sh` | 로컬 개발 실행 보조 |
| `scripts/test.sh` | 테스트 실행 보조 |
| `scripts/build.sh` | 빌드 실행 보조 |
| `docker-compose.yml` | PostgreSQL, Backend, Frontend 통합 실행 |

### 8.2 DB 초기화 자동화

| 파일 | 역할 |
| --- | --- |
| `db/init/001_schema.sql` | PostgreSQL schema 초기화 |
| `db/init/002_seed.sql` | 내 업무 검증용 샘플 데이터 적재 |

### 8.3 테스트 자동화

| 영역 | 자동화 내용 |
| --- | --- |
| Backend | Gradle 기반 단위/Controller 테스트 |
| Frontend | Vitest 기반 util 테스트 |
| Build | Backend build, Frontend type-check/build |
| Docker | Docker Compose 기반 통합 실행 |

PoC에서는 CI/CD까지 구성하지 않았지만, 로컬에서 반복 가능한 테스트/빌드/실행 명령 체계는 확보했다.

## 9. 남은 이슈

| 이슈 ID | 우선순위 | 상태 | 내용 | 대응 방향 |
| --- | --- | --- | --- | --- |
| ISSUE-001 | P1 | 미해결 | Frontend npm audit 취약점 3건 | 본 개발 또는 외부 배포 전 영향 패키지와 호환성 검토 후 조치 |
| ISSUE-002 | P3 | 관찰 유지 | Codex 실행 환경 host curl 실패 관찰 이슈 | 현재 검증에서는 재현되지 않음. 환경 차이 관찰 대상으로 유지 |

해결 완료 이슈:

- `ISSUE-003`: API query/paging/totalCount 정합화
- `ISSUE-004`: 화면 흐름과 캐러셀 보완
- `ISSUE-005`: Repository를 Spring Data JPA 기반으로 전환
- `ISSUE-006`: ESC 닫기, 포커스 트랩, 긴 텍스트 확인 보완
- `ISSUE-007`: Frontend 테스트 도구 도입
- `ISSUE-008`: Phase3 이후 카드형/캘린더형 UI 보완

## 10. 본 개발 적용 가능성

본 PoC 결과, H-PMS 본 개발에 적용 가능한 항목은 다음과 같다.

### 10.1 적용 가능 항목

- 문서 기반 개발 흐름
- Phase 단위 구현 및 검증 방식
- Vue3 + Vite + TypeScript Frontend 구조
- Spring Boot 3 + Java 21 Backend 구조
- REST API 기반 Frontend/Backend 연동 방식
- PostgreSQL schema/seed 기반 로컬 데이터 검증
- Docker Compose 기반 통합 실행
- Backend 테스트와 Frontend 테스트 도구 구성
- API 명세와 구현 결과를 검증 리포트로 비교하는 방식
- 사용자 화면 피드백을 계획, 구현, 검증으로 연결하는 방식

### 10.2 본 개발 전 보완 필요 항목

- Spring Security 기반 인증/권한 전환
- 실제 사용자/조직/역할 기반 조회 범위 확정
- 일정 등록/수정/삭제 본기능 설계 및 구현
- WBS 상세 본기능 설계 및 구현
- Frontend 의존성 취약점 정비
- API 오류 응답 표준화
- Store/컴포넌트/E2E 테스트 확대
- 운영 배포 환경 구성
- DB index, query plan, 대용량 데이터 검증
- 운영 수준 접근성 검수

### 10.3 적용 판단

PoC는 기능 구현 가능성뿐 아니라 개발 프로세스 적용 가능성도 확인했다. 특히 요구사항 문서화, 설계 검토, Phase 계획, 승인 후 구현, 테스트/빌드, 화면 검증, 이슈 관리의 흐름은 본 개발에도 적용 가능하다.

다만 PoC 구현체를 그대로 운영 코드로 전환하기보다는 인증/권한, 일정관리, WBS 상세, 보안, 성능, 테스트 범위를 보강한 뒤 본 개발 기준으로 재정리해야 한다.

## 11. 결론

H-PMS `내 업무` 프로토타입 개발 PoC는 성공으로 판단한다.

PoC를 통해 다음을 확인했다.

- PPTX 기반 화면 기획을 요구사항과 설계 문서로 전환 가능
- 지정 기술 스택으로 Frontend/Backend/DB/Docker 실행 환경 구성 가능
- 내 업무 카드형/캘린더형 핵심 조회 기능 구현 가능
- Backend REST API와 Frontend 화면 연동 가능
- PostgreSQL 기반 샘플 데이터 조회 가능
- 테스트, 빌드, Docker Compose 실행 검증 가능
- Codex를 활용한 문서화, 구현, 검증, 수정 루프 운영 가능

PoC 범위 내 핵심 기능은 완료됐으며, 기능 검증에 직접 영향을 주는 미해결 결함은 발견되지 않았다. 본 개발로 전환하기 전에는 Frontend 보안 취약점 정비, 인증/권한 정책 확정, 일정관리/WBS 상세 본기능 설계, 테스트 자동화 확대, 운영 배포 기준 수립이 필요하다.

