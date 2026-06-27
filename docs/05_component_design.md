# 내 업무 컴포넌트 설계서

## 1. 문서 목적

본 문서는 Vue3 기반 내 업무 화면의 컴포넌트 구조를 정의한다. 설계 기준은 `docs/00_project_context.md`를 따른다.

- Frontend: Vue3
- Nuxt 사용 금지
- Router: Vue Router
- State: Pinia
- Build: Vite
- Language: TypeScript
- API: REST API

## 2. 프론트엔드 설계 원칙

- Composition API를 사용한다.
- TypeScript Strict Mode를 전제로 타입을 설계한다.
- 화면 상태와 API 호출 상태는 Pinia store에서 관리한다.
- 컴포넌트는 화면, 섹션, 표시 컴포넌트로 분리한다.
- API 응답 DTO와 화면 표시 모델은 필요 시 분리한다.
- Nuxt 기능, 파일 기반 라우팅, 서버 사이드 렌더링 전제는 사용하지 않는다.

## 3. 라우트 설계

| Route Name | Path | Component | 설명 |
| --- | --- | --- | --- |
| `my-work` | `/my-work` | `MyWorkView` | 내 업무 메인 화면 |

Query Parameters:

| 이름 | 값 | 설명 |
| --- | --- | --- |
| view | `card`, `calendar` | 보기 방식 |
| year | number | 캘린더 기준 연도 |
| month | number | 캘린더 기준 월 |

## 4. 컴포넌트 트리

```text
MyWorkView
  AppSidebar
  MyWorkHeader
    ViewModeTabs
  MyWorkSummaryBar
    SummaryMetricItem
  MyWorkCardView
    ProgressProjectSection
      ProjectCardCarousel
        ProjectCard
      EmptyState
    MyTaskSection
      MyTaskTable
        MyTaskRow
        TaskActionMenu
      EmptyState
    WaitingProjectSection
      WaitingProjectCarousel
        WaitingProjectCard
      EmptyState
    MyWorkNotice
  MyWorkCalendarView
    CalendarToolbar
    MonthlyCalendar
      CalendarDayCell
        CalendarEventItem
        CalendarMoreButton
    UnscheduledTaskPanel
      UnscheduledTaskItem
      EmptyState
    DailyTaskPopup
      DailyTaskList
```

## 5. 화면 컴포넌트

### 5.1 MyWorkView

| 항목 | 내용 |
| --- | --- |
| 역할 | 내 업무 화면 최상위 컨테이너 |
| 책임 | URL query 해석, 보기 방식 전환, store 초기 로딩 호출 |
| 하위 컴포넌트 | `MyWorkHeader`, `MyWorkSummaryBar`, `MyWorkCardView`, `MyWorkCalendarView` |

상태:

| 상태 | 설명 |
| --- | --- |
| `viewMode` | `card` 또는 `calendar` |
| `selectedYear` | 캘린더 기준 연도 |
| `selectedMonth` | 캘린더 기준 월 |

### 5.2 MyWorkHeader

| 항목 | 내용 |
| --- | --- |
| 역할 | 화면 제목, 브레드크럼, 보기 전환 표시 |
| Props | `viewMode` |
| Emits | `change:viewMode` |

### 5.3 ViewModeTabs

| 항목 | 내용 |
| --- | --- |
| 역할 | 카드형/캘린더형 전환 |
| 표시 항목 | `캘린더형`, `카드형` |
| 동작 | 선택 시 URL query와 store 상태 갱신 |

### 5.4 MyWorkSummaryBar

| 항목 | 내용 |
| --- | --- |
| 역할 | 요약 지표 표시 |
| Props | `summary` |
| 하위 컴포넌트 | `SummaryMetricItem` |

## 6. 카드형 컴포넌트

### 6.1 MyWorkCardView

| 항목 | 내용 |
| --- | --- |
| 역할 | 카드형 화면 섹션 조합 |
| Props | `progressProjects`, `myTasks`, `waitingProjects`, `loading`, `error` |
| 하위 컴포넌트 | `ProgressProjectSection`, `MyTaskSection`, `WaitingProjectSection`, `MyWorkNotice` |

### 6.2 ProgressProjectSection

| 항목 | 내용 |
| --- | --- |
| 역할 | 진행중 프로젝트 영역 |
| Props | `items`, `totalCount` |
| 빈 상태 | `배정된 프로젝트가 없습니다.` |

### 6.3 ProjectCard

| 항목 | 내용 |
| --- | --- |
| 역할 | 진행 프로젝트 카드 표시 |
| Props | `project` |
| 표시 데이터 | 상태, 기준일/D-day, 프로젝트명, 공정률, 관련 건수 |

### 6.4 MyTaskSection

| 항목 | 내용 |
| --- | --- |
| 역할 | 내 할 일 영역 |
| Props | `items`, `totalCount` |
| 빈 상태 | `진행중인 업무가 없습니다.` |

### 6.5 MyTaskTable

| 항목 | 내용 |
| --- | --- |
| 역할 | 내 할 일 테이블 |
| 컬럼 | 업무명, 마감일(D-day), 공정률, 프로젝트명, 액션 |

### 6.6 MyTaskRow

| 항목 | 내용 |
| --- | --- |
| 역할 | 내 할 일 단일 행 |
| Props | `task` |
| 하위 컴포넌트 | `TaskActionMenu` |

### 6.7 TaskActionMenu

| 항목 | 내용 |
| --- | --- |
| 역할 | 업무 행 더보기 메뉴 |
| 메뉴 | 일정관리, WBS 상세 |
| Emits | `openSchedule`, `openWbsDetail` |

### 6.8 WaitingProjectSection

| 항목 | 내용 |
| --- | --- |
| 역할 | 대기 프로젝트 영역 |
| Props | `items`, `totalCount` |
| 빈 상태 | `접수된 프로젝트가 없습니다.` |

### 6.9 WaitingProjectCard

| 항목 | 내용 |
| --- | --- |
| 역할 | 대기 프로젝트 카드 표시 |
| 표시 데이터 | 상태, 프로젝트명, 담당 조직, 예정일 |

### 6.10 MyWorkNotice

| 항목 | 내용 |
| --- | --- |
| 역할 | WBS 및 일정 표시 정책 안내 |
| 표시 위치 | 카드형 화면 하단 |

## 7. 캘린더형 컴포넌트

### 7.1 MyWorkCalendarView

| 항목 | 내용 |
| --- | --- |
| 역할 | 캘린더형 화면 섹션 조합 |
| Props | `year`, `month`, `events`, `unscheduledTasks`, `loading`, `error` |
| 하위 컴포넌트 | `CalendarToolbar`, `MonthlyCalendar`, `UnscheduledTaskPanel`, `DailyTaskPopup` |

### 7.2 CalendarToolbar

| 항목 | 내용 |
| --- | --- |
| 역할 | 월 이동 및 오늘 버튼 |
| Props | `year`, `month` |
| Emits | `prevMonth`, `nextMonth`, `goToday` |

### 7.3 MonthlyCalendar

| 항목 | 내용 |
| --- | --- |
| 역할 | 월간 캘린더 그리드 표시 |
| Props | `year`, `month`, `events` |
| Emits | `openDailyTasks` |

### 7.4 CalendarDayCell

| 항목 | 내용 |
| --- | --- |
| 역할 | 날짜 셀 단위 표시 |
| Props | `date`, `events`, `maxVisibleEventCount` |
| 동작 | 표시 한도 초과 시 `CalendarMoreButton` 표시 |

### 7.5 CalendarEventItem

| 항목 | 내용 |
| --- | --- |
| 역할 | 캘린더 일정 항목 표시 |
| Props | `event` |
| 표시 데이터 | 업무명, 기간/마감일, 프로젝트명, 지연 배지 |

### 7.6 CalendarMoreButton

| 항목 | 내용 |
| --- | --- |
| 역할 | 날짜별 추가 업무 팝업 열기 |
| Props | `hiddenCount` |
| Emits | `click` |

### 7.7 DailyTaskPopup

| 항목 | 내용 |
| --- | --- |
| 역할 | 선택 날짜의 내 업무 목록 팝업 |
| Props | `open`, `date`, `tasks` |
| Emits | `close` |
| 접근성 | ESC 닫기, 포커스 트랩 |

### 7.8 UnscheduledTaskPanel

| 항목 | 내용 |
| --- | --- |
| 역할 | 일정 미등록 업무 목록 표시 |
| Props | `items`, `totalCount` |
| 빈 상태 | `일정 미등록 업무가 없습니다.` |

### 7.9 UnscheduledTaskItem

| 항목 | 내용 |
| --- | --- |
| 역할 | 일정 미등록 업무 단일 항목 |
| Props | `task` |
| Emits | `registerSchedule` |

## 8. Store 설계

### 8.1 useMyWorkStore

| 상태 | 타입 | 설명 |
| --- | --- | --- |
| `summary` | `MyWorkSummary \| null` | 요약 지표 |
| `progressProjects` | `ProjectCardItem[]` | 진행 프로젝트 |
| `myTasks` | `MyTaskItem[]` | 내 할 일 |
| `waitingProjects` | `WaitingProjectItem[]` | 대기 프로젝트 |
| `calendarEvents` | `CalendarEventItem[]` | 캘린더 일정 |
| `unscheduledTasks` | `UnscheduledTaskItem[]` | 일정 미등록 업무 |
| `dailyTasks` | `DailyTaskItem[]` | 날짜별 팝업 업무 |
| `loading` | `boolean` | 로딩 여부 |
| `errorMessage` | `string \| null` | 오류 메시지 |

Actions:

| Action | 설명 | API |
| --- | --- | --- |
| `fetchSummary` | 요약 지표 조회 | `GET /api/v1/my-work/summary` |
| `fetchCardData` | 카드형 데이터 조회 | `GET /api/v1/my-work/card` |
| `fetchCalendarData` | 캘린더 데이터 조회 | `GET /api/v1/my-work/calendar` |
| `fetchDailyTasks` | 특정 날짜 업무 조회 | `GET /api/v1/my-work/calendar/daily` |
| `fetchUnscheduledTasks` | 일정 미등록 업무 조회 | `GET /api/v1/my-work/unscheduled` |
| `fetchTaskActions` | 업무 가능 액션 조회 | `GET /api/v1/my-work/tasks/{taskId}/actions` |

데이터 흐름:

- 카드형 진입 시 `fetchCardData`만 호출한다.
- 캘린더형 진입 시 `fetchCalendarData`만 호출하고, 응답에 포함된 `unscheduledTasks`를 패널에 표시한다.
- 날짜별 더보기 팝업을 열 때만 `fetchDailyTasks`를 호출한다.
- 일정 미등록 업무가 20건을 초과하는 별도 확장 화면이 필요할 때 `fetchUnscheduledTasks`를 사용한다.

## 9. TypeScript 타입 설계

| 타입 | 설명 |
| --- | --- |
| `ViewMode` | `card` 또는 `calendar` |
| `MyWorkSummary` | 요약 지표 |
| `ProjectCardItem` | 진행 프로젝트 카드 |
| `MyTaskItem` | 내 할 일 업무 |
| `WaitingProjectItem` | 대기 프로젝트 카드 |
| `CalendarEventItem` | 캘린더 일정 |
| `UnscheduledTaskItem` | 일정 미등록 업무 |
| `DailyTaskItem` | 날짜별 업무 |
| `TaskActionAvailability` | 일정관리/WBS 상세 가능 액션 |

`TaskActionAvailability`는 Frontend 라우트 경로를 저장하지 않는다. 프로토타입에서는 `taskId`, `projectId`, `scheduleManagement`, `wbsDetail` 값을 기준으로 Vue Router 경로를 생성한다.

## 10. API 모듈 설계

| 모듈 | 책임 |
| --- | --- |
| `myWorkApi` | 내 업무 API 호출 함수 제공 |
| `httpClient` | 공통 REST 요청 처리 |
| `dateFormatter` | 날짜, D-day 표시 포맷 |
| `calendarUtils` | 월간 캘린더 날짜 그리드 계산 |

## 11. 화면 상태 처리

| 상태 | 처리 |
| --- | --- |
| 초기 로딩 | 섹션별 로딩 표시 |
| 조회 성공 | 데이터 렌더링 |
| 빈 상태 | 요구사항의 빈 상태 문구 표시 |
| 오류 | 오류 안내 및 재시도 버튼 표시 |
| 팝업 열림 | 배경 스크롤 제한, 포커스 트랩 |

## 12. 구현 제외

본 문서는 컴포넌트 설계 문서이며 구현은 포함하지 않는다.

- Vue 파일 생성 없음
- Pinia store 생성 없음
- TypeScript 타입 파일 생성 없음
- API 호출 모듈 생성 없음
- CSS/스타일 파일 생성 없음
