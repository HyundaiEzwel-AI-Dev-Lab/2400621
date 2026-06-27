import { describe, expect, it } from 'vitest';

import { createCalendarDays, createCalendarWeeks } from '@/utils/calendarUtils';
import type { CalendarEvent } from '@/types/myWork';

const events: CalendarEvent[] = [
  {
    eventId: 1,
    taskId: 1,
    taskName: '단위 테스트',
    projectId: 1,
    projectName: '프로모션 운영',
    startDate: '2026-03-18',
    endDate: '2026-03-20',
    displayDateText: '~ 3/20',
    delayed: true
  },
  {
    eventId: 2,
    taskId: 2,
    taskName: '일일 업무',
    projectId: 1,
    projectName: '프로모션 운영',
    startDate: null,
    endDate: '2026-03-21',
    displayDateText: '~ 3/21',
    delayed: false
  }
];

describe('calendarUtils', () => {
  it('creates a fixed 6-week calendar grid', () => {
    const days = createCalendarDays(2026, 3, events);

    expect(days).toHaveLength(42);
    expect(days[0].dateKey).toBe('2026-03-01');
    expect(days[41].dateKey).toBe('2026-04-11');
  });

  it('places range and one-day events on matching dates', () => {
    const days = createCalendarDays(2026, 3, events);
    const march19 = days.find((day) => day.dateKey === '2026-03-19');
    const march21 = days.find((day) => day.dateKey === '2026-03-21');

    expect(march19?.events.map((event) => event.taskName)).toEqual(['단위 테스트']);
    expect(march21?.events.map((event) => event.taskName)).toEqual(['일일 업무']);
  });

  it('groups continuous events from the same project into a calendar bar', () => {
    const weeks = createCalendarWeeks(2026, 3, events);
    const week = weeks[2];

    expect(week.bars).toHaveLength(1);
    expect(week.bars[0]).toMatchObject({
      projectId: 1,
      projectName: '프로모션 운영',
      startDateKey: '2026-03-18',
      endDateKey: '2026-03-21',
      startColumn: 4,
      span: 4,
      taskCount: 2,
      delayed: true
    });
  });

  it('splits a project bar at the week boundary', () => {
    const weeks = createCalendarWeeks(2026, 3, [
      {
        eventId: 3,
        taskId: 3,
        taskName: '통합 테스트',
        projectId: 2,
        projectName: '정산 시스템',
        startDate: '2026-03-06',
        endDate: '2026-03-10',
        displayDateText: '~ 3/10',
        delayed: false
      }
    ]);

    expect(weeks[0].bars[0]).toMatchObject({
      startDateKey: '2026-03-06',
      endDateKey: '2026-03-07',
      startColumn: 6,
      span: 2,
      continuesToNextWeek: true
    });
    expect(weeks[1].bars[0]).toMatchObject({
      startDateKey: '2026-03-08',
      endDateKey: '2026-03-10',
      startColumn: 1,
      span: 3,
      continuesFromPreviousWeek: true
    });
  });

  it('assigns different lanes to overlapping project bars', () => {
    const weeks = createCalendarWeeks(2026, 3, [
      {
        eventId: 4,
        taskId: 4,
        taskName: '개발',
        projectId: 3,
        projectName: '회원 시스템',
        startDate: '2026-03-16',
        endDate: '2026-03-18',
        displayDateText: '~ 3/18',
        delayed: false
      },
      {
        eventId: 5,
        taskId: 5,
        taskName: '검수',
        projectId: 4,
        projectName: '주문 시스템',
        startDate: '2026-03-17',
        endDate: '2026-03-20',
        displayDateText: '~ 3/20',
        delayed: false
      }
    ]);

    expect(weeks[2].laneCount).toBe(2);
    expect(weeks[2].bars.map((bar) => bar.lane)).toEqual([0, 1]);
  });

  it('keeps different projects on the same day as separate bars', () => {
    const weeks = createCalendarWeeks(2026, 3, [
      {
        eventId: 6,
        taskId: 6,
        taskName: '단위 테스트',
        projectId: 5,
        projectName: '프로모션 운영',
        startDate: '2026-03-20',
        endDate: '2026-03-20',
        displayDateText: '~ 3/20',
        delayed: false
      },
      {
        eventId: 7,
        taskId: 7,
        taskName: '바우처 개발',
        projectId: 6,
        projectName: '바우처 정책 변경',
        startDate: '2026-03-20',
        endDate: '2026-03-20',
        displayDateText: '~ 3/20',
        delayed: false
      }
    ]);

    const week = weeks[2];

    expect(week.bars).toHaveLength(2);
    expect(week.laneCount).toBe(2);
    expect(week.bars.map((bar) => bar.projectName)).toEqual(['프로모션 운영', '바우처 정책 변경']);
    expect(week.bars.map((bar) => bar.startColumn)).toEqual([6, 6]);
    expect(week.bars.map((bar) => bar.span)).toEqual([1, 1]);
    expect(week.bars.map((bar) => bar.lane)).toEqual([0, 1]);
  });
});
