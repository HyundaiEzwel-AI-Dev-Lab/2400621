package com.hpms.prototype.api.dto;

public record UnscheduledTaskItemResponse(
    long taskId,
    long projectId,
    String projectName,
    String wbsTaskName,
    boolean scheduleRegisterable) {}
