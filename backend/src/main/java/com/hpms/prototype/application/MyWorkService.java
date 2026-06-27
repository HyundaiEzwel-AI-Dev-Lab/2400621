package com.hpms.prototype.application;

import com.hpms.prototype.api.dto.CalendarEventResponse;
import com.hpms.prototype.api.dto.DailyTaskItemResponse;
import com.hpms.prototype.api.dto.DailyTaskResponse;
import com.hpms.prototype.api.dto.MyTaskResponse;
import com.hpms.prototype.api.dto.MyWorkCalendarResponse;
import com.hpms.prototype.api.dto.MyWorkCardResponse;
import com.hpms.prototype.api.dto.MyWorkSummaryResponse;
import com.hpms.prototype.api.dto.ProjectCardResponse;
import com.hpms.prototype.api.dto.TaskActionResponse;
import com.hpms.prototype.api.dto.TaskActionResponse.TaskActions;
import com.hpms.prototype.api.dto.UnscheduledTaskItemResponse;
import com.hpms.prototype.api.dto.UnscheduledTaskResponse;
import com.hpms.prototype.api.dto.WaitingProjectResponse;
import com.hpms.prototype.domain.ProjectStatus;
import com.hpms.prototype.repository.MyWorkRepository;
import com.hpms.prototype.repository.ProjectRow;
import com.hpms.prototype.repository.TaskRow;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MyWorkService {

  private static final String DEFAULT_SCOPE = "INTEGRATED";
  private static final int UNSCHEDULED_PANEL_LIMIT = 20;
  private static final int DEFAULT_PROJECT_SIZE = 10;
  private static final int DEFAULT_WAITING_SIZE = 10;
  private static final int MAX_PAGE_SIZE = 100;

  private final MyWorkRepository repository;
  private final DdayCalculator ddayCalculator;

  public MyWorkService(MyWorkRepository repository, DdayCalculator ddayCalculator) {
    this.repository = repository;
    this.ddayCalculator = ddayCalculator;
  }

  public MyWorkSummaryResponse getSummary(long userId, String scope, LocalDate baseDate) {
    validateScope(scope);
    List<TaskRow> myTasks = repository.findMyTasks(userId);

    int weeklyDueCount =
        (int)
            myTasks.stream()
                .filter(task -> ddayCalculator.isDueThisWeek(task.dueDate(), baseDate))
                .count();
    int delayedCount =
        (int)
            myTasks.stream()
                .filter(task -> ddayCalculator.isDelayed(task.dueDate(), baseDate))
                .count();

    return new MyWorkSummaryResponse(
        DEFAULT_SCOPE,
        toIntCount(repository.countProgressProjects(userId)),
        myTasks.size(),
        weeklyDueCount,
        delayedCount,
        toIntCount(repository.countWaitingProjects(userId)));
  }

  public MyWorkCardResponse getCard(
      long userId,
      String scope,
      LocalDate baseDate,
      int projectPage,
      int projectSize,
      int waitingPage,
      int waitingSize) {
    validateScope(scope);
    validatePage(projectPage, projectSize);
    validatePage(waitingPage, waitingSize);

    List<ProjectRow> progressProjects =
        repository.findProgressProjects(userId, projectPage, projectSize);
    List<ProjectRow> waitingProjects = repository.findWaitingProjects(userId, waitingPage, waitingSize);
    List<TaskRow> myTasks = repository.findMyTasks(userId);
    MyWorkSummaryResponse summary = getSummary(userId, scope, baseDate);

    return new MyWorkCardResponse(
        summary,
        progressProjects.stream().map(project -> toProjectCard(project, baseDate)).toList(),
        myTasks.stream().map(task -> toMyTask(task, baseDate)).toList(),
        waitingProjects.stream().map(this::toWaitingProject).toList());
  }

  public MyWorkCalendarResponse getCalendar(
      long userId, String scope, int year, int month, LocalDate baseDate) {
    validateScope(scope);
    YearMonth yearMonth = YearMonth.of(year, month);
    LocalDate monthStart = yearMonth.atDay(1);
    LocalDate monthEnd = yearMonth.atEndOfMonth();
    List<TaskRow> events = repository.findCalendarEvents(userId, monthStart, monthEnd);
    List<TaskRow> unscheduledTasks = repository.findUnscheduledTasks(userId, 0, UNSCHEDULED_PANEL_LIMIT);

    return new MyWorkCalendarResponse(
        getSummary(userId, scope, baseDate),
        year,
        month,
        events.stream().map(task -> toCalendarEvent(task, baseDate)).toList(),
        toIntCount(repository.countUnscheduledTasks(userId)),
        unscheduledTasks.stream().map(this::toUnscheduledTask).toList());
  }

  public DailyTaskResponse getDailyTasks(
      long userId, String scope, LocalDate date, LocalDate baseDate) {
    validateScope(scope);
    List<DailyTaskItemResponse> tasks =
        repository.findDailyTasks(userId, date).stream()
            .map(task -> toDailyTask(task, baseDate))
            .toList();
    return new DailyTaskResponse(date, tasks.size(), tasks);
  }

  public UnscheduledTaskResponse getUnscheduledTasks(long userId, String scope, int page, int size) {
    validateScope(scope);
    validatePage(page, size);
    List<UnscheduledTaskItemResponse> items =
        repository.findUnscheduledTasks(userId, page, size).stream()
            .map(this::toUnscheduledTask)
            .toList();
    return new UnscheduledTaskResponse(toIntCount(repository.countUnscheduledTasks(userId)), items);
  }

  public TaskActionResponse getTaskActions(long userId, long taskId) {
    if (!repository.existsAssignedTask(userId, taskId)) {
      throw new IllegalArgumentException("업무를 찾을 수 없습니다.");
    }
    TaskRow task =
        repository.findMyTasks(userId).stream()
            .filter(item -> item.taskId() == taskId)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("업무를 찾을 수 없습니다."));
    return new TaskActionResponse(taskId, task.projectId(), new TaskActions(true, true));
  }

  private ProjectCardResponse toProjectCard(ProjectRow project, LocalDate baseDate) {
    Integer dDay = project.targetDate() == null ? null : ddayCalculator.calculate(project.targetDate(), baseDate);
    return new ProjectCardResponse(
        project.projectId(),
        project.projectName(),
        project.status(),
        projectStatusName(project.status()),
        project.targetDate(),
        dDay,
        toPercent(project.progressRate()),
        0,
        0,
        0);
  }

  private MyTaskResponse toMyTask(TaskRow task, LocalDate baseDate) {
    boolean scheduled = task.dueDate() != null;
    Integer dDay = scheduled ? ddayCalculator.calculate(task.dueDate(), baseDate) : null;
    Integer progressRate = scheduled ? toPercent(task.progressRate()) : null;
    return new MyTaskResponse(
        task.taskId(),
        task.taskName(),
        task.projectId(),
        task.projectName(),
        task.dueDate(),
        dDay,
        ddayCalculator.isDelayed(task.dueDate(), baseDate),
        progressRate,
        scheduled,
        true,
        true);
  }

  private WaitingProjectResponse toWaitingProject(ProjectRow project) {
    return new WaitingProjectResponse(
        project.projectId(),
        project.projectName(),
        project.status(),
        projectStatusName(project.status()),
        project.departmentName(),
        project.openDate(),
        project.openDateText());
  }

  private CalendarEventResponse toCalendarEvent(TaskRow task, LocalDate baseDate) {
    return new CalendarEventResponse(
        task.taskId(),
        task.taskId(),
        task.taskName(),
        task.projectId(),
        task.projectName(),
        task.startDate(),
        task.dueDate(),
        displayDateText(task.dueDate()),
        ddayCalculator.isDelayed(task.dueDate(), baseDate));
  }

  private DailyTaskItemResponse toDailyTask(TaskRow task, LocalDate baseDate) {
    return new DailyTaskItemResponse(
        task.taskId(),
        task.taskName(),
        task.dueDate(),
        displayDateText(task.dueDate()),
        task.projectId(),
        task.projectName(),
        ddayCalculator.isDelayed(task.dueDate(), baseDate));
  }

  private UnscheduledTaskItemResponse toUnscheduledTask(TaskRow task) {
    return new UnscheduledTaskItemResponse(
        task.taskId(), task.projectId(), task.projectName(), task.taskName(), true);
  }

  private int toPercent(BigDecimal value) {
    return value == null ? 0 : value.setScale(0, java.math.RoundingMode.HALF_UP).intValue();
  }

  public int defaultProjectSize(Integer size) {
    return size == null ? DEFAULT_PROJECT_SIZE : size;
  }

  public int defaultWaitingSize(Integer size) {
    return size == null ? DEFAULT_WAITING_SIZE : size;
  }

  private void validateScope(String scope) {
    if (scope != null && !DEFAULT_SCOPE.equals(scope)) {
      throw new InvalidMyWorkRequestException("지원하지 않는 조회 범위입니다.");
    }
  }

  private void validatePage(int page, int size) {
    if (page < 0) {
      throw new InvalidMyWorkRequestException("페이지 번호는 0 이상이어야 합니다.");
    }
    if (size < 1 || size > MAX_PAGE_SIZE) {
      throw new InvalidMyWorkRequestException("페이지 크기는 1 이상 100 이하이어야 합니다.");
    }
  }

  private int toIntCount(long count) {
    return Math.toIntExact(count);
  }

  private String displayDateText(LocalDate dueDate) {
    return dueDate == null ? "일정 미등록" : "~ " + dueDate.getMonthValue() + "/" + dueDate.getDayOfMonth();
  }

  private String projectStatusName(String status) {
    try {
      return ProjectStatus.valueOf(status).displayName();
    } catch (IllegalArgumentException ex) {
      return status;
    }
  }
}
