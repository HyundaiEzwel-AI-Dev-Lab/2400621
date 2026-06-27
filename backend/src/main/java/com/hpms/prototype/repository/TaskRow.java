package com.hpms.prototype.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TaskRow(
    Long taskId,
    String taskName,
    String taskStatus,
    Long projectId,
    String projectName,
    BigDecimal progressRate,
    LocalDate startDate,
    LocalDate dueDate,
    int sortOrder) {}
