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
          <p class="breadcrumb">내업무 &gt; WBS 상세</p>
          <h2>WBS 상세</h2>
          <span class="scope-label">프로젝트 {{ projectId }} · 업무 {{ taskId }}</span>
        </div>
        <button class="text-button" type="button" @click="goBack">뒤로가기</button>
      </header>

      <section class="placeholder-panel">
        <h3>WBS 업무 상세 화면</h3>
        <dl>
          <div>
            <dt>프로젝트 ID</dt>
            <dd>{{ projectId }}</dd>
          </div>
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
const projectId = computed(() => String(route.params.projectId ?? '-'));
const taskId = computed(() => String(route.params.taskId ?? '-'));

function goBack() {
  if (window.history.length > 1) {
    router.back();
    return;
  }

  router.push('/my-work?view=card');
}
</script>
