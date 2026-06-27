package com.hpms.prototype.repository;

import java.time.LocalDate;
import java.util.List;

public interface MyWorkRepository {

  List<ProjectRow> findProgressProjects(long userId, int page, int size);

  long countProgressProjects(long userId);

  List<ProjectRow> findWaitingProjects(long userId, int page, int size);

  long countWaitingProjects(long userId);

  List<TaskRow> findMyTasks(long userId);

  List<TaskRow> findCalendarEvents(long userId, LocalDate monthStart, LocalDate monthEnd);

  List<TaskRow> findDailyTasks(long userId, LocalDate date);

  List<TaskRow> findUnscheduledTasks(long userId, int page, int size);

  long countUnscheduledTasks(long userId);

  boolean existsAssignedTask(long userId, long taskId);
}
