package com.hpms.prototype.api.dto;

public record MyWorkSummaryResponse(
    String scope,
    int progressProjectCount,
    int myTaskCount,
    int weeklyDueCount,
    int delayedCount,
    int waitingProjectCount) {}
