package com.hpms.prototype.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hpms.prototype.api.dto.MyWorkSummaryResponse;
import com.hpms.prototype.api.dto.TaskActionResponse;
import com.hpms.prototype.api.dto.TaskActionResponse.TaskActions;
import com.hpms.prototype.application.InvalidMyWorkRequestException;
import com.hpms.prototype.application.MyWorkService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class MyWorkControllerTest {

  private MyWorkService service;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    service = Mockito.mock(MyWorkService.class);
    mockMvc =
        MockMvcBuilders.standaloneSetup(new MyWorkController(service))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void returnsSummary() throws Exception {
    when(service.getSummary(eq(1L), eq("INTEGRATED"), any(LocalDate.class)))
        .thenReturn(new MyWorkSummaryResponse("INTEGRATED", 5, 4, 2, 1, 2));

    mockMvc
        .perform(get("/api/v1/my-work/summary").header("X-User-Id", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.scope").value("INTEGRATED"))
        .andExpect(jsonPath("$.data.progressProjectCount").value(5))
        .andExpect(jsonPath("$.data.myTaskCount").value(4));
  }

  @Test
  void returnsBadRequestWhenScopeIsUnsupported() throws Exception {
    when(service.getSummary(eq(1L), eq("DEPARTMENT"), any(LocalDate.class)))
        .thenThrow(new InvalidMyWorkRequestException("지원하지 않는 조회 범위입니다."));

    mockMvc
        .perform(get("/api/v1/my-work/summary").header("X-User-Id", "1").param("scope", "DEPARTMENT"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("MY_WORK_BAD_REQUEST"));
  }

  @Test
  void returnsTaskActions() throws Exception {
    when(service.getTaskActions(1L, 1L))
        .thenReturn(new TaskActionResponse(1L, 10L, new TaskActions(true, true)));

    mockMvc
        .perform(get("/api/v1/my-work/tasks/1/actions").header("X-User-Id", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.taskId").value(1))
        .andExpect(jsonPath("$.data.projectId").value(10))
        .andExpect(jsonPath("$.data.actions.scheduleManagement").value(true))
        .andExpect(jsonPath("$.data.actions.wbsDetail").value(true));
  }

  @Test
  void returnsNotFoundWhenTaskDoesNotExist() throws Exception {
    when(service.getTaskActions(1L, 999L)).thenThrow(new IllegalArgumentException("업무를 찾을 수 없습니다."));

    mockMvc
        .perform(get("/api/v1/my-work/tasks/999/actions").header("X-User-Id", "1"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("MY_WORK_NOT_FOUND"));
  }
}
