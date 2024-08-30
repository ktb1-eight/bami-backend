package com.example.bami.city.controller;

import com.example.bami.city.domain.Schedule;
import com.example.bami.city.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/longstay")
@Tag(name = "장기 여행지 도시 추천", description = "장기 여행지로 추천된 도시의 정보를 조회 및 저장합니다.")
public class ScheduleController {

    private final ScheduleService service;

    public ScheduleController(ScheduleService service) {
        this.service = service;
    }

    @Operation(summary = "여행 일정 저장", description = "새로운 장기 여행지 일정을 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "여행 일정 저장 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/save-schedule")
    public Schedule saveSchedule(
            @Parameter(description = "저장할 여행 일정 정보", required = true) @RequestBody Schedule schedule) {
        return service.saveSchedule(schedule);
    }
}
