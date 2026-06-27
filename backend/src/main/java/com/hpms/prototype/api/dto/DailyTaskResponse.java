package com.hpms.prototype.api.dto;

import java.time.LocalDate;
import java.util.List;

public record DailyTaskResponse(LocalDate date, int taskCount, List<DailyTaskItemResponse> tasks) {}
