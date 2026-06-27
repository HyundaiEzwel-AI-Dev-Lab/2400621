package com.hpms.prototype.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hpms.prototype.api.dto.MyWorkCalendarResponse;
import com.hpms.prototype.api.dto.MyWorkCardResponse;
import com.hpms.prototype.api.dto.MyWorkSummaryResponse;
import com.hpms.prototype.repository.MyWorkRepository;
import com.hpms.prototype.repository.ProjectRow;
import com.hpms.prototype.repository.TaskRow;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class MyWorkServiceTest {

  private final MyWorkService service = new MyWorkService(new FakeRepository(), new DdayCalculator());

  @Test
  void returnsCardDataWithSummaryAndUnscheduledTask() {
    MyWorkCardResponse response =
        service.getCard(1L, "INTEGRATED", LocalDate.of(2026, 3, 22), 0, 10, 0, 10);

    assertThat(response.summary().progressProjectCount()).isEqualTo(1);
    assertThat(response.summary().myTaskCount()).isEqualTo(3);
    assertThat(response.summary().weeklyDueCount()).isEqualTo(2);
    assertThat(response.summary().delayedCount()).isEqualTo(1);
    assertThat(response.summary().waitingProjectCount()).isEqualTo(1);
    assertThat(response.myTasks()).hasSize(3);
    assertThat(response.myTasks().get(2).scheduled()).isFalse();
    assertThat(response.myTasks().get(2).progressRate()).isNull();
  }

  @Test
  void returnsCalendarWithEventsAndUnscheduledTasks() {
    MyWorkCalendarResponse response =
        service.getCalendar(1L, "INTEGRATED", 2026, 3, LocalDate.of(2026, 3, 22));

    assertThat(response.events()).hasSize(2);
    assertThat(response.unscheduledTaskCount()).isEqualTo(1);
    assertThat(response.unscheduledTasks()).hasSize(1);
  }

  @Test
  void returnsSummary() {
    MyWorkSummaryResponse response =
        service.getSummary(1L, "INTEGRATED", LocalDate.of(2026, 3, 22));

    assertThat(response.scope()).isEqualTo("INTEGRATED");
    assertThat(response.progressProjectCount()).isEqualTo(1);
  }

  @Test
  void returnsUnscheduledTotalCountIndependentFromPageSize() {
    assertThat(service.getUnscheduledTasks(1L, "INTEGRATED", 0, 1).totalCount()).isEqualTo(1);
    assertThat(service.getUnscheduledTasks(1L, "INTEGRATED", 0, 1).items()).hasSize(1);
  }

  @Test
  void rejectsUnsupportedScope() {
    assertThatThrownBy(() -> service.getSummary(1L, "DEPARTMENT", LocalDate.of(2026, 3, 22)))
        .isInstanceOf(InvalidMyWorkRequestException.class);
  }

  private static class FakeRepository implements MyWorkRepository {

    @Override
    public List<ProjectRow> findProgressProjects(long userId, int page, int size) {
      return List.of(
          new ProjectRow(
              1L,
              "프로모션 운영",
              "TESTING",
              LocalDate.of(2026, 3, 30),
              null,
              null,
              BigDecimal.valueOf(80),
              "테크담당"));
    }

    @Override
    public long countProgressProjects(long userId) {
      return 1;
    }

    @Override
    public List<ProjectRow> findWaitingProjects(long userId, int page, int size) {
      return List.of(
          new ProjectRow(
              2L,
              "전사 PMS",
              "RECEIVED",
              null,
              LocalDate.of(2026, 4, 10),
              null,
              BigDecimal.ZERO,
              "테크담당"));
    }

    @Override
    public long countWaitingProjects(long userId) {
      return 1;
    }

    @Override
    public List<TaskRow> findMyTasks(long userId) {
      return List.of(
          task(1L, "단위 테스트", LocalDate.of(2026, 3, 20), BigDecimal.valueOf(73), 1),
          task(2L, "바우처 배정", LocalDate.of(2026, 3, 22), BigDecimal.valueOf(45), 2),
          task(3L, "일정 미등록", null, BigDecimal.ZERO, 3));
    }

    @Override
    public List<TaskRow> findCalendarEvents(long userId, LocalDate monthStart, LocalDate monthEnd) {
      return findMyTasks(userId).stream().filter(task -> task.dueDate() != null).toList();
    }

    @Override
    public List<TaskRow> findDailyTasks(long userId, LocalDate date) {
      return findCalendarEvents(userId, date, date).stream()
          .filter(task -> task.dueDate().equals(date))
          .toList();
    }

    @Override
    public List<TaskRow> findUnscheduledTasks(long userId, int page, int size) {
      return List.of(task(3L, "일정 미등록", null, BigDecimal.ZERO, 3));
    }

    @Override
    public long countUnscheduledTasks(long userId) {
      return 1;
    }

    @Override
    public boolean existsAssignedTask(long userId, long taskId) {
      return true;
    }

    private TaskRow task(Long id, String name, LocalDate dueDate, BigDecimal progressRate, int sortOrder) {
      return new TaskRow(
          id,
          name,
          "IN_PROGRESS",
          1L,
          "프로모션 운영",
          progressRate,
          dueDate == null ? null : dueDate.minusDays(1),
          dueDate,
          sortOrder);
    }
  }
}
