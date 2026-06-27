package com.hpms.prototype.api.dto;

import java.util.List;

public record UnscheduledTaskResponse(int totalCount, List<UnscheduledTaskItemResponse> items) {}
