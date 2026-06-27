<template>
  <main class="page">
    <aside class="sidebar">
      <h1>H-PMS</h1>
      <nav>
        <RouterLink class="active" to="/my-work">내업무</RouterLink>
      </nav>
    </aside>

    <section class="content">
      <header class="header">
        <div>
          <p class="breadcrumb">내업무 &gt; 일정관리</p>
          <h2>일정관리</h2>
          <span class="scope-label">업무 ID {{ taskId }}</span>
        </div>
        <button class="text-button" type="button" @click="goBack">뒤로가기</button>
      </header>

      <section class="placeholder-panel">
        <h3>일정관리 화면</h3>
        <dl>
          <div>
            <dt>업무 ID</dt>
            <dd>{{ taskId }}</dd>
          </div>
          <div>
            <dt>상태</dt>
            <dd>프로토타입 연결 확인</dd>
          </div>
        </dl>
      </section>
    </section>
  </main>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const taskId = computed(() => String(route.params.taskId ?? '-'));

function goBack() {
  if (window.history.length > 1) {
    router.back();
    return;
  }

  router.push('/my-work?view=calendar');
}
</script>
