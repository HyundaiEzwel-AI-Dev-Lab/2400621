package com.hpms.prototype.api.dto;

import java.time.LocalDate;

public record CalendarEventResponse(
    long eventId,
    long taskId,
    String taskName,
    long projectId,
    String projectName,
    LocalDate startDate,
    LocalDate endDate,
    String displayDateText,
    boolean delayed) {}
