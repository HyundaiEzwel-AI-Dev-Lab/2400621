---
name: refactoring
description: H-PMS 프로젝트에서 외부 동작을 유지한 채 코드 구조, 중복, 책임 분리, 타입 안정성, 가독성, 유지보수성을 개선할 때 사용한다. 계획, 사용자 승인, 리팩토링 실행, 회귀 검증, docs/worklog.md 기록, git diff 요약, Conventional Commit 메시지 작성을 표준 절차로 수행한다.
---

# Refactoring

H-PMS 프로젝트에서 동작 변경 없이 내부 구조를 개선할 때 이 절차를 따른다.

## Purpose

- 기존 사용자 동작과 공개 계약을 유지하면서 코드 품질을 개선한다.
- 중복, 복잡도, 책임 혼재, 타입 불명확성을 줄인다.

## Use Cases

- 컴포넌트, store, composable, service, router 구성을 정리할 때
- 중복 로직을 공통 함수나 적절한 모듈로 이동할 때
- 타입 안정성, 이름, 파일 구조, 책임 분리를 개선할 때
- 테스트 가능한 구조로 코드를 재배치할 때

## Workflow

### 1. Plan

리팩토링 목표와 유지해야 할 동작을 확인한다.

- 개선하려는 문제
- 변경하지 말아야 할 사용자 동작
- 변경하지 말아야 할 API, route, prop, event, store 계약
- 리팩토링 대상과 제외 대상

영향 범위를 분석한다.

- 관련 파일과 모듈
- 호출자와 의존성
- 테스트 영향
- 회귀 위험이 있는 화면 또는 흐름

작은 단위의 리팩토링 계획을 제시한다.

- 수정 대상 파일
- 구조 변경 방식
- 동작 보존 확인 방법
- 테스트/빌드 방법
- `docs/worklog.md` 기록 방식

### 2. Approval

계획을 사용자에게 제시하고 승인받는다. 승인 전에는 파일을 수정하지 않는다.

### 3. Execute

승인된 범위 안에서 작게 변경한다. 리팩토링 중 기능 변경 필요성이 발견되면 멈추고 별도 변경 요청으로 분리한다.

### 4. Verify

회귀 검증을 수행한다.

```bash
cd frontend && npm test
cd frontend && npm run type-check
cd frontend && npm run build
```

가능하면 변경 전후 동작이 같다는 근거를 함께 제시한다. 테스트 또는 빌드가 실패하면 완료 처리하지 않는다.

### 5. Record

`docs/worklog.md`에 기록한다.

- 날짜
- 리팩토링 목표
- 유지한 동작
- 변경 파일
- 검증 결과
- 남은 위험 또는 후속 개선

커밋 전 `git diff` 요약을 제시하고 Conventional Commit 메시지를 작성한다.

예시:

```text
refactor(reservation): split room assignment helpers
refactor(store): clarify booking state transitions
```

## Forbidden

- 승인 전 파일을 수정하지 않는다.
- 기능 변경, 정책 변경, UI 동작 변경을 리팩토링에 섞지 않는다.
- 공개 계약을 변경하지 않는다.
- 대규모 포맷팅만으로 의미 있는 변경을 가리지 않는다.
- 검증 없이 완료 처리하지 않는다.

## Deliverables

- 리팩토링 목표
- 보존할 동작과 계약
- 영향 범위 분석
- 리팩토링 계획
- 변경 파일 목록
- 회귀 검증 결과
- `docs/worklog.md` 기록
- `git diff` 요약
- Conventional Commit 메시지

## Completion Criteria

- 사용자 승인 후에만 변경했다.
- 외부 동작을 유지했다.
- 관련 테스트, 타입 검사, 빌드가 성공했다.
- `docs/worklog.md`를 업데이트했다.
- 커밋 전 `git diff` 요약과 커밋 메시지를 제시했다.
