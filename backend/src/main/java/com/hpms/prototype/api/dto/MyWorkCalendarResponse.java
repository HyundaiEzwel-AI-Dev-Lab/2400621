package com.hpms.prototype.api.dto;

import java.util.List;

public record MyWorkCalendarResponse(
    MyWorkSummaryResponse summary,
    int year,
    int month,
    List<CalendarEventResponse> events,
    int unscheduledTaskCount,
    List<UnscheduledTaskItemResponse> unscheduledTasks) {}
