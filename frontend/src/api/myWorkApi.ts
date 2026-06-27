import { getApi } from '@/api/httpClient';
import type { DailyTask, MyWorkCalendar, MyWorkCard, MyWorkSummary } from '@/types/myWork';

const BASE_DATE = import.meta.env.VITE_MY_WORK_BASE_DATE ?? '2026-03-22';
const DEFAULT_SCOPE = 'INTEGRATED';

export function fetchMyWorkSummary() {
  return getApi<MyWorkSummary>('/api/v1/my-work/summary', {
    scope: DEFAULT_SCOPE,
    baseDate: BASE_DATE
  });
}

export function fetchMyWorkCard() {
  return getApi<MyWorkCard>('/api/v1/my-work/card', {
    scope: DEFAULT_SCOPE,
    baseDate: BASE_DATE,
    projectPage: 0,
    projectSize: 10,
    waitingPage: 0,
    waitingSize: 10
  });
}

export function fetchMyWorkCalendar(year: number, month: number) {
  return getApi<MyWorkCalendar>('/api/v1/my-work/calendar', {
    scope: DEFAULT_SCOPE,
    year,
    month,
    baseDate: BASE_DATE
  });
}

export function fetchDailyTasks(date: string) {
  return getApi<DailyTask>('/api/v1/my-work/calendar/daily', {
    scope: DEFAULT_SCOPE,
    date,
    baseDate: BASE_DATE
  });
}
