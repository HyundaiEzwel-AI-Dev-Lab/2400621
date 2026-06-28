---
name: change-request
description: H-PMS 프로젝트에서 요구사항 수정, 운영 수정, 버그 수정, 정책 변경, 기능 변경 요청을 처리할 때 사용한다. 수정 요건 확인, 영향 범위 분석, 계획 수립, 사용자 승인, 실행, 검증, docs/worklog.md 기록, git diff 요약, Conventional Commit 메시지 작성을 표준 절차로 수행한다.
---

# Change Request

H-PMS 프로젝트의 요구사항 수정 또는 운영 수정 건을 처리할 때 이 절차를 따른다.

## Purpose

- 기존 요구사항, 운영 정책, 화면 동작, 도메인 규칙, 버그성 동작을 안전하게 수정한다.
- 수정 전 영향 범위와 완료 기준을 명확히 하여 의도하지 않은 회귀를 줄인다.

## Use Cases

- 운영 중 발견된 수정 요청을 처리할 때
- 기존 기능의 동작, 검증 규칙, 표시 문구, 상태 전이를 변경할 때
- 버그 수정과 요구사항 변경의 경계가 있는 작업을 처리할 때

## Workflow

### 1. Plan

수정 요건을 먼저 확인한다.

- 변경 목적
- 현재 문제 또는 운영 수정 사유
- 기대 동작
- 완료 기준
- 제약 조건
- 불명확한 사항

영향 범위를 반드시 분석한다.

- 관련 화면, API, 도메인 로직
- 관련 파일과 모듈
- 데이터 구조, 타입, 스키마 영향
- 테스트 영향
- 빌드 또는 배포 영향
- 회귀 가능성이 있는 기존 기능

수정 계획을 제시한다.

- 수정 대상 파일
- 변경 방식
- 테스트/빌드 방법
- `docs/worklog.md` 기록 방식
- 예상 위험과 확인 방법

### 2. Approval

수정 계획을 제시한 뒤 사용자 승인을 받는다. 승인 전에는 어떤 파일도 생성, 수정, 삭제하지 않는다.

### 3. Execute

승인된 범위 안에서만 변경한다. 작업 중 새 영향 범위가 발견되면 즉시 멈추고 사용자에게 알린 뒤 추가 승인을 받는다.

### 4. Verify

관련 테스트와 빌드를 실행한다.

```bash
cd frontend && npm test
cd frontend && npm run type-check
cd frontend && npm run build
```

변경 범위가 매우 좁아 일부 명령을 생략할 때는 이유를 명확히 보고한다. 테스트 또는 빌드가 실패하면 완료 처리하지 않는다.

### 5. Record

변경사항을 `docs/worklog.md`에 기록한다.

- 날짜
- 요청 요약
- 영향 범위
- 변경 파일
- 테스트/빌드 결과
- 남은 이슈 또는 주의사항

커밋 전 `git diff`를 확인하고 사용자에게 요약한다.

- 파일별 변경 내용
- 주요 로직 변경
- 테스트/빌드 결과
- worklog 기록 여부
- 커밋에 포함할 변경 범위

Conventional Commit 형식으로 커밋 메시지를 작성한다.

```text
<type>(<scope>): <summary>
```

예시:

```text
fix(reservation): correct room assignment validation
feat(checkin): update operational status flow
docs(worklog): record change request handling
```

## Forbidden

- 승인 전 코드, 설정, 문서, 테스트 파일을 수정하지 않는다.
- 영향 범위 분석을 생략하지 않는다.
- 실패한 테스트 또는 빌드를 성공처럼 보고하지 않는다.
- 승인된 범위를 벗어난 리팩토링이나 기능 추가를 끼워 넣지 않는다.
- 사용자가 명시적으로 승인하기 전에는 커밋하지 않는다.

## Deliverables

- 수정 요건 요약
- 영향 범위 분석
- 수정 계획
- 변경 파일 목록
- 검증 결과
- `docs/worklog.md` 기록
- `git diff` 요약
- Conventional Commit 메시지

## Completion Criteria

- 사용자 승인 후에만 변경했다.
- 승인된 범위 안에서 변경했다.
- 관련 검증이 성공했거나, 실행하지 못한 이유를 명확히 보고했다.
- `docs/worklog.md`를 업데이트했다.
- 커밋 전 `git diff` 요약과 커밋 메시지를 제시했다.
