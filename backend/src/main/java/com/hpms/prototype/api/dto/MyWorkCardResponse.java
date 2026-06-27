package com.hpms.prototype.api.dto;

import java.util.List;

public record MyWorkCardResponse(
    MyWorkSummaryResponse summary,
    List<ProjectCardResponse> progressProjects,
    List<MyTaskResponse> myTasks,
    List<WaitingProjectResponse> waitingProjects) {}
