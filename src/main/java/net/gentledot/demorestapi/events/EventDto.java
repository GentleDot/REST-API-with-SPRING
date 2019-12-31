package net.gentledot.demorestapi.events;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventDto {

    @Schema(example = "testEvent", description = "이벤트 이름")
    @NotEmpty
    private String name;    // 이벤트 이름

    @Schema(example = "테스트용도로 생성한 Event", description = "이벤트 설명")
    @NotEmpty
    private String description;     // 이벤트 설명

    @Schema(example = "2019-11-19T14:21:00", description = "등록 시작일시")
    @NotNull
    private LocalDateTime beginEnrollmentDateTime;  // 등록 시작일시

    @Schema(example = "2019-11-20T14:21:00", description = "등록 종료일시")
    @NotNull
    private LocalDateTime closeEnrollmentDateTime;  // 등록 종료일시

    @Schema(example = "2019-11-21T14:21:00", description = "이벤트 시작일시")
    @NotNull
    private LocalDateTime beginEventDateTime;   // 이벤트 시작일시

    @Schema(example = "2019-11-26T14:21:00", description = "이벤트 종료일시")
    @NotNull
    private LocalDateTime endEventDateTime;     // 이벤트 종료일시

    @Schema(example = "강남역 5번 출구", description = "이벤트 장소")
    private String location; // 장소 (optional) 이게 없으면 온라인 모임

    @Schema(example = "100", description = "이벤트 참가비(기준)")
    @Min(0)
    private int basePrice; // (optional)

    @Schema(example = "200", description = "이벤트 참가비(최대)")
    @Min(0)
    private int maxPrice; // (optional)

    @Schema(example = "100", description = "최대 이벤트 참여인원")
    @Min(0)
    private int limitOfEnrollment;
}
