package net.gentledot.demorestapi.events;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import net.gentledot.demorestapi.accounts.Account;
import net.gentledot.demorestapi.accounts.AccountSerializer;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;


@Builder(builderMethodName = "eventBuilder") // 빌더패턴으로 객체 생성
@AllArgsConstructor // 생성자(모든 변수를 파라미터로)
@NoArgsConstructor // 생성자(파라미터 없음)
@Getter // (getXXX 생성)
@Setter // (setXXX 생성)
@EqualsAndHashCode(of = "id")   // ??
@Entity // JPA 설정
@DynamicUpdate
public class Event {

    @Id // ??
    @GeneratedValue     // JPA에서 자동 생성
    @Schema(example = "10", description = "자동생성")
    private Integer id;

    @Schema(example = "testEvent", description = "이벤트 이름")
    private String name;    // 이벤트 이름

    @Schema(example = "테스트용도로 생성한 Event", description = "이벤트 설명")
    private String description;     // 이벤트 설명

    // JPA 3.2 ~ LocalDateTime 매핑 지원
    @Schema(example = "2019-11-19T14:21:00", description = "등록 시작일시")
    private LocalDateTime beginEnrollmentDateTime;  // 등록 시작일시

    @Schema(example = "2019-11-20T14:21:00", description = "등록 종료일시")
    private LocalDateTime closeEnrollmentDateTime;  // 등록 종료일시

    @Schema(example = "2019-11-21T14:21:00", description = "이벤트 시작일시")
    private LocalDateTime beginEventDateTime;   // 이벤트 시작일시

    @Schema(example = "2019-11-26T14:21:00", description = "이벤트 종료일시")
    private LocalDateTime endEventDateTime;     // 이벤트 종료일시

    @Schema(example = "강남역 5번 출구", description = "이벤트 장소")
    private String location; // 장소 (optional) 이게 없으면 온라인 모임

    @Schema(example = "100", description = "이벤트 참가비(기준)")
    private int basePrice; // (optional)

    @Schema(example = "200", description = "이벤트 참가비(최대)")
    private int maxPrice; // (optional)

    @Schema(example = "100", description = "최대 이벤트 참여인원")
    private int limitOfEnrollment;

    @Schema(example = "true", description = "오프라인 여부")
    private boolean offline;    // 오프라인 여부

    @Schema(example = "false", description = "무료 이벤트 여부")
    private boolean free;   // 무료 여부

    // enum은 default가 EnumType.ORDINAL (순서에 따라 0, 1, n)
    @Schema(example = "DRAFT", description = "설정 없을 시 기본이 DRAFT")
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    @ManyToOne
    @JsonSerialize(using = AccountSerializer.class) // 동일한 Annotation이 존재하므로 주의 (org.codehaus.jackson.map.annotate.JsonSerialize)
    private Account manager;

    public void updateFree() {
        if (this.basePrice == 0 && this.maxPrice == 0){
            this.free = true;
        } else {
            this.free = false;
        }
    }

    public void updateOffline() {
        // this.location.isBlank() : java11 부터 등장, 공백 trim 및 공백문자 replace 후 this.location.isEmpty() 처리
        if (this.location == null || this.location.isBlank()){
            this.offline = false;
        } else {
            this.offline = true;
        }
    }
}
