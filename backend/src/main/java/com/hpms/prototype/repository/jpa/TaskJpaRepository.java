package com.hpms.prototype.repository.jpa;

import com.hpms.prototype.domain.WbsTask;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskJpaRepository extends JpaRepository<WbsTask, Long> {

  @Query(
      value =
          """
          select t.id as taskId, t.name as taskName, t.status as taskStatus,
                 t.project_id as projectId, p.name as projectName,
                 t.progress_rate as progressRate, s.start_date as startDate,
                 s.due_date as dueDate, t.sort_order as sortOrder
          from wbs_task t
          join task_assignment ta on ta.task_id = t.id
          join user_account u on u.id = ta.user_id and u.active = true
          join project p on p.id = t.project_id
          left join task_schedule s on s.task_id = t.id
          where ta.user_id = :userId
            and t.status <> 'DONE'
          order by t.sort_order, t.id
          """,
      nativeQuery = true)
  List<TaskProjection> findMyTasks(@Param("userId") long userId);

  @Query(
      value =
          """
          select t.id as taskId, t.name as taskName, t.status as taskStatus,
                 t.project_id as projectId, p.name as projectName,
                 t.progress_rate as progressRate, s.start_date as startDate,
                 s.due_date as dueDate, t.sort_order as sortOrder
          from wbs_task t
          join task_assignment ta on ta.task_id = t.id
          join user_account u on u.id = ta.user_id and u.active = true
          join project p on p.id = t.project_id
          join task_schedule s on s.task_id = t.id
          where ta.user_id = :userId
            and t.status <> 'DONE'
            and s.due_date is not null
            and coalesce(s.start_date, s.due_date) <= :monthEnd
            and s.due_date >= :monthStart
          order by s.due_date, t.sort_order, t.id
          """,
      nativeQuery = true)
  List<TaskProjection> findCalendarEvents(
      @Param("userId") long userId,
      @Param("monthStart") LocalDate monthStart,
      @Param("monthEnd") LocalDate monthEnd);

  @Query(
      value =
          """
          select t.id as taskId, t.name as taskName, t.status as taskStatus,
                 t.project_id as projectId, p.name as projectName,
                 t.progress_rate as progressRate, s.start_date as startDate,
                 s.due_date as dueDate, t.sort_order as sortOrder
          from wbs_task t
          join task_assignment ta on ta.task_id = t.id
          join user_account u on u.id = ta.user_id and u.active = true
          join project p on p.id = t.project_id
          join task_schedule s on s.task_id = t.id
          where ta.user_id = :userId
            and t.status <> 'DONE'
            and s.due_date is not null
            and coalesce(s.start_date, s.due_date) <= :date
            and s.due_date >= :date
          order by s.due_date, t.sort_order, t.id
          """,
      nativeQuery = true)
  List<TaskProjection> findDailyTasks(@Param("userId") long userId, @Param("date") LocalDate date);

  @Query(
      value =
          """
          select t.id as taskId, t.name as taskName, t.status as taskStatus,
                 t.project_id as projectId, p.name as projectName,
                 t.progress_rate as progressRate, s.start_date as startDate,
                 s.due_date as dueDate, t.sort_order as sortOrder
          from wbs_task t
          join task_assignment ta on ta.task_id = t.id
          join user_account u on u.id = ta.user_id and u.active = true
          join project p on p.id = t.project_id
          left join task_schedule s on s.task_id = t.id
          where ta.user_id = :userId
            and t.status <> 'DONE'
            and (s.id is null or s.due_date is null)
          order by t.sort_order, t.id
          """,
      nativeQuery = true)
  List<TaskProjection> findUnscheduledTasks(@Param("userId") long userId, Pageable pageable);

  @Query(
      value =
          """
          select count(*)
          from wbs_task t
          join task_assignment ta on ta.task_id = t.id
          join user_account u on u.id = ta.user_id and u.active = true
          left join task_schedule s on s.task_id = t.id
          where ta.user_id = :userId
            and t.status <> 'DONE'
            and (s.id is null or s.due_date is null)
          """,
      nativeQuery = true)
  long countUnscheduledTasks(@Param("userId") long userId);

  @Query(
      value =
          """
          select count(*)
          from wbs_task t
          join task_assignment ta on ta.task_id = t.id
          join user_account u on u.id = ta.user_id and u.active = true
          where ta.user_id = :userId
            and t.id = :taskId
          """,
      nativeQuery = true)
  long countAssignedTask(@Param("userId") long userId, @Param("taskId") long taskId);
}
