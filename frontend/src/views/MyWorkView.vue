<template>
  <main class="page">
    <aside class="sidebar">
      <h1>H-PMS</h1>
      <nav>
        <a class="active" href="/my-work">내업무</a>
      </nav>
    </aside>

    <section class="content">
      <header class="header">
        <div>
          <p class="breadcrumb">내업무</p>
          <h2>내업무</h2>
          <span class="scope-label">통합</span>
        </div>
        <div class="tabs" aria-label="보기 전환">
          <button :class="{ selected: viewMode === 'calendar' }" type="button" @click="setView('calendar')">
            캘린더형
          </button>
          <button :class="{ selected: viewMode === 'card' }" type="button" @click="setView('card')">
            카드형
          </button>
        </div>
      </header>

      <section v-if="summary" class="summary-bar" aria-label="내 업무 요약">
        <div class="summary-item">
          <span>진행 프로젝트</span>
          <strong>{{ summary.progressProjectCount }}</strong>
        </div>
        <div class="summary-item">
          <span>내 할 일</span>
          <strong>{{ summary.myTaskCount }}</strong>
        </div>
        <div class="summary-item">
          <span>금주 마감</span>
          <strong>{{ summary.weeklyDueCount }}</strong>
        </div>
        <div class="summary-item alert">
          <span>지연</span>
          <strong>{{ summary.delayedCount }}</strong>
        </div>
        <div class="summary-item">
          <span>대기 프로젝트</span>
          <strong>{{ summary.waitingProjectCount }}</strong>
        </div>
      </section>

      <section v-if="store.errorMessage" class="state-panel error">
        {{ store.errorMessage }}
      </section>

      <section v-else-if="store.loading" class="state-panel">데이터를 불러오는 중입니다.</section>

      <template v-else>
        <section v-if="viewMode === 'card'" class="view-stack">
          <section class="section-block">
            <div class="section-title">
              <h3>진행중인 프로젝트</h3>
              <span>{{ cardData?.progressProjects.length ?? 0 }}건</span>
            </div>
            <div v-if="cardData?.progressProjects.length" class="carousel-shell">
              <button
                aria-label="이전 진행 프로젝트"
                class="carousel-button"
                type="button"
                :disabled="progressProjectIndex === 0"
                @click="moveProgressProject(-1)"
              >
                &lt;
              </button>
              <div class="project-grid">
                <article
                  v-for="project in visibleProgressProjects"
                  :key="project.projectId"
                  class="project-card"
                  :title="project.projectName"
                >
                <div class="card-topline">
                  <span class="status">{{ project.statusName }}</span>
                  <strong>{{ formatDday(project.dDay) }}</strong>
                </div>
                <h4 :title="project.projectName">{{ project.projectName }}</h4>
                <p>목표일 {{ formatDate(project.targetDate) }}</p>
                <div class="progress-track">
                  <span :style="{ width: `${project.progressRate}%` }"></span>
                </div>
                <div class="card-meta">
                  <span>공정률</span>
                  <strong>{{ project.progressRate }}%</strong>
                </div>
                </article>
              </div>
              <button
                aria-label="다음 진행 프로젝트"
                class="carousel-button"
                type="button"
                :disabled="progressProjectIndex >= progressProjectMaxIndex"
                @click="moveProgressProject(1)"
              >
                &gt;
              </button>
            </div>
            <p v-else class="empty-text">배정된 프로젝트가 없습니다.</p>
          </section>

          <section class="section-block">
            <div class="section-title">
              <h3>내 할 일</h3>
              <span>{{ cardData?.myTasks.length ?? 0 }}건</span>
            </div>
            <div class="task-table">
              <div class="task-row head">
                <span>업무명</span>
                <span>마감일</span>
                <span>공정률</span>
                <span>프로젝트</span>
                <span>액션</span>
              </div>
              <div v-for="task in cardData?.myTasks ?? []" :key="task.taskId" class="task-row">
                <strong :title="task.taskName">{{ task.taskName }}</strong>
                <span :class="{ danger: task.delayed }">{{ formatDate(task.dueDate) }} / {{ formatDday(task.dDay) }}</span>
                <span>{{ formatPercent(task.progressRate) }}</span>
                <span :title="task.projectName">{{ task.projectName }}</span>
                <span class="action-links">
                  <button type="button" aria-haspopup="menu" @click="toggleTaskMenu(task.taskId)">...</button>
                  <span v-if="openedTaskId === task.taskId" class="task-menu" role="menu">
                    <RouterLink
                      v-if="task.scheduleActionEnabled"
                      :to="`/schedule/tasks/${task.taskId}`"
                      role="menuitem"
                    >
                      일정관리
                    </RouterLink>
                    <RouterLink
                      v-if="task.wbsDetailActionEnabled"
                      :to="`/projects/${task.projectId}/wbs/tasks/${task.taskId}`"
                      role="menuitem"
                    >
                      WBS 상세
                    </RouterLink>
                  </span>
                </span>
              </div>
            </div>
            <p v-if="!cardData?.myTasks.length" class="empty-text">진행중인 업무가 없습니다.</p>
          </section>

          <section class="section-block">
            <div class="section-title">
              <h3>대기 프로젝트</h3>
              <span>{{ cardData?.waitingProjects.length ?? 0 }}건</span>
            </div>
            <div v-if="cardData?.waitingProjects.length" class="carousel-shell">
              <button
                aria-label="이전 대기 프로젝트"
                class="carousel-button"
                type="button"
                :disabled="waitingProjectIndex === 0"
                @click="moveWaitingProject(-1)"
              >
                &lt;
              </button>
              <div class="waiting-list">
                <article
                  v-for="project in visibleWaitingProjects"
                  :key="project.projectId"
                  class="waiting-card"
                  :title="project.projectName"
                >
                <span class="status muted">{{ project.statusName }}</span>
                <h4 :title="project.projectName">{{ project.projectName }}</h4>
                <p>{{ project.departmentName ?? '-' }} · {{ project.openDateText ?? formatDate(project.openDate) }}</p>
                </article>
              </div>
              <button
                aria-label="다음 대기 프로젝트"
                class="carousel-button"
                type="button"
                :disabled="waitingProjectIndex >= waitingProjectMaxIndex"
                @click="moveWaitingProject(1)"
              >
                &gt;
              </button>
            </div>
            <p v-else class="empty-text">접수된 프로젝트가 없습니다.</p>
          </section>

          <section class="notice-panel">
            <p>프로젝트 관리 &gt; 요구사항 관리에서 요구사항이 확정되면 WBS 관리 메뉴에 작업범위가 자동 생성됩니다.</p>
            <p>WBS 관리 메뉴에서 일정이 등록된 업무만 내 할 일 업무에 일정이 표시됩니다.</p>
            <p>단, 캘린더에는 일정 등록된 업무만 표시됩니다.</p>
          </section>
        </section>

        <section v-else class="calendar-view">
          <div class="calendar-main">
            <div class="calendar-toolbar">
              <button type="button" @click="moveMonth(-1)">이전</button>
              <strong>{{ selectedYear }}년 {{ selectedMonth }}월</strong>
              <button type="button" @click="moveMonth(1)">다음</button>
              <button type="button" @click="goToday">오늘</button>
            </div>

            <div class="calendar-grid weekday">
              <span v-for="weekday in weekdays" :key="weekday">{{ weekday }}</span>
            </div>
            <div class="calendar-month">
              <section
                v-for="week in calendarWeeks"
                :key="week.weekIndex"
                class="calendar-week"
                :style="{ gridTemplateRows: calendarWeekRows(week) }"
              >
                <div
                  v-for="(day, dayIndex) in week.days"
                  :key="day.dateKey"
                  :class="{ muted: !day.inCurrentMonth, today: day.dateKey === todayKey }"
                  class="calendar-day-cell"
                  :style="{ gridColumn: dayIndex + 1, gridRow: calendarDayGridRow(week) }"
                >
                  <button
                    class="calendar-date-button"
                    type="button"
                    :aria-label="`${day.dateKey} 내 업무 보기`"
                    @click="openDaily(day.dateKey)"
                  >
                    {{ day.day }}
                  </button>
                </div>
                <button
                  v-for="bar in week.bars"
                  :key="bar.id"
                  :class="{
                    delayed: bar.delayed,
                    'continues-left': bar.continuesFromPreviousWeek,
                    'continues-right': bar.continuesToNextWeek
                  }"
                  class="calendar-bar"
                  type="button"
                  :style="{ gridColumn: `${bar.startColumn} / span ${bar.span}`, gridRow: `${bar.lane + 2}` }"
                  :title="calendarBarTitle(bar)"
                  :aria-label="calendarBarTitle(bar)"
                  @click.stop="openProjectBar(bar)"
                >
                  <strong v-if="bar.delayed">지연</strong>
                  <span>{{ bar.projectName }}</span>
                  <small>{{ bar.taskCount }}개 업무</small>
                </button>
              </section>
            </div>
          </div>

          <aside class="unscheduled-panel">
            <div class="section-title">
              <h3>일정 미등록 업무</h3>
              <span>{{ calendarData?.unscheduledTaskCount ?? 0 }}건</span>
            </div>
            <article v-for="task in calendarData?.unscheduledTasks ?? []" :key="task.taskId" class="unscheduled-item">
              <strong :title="task.wbsTaskName">{{ task.wbsTaskName }}</strong>
              <span :title="task.projectName">{{ task.projectName }}</span>
              <RouterLink
                v-if="task.scheduleRegisterable"
                class="button-link"
                :to="`/schedule/tasks/${task.taskId}`"
              >
                일정 등록
              </RouterLink>
              <button v-else type="button" disabled>일정 등록</button>
            </article>
            <p v-if="!calendarData?.unscheduledTasks.length" class="empty-text">일정 미등록 업무가 없습니다.</p>
          </aside>

          <div v-if="store.dailyTask" class="modal-backdrop" role="presentation" @click.self="closeDaily">
            <section
              ref="dailyModalRef"
              class="daily-modal"
              role="dialog"
              aria-modal="true"
              aria-labelledby="daily-modal-title"
              tabindex="-1"
            >
              <header>
                <h3 id="daily-modal-title">{{ store.dailyTask.date }} 내 업무 ({{ store.dailyTask.taskCount }}건)</h3>
                <button type="button" aria-label="닫기" @click="closeDaily">×</button>
              </header>
              <p v-if="store.dailyLoading" class="empty-text">업무를 불러오는 중입니다.</p>
              <article v-for="task in store.dailyTask.tasks" :key="task.taskId" class="daily-task">
                <strong :title="task.taskName">{{ task.taskName }}</strong>
                <span :class="{ danger: task.delayed }" :title="task.projectName">
                  {{ task.displayDueText }} · {{ task.projectName }}
                </span>
              </article>
              <p v-if="!store.dailyTask.tasks.length" class="empty-text">해당 날짜의 업무가 없습니다.</p>
            </section>
          </div>

          <div v-if="selectedProjectBar" class="modal-backdrop" role="presentation" @click.self="closeProjectBar">
            <section
              ref="projectModalRef"
              class="daily-modal"
              role="dialog"
              aria-modal="true"
              aria-labelledby="project-modal-title"
              tabindex="-1"
            >
              <header>
                <h3 id="project-modal-title">
                  {{ selectedProjectBar.projectName }} ({{ selectedProjectBar.taskCount }}건)
                </h3>
                <button type="button" aria-label="닫기" @click="closeProjectBar">×</button>
              </header>
              <p class="project-modal-meta">
                {{ formatDate(selectedProjectBar.startDateKey) }} ~ {{ formatDate(selectedProjectBar.endDateKey) }}
                <strong v-if="selectedProjectBar.delayed">지연</strong>
              </p>
              <article v-for="taskName in selectedProjectBar.taskNames" :key="taskName" class="daily-task">
                <strong :title="taskName">{{ taskName }}</strong>
                <span>{{ selectedProjectBar.projectName }}</span>
              </article>
            </section>
          </div>
        </section>
      </template>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { useMyWorkStore } from '@/stores/myWorkStore';
import type { CalendarBar, CalendarWeek } from '@/utils/calendarUtils';
import { createCalendarWeeks } from '@/utils/calendarUtils';
import type { ViewMode } from '@/types/myWork';
import { formatDate, formatDday, formatPercent, toDateKey } from '@/utils/dateFormatter';

const route = useRoute();
const router = useRouter();
const store = useMyWorkStore();

const weekdays = ['일', '월', '화', '수', '목', '금', '토'];
const today = new Date();
const todayKey = toDateKey(today);

const viewMode = computed<ViewMode>(() => (route.query.view === 'calendar' ? 'calendar' : 'card'));
const selectedYear = computed(() => Number(route.query.year ?? 2026));
const selectedMonth = computed(() => Number(route.query.month ?? 3));
const summary = computed(() => store.summary);
const cardData = computed(() => store.card);
const calendarData = computed(() => store.calendar);
const calendarWeeks = computed(() =>
  createCalendarWeeks(selectedYear.value, selectedMonth.value, calendarData.value?.events ?? [])
);
const openedTaskId = ref<number | null>(null);
const progressProjectIndex = ref(0);
const waitingProjectIndex = ref(0);
const dailyModalRef = ref<HTMLElement | null>(null);
const projectModalRef = ref<HTMLElement | null>(null);
const previousFocusedElement = ref<HTMLElement | null>(null);
const selectedProjectBar = ref<CalendarBar | null>(null);
const progressProjectMaxIndex = computed(() => Math.max((cardData.value?.progressProjects.length ?? 1) - 1, 0));
const waitingProjectMaxIndex = computed(() => Math.max((cardData.value?.waitingProjects.length ?? 1) - 1, 0));
const visibleProgressProjects = computed(() =>
  (cardData.value?.progressProjects ?? []).slice(progressProjectIndex.value, progressProjectIndex.value + 1)
);
const visibleWaitingProjects = computed(() =>
  (cardData.value?.waitingProjects ?? []).slice(waitingProjectIndex.value, waitingProjectIndex.value + 1)
);

onMounted(() => {
  window.addEventListener('keydown', handleKeydown);
  loadCurrentView();
});

onBeforeUnmount(() => {
  window.removeEventListener('keydown', handleKeydown);
});

watch([viewMode, selectedYear, selectedMonth], () => {
  openedTaskId.value = null;
  loadCurrentView();
});

watch(cardData, () => {
  progressProjectIndex.value = 0;
  waitingProjectIndex.value = 0;
});

function loadCurrentView() {
  if (viewMode.value === 'calendar') {
    store.loadCalendar(selectedYear.value, selectedMonth.value);
    return;
  }
  store.loadCard();
}

function setView(view: ViewMode) {
  router.push({
    path: '/my-work',
    query: {
      ...route.query,
      view
    }
  });
}

function moveMonth(delta: number) {
  const target = new Date(selectedYear.value, selectedMonth.value - 1 + delta, 1);
  router.push({
    path: '/my-work',
    query: {
      ...route.query,
      view: 'calendar',
      year: target.getFullYear(),
      month: target.getMonth() + 1
    }
  });
}

function goToday() {
  router.push({
    path: '/my-work',
    query: {
      ...route.query,
      view: 'calendar',
      year: today.getFullYear(),
      month: today.getMonth() + 1
    }
  });
}

async function openDaily(date: string) {
  selectedProjectBar.value = null;
  previousFocusedElement.value = document.activeElement instanceof HTMLElement ? document.activeElement : null;
  await store.loadDailyTask(date);
  await nextTick();
  dailyModalRef.value?.focus();
}

function closeDaily() {
  store.clearDailyTask();
  nextTick(() => {
    previousFocusedElement.value?.focus();
    previousFocusedElement.value = null;
  });
}

async function openProjectBar(bar: CalendarBar) {
  store.clearDailyTask();
  previousFocusedElement.value = document.activeElement instanceof HTMLElement ? document.activeElement : null;
  selectedProjectBar.value = bar;
  await nextTick();
  projectModalRef.value?.focus();
}

function closeProjectBar() {
  selectedProjectBar.value = null;
  nextTick(() => {
    previousFocusedElement.value?.focus();
    previousFocusedElement.value = null;
  });
}

function toggleTaskMenu(taskId: number) {
  openedTaskId.value = openedTaskId.value === taskId ? null : taskId;
}

function handleKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape') {
    openedTaskId.value = null;
    closeProjectBar();
    closeDaily();
    return;
  }
  if (event.key === 'Tab' && store.dailyTask) {
    trapModalFocus(event, dailyModalRef);
    return;
  }
  if (event.key === 'Tab' && selectedProjectBar.value) {
    trapModalFocus(event, projectModalRef);
  }
}

function moveProgressProject(delta: number) {
  progressProjectIndex.value = clampIndex(progressProjectIndex.value + delta, progressProjectMaxIndex.value);
}

function moveWaitingProject(delta: number) {
  waitingProjectIndex.value = clampIndex(waitingProjectIndex.value + delta, waitingProjectMaxIndex.value);
}

function clampIndex(value: number, max: number) {
  return Math.min(Math.max(value, 0), max);
}

function calendarBarTitle(bar: CalendarBar) {
  const taskNames = bar.taskNames.join(', ');
  const rangeText = bar.startDateKey === bar.endDateKey ? bar.startDateKey : `${bar.startDateKey} ~ ${bar.endDateKey}`;
  return `${bar.projectName} · ${bar.taskCount}개 업무 · ${rangeText}${taskNames ? ` · ${taskNames}` : ''}`;
}

function calendarWeekRows(week: CalendarWeek) {
  const laneCount = Math.max(week.laneCount, 0);
  return laneCount > 0 ? `minmax(74px, 1fr) repeat(${laneCount}, 30px)` : 'minmax(104px, 1fr)';
}

function calendarDayGridRow(week: CalendarWeek) {
  return `1 / span ${Math.max(week.laneCount, 0) + 1}`;
}

function trapModalFocus(event: KeyboardEvent, modalRef: { value: HTMLElement | null }) {
  const focusableElements = getModalFocusableElements(modalRef.value);
  if (focusableElements.length === 0) {
    event.preventDefault();
    modalRef.value?.focus();
    return;
  }

  const firstElement = focusableElements[0];
  const lastElement = focusableElements[focusableElements.length - 1];
  const activeElement = document.activeElement;

  if (activeElement === modalRef.value) {
    event.preventDefault();
    (event.shiftKey ? lastElement : firstElement).focus();
    return;
  }

  if (event.shiftKey && activeElement === firstElement) {
    event.preventDefault();
    lastElement.focus();
    return;
  }

  if (!event.shiftKey && activeElement === lastElement) {
    event.preventDefault();
    firstElement.focus();
    return;
  }

  if (activeElement && !modalRef.value?.contains(activeElement)) {
    event.preventDefault();
    firstElement.focus();
  }
}

function getModalFocusableElements(modalElement: HTMLElement | null) {
  if (!modalElement) {
    return [];
  }
  return Array.from(
    modalElement.querySelectorAll<HTMLElement>(
      'a[href], button:not([disabled]), [tabindex]:not([tabindex="-1"])'
    )
  ).filter((element) => !element.hasAttribute('disabled'));
}
</script>
