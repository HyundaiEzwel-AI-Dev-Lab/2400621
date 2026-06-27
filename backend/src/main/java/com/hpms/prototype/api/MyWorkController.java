package com.hpms.prototype.api;

import com.hpms.prototype.api.dto.ApiResponse;
import com.hpms.prototype.api.dto.DailyTaskResponse;
import com.hpms.prototype.api.dto.MyWorkCalendarResponse;
import com.hpms.prototype.api.dto.MyWorkCardResponse;
import com.hpms.prototype.api.dto.MyWorkSummaryResponse;
import com.hpms.prototype.api.dto.TaskActionResponse;
import com.hpms.prototype.api.dto.UnscheduledTaskResponse;
import com.hpms.prototype.application.MyWorkService;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/my-work")
public class MyWorkController {

  private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
  private static final String DEFAULT_SCOPE = "INTEGRATED";
  private final MyWorkService myWorkService;

  public MyWorkController(MyWorkService myWorkService) {
    this.myWorkService = myWorkService;
  }

  @GetMapping("/summary")
  public ApiResponse<MyWorkSummaryResponse> summary(
      @RequestHeader(name = "X-User-Id", defaultValue = "1") long userId,
      @RequestParam(defaultValue = DEFAULT_SCOPE) String scope,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate baseDate) {
    return ApiResponse.ok(myWorkService.getSummary(userId, scope, defaultBaseDate(baseDate)));
  }

  @GetMapping("/card")
  public ApiResponse<MyWorkCardResponse> card(
      @RequestHeader(name = "X-User-Id", defaultValue = "1") long userId,
      @RequestParam(defaultValue = DEFAULT_SCOPE) String scope,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate baseDate,
      @RequestParam(defaultValue = "0") int projectPage,
      @RequestParam(defaultValue = "10") int projectSize,
      @RequestParam(defaultValue = "0") int waitingPage,
      @RequestParam(defaultValue = "10") int waitingSize) {
    return ApiResponse.ok(
        myWorkService.getCard(
            userId,
            scope,
            defaultBaseDate(baseDate),
            projectPage,
            projectSize,
            waitingPage,
            waitingSize));
  }

  @GetMapping("/calendar")
  public ApiResponse<MyWorkCalendarResponse> calendar(
      @RequestHeader(name = "X-User-Id", defaultValue = "1") long userId,
      @RequestParam(defaultValue = DEFAULT_SCOPE) String scope,
      @RequestParam int year,
      @RequestParam int month,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate baseDate) {
    return ApiResponse.ok(
        myWorkService.getCalendar(userId, scope, year, month, defaultBaseDate(baseDate)));
  }

  @GetMapping("/calendar/daily")
  public ApiResponse<DailyTaskResponse> daily(
      @RequestHeader(name = "X-User-Id", defaultValue = "1") long userId,
      @RequestParam(defaultValue = DEFAULT_SCOPE) String scope,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate baseDate) {
    return ApiResponse.ok(
        myWorkService.getDailyTasks(userId, scope, date, defaultBaseDate(baseDate)));
  }

  @GetMapping("/unscheduled")
  public ApiResponse<UnscheduledTaskResponse> unscheduled(
      @RequestHeader(name = "X-User-Id", defaultValue = "1") long userId,
      @RequestParam(defaultValue = DEFAULT_SCOPE) String scope,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ApiResponse.ok(myWorkService.getUnscheduledTasks(userId, scope, page, size));
  }

  @GetMapping("/tasks/{taskId}/actions")
  public ApiResponse<TaskActionResponse> actions(
      @RequestHeader(name = "X-User-Id", defaultValue = "1") long userId,
      @PathVariable long taskId) {
    return ApiResponse.ok(myWorkService.getTaskActions(userId, taskId));
  }

  private LocalDate defaultBaseDate(LocalDate baseDate) {
    return baseDate == null ? LocalDate.now(SEOUL) : baseDate;
  }
}
