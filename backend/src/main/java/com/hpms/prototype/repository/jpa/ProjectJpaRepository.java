package com.hpms.prototype.repository.jpa;

import com.hpms.prototype.domain.Project;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

  @Query(
      value =
          """
          select p.id as projectId, p.name as projectName, p.status as status,
                 p.target_date as targetDate, p.open_date as openDate,
                 p.open_date_text as openDateText, p.progress_rate as progressRate,
                 d.name as departmentName
          from project p
          join project_assignment pa on pa.project_id = p.id
          join user_account u on u.id = pa.user_id and u.active = true
          left join department d on d.id = p.department_id
          where pa.user_id = :userId
            and p.status in ('TESTING', 'DISCUSSING', 'PROCESSING')
          order by p.target_date nulls last, p.id
          """,
      nativeQuery = true)
  List<ProjectProjection> findProgressProjects(@Param("userId") long userId, Pageable pageable);

  @Query(
      value =
          """
          select count(*)
          from project p
          join project_assignment pa on pa.project_id = p.id
          join user_account u on u.id = pa.user_id and u.active = true
          where pa.user_id = :userId
            and p.status in ('TESTING', 'DISCUSSING', 'PROCESSING')
          """,
      nativeQuery = true)
  long countProgressProjects(@Param("userId") long userId);

  @Query(
      value =
          """
          select p.id as projectId, p.name as projectName, p.status as status,
                 p.target_date as targetDate, p.open_date as openDate,
                 p.open_date_text as openDateText, p.progress_rate as progressRate,
                 d.name as departmentName
          from project p
          join project_assignment pa on pa.project_id = p.id
          join user_account u on u.id = pa.user_id and u.active = true
          left join department d on d.id = p.department_id
          where pa.user_id = :userId
            and p.status = 'RECEIVED'
          order by p.open_date nulls last, p.id
          """,
      nativeQuery = true)
  List<ProjectProjection> findWaitingProjects(@Param("userId") long userId, Pageable pageable);

  @Query(
      value =
          """
          select count(*)
          from project p
          join project_assignment pa on pa.project_id = p.id
          join user_account u on u.id = pa.user_id and u.active = true
          where pa.user_id = :userId
            and p.status = 'RECEIVED'
          """,
      nativeQuery = true)
  long countWaitingProjects(@Param("userId") long userId);
}
