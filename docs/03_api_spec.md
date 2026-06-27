# 내 업무 API 설계서

## 1. 문서 목적

본 문서는 내 업무 기능 구현을 위한 REST API를 정의한다. 설계 기준은 `docs/00_project_context.md`를 따른다.

- Backend: Spring Boot 3.x + Java 21
- Architecture: Layered Architecture
- Database: PostgreSQL
- API Style: REST API
- Entity 직접 노출 금지, DTO 사용
- OpenAPI(Swagger) 제공 대상

## 2. 공통 규칙

### 2.1 Base URL

| 환경 | Base URL |
| --- | --- |
| Local | `/api/v1` |

### 2.2 인증 기준

프로토타입 단계에서는 로그인 사용자를 임시 사용자로 가정할 수 있다. 단, API 설계는 로그인 사용자 기준으로 내 업무를 조회하는 구조를 유지한다.

| 항목 | 설계 |
| --- | --- |
| 사용자 식별 | 인증 컨텍스트의 사용자 ID |
| 프로토타입 대체 | 요청 헤더 `X-User-Id` 사용 가능 |
| 운영 전환 | Spring Security 인증 사용자로 대체 |

### 2.3 공통 응답 형식

```json
{
  "data": {},
  "timestamp": "2026-06-27T00:00:00+09:00"
}
```

### 2.4 공통 오류 응답 형식

```json
{
  "code": "MY_WORK_ERROR",
  "message": "요청을 처리할 수 없습니다.",
  "timestamp": "2026-06-27T00:00:00+09:00"
}
```

### 2.5 공통 HTTP 상태 코드

| 상태 코드 | 의미 |
| --- | --- |
| 200 | 정상 조회 |
| 400 | 잘못된 요청 |
| 401 | 인증 필요 |
| 403 | 권한 없음 |
| 404 | 리소스 없음 |
| 500 | 서버 오류 |

### 2.6 D-day 표현 규칙

| API 필드 | 의미 | 화면 표시 |
| --- | --- | --- |
| `dDay < 0` | 마감일까지 남은 일수 | `D-N` |
| `dDay = 0` | 오늘 마감 | `D-Day` |
| `dDay > 0` | 마감일 초과 일수 | `D+N` |

예: `dDay = -14`는 `D-14`, `dDay = 2`는 `D+2`로 표시한다.

## 3. API 목록

| API ID | Method | Endpoint | 설명 |
| --- | --- | --- | --- |
| API-MYWORK-001 | GET | `/api/v1/my-work/summary` | 내 업무 요약 지표 조회 |
| API-MYWORK-002 | GET | `/api/v1/my-work/card` | 카드형 화면 데이터 조회 |
| API-MYWORK-003 | GET | `/api/v1/my-work/calendar` | 캘린더형 화면 데이터 조회 |
| API-MYWORK-004 | GET | `/api/v1/my-work/calendar/daily` | 특정 날짜 내 업무 목록 조회 |
| API-MYWORK-005 | GET | `/api/v1/my-work/unscheduled` | 일정 미등록 업무 목록 조회 |
| API-MYWORK-006 | GET | `/api/v1/my-work/tasks/{taskId}/actions` | 업무별 가능 액션 조회 |

## 4. API-MYWORK-001 내 업무 요약 지표 조회

### 4.1 Request

| 항목 | 값 |
| --- | --- |
| Method | GET |
| Endpoint | `/api/v1/my-work/summary` |

Query Parameters:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| scope | string | N | 조회 범위. 기본값 `INTEGRATED` |
| baseDate | date | N | 집계 기준일. 기본값 오늘 |

### 4.2 Response

```json
{
  "data": {
    "scope": "INTEGRATED",
    "progressProjectCount": 5,
    "myTaskCount": 4,
    "weeklyDueCount": 2,
    "delayedCount": 1,
    "waitingProjectCount": 2
  },
  "timestamp": "2026-06-27T00:00:00+09:00"
}
```

## 5. API-MYWORK-002 카드형 화면 데이터 조회

### 5.1 Request

| 항목 | 값 |
| --- | --- |
| Method | GET |
| Endpoint | `/api/v1/my-work/card` |

Query Parameters:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| scope | string | N | 조회 범위. 기본값 `INTEGRATED` |
| baseDate | date | N | 기준일 |
| projectPage | integer | N | 진행 프로젝트 페이지 |
| projectSize | integer | N | 진행 프로젝트 조회 수 |
| waitingPage | integer | N | 대기 프로젝트 페이지 |
| waitingSize | integer | N | 대기 프로젝트 조회 수 |

### 5.2 Response

```json
{
  "data": {
    "summary": {
      "progressProjectCount": 5,
      "myTaskCount": 4,
      "weeklyDueCount": 2,
      "delayedCount": 1,
      "waitingProjectCount": 2
    },
    "progressProjects": [
      {
        "projectId": 101,
        "projectName": "주문취소 시 쿠폰 할인취소 정보 노출",
        "status": "TESTING",
        "statusName": "테스트",
        "targetDate": "2026-03-20",
        "dDay": -14,
        "progressRate": 80,
        "metricA": 6,
        "metricB": 9,
        "metricC": 8
      }
    ],
    "myTasks": [
      {
        "taskId": 1001,
        "taskName": "단위 테스트",
        "projectId": 201,
        "projectName": "프로모션 운영 프로세스 및 기능 개발",
        "dueDate": "2026-03-20",
        "dDay": 2,
        "delayed": true,
        "progressRate": 73,
        "scheduled": true,
        "scheduleActionEnabled": true,
        "wbsDetailActionEnabled": true
      }
    ],
    "waitingProjects": [
      {
        "projectId": 301,
        "projectName": "전사 프로젝트 관리 시스템 구축",
        "status": "RECEIVED",
        "statusName": "접수",
        "departmentName": "테크담당",
        "openDate": "2026-03-20",
        "openDateText": null
      }
    ]
  },
  "timestamp": "2026-06-27T00:00:00+09:00"
}
```

## 6. API-MYWORK-003 캘린더형 화면 데이터 조회

### 6.1 Request

| 항목 | 값 |
| --- | --- |
| Method | GET |
| Endpoint | `/api/v1/my-work/calendar` |

Query Parameters:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| scope | string | N | 조회 범위. 기본값 `INTEGRATED` |
| year | integer | Y | 조회 연도 |
| month | integer | Y | 조회 월 |
| baseDate | date | N | D-day/지연 집계 기준일. 기본값 서버 오늘 |

### 6.2 Response

```json
{
  "data": {
    "summary": {
      "progressProjectCount": 5,
      "myTaskCount": 4,
      "weeklyDueCount": 2,
      "delayedCount": 1,
      "waitingProjectCount": 2
    },
    "year": 2026,
    "month": 3,
    "events": [
      {
        "eventId": 5001,
        "taskId": 1001,
        "taskName": "바우처 특복 배정",
        "projectId": 201,
        "projectName": "DL이앤씨 바우처 정책 변경_숙박바우처",
        "startDate": "2026-03-21",
        "endDate": "2026-03-21",
        "displayDateText": "~ 3/21",
        "delayed": false
      }
    ],
    "unscheduledTaskCount": 2,
    "unscheduledTasks": [
      {
        "taskId": 2001,
        "projectId": 201,
        "projectName": "DL이앤씨 바우처 정책 변경_숙박바우처",
        "wbsTaskName": "HIMS 바우처 강제회수",
        "scheduleRegisterable": true
      }
    ]
  },
  "timestamp": "2026-06-27T00:00:00+09:00"
}
```

설계 기준:

- 캘린더형 화면 초기 진입에 필요한 일정 미등록 업무 패널 데이터는 본 API 응답에 포함한다.
- `unscheduledTasks`는 프로토타입 기준 최대 20건까지 반환한다.
- 20건을 초과하는 전체 목록이 필요한 경우 `API-MYWORK-005`를 사용한다.

## 7. API-MYWORK-004 특정 날짜 내 업무 목록 조회

### 7.1 Request

| 항목 | 값 |
| --- | --- |
| Method | GET |
| Endpoint | `/api/v1/my-work/calendar/daily` |

Query Parameters:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| date | date | Y | 조회 날짜 |
| scope | string | N | 조회 범위. 기본값 `INTEGRATED` |

### 7.2 Response

```json
{
  "data": {
    "date": "2026-03-22",
    "taskCount": 5,
    "tasks": [
      {
        "taskId": 1001,
        "taskName": "단위테스트",
        "dueDate": "2026-03-20",
        "displayDueText": "~ 3/20",
        "projectId": 201,
        "projectName": "프로모션 운영 프로세스 개발",
        "delayed": true
      }
    ]
  },
  "timestamp": "2026-06-27T00:00:00+09:00"
}
```

## 8. API-MYWORK-005 일정 미등록 업무 목록 조회

### 8.1 Request

| 항목 | 값 |
| --- | --- |
| Method | GET |
| Endpoint | `/api/v1/my-work/unscheduled` |

Query Parameters:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| scope | string | N | 조회 범위. 기본값 `INTEGRATED` |
| page | integer | N | 페이지 번호 |
| size | integer | N | 페이지 크기 |

### 8.2 Response

```json
{
  "data": {
    "totalCount": 2,
    "items": [
      {
        "taskId": 2001,
        "projectId": 201,
        "projectName": "DL이앤씨 바우처 정책 변경_숙박바우처",
        "wbsTaskName": "HIMS 바우처 강제회수",
        "scheduleRegisterable": true
      }
    ]
  },
  "timestamp": "2026-06-27T00:00:00+09:00"
}
```

사용 기준:

- 캘린더형 초기 화면은 `API-MYWORK-003`의 `unscheduledTasks`를 우선 사용한다.
- 본 API는 일정 미등록 업무가 20건을 초과하거나 별도 목록 화면/패널 확장이 필요한 경우 사용한다.

## 9. API-MYWORK-006 업무별 가능 액션 조회

### 9.1 Request

| 항목 | 값 |
| --- | --- |
| Method | GET |
| Endpoint | `/api/v1/my-work/tasks/{taskId}/actions` |

Path Parameters:

| 이름 | 타입 | 필수 | 설명 |
| --- | --- | --- | --- |
| taskId | long | Y | 업무 ID |

### 9.2 Response

```json
{
  "data": {
    "taskId": 1001,
    "projectId": 201,
    "actions": {
      "scheduleManagement": true,
      "wbsDetail": true
    }
  },
  "timestamp": "2026-06-27T00:00:00+09:00"
}
```

설계 기준:

- Backend는 Frontend 라우트 경로를 직접 반환하지 않는다.
- Frontend는 `taskId`, `projectId`, `actions` 값을 바탕으로 Vue Router 경로를 생성한다.
- 프로토타입에서 `일정 등록`은 일정관리 화면 이동으로 처리하며, 일정 저장 API는 제공하지 않는다.

## 10. 주요 Enum

### 10.1 Scope

| 값 | 설명 |
| --- | --- |
| INTEGRATED | 통합 |

### 10.2 ProjectStatus

| 값 | 표시명 |
| --- | --- |
| TESTING | 테스트 |
| DISCUSSING | 협의중 |
| PROCESSING | 처리중 |
| RECEIVED | 접수 |

### 10.3 TaskScheduleStatus

| 값 | 설명 |
| --- | --- |
| SCHEDULED | 일정 등록 |
| UNSCHEDULED | 일정 미등록 |

### 10.4 TaskStatus

| 값 | 설명 |
| --- | --- |
| TODO | 대기 |
| IN_PROGRESS | 진행중 |
| DONE | 완료 |
| HOLD | 보류 |

### 10.5 DisplayStatus

| 값 | 설명 |
| --- | --- |
| DELAYED | 지연 |
| DUE_TODAY | 오늘 마감 |
| UNSCHEDULED | 일정 미등록 |

DisplayStatus는 DB에 저장하지 않는 화면 파생 상태다. Service Layer에서 기준일, 일정, 업무 상태를 바탕으로 계산한다.

## 11. Backend Layered Architecture 매핑

| Layer | 책임 |
| --- | --- |
| Controller | REST API 요청/응답 처리, DTO 변환 |
| Service | 내 업무 집계, D-day, 지연 판정, 조회 정책 수행 |
| Repository | Spring Data JPA 기반 데이터 조회 |
| Domain | Project, WbsTask, TaskSchedule, Assignment 등 업무 모델 |
| DTO | API 요청/응답 전용 객체 |

## 12. 구현 제외

본 문서는 API 설계 문서이며 구현은 포함하지 않는다.

- Controller 클래스 생성 없음
- Service 클래스 생성 없음
- Repository 클래스 생성 없음
- OpenAPI 설정 파일 생성 없음
