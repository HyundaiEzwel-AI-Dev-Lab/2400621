package com.hpms.prototype.repository;

import com.hpms.prototype.repository.jpa.ProjectJpaRepository;
import com.hpms.prototype.repository.jpa.ProjectProjection;
import com.hpms.prototype.repository.jpa.TaskJpaRepository;
import com.hpms.prototype.repository.jpa.TaskProjection;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class JpaMyWorkRepository implements MyWorkRepository {

  private final ProjectJpaRepository projectJpaRepository;
  private final TaskJpaRepository taskJpaRepository;

  public JpaMyWorkRepository(
      ProjectJpaRepository projectJpaRepository, TaskJpaRepository taskJpaRepository) {
    this.projectJpaRepository = projectJpaRepository;
    this.taskJpaRepository = taskJpaRepository;
  }

  @Override
  public List<ProjectRow> findProgressProjects(long userId, int page, int size) {
    return projectJpaRepository.findProgressProjects(userId, PageRequest.of(page, size)).stream()
        .map(this::toProjectRow)
        .toList();
  }

  @Override
  public long countProgressProjects(long userId) {
    return projectJpaRepository.countProgressProjects(userId);
  }

  @Override
  public List<ProjectRow> findWaitingProjects(long userId, int page, int size) {
    return projectJpaRepository.findWaitingProjects(userId, PageRequest.of(page, size)).stream()
        .map(this::toProjectRow)
        .toList();
  }

  @Override
  public long countWaitingProjects(long userId) {
    return projectJpaRepository.countWaitingProjects(userId);
  }

  @Override
  public List<TaskRow> findMyTasks(long userId) {
    return taskJpaRepository.findMyTasks(userId).stream().map(this::toTaskRow).toList();
  }

  @Override
  public List<TaskRow> findCalendarEvents(long userId, LocalDate monthStart, LocalDate monthEnd) {
    return taskJpaRepository.findCalendarEvents(userId, monthStart, monthEnd).stream()
        .map(this::toTaskRow)
        .toList();
  }

  @Override
  public List<TaskRow> findDailyTasks(long userId, LocalDate date) {
    return taskJpaRepository.findDailyTasks(userId, date).stream().map(this::toTaskRow).toList();
  }

  @Override
  public List<TaskRow> findUnscheduledTasks(long userId, int page, int size) {
    return taskJpaRepository.findUnscheduledTasks(userId, PageRequest.of(page, size)).stream()
        .map(this::toTaskRow)
        .toList();
  }

  @Override
  public long countUnscheduledTasks(long userId) {
    return taskJpaRepository.countUnscheduledTasks(userId);
  }

  @Override
  public boolean existsAssignedTask(long userId, long taskId) {
    return taskJpaRepository.countAssignedTask(userId, taskId) > 0;
  }

  private ProjectRow toProjectRow(ProjectProjection projection) {
    return new ProjectRow(
        projection.getProjectId(),
        projection.getProjectName(),
        projection.getStatus(),
        projection.getTargetDate(),
        projection.getOpenDate(),
        projection.getOpenDateText(),
        projection.getProgressRate(),
        projection.getDepartmentName());
  }

  private TaskRow toTaskRow(TaskProjection projection) {
    return new TaskRow(
        projection.getTaskId(),
        projection.getTaskName(),
        projection.getTaskStatus(),
        projection.getProjectId(),
        projection.getProjectName(),
        projection.getProgressRate(),
        projection.getStartDate(),
        projection.getDueDate(),
        projection.getSortOrder() == null ? 0 : projection.getSortOrder());
  }
}
