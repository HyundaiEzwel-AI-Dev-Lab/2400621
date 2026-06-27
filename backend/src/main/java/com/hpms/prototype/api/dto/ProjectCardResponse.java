package com.hpms.prototype.api.dto;

import java.time.LocalDate;

public record ProjectCardResponse(
    long projectId,
    String projectName,
    String status,
    String statusName,
    LocalDate targetDate,
    Integer dDay,
    int progressRate,
    int metricA,
    int metricB,
    int metricC) {}
