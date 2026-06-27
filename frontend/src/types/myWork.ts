export type ViewMode = 'card' | 'calendar';

export interface ApiResponse<T> {
  data: T;
  timestamp: string;
}

export interface MyWorkSummary {
  scope: string;
  progressProjectCount: number;
  myTaskCount: number;
  weeklyDueCount: number;
  delayedCount: number;
  waitingProjectCount: number;
}

export interface ProjectCard {
  projectId: number;
  projectName: string;
  status: string;
  statusName: string;
  targetDate: string | null;
  dDay: number | null;
  progressRate: number;
  metricA: number;
  metricB: number;
  metricC: number;
}

export interface MyTask {
  taskId: number;
  taskName: string;
  projectId: number;
  projectName: string;
  dueDate: string | null;
  dDay: number | null;
  delayed: boolean;
  progressRate: number | null;
  scheduled: boolean;
  scheduleActionEnabled: boolean;
  wbsDetailActionEnabled: boolean;
}

export interface WaitingProject {
  projectId: number;
  projectName: string;
  status: string;
  statusName: string;
  departmentName: string | null;
  openDate: string | null;
  openDateText: string | null;
}

export interface MyWorkCard {
  summary: MyWorkSummary;
  progressProjects: ProjectCard[];
  myTasks: MyTask[];
  waitingProjects: WaitingProject[];
}

export interface CalendarEvent {
  eventId: number;
  taskId: number;
  taskName: string;
  projectId: number;
  projectName: string;
  startDate: string | null;
  endDate: string | null;
  displayDateText: string;
  delayed: boolean;
}

export interface UnscheduledTaskItem {
  taskId: number;
  projectId: number;
  projectName: string;
  wbsTaskName: string;
  scheduleRegisterable: boolean;
}

export interface MyWorkCalendar {
  summary: MyWorkSummary;
  year: number;
  month: number;
  events: CalendarEvent[];
  unscheduledTaskCount: number;
  unscheduledTasks: UnscheduledTaskItem[];
}

export interface DailyTaskItem {
  taskId: number;
  taskName: string;
  dueDate: string | null;
  displayDueText: string;
  projectId: number;
  projectName: string;
  delayed: boolean;
}

export interface DailyTask {
  date: string;
  taskCount: number;
  tasks: DailyTaskItem[];
}
