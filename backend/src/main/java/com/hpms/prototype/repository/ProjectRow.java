package com.hpms.prototype.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectRow(
    Long projectId,
    String projectName,
    String status,
    LocalDate targetDate,
    LocalDate openDate,
    String openDateText,
    BigDecimal progressRate,
    String departmentName) {}
