# 프로젝트 이슈

## 우선순위 요약

| 우선순위 | 이슈 ID | 상태 | 요약 |
| --- | --- | --- | --- |
| P1 | ISSUE-001 | 미해결 | frontend npm audit 취약점 3건 |
| P3 | ISSUE-002 | 관찰 유지 | host curl 실패 관찰 이슈는 Phase2에서 미재현 |
| 완료 | ISSUE-003 | 해결 | API query/paging/totalCount 정합화 |
| 완료 | ISSUE-004 | 해결 | Phase3 prototype 화면 흐름과 캐러셀 보완 |
| 완료 | ISSUE-005 | 해결 | Repository를 Spring Data JPA 기반으로 전환 |
| 완료 | ISSUE-006 | 해결 | ESC 닫기, 포커스 트랩, 긴 텍스트 확인 보완 |
| 완료 | ISSUE-007 | 해결 | Frontend 테스트 도구 도입 |
| 완료 | ISSUE-008 | 해결 | Phase3 이후 카드형/캘린더형 UI 보완 |

## 최종 기능 검증 기준 이슈 상태

- 최종 검증 일시: 2026-06-27
- 기준 문서: `docs/09_final_function_review.md`
- PoC 기능 검증에 직접 영향을 주는 미해결 기능 결함은 발견되지 않음
- 본 개발 전 우선 처리 대상은 `ISSUE-001` Frontend npm audit 취약점 3건
- `ISSUE-002`는 Phase2~3 검증에서 재현되지 않았으나 실행 환경 관찰 이슈로 유지
- `ISSUE-003`~`ISSUE-008`은 Phase1~3 및 후속 UI 보완 범위에서 해결 완료

## ISSUE-001 frontend npm audit 취약점

- 관찰 일시: 2026-06-27
- 상태: 미해결
- 우선순위: P1
- 대상: `frontend/`
- 내용:
  - Phase1 `npm install` 후 `npm audit` 기준 취약점 2건 관찰
  - moderate 1건
  - high 1건
  - Phase3에서 Vitest 도입 후 `npm install` 기준 취약점 3건으로 변경
  - moderate 1건
  - high 1건
  - critical 1건
- Phase1 처리:
  - 임의 의존성 업그레이드 금지 원칙에 따라 수정하지 않음
  - `npm audit fix --force` 미수행
- Phase3 처리:
  - Frontend 테스트 도구 도입 승인에 따라 Vitest를 추가했으나, audit fix 또는 의존성 강제 업그레이드는 수행하지 않음
- 최종 검증:
  - PoC 기능 동작, 테스트, 빌드에는 직접 영향 없음
  - 본 개발 또는 외부 배포 전에는 보안 영향도와 호환성 검토 필요
- 후속 검토:
  - 배포 전 별도 의존성 정비 범위에서 영향 패키지와 호환성 확인 후 처리 여부 결정

## ISSUE-002 Codex 실행 환경 host curl 실패 관찰

- 관찰 일시: 2026-06-27
- 상태: 관찰 유지
- 우선순위: P3
- 대상:
  - `127.0.0.1:8080`
  - `127.0.0.1:5173`
- 내용:
  - Codex 실행 환경에서 host 기준 curl 실패가 관찰됨
  - Docker Compose 포트 publish는 정상
  - 컨테이너 내부 health 및 Backend-DB 연결은 정상
- Phase1 처리:
  - Phase1 개발 범위에서는 임의 수정하지 않음
- 후속 검토:
  - 로컬 브라우저 접근, Codex 실행 환경 네트워크, Docker Desktop 포트 publish 상태를 분리해 재확인
  - 2026-06-27 Phase1 구현 완료 재검증 시점에는 backend host API curl이 정상 응답했으나, 기존 관찰 사항은 재현 가능성 확인 대상으로 유지
  - Phase2 검증에서도 host API curl과 화면 접속은 정상 확인됨
  - Phase3 검증에서도 브라우저 화면 접속, API curl, Docker Compose 컨테이너 상태 확인은 정상
  - 최종 기능 검증에서도 host API curl, Docker Compose 컨테이너 상태, Frontend 화면 접속은 정상

## ISSUE-003 API query parameter 일부 미구현

- 관찰 일시: 2026-06-27
- 상태: Phase2 해결
- 우선순위: 완료
- 대상: Backend 내 업무 API
- 내용:
  - `docs/03_api_spec.md`에는 `scope`, `projectPage`, `projectSize`, `waitingPage`, `waitingSize`, `page`, `size` 등이 정의되어 있음
  - Phase1 구현은 화면 검증에 필요한 최소 조회 중심으로 구현되어 `scope`는 `INTEGRATED` 고정, `/card` paging은 미구현
  - `/unscheduled`는 `size`만 지원하며 `totalCount`는 전체 건수가 아니라 조회된 항목 수 기준
- Phase1 처리:
  - 프로토타입 화면 동작에는 영향이 없어 코드 수정하지 않음
- 후속 검토:
  - Phase2 시작 전 명세 유지/축소 여부 결정
  - paging 및 전체 건수 정책 확정 후 API와 문서 중 하나를 정합화
- Phase2 처리:
  - `scope=INTEGRATED` query parameter를 API에서 수신하고, 미지원 scope는 `400 MY_WORK_BAD_REQUEST`로 응답
  - `/card`의 `projectPage`, `projectSize`, `waitingPage`, `waitingSize` 반영
  - `/unscheduled`의 `page`, `size` 반영
  - `/unscheduled.totalCount`를 조회 목록 크기가 아닌 전체 일정 미등록 업무 건수로 보정
  - API 호출로 paging 및 bad request 응답 확인 완료

## ISSUE-004 화면 세부 UX 일부 미구현

- 관찰 일시: 2026-06-27
- 상태: Phase3 해결
- 우선순위: 완료
- 대상: Frontend 내 업무 화면
- 내용:
  - 진행 프로젝트/대기 프로젝트 좌우 이동 미구현
  - 카드형 하단 안내 문구 미구현
  - 업무 행 `...` 더보기 메뉴 대신 `일정관리`, `WBS` 버튼 직접 표시
  - 일정관리/WBS 상세/일정 등록 실제 라우트 이동 미구현
  - 캘린더 일정 항목의 프로젝트명, 기간, `지연` 텍스트 배지 표시가 제한적
- Phase1 처리:
  - 핵심 조회 및 화면 표시 검증을 우선하여 코드 수정하지 않음
- 후속 검토:
  - PPTX 재현도를 높일 UX 항목의 Phase2 포함 여부 결정
- Phase2 처리:
  - 카드형 하단 안내 문구 추가
  - 업무 행 `...` 더보기 메뉴 추가
  - 일정관리/WBS 상세 메뉴 링크 추가
  - 캘린더 일정 항목에 기간, 프로젝트명, 지연 텍스트 표시 보완
  - 날짜별 팝업 업무 건수 표시 보완
- Phase2 기준 남은 항목:
  - P1: 일정관리/WBS 상세/일정 등록 대상 화면 자체는 아직 미구현
  - P2: 진행 프로젝트/대기 프로젝트 좌우 이동은 아직 미구현
- Phase3 처리:
  - 진행 프로젝트 좌우 이동 구현 및 화면 검증 완료
  - 대기 프로젝트 좌우 이동 구현 및 화면 검증 완료
  - 일정관리 placeholder 화면 추가
  - WBS 상세 placeholder 화면 추가
  - 업무 행 더보기 메뉴에서 placeholder 화면으로 이동 확인
  - 일정 미등록 업무의 `일정 등록` 링크가 일정관리 placeholder 화면으로 연결됨을 확인
- 비고:
  - 실제 일정 저장/수정/삭제, WBS 상세 본기능은 프로토타입 범위 밖으로 유지

## ISSUE-005 Repository 구현 방식과 Spring Data JPA 설계 차이

- 관찰 일시: 2026-06-27
- 상태: Phase2 해결
- 우선순위: 완료
- 대상: Backend Repository Layer
- 내용:
  - `docs/03_api_spec.md`, `docs/04_db_design.md`는 Spring Data JPA 기반을 전제로 함
  - Phase1 구현은 조회 전용 단순화를 위해 `JdbcTemplate` 기반 `JdbcMyWorkRepository`를 사용
  - JPA Entity/Repository는 아직 구현하지 않음
- Phase1 처리:
  - DTO 응답과 Layered Architecture 분리는 유지했으나, Repository 구현 방식은 프로토타입 예외로 기록
- 후속 검토:
  - Phase2에서 JPA Entity/Repository로 전환할지, 조회 전용 JDBC 방식을 유지할지 결정
- Phase2 처리:
  - DB 설계 기준 Entity 추가
  - Spring Data JPA Repository 및 native projection 조회 추가
  - 기존 `JdbcMyWorkRepository` 제거
  - 내 업무 조회 Repository 구현을 JPA 기반으로 전환
  - `user_account.active=true` 조건 반영

## ISSUE-006 접근성 및 팝업 세부 정책 미구현

- 관찰 일시: 2026-06-27
- 상태: Phase3 해결
- 우선순위: 완료
- 대상: Frontend 내 업무 화면
- 내용:
  - 날짜별 팝업 ESC 닫기 미구현
  - 포커스 트랩 미구현
  - `지연` 상태가 일부 화면에서 텍스트 배지가 아니라 색상 위주로 표현됨
  - 긴 프로젝트명/업무명의 전체 정보 확인 수단 미구현
- Phase1 처리:
  - 프로토타입 최소 동작 범위로 보고 코드 수정하지 않음
- 후속 검토:
  - Phase2 화면 고도화 시 접근성 기준과 테스트 방법 확정
- Phase2 처리:
  - 날짜별 팝업 ESC 닫기 구현 및 화면 검증 완료
  - 일부 지연 상태를 텍스트로 표시하도록 보완
- Phase2 기준 남은 항목:
  - 포커스 트랩 미구현
  - 긴 프로젝트명/업무명의 전체 정보 확인 수단 미구현
- Phase3 처리:
  - 날짜별 팝업 포커스 트랩 구현
  - Tab/Shift+Tab 키보드 이동이 팝업 내부에 머무르는지 확인
  - ESC 닫기 재확인
  - 팝업 닫힘 후 이전 날짜 셀로 포커스 복귀 확인
  - 프로젝트명/업무명 주요 표시 요소에 `title` 속성 추가

## ISSUE-007 Frontend 테스트 자동화 부재

- 관찰 일시: 2026-06-27
- 상태: Phase3 해결
- 우선순위: 완료
- 대상: Frontend 테스트
- 내용:
  - Phase2 검증은 `npm run type-check`, `npm run build`, 브라우저 수동 검증 중심으로 수행됨
  - Frontend 단위 테스트 또는 컴포넌트 테스트 도구는 아직 도입되지 않음
  - Phase3에서 화면 변경 범위가 커질 경우 회귀 검증 비용이 증가할 수 있음
- Phase2 처리:
  - 임의 기술 스택 추가 금지 원칙에 따라 테스트 도구를 추가하지 않음
- Phase3 처리:
  - 사용자 승인에 따라 Vitest 도입
  - `npm run test`를 `vitest run`으로 변경
  - 날짜 포맷 유틸 테스트 추가
  - 캘린더 유틸 테스트 추가
  - `npm run test` 성공 확인
- 후속 검토:
  - Phase4 또는 후속 화면 고도화 시 Store, 주요 화면 컴포넌트 테스트 확장 권장

## ISSUE-008 Phase3 이후 카드형/캘린더형 UI 보완

- 관찰 일시: 2026-06-27
- 상태: 해결
- 우선순위: 완료
- 대상: Frontend 내 업무 화면
- 내용:
  - 캘린더형에서 동일 프로젝트 기간 일정이 날짜별로 반복 표시됨
  - 프로젝트 막대 클릭 시 날짜별 팝업이 열려 프로젝트 막대 맥락과 불일치함
  - 같은 날짜에 다른 프로젝트가 있을 때 별도 라인 표시 검증이 부족함
  - 카드형 내 할 일 영역의 좁은 폭 반응형이 미흡함
  - 카드형 데스크톱 폭에서 업무 액션 메뉴가 `overflow` 영향으로 보이지 않음
  - 일정관리/WBS placeholder 화면의 상단 버튼이 `내업무`로 표시되어 이전 화면 맥락을 잃음
  - Breadcrumb 앞쪽 `>` 기호가 화면 간 일관되지 않음
- 처리:
  - 캘린더 일정 표시를 프로젝트 기간 막대 UI로 변경
  - 프로젝트 막대 전용 상세 팝업 추가
  - 같은 날짜 다른 프로젝트가 서로 다른 lane으로 표시되는 테스트 추가
  - 샘플 seed 데이터에 같은 날짜 다른 프로젝트 검증 케이스 반영
  - 카드형 내 할 일 영역을 좁은 폭에서 카드형 레이아웃으로 전환
  - 카드형 업무 액션 메뉴의 데스크톱/모바일 표시 오류 보정
  - 일정관리/WBS placeholder 상단 버튼을 `뒤로가기`로 변경
  - 내 업무 및 placeholder breadcrumb 앞쪽 `>` 기호 제거
- 최종 검증:
  - Frontend test 10건 통과
  - Frontend build 성공
  - Backend test/build 성공
  - API 수동 검증 성공
  - Docker Compose 컨테이너 상태 정상
