package com.hpms.prototype.api.dto;

import java.time.LocalDate;

public record MyTaskResponse(
    long taskId,
    String taskName,
    long projectId,
    String projectName,
    LocalDate dueDate,
    Integer dDay,
    boolean delayed,
    Integer progressRate,
    boolean scheduled,
    boolean scheduleActionEnabled,
    boolean wbsDetailActionEnabled) {}
