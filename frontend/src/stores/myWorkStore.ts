import { defineStore } from 'pinia';

import { fetchDailyTasks, fetchMyWorkCalendar, fetchMyWorkCard } from '@/api/myWorkApi';
import type { DailyTask, MyWorkCalendar, MyWorkCard } from '@/types/myWork';

interface MyWorkState {
  card: MyWorkCard | null;
  calendar: MyWorkCalendar | null;
  dailyTask: DailyTask | null;
  loading: boolean;
  dailyLoading: boolean;
  errorMessage: string | null;
}

export const useMyWorkStore = defineStore('myWork', {
  state: (): MyWorkState => ({
    card: null,
    calendar: null,
    dailyTask: null,
    loading: false,
    dailyLoading: false,
    errorMessage: null
  }),
  getters: {
    summary: (state) => state.card?.summary ?? state.calendar?.summary ?? null
  },
  actions: {
    async loadCard() {
      this.loading = true;
      this.errorMessage = null;
      try {
        this.card = await fetchMyWorkCard();
      } catch (error) {
        this.errorMessage = error instanceof Error ? error.message : '카드형 데이터를 불러오지 못했습니다.';
      } finally {
        this.loading = false;
      }
    },
    async loadCalendar(year: number, month: number) {
      this.loading = true;
      this.errorMessage = null;
      try {
        this.calendar = await fetchMyWorkCalendar(year, month);
      } catch (error) {
        this.errorMessage = error instanceof Error ? error.message : '캘린더 데이터를 불러오지 못했습니다.';
      } finally {
        this.loading = false;
      }
    },
    async loadDailyTask(date: string) {
      this.dailyLoading = true;
      try {
        this.dailyTask = await fetchDailyTasks(date);
      } finally {
        this.dailyLoading = false;
      }
    },
    clearDailyTask() {
      this.dailyTask = null;
    }
  }
});
