import type { CalendarEvent } from '@/types/myWork';
import { toDateKey } from '@/utils/dateFormatter';

export interface CalendarDay {
  date: Date;
  dateKey: string;
  day: number;
  inCurrentMonth: boolean;
  events: CalendarEvent[];
}

export interface CalendarBar {
  id: string;
  projectId: number;
  projectName: string;
  startDateKey: string;
  endDateKey: string;
  startColumn: number;
  span: number;
  lane: number;
  taskCount: number;
  taskNames: string[];
  delayed: boolean;
  continuesFromPreviousWeek: boolean;
  continuesToNextWeek: boolean;
}

export interface CalendarWeek {
  weekIndex: number;
  days: CalendarDay[];
  bars: CalendarBar[];
  laneCount: number;
}

interface EventRange {
  projectId: number;
  projectName: string;
  startDateKey: string;
  endDateKey: string;
  taskIds: Set<number>;
  taskNames: Set<string>;
  delayed: boolean;
}

export function createCalendarDays(year: number, month: number, events: CalendarEvent[]): CalendarDay[] {
  const firstDay = new Date(year, month - 1, 1);
  const start = new Date(firstDay);
  start.setDate(firstDay.getDate() - firstDay.getDay());

  return Array.from({ length: 42 }, (_, index) => {
    const date = new Date(start);
    date.setDate(start.getDate() + index);
    const dateKey = toDateKey(date);

    return {
      date,
      dateKey,
      day: date.getDate(),
      inCurrentMonth: date.getMonth() === month - 1,
      events: events.filter((event) => includesDate(event, dateKey))
    };
  });
}

export function createCalendarWeeks(year: number, month: number, events: CalendarEvent[]): CalendarWeek[] {
  const days = createCalendarDays(year, month, events);
  const projectRanges = createProjectRanges(events);

  return Array.from({ length: 6 }, (_, weekIndex) => {
    const weekDays = days.slice(weekIndex * 7, weekIndex * 7 + 7);
    const weekStart = weekDays[0].dateKey;
    const weekEnd = weekDays[6].dateKey;
    const bars = projectRanges
      .filter((range) => overlaps(range.startDateKey, range.endDateKey, weekStart, weekEnd))
      .map((range, rangeIndex) => createCalendarBar(range, weekDays, weekIndex, rangeIndex));

    assignBarLanes(bars);

    return {
      weekIndex,
      days: weekDays,
      bars,
      laneCount: Math.max(...bars.map((bar) => bar.lane + 1), 0)
    };
  });
}

function includesDate(event: CalendarEvent, dateKey: string): boolean {
  const startDate = event.startDate ?? event.endDate;
  const endDate = event.endDate ?? event.startDate;

  if (!startDate || !endDate) {
    return false;
  }

  return startDate <= dateKey && dateKey <= endDate;
}

function createProjectRanges(events: CalendarEvent[]): EventRange[] {
  const grouped = new Map<number, EventRange[]>();

  events.forEach((event) => {
    const eventRange = toEventRange(event);
    if (!eventRange) {
      return;
    }
    grouped.set(event.projectId, [...(grouped.get(event.projectId) ?? []), eventRange]);
  });

  return Array.from(grouped.values()).flatMap((ranges) => mergeRanges(ranges));
}

function toEventRange(event: CalendarEvent): EventRange | null {
  const startDate = event.startDate ?? event.endDate;
  const endDate = event.endDate ?? event.startDate;

  if (!startDate || !endDate) {
    return null;
  }

  return {
    projectId: event.projectId,
    projectName: event.projectName,
    startDateKey: startDate <= endDate ? startDate : endDate,
    endDateKey: startDate <= endDate ? endDate : startDate,
    taskIds: new Set([event.taskId]),
    taskNames: new Set([event.taskName]),
    delayed: event.delayed
  };
}

function mergeRanges(ranges: EventRange[]): EventRange[] {
  return ranges
    .sort((left, right) => left.startDateKey.localeCompare(right.startDateKey))
    .reduce<EventRange[]>((merged, range) => {
      const current = merged[merged.length - 1];

      if (!current || range.startDateKey > addDays(current.endDateKey, 1)) {
        merged.push(range);
        return merged;
      }

      current.endDateKey = maxDateKey(current.endDateKey, range.endDateKey);
      current.delayed = current.delayed || range.delayed;
      range.taskIds.forEach((taskId) => current.taskIds.add(taskId));
      range.taskNames.forEach((taskName) => current.taskNames.add(taskName));

      return merged;
    }, []);
}

function createCalendarBar(range: EventRange, weekDays: CalendarDay[], weekIndex: number, rangeIndex: number): CalendarBar {
  const weekStart = weekDays[0].dateKey;
  const weekEnd = weekDays[6].dateKey;
  const startDateKey = maxDateKey(range.startDateKey, weekStart);
  const endDateKey = minDateKey(range.endDateKey, weekEnd);
  const startColumn = weekDays.findIndex((day) => day.dateKey === startDateKey) + 1;
  const endColumn = weekDays.findIndex((day) => day.dateKey === endDateKey) + 1;

  return {
    id: `${range.projectId}-${weekIndex}-${rangeIndex}-${startDateKey}`,
    projectId: range.projectId,
    projectName: range.projectName,
    startDateKey,
    endDateKey,
    startColumn,
    span: endColumn - startColumn + 1,
    lane: 0,
    taskCount: range.taskIds.size,
    taskNames: Array.from(range.taskNames),
    delayed: range.delayed,
    continuesFromPreviousWeek: range.startDateKey < weekStart,
    continuesToNextWeek: range.endDateKey > weekEnd
  };
}

function assignBarLanes(bars: CalendarBar[]) {
  const lanes: boolean[][] = [];

  bars
    .sort((left, right) => left.startColumn - right.startColumn || right.span - left.span)
    .forEach((bar) => {
      const lane = findAvailableLane(lanes, bar.startColumn, bar.span);
      bar.lane = lane;

      for (let column = bar.startColumn - 1; column < bar.startColumn - 1 + bar.span; column += 1) {
        lanes[lane][column] = true;
      }
    });
}

function findAvailableLane(lanes: boolean[][], startColumn: number, span: number) {
  const startIndex = startColumn - 1;
  const endIndex = startIndex + span;

  for (let lane = 0; lane < lanes.length; lane += 1) {
    if (lanes[lane].slice(startIndex, endIndex).every((occupied) => !occupied)) {
      return lane;
    }
  }

  lanes.push(Array.from({ length: 7 }, () => false));
  return lanes.length - 1;
}

function overlaps(startDateKey: string, endDateKey: string, targetStartDateKey: string, targetEndDateKey: string) {
  return startDateKey <= targetEndDateKey && targetStartDateKey <= endDateKey;
}

function addDays(dateKey: string, days: number) {
  const date = new Date(`${dateKey}T00:00:00`);
  date.setDate(date.getDate() + days);
  return toDateKey(date);
}

function minDateKey(left: string, right: string) {
  return left <= right ? left : right;
}

function maxDateKey(left: string, right: string) {
  return left >= right ? left : right;
}
