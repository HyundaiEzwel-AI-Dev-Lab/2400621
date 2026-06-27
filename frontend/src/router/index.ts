import { createRouter, createWebHistory } from 'vue-router';

import MyWorkView from '@/views/MyWorkView.vue';
import ScheduleTaskView from '@/views/ScheduleTaskView.vue';
import WbsTaskDetailView from '@/views/WbsTaskDetailView.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/my-work'
    },
    {
      path: '/my-work',
      name: 'my-work',
      component: MyWorkView
    },
    {
      path: '/schedule/tasks/:taskId',
      name: 'schedule-task',
      component: ScheduleTaskView
    },
    {
      path: '/projects/:projectId/wbs/tasks/:taskId',
      name: 'wbs-task-detail',
      component: WbsTaskDetailView
    }
  ]
});

export default router;
