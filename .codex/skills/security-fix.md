---
name: security-fix
description: H-PMS 프로젝트에서 인증, 인가, 입력 검증, 출력 처리, 민감정보 노출, 의존성 취약점, 보안 회귀를 수정할 때 사용한다. 취약점 영향 분석, 수정 계획, 사용자 승인, 최소 변경 수정, 보안 검증, 테스트/빌드, docs/worklog.md 기록, git diff 요약, Conventional Commit 메시지 작성을 표준 절차로 수행한다.
---

# Security Fix

H-PMS 프로젝트의 보안 취약점 또는 보안 회귀를 수정할 때 이 절차를 따른다.

## Purpose

- 보안 취약점을 최소 변경으로 안전하게 수정한다.
- 취약점 영향, 악용 가능 범위, 회귀 위험을 명확히 관리한다.

## Use Cases

- 인증 또는 인가 누락을 수정할 때
- 입력 검증, 출력 처리, 상태 변경 권한을 강화할 때
- 민감정보 노출, 로그 노출, 토큰/식별자 취급 문제를 수정할 때
- 보안 관련 의존성, 설정, 빌드 경고를 처리할 때

## Workflow

### 1. Plan

취약점 또는 보안 이슈를 요약한다.

- 문제 유형
- 영향 받는 화면, route, component, store, API 연동
- 영향 받는 사용자 또는 데이터
- 악용 가능 조건
- 기대되는 보안 동작

영향 범위를 분석한다.

- 관련 파일과 모듈
- 인증/인가 흐름
- 입력 검증과 출력 처리
- 민감정보 저장, 표시, 로그 여부
- 회귀 위험
- 테스트 영향

수정 계획을 제시한다.

- 최소 변경 전략
- 수정 대상 파일
- 보안 검증 시나리오
- 테스트/빌드 방법
- `docs/worklog.md` 기록 방식

### 2. Approval

수정 계획을 사용자에게 제시하고 승인받는다. 승인 전에는 파일을 생성하거나 수정하지 않는다.

### 3. Execute

승인된 범위 안에서 최소 변경으로 수정한다. 취약점 악용 절차나 민감정보를 불필요하게 상세히 공개하지 않는다.

### 4. Verify

보안 시나리오와 프로젝트 검증을 수행한다.

```bash
cd frontend && npm test
cd frontend && npm run type-check
cd frontend && npm run build
```

필요하면 관련 입력, 권한, 예외 시나리오를 수동으로 확인하고 결과를 기록한다. 테스트 또는 빌드가 실패하면 완료 처리하지 않는다.

### 5. Record

`docs/worklog.md`에 기록한다.

- 날짜
- 보안 이슈 요약
- 영향 범위
- 수정 파일
- 보안 검증 결과
- 테스트/빌드 결과
- 남은 위험 또는 후속 조치

커밋 전 `git diff` 요약을 제시하고 Conventional Commit 메시지를 작성한다.

예시:

```text
fix(auth): enforce permission check before status update
fix(security): prevent sensitive guest data exposure
```

## Forbidden

- 승인 전 파일을 수정하지 않는다.
- 취약점 악용 절차를 불필요하게 상세 공개하지 않는다.
- 민감정보, 토큰, 개인 정보를 로그나 응답에 노출하지 않는다.
- 임시 우회성 보안 처리로 완료하지 않는다.
- 보안 검증 없이 완료 처리하지 않는다.
- 테스트 또는 빌드 실패를 무시하지 않는다.

## Deliverables

- 보안 이슈 요약
- 영향 범위 분석
- 최소 변경 수정 계획
- 변경 파일 목록
- 보안 검증 결과
- 테스트/빌드 결과
- `docs/worklog.md` 기록
- `git diff` 요약
- Conventional Commit 메시지

## Completion Criteria

- 사용자 승인 후에만 변경했다.
- 보안 이슈를 최소 변경으로 수정했다.
- 관련 보안 시나리오를 검증했다.
- 관련 테스트, 타입 검사, 빌드가 성공했다.
- `docs/worklog.md`를 업데이트했다.
- 커밋 전 `git diff` 요약과 커밋 메시지를 제시했다.
