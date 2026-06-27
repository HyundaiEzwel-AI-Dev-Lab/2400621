package com.hpms.prototype.api.dto;

import java.time.LocalDate;

public record WaitingProjectResponse(
    long projectId,
    String projectName,
    String status,
    String statusName,
    String departmentName,
    LocalDate openDate,
    String openDateText) {}
