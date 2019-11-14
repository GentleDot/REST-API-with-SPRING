package net.gentledot.demorestapi.events;

import lombok.*;

import java.time.LocalDateTime;


@Builder(builderMethodName = "eventBuilder")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Event {

    private Integer id;
    private String name;    // 이벤트 이름
    private String description;     // 이벤트 설명
    private LocalDateTime beginEnrollmentDateTime;  // 등록 시작일시
    private LocalDateTime closeEnrollmentDateTime;  // 등록 종료일시
    private LocalDateTime beginEventDateTime;   // 이벤트 시작일시
    private LocalDateTime endEventDateTime;     // 이벤트 종료일시
    private String location; // 장소 (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;    // 오프라인 여부
    private boolean free;   // 무료 여부
    private EventStatus eventStatus;

}
