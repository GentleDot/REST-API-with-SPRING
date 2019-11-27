package net.gentledot.demorestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.gentledot.demorestapi.common.RestDocsConfiguration;
import net.gentledot.demorestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
//@WebMvcTest
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTest {
    // charset = UTF-8 로 명시된 상수는 deprecated
    private static String CONTENT_TYPE_HAL_JSON = MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8";

    // @SpringBootTest 에서 MockMvc 클래스를 사용하려면 @AutoConfigureMockMvc를 사용한다.
    // WebEnvironment webEnvironment() default WebEnvironment.MOCK; >> Mocking을 하는 DispatcherServlet를 생성
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // @WebMvcTest는 MVC 관련 Bean을 테스트배드에 올리는 것이므로 Transaction 처리 결과는 모두 Null
    // 따라서 Transaction에 대한 처리 방식이 어떻게 되는지 구현 필요 (Stubbing)
    // Bean 전체를 테스트배드에 올리는 @SpringBootTest 를 설정하면 Stubbing이 불필요 (통합 테스트)
//    @MockBean
//    EventRepository eventRepository;

    private FieldDescriptor[] eventFields = new FieldDescriptor[]{
            fieldWithPath("name").description("name of new event"),
            fieldWithPath("description").description("description of new event"),
            fieldWithPath("beginEnrollmentDateTime").description("date time of beginEnrollmentDateTime"),
            fieldWithPath("closeEnrollmentDateTime").description("date time of closeEnrollmentDateTime"),
            fieldWithPath("beginEventDateTime").description("date time of beginEventDateTime"),
            fieldWithPath("endEventDateTime").description("date time of endEventDateTime"),
            fieldWithPath("location").description("location of new event"),
            fieldWithPath("basePrice").description("basePrice of new event"),
            fieldWithPath("maxPrice").description("maxPrice of new event"),
            fieldWithPath("limitOfEnrollment").description("limit Of enrollment")
    };

    /* 입력 값을 전달 시 JSON 응답으로 status=201 나오는지 확인 - DTO 이외의 값은 무시, default로 설정됨*/
    @Test
    @TestDescription("정상적인 이벤트를 생성하는 테스트 (201 응답)")
    public void createEvent() throws Exception {

        // 요청 입력
        EventDto event = EventDto.builder()
                // Id는 AutoGenerated
                .name("Spring")
                .description("REST API Dev. with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 11, 19, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 11, 20, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2019, 11, 21, 14, 21))
                .endEventDateTime(LocalDateTime.of(2019, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 5번 출구")
                .build();

        // @WebMvcTest 에서 JPA 테스트가 없으므로 save에 대한 Mocking이 필요
        // event는 eventBuilder로 생성된 객체가 아니고 ModelMapper를 통해 생성된 객체
        // Mocking이 성립되지 않으므로 Null을 반환하게 된다.
//        when(eventRepository.save(event)).thenReturn(event);

        // 요청 동작
        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
                .characterEncoding("UTF-8")
        )
                .andDo(print())
                .andExpect(status().isCreated())
                // org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
                .andExpect(jsonPath("id").exists())
//                .andExpect(header().exists("Location"))
//                .andExpect(header().string("Content-Type", "application/hal+json"))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_HAL_JSON))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing"),
                                linkWithRel("profile").description("link to profile (how to create event)")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content header")
                        ),
                        requestFields(
                                this.eventFields
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header (생성된 이벤트 조회 url)"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type (application/hal+json;charset=UTF-8)")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("offline").description("information about if this event is offline event or not"),
                                fieldWithPath("free").description("information about if this event is free or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                subsectionWithPath("_links").description("links in event (self, query-events, update-event)")
                        ).and(this.eventFields))
                );
    }


    /* 입력 값 중에서 받기로 한 값 이외의 값이 들어온 경우 - 400 error 출력 */
    @Test
    @TestDescription("입력 받기로 한 값 이외의 값이 들어올 때 에러가 발생하는 테스트 (400 error)")
    public void createEvent_badRequest_inputNotDtoData() throws Exception {
        // 요청 입력
        Event event = Event.eventBuilder()
                // Id는 AutoGenerated
                .id(100)
                .name("Spring")
                .description("REST API Dev. with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 11, 19, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 11, 20, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2019, 11, 21, 14, 21))
                .endEventDateTime(LocalDateTime.of(2019, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 5번 출구")
                .free(true) // DTO에서 제외되어 있으므로 default인 false로 설정됨
                .offline(true) // DTO에서 제외되어 있으므로 default인 false로 설정됨
                .eventStatus(EventStatus.ENDED) // DTO에서 제외되어 있으므로 default로 설정됨
                .build();

        // @WebMvcTest 에서 JPA 테스트가 없으므로 save에 대한 Mocking이 필요
        // event는 eventBuilder로 생성된 객체가 아니고 ModelMapper를 통해 생성된 객체
        // Mocking이 성립되지 않으므로 Null을 반환하게 된다.
//        when(eventRepository.save(event)).thenReturn(event);

        // 요청 동작
        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
                .characterEncoding("UTF-8")
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("빈값이 들어올 때 에러를 발생하는 테스트 (400 error)")
    public void createEvent_badRequest_emptyInput() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("정상적이지 않은 값이 들어올 때 에러가 발생하는 테스트 (400 error)")
    public void createEvent_badRequest_wrongInput() throws Exception {
        EventDto eventDto = EventDto.builder()
                // Id는 AutoGenerated
                .name("Spring")
                .description("REST API Dev. with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 11, 26, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 11, 25, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2019, 11, 24, 14, 21))
                .endEventDateTime(LocalDateTime.of(2019, 11, 23, 14, 21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 5번 출구")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
        ;
    }
}
