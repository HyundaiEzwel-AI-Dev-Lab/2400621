---
name: new-feature
description: H-PMS 프로젝트에서 신규 화면, 사용자 흐름, 도메인 기능, 상태 관리, route, component, store, API 연동을 추가할 때 사용한다. 요구사항 확인, 수용 기준 정의, 설계와 영향 범위 분석, 사용자 승인, 구현, 검증, docs/worklog.md 기록, git diff 요약, Conventional Commit 메시지 작성을 표준 절차로 수행한다.
---

# New Feature

H-PMS 프로젝트에 신규 기능을 개발할 때 이 절차를 따른다.

## Purpose

- 신규 기능을 요구사항과 수용 기준에 맞춰 구현한다.
- 기존 기능과의 충돌, 상태 흐름 누락, 검증 누락을 줄인다.

## Use Cases

- 새 화면, route, component, store를 추가할 때
- 신규 사용자 흐름 또는 업무 흐름을 구현할 때
- 새 입력 검증, 상태 전이, 목록/상세/검색 기능을 추가할 때
- API 연동 또는 mock 기반 기능을 추가할 때

## Workflow

### 1. Plan

기능 요구사항을 확인한다.

- 사용자 목표
- 주요 시나리오
- 입력과 출력
- 상태와 예외 흐름
- 권한, 검증, 에러 처리
- 완료 기준

수용 기준을 작성한다.

- 정상 시나리오
- 예외 시나리오
- UI 표시 기준
- 데이터 갱신 기준
- 테스트 가능한 조건

설계와 영향 범위를 분석한다.

- 추가/수정할 화면, route, component, store, type
- 기존 기능과의 연결점
- 데이터 구조와 상태 관리
- 테스트 영향
- 회귀 위험

구현 계획을 제시한다.

- 수정 대상 파일
- 새로 만들 파일
- 구현 순서
- 테스트/빌드 방법
- `docs/worklog.md` 기록 방식

### 2. Approval

요구사항, 수용 기준, 구현 계획을 사용자에게 제시하고 승인받는다. 승인 전에는 파일을 생성하거나 수정하지 않는다.

### 3. Execute

승인된 범위 안에서 구현한다. 요구사항에 없는 기능이나 UX 정책은 임의로 확정하지 말고 사용자에게 확인한다.

### 4. Verify

기능 검증과 프로젝트 검증을 수행한다.

```bash
cd frontend && npm test
cd frontend && npm run type-check
cd frontend && npm run build
```

필요하면 핵심 수용 기준별 수동 확인 결과도 기록한다. 테스트 또는 빌드가 실패하면 완료 처리하지 않는다.

### 5. Record

`docs/worklog.md`에 기록한다.

- 날짜
- 기능 요약
- 수용 기준
- 영향 범위
- 변경 파일
- 테스트/빌드 결과
- 남은 이슈 또는 후속 작업

커밋 전 `git diff` 요약을 제시하고 Conventional Commit 메시지를 작성한다.

예시:

```text
feat(reservation): add room availability calendar
feat(checkin): add guest status update workflow
```

## Forbidden

- 수용 기준 없이 구현하지 않는다.
- 승인 전 파일을 생성하거나 수정하지 않는다.
- 요구사항에 없는 정책, 권한, UX를 임의로 추가하지 않는다.
- 기존 기능 회귀 가능성을 검토하지 않고 완료 처리하지 않는다.
- 테스트 또는 빌드 실패를 무시하지 않는다.

## Deliverables

- 기능 요구사항 요약
- 수용 기준
- 설계와 영향 범위 분석
- 구현 계획
- 변경 파일 목록
- 검증 결과
- `docs/worklog.md` 기록
- `git diff` 요약
- Conventional Commit 메시지

## Completion Criteria

- 사용자 승인 후에만 구현했다.
- 수용 기준을 만족했다.
- 관련 테스트, 타입 검사, 빌드가 성공했다.
- `docs/worklog.md`를 업데이트했다.
- 커밋 전 `git diff` 요약과 커밋 메시지를 제시했다.
