package com.hpms.prototype.api.dto;

import java.time.LocalDate;

public record DailyTaskItemResponse(
    long taskId,
    String taskName,
    LocalDate dueDate,
    String displayDueText,
    long projectId,
    String projectName,
    boolean delayed) {}
