package net.gentledot.demorestapi.events;

import net.gentledot.demorestapi.accounts.Account;
import net.gentledot.demorestapi.accounts.AccountRepository;
import net.gentledot.demorestapi.accounts.AccountRole;
import net.gentledot.demorestapi.accounts.AccountService;
import net.gentledot.demorestapi.common.AppProperties;
import net.gentledot.demorestapi.common.BaseControllerTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTest extends BaseControllerTest {
    // charset = UTF-8 로 명시된 상수는 deprecated
    private static String CONTENT_TYPE_HAL_JSON = MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8";

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties properties;

    // @WebMvcTest는 MVC 관련 Bean을 테스트배드에 올리는 것이므로 Transaction 처리 결과는 모두 Null
    // 따라서 Transaction에 대한 처리 방식이 어떻게 되는지 구현 필요 (Stubbing)
    // Bean 전체를 테스트배드에 올리는 @SpringBootTest 를 설정하면 Stubbing이 불필요 (통합 테스트)
//    @MockBean
//    EventRepository eventRepository;

    @BeforeEach
    public void setUp() {
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    private FieldDescriptor[] eventFields = new FieldDescriptor[]{
            fieldWithPath("name").description("이벤트명(event name)"),
            fieldWithPath("description").description("이벤트 설명(description of event)"),
            fieldWithPath("beginEnrollmentDateTime").description("이벤트 등록 시작일시(date time of begin enrollment)"),
            fieldWithPath("closeEnrollmentDateTime").description("이벤트 등록 종료일시(date time of close enrollment)"),
            fieldWithPath("beginEventDateTime").description("이벤트 시작일시(date time of begin event)"),
            fieldWithPath("endEventDateTime").description("이벤트 종료일시(date time of end event"),
            fieldWithPath("location").description("이벤트 장소(location of event) * 장소가 없다면 온라인 이벤트"),
            fieldWithPath("basePrice").description("이벤트 등록비 - 최소한도(base price of event)"),
            fieldWithPath("maxPrice").description("이벤트 등록비 - 최대한도(max price of event)"),
            fieldWithPath("limitOfEnrollment").description("이벤트 등록 가능 인원(limit Of enrollment)"),

    };

    private FieldDescriptor[] getEventsFields = new FieldDescriptor[]{
            fieldWithPath("_embedded.eventList[].id").description("이벤트 구분자(identifier of event)"),
            fieldWithPath("_embedded.eventList[].offline").description("이벤트 장소 구분 - 오프라인 / 온라인(information about if this event is offline event or not)"),
            fieldWithPath("_embedded.eventList[].free").description("이벤트 무료 여부(information about if this event is free or not)"),
            fieldWithPath("_embedded.eventList[].eventStatus").description("이벤트 진행 상태(event status)"),
            fieldWithPath("_embedded.eventList[].name").description("이벤트명(event name)"),
            fieldWithPath("_embedded.eventList[].description").description("이벤트 설명(description of event)"),
            fieldWithPath("_embedded.eventList[].beginEnrollmentDateTime").description("이벤트 등록 시작일시(date time of begin enrollment)"),
            fieldWithPath("_embedded.eventList[].closeEnrollmentDateTime").description("이벤트 등록 종료일시(date time of close enrollment)"),
            fieldWithPath("_embedded.eventList[].beginEventDateTime").description("이벤트 시작일시(date time of begin event)"),
            fieldWithPath("_embedded.eventList[].endEventDateTime").description("이벤트 종료일시(date time of end event"),
            fieldWithPath("_embedded.eventList[].location").description("이벤트 장소(location of event) * 장소가 없다면 온라인 이벤트"),
            fieldWithPath("_embedded.eventList[].basePrice").description("이벤트 등록비 - 최소한도(base price of event)"),
            fieldWithPath("_embedded.eventList[].maxPrice").description("이벤트 등록비 - 최대한도(max price of event)"),
            fieldWithPath("_embedded.eventList[].limitOfEnrollment").description("이벤트 등록 가능 인원(limit Of enrollment)"),
            fieldWithPath("_embedded.eventList[]._links.self.href").description("self link")
    };

    /* 입력 값을 전달 시 JSON 응답으로 status=201 나오는지 확인 - DTO 이외의 값은 무시, default로 설정됨*/
    @Test
    @DisplayName("정상적인 이벤트를 생성하는 테스트 (201 응답)")
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
                                fieldWithPath("manager.id").description("identifier of event manager"),
                                fieldWithPath("manager.email").description("email address of event manager"),
                                subsectionWithPath("_links").description("links in event (self, query-events, update-event)")
                        ).and(this.eventFields))
                );
    }

    /* 입력 값 중에서 받기로 한 값 이외의 값이 들어온 경우 - 400 error 출력 */
    @Test
    @DisplayName("입력 받기로 한 값 이외의 값이 들어올 때 에러가 발생하는 테스트 (400 error)")
    public void createEventWithBadRequestInputNotDtoData() throws Exception {
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
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
    @DisplayName("빈값이 들어올 때 에러를 발생하는 테스트 (400 error)")
    public void createEventWithBadRequestEmptyInput() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("정상적이지 않은 값이 들어올 때 에러가 발생하는 테스트 (400 error)")
    public void createEventWithBadRequestWrongInput() throws Exception {
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
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                // errors 가 JSON array 로 생성되면서 @unwrapped 가 동작하지 않고 content에 wrapping 되기 때문에 $[0] -> content[0] 로 수정되었음.
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 출력하여 2번째 페이지 조회하기 - 권한정보 확인")
    public void getEventsWithPagingAndAuthentication() throws Exception {
        //  given
        Account account = createAccount();

        // Lambda
//        IntStream.range(0, 30).forEach(i -> {
//            this.generateEvent(i);
//        });
        // Method Reference
//        IntStream.range(0, 30).forEach(this::generateEvent);
        // For loop
        for (int i = 0; i < 30; i++) {
            this.generateEvent(i, account);
        }

        // when
        ResultActions perform = this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC"));

        // then
        this.mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("get-events",
                        links(
                                linkWithRel("first").description("link to first event page"),
                                linkWithRel("prev").description("link to previous event page"),
                                linkWithRel("self").description("link to self"),
                                linkWithRel("next").description("link to next event page"),
                                linkWithRel("last").description("link to last event page"),
                                linkWithRel("profile").description("link to profile (how to get events)"),
                                linkWithRel("create-event").description("link to create event (when authorized user was logged in)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type (application/hal+json;charset=UTF-8)")
                        ),
                        responseFields(
                                fieldWithPath("page.size").description("printed events number in a page"),
                                fieldWithPath("page.totalElements").description("total event count"),
                                fieldWithPath("page.totalPages").description("total page count"),
                                fieldWithPath("page.number").description("current page number (start with 0)"),
                                fieldWithPath("_embedded.eventList[].manager.id").description("이벤트 매니저 구분자(identifier of event manager)"),
                                fieldWithPath("_embedded.eventList[].manager.email").description("이벤트 매니저 이메일주소(email adress or event manager)"),
                                subsectionWithPath("_links").description("links in getEvents (first, prev, self, next, last, profile)")
                        ).and(this.getEventsFields))
                );

    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 출력하여 2번째 페이지 조회하기 - anonymous")
    public void getEventsWithPagingWhenNotLogin() throws Exception {
        //  given
        // Lambda
//        IntStream.range(0, 30).forEach(i -> {
//            this.generateEvent(i);
//        });
        // Method Reference
//        IntStream.range(0, 30).forEach(this::generateEvent);
        // For loop
        for (int i = 0; i < 30; i++) {
            this.generateEvent(i);
        }

        // when
        ResultActions perform = this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC"));

        // then
        this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists());
    }

    @Test
    @DisplayName("기존 생성 되어있는 이벤트 조회하기")
    public void getEvent() throws Exception {
        // given
        Account account = this.createAccount();
        Event event = this.generateEvent(100, account);

        // when
        ResultActions perform = this.mockMvc.perform(get("/api/events/{id}", event.getId()));

        // then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile (how to get event)")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type (application/hal+json;charset=UTF-8)")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("offline").description("information about if this event is offline event or not"),
                                fieldWithPath("free").description("information about if this event is free or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("manager.id").description("identifier of event manager"),
                                fieldWithPath("manager.email").description("email address of event manager"),
                                subsectionWithPath("_links").description("links in event (self, profile)")
                        ).and(this.eventFields))
                );
    }

    @Test
    @DisplayName("없는 이벤트 조회하기 (404 not found)")
    public void getNullEvent() throws Exception {
        // given

        // when
        ResultActions perform = this.mockMvc.perform(get("/api/events/000000"));

        // then
        this.mockMvc.perform(get("/api/events/000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("기존 생성 되어있는 이벤트 수정하기")
    public void updateEvent() throws Exception {
        // given
        Account account = this.createAccount();
        Event event = this.generateEvent(108, account);
        EventDto eventDto = EventDto.builder()
                .name("modified event")
                .description(event.getDescription())
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 2, 9, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 6, 23, 59))
                .beginEventDateTime(LocalDateTime.of(2019, 12, 16, 10, 0))
                .endEventDateTime(LocalDateTime.of(2020, 1, 3, 18, 30))
                .basePrice(85000)
                .maxPrice(1250000)
                .limitOfEnrollment(60)
                .location("")
                .build();

        // when
        ResultActions actions = this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)));

        // then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("update-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile (how to update event)")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content header")
                        ),
                        requestFields(
                                this.eventFields
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type (application/hal+json;charset=UTF-8)")
                        ),
                        responseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("offline").description("information about if this event is offline event or not"),
                                fieldWithPath("free").description("information about if this event is free or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("manager.id").description("identifier of event manager"),
                                fieldWithPath("manager.email").description("email address of event manager"),
                                subsectionWithPath("_links").description("links in event (self, profile)")
                        ).and(this.eventFields))
                );
    }

    @Test
    @DisplayName("기존 생성 되어있는 이벤트 수정하기 - Account 정보 없음")
    public void updateEventWithUnauthorized() throws Exception {
        // given
        Event event = this.generateEvent(108);
        EventDto eventDto = EventDto.builder()
                .name("modified event")
                .description(event.getDescription())
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 2, 9, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 6, 23, 59))
                .beginEventDateTime(LocalDateTime.of(2019, 12, 16, 10, 0))
                .endEventDateTime(LocalDateTime.of(2020, 1, 3, 18, 30))
                .basePrice(85000)
                .maxPrice(1250000)
                .limitOfEnrollment(60)
                .location("")
                .build();

        // when
        ResultActions actions = this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)));

        // then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("기존 생성 되어있는 이벤트 수정하기 - 사용자가 다른 경우")
    public void updateEventWithForbiddenResponse() throws Exception {
        // given
        Account account = this.createAccount();
        Account anotherAccount = this.createTestAccount();
        Event event = this.generateEvent(108, account);
        EventDto eventDto = EventDto.builder()
                .name("modified event")
                .description(event.getDescription())
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 2, 9, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 6, 23, 59))
                .beginEventDateTime(LocalDateTime.of(2019, 12, 16, 10, 0))
                .endEventDateTime(LocalDateTime.of(2020, 1, 3, 18, 30))
                .basePrice(85000)
                .maxPrice(1250000)
                .limitOfEnrollment(60)
                .location("")
                .build();

        // when
        ResultActions actions = this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)));

        // then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getAnotherTestAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("없는 이벤트 수정하기 (404 not found)")
    public void updateNullEvent() throws Exception {
        // given
        this.generateEvent(100);
        EventDto eventDto = EventDto.builder()
                .name("modified event")
                .description("Changed online event")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 12, 2, 9, 0))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 12, 6, 23, 59))
                .beginEventDateTime(LocalDateTime.of(2019, 12, 16, 10, 0))
                .endEventDateTime(LocalDateTime.of(2020, 1, 3, 18, 30))
                .basePrice(85000)
                .maxPrice(1250000)
                .limitOfEnrollment(60)
                .location("")
                .build();

        // when
        ResultActions actions = this.mockMvc.perform(put("/api/events/000000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)));

        // then
        this.mockMvc.perform(put("/api/events/000000")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("빈 수정 데이터가 들어올 때 에러를 발생하는 테스트 (400 error)")
    public void updateEventWithBadRequestEmptyInput() throws Exception {
        // given
        Event event = this.generateEvent(108);
        EventDto eventDto = EventDto.builder().build();

        // when
        ResultActions actions = this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)));

        // then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("수정값 중에 받기로 한 값 이외의 값이 들어올 때 에러가 발생하는 테스트 (400 error)")
    public void updateEventWithBadRequestInputNotDtoData() throws Exception {
        // given
        Event event = this.generateEvent(108);
        EventDto eventDto = EventDto.builder()
                .name("testWrongEvent_update")
                .description("비정상적인 값 처리 테스트")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 11, 26, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 11, 25, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2019, 11, 24, 14, 21))
                .endEventDateTime(LocalDateTime.of(2019, 11, 23, 14, 21))
                .basePrice(999999)
                .maxPrice(100)
                .limitOfEnrollment(1)
                .location("강남역 5번 출구")
                .build();

        // when
        ResultActions actions = this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)));

        // then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                // errors 가 JSON array 로 생성되면서 @unwrapped 가 동작하지 않고 content에 wrapping 되기 때문에 $[0] -> content[0] 로 수정되었음.
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }

    private Event generateEvent(int index) {
        Event event = buildEvent(index);
        return this.eventRepository.save(event);
    }


    private Event generateEvent(int index, Account account) {
        Event event = buildEvent(index);
        event.setManager(account);
        return this.eventRepository.save(event);
    }

    private Event buildEvent(int index) {
        return Event.eventBuilder()
                .name("testEvent" + index)
                .description("test event_" + index)
                .basePrice(100)
                .build();
    }

    private String getBearerToken() throws Exception {
        return "Bearer" + getAccessToken(true);
    }

    private String getBearerToken(Boolean needToCreateAccount) throws Exception {
        return "Bearer" + getAccessToken(needToCreateAccount);
    }

    private String getAccessToken(Boolean needToCreateAccount) throws Exception {
        if (needToCreateAccount) {
            createAccount();
        }

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(properties.getClientId(), properties.getClientSecret()))
                .param("username", properties.getUsername())
                .param("password", properties.getUserPassword())
                .param("grant_type", "password"));

//        MockHttpServletResponse response = perform.andReturn().getResponse();
//        String responseBody = response.getContentAsString();
        String responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser jsonParser = new Jackson2JsonParser();
        return jsonParser.parseMap(responseBody).get("access_token").toString();
    }

    private Account createAccount() {
        Account account = Account.builder()
                .email(properties.getUsername())
                .password(properties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        return this.accountService.saveAccount(account);
    }

    private Account createTestAccount() {
        Account account = Account.builder()
                .email(properties.getTestUsername())
                .password(properties.getTestUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        return this.accountService.saveAccount(account);
    }

    private String getAnotherTestAccessToken() throws Exception {

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(properties.getClientId(), properties.getClientSecret()))
                .param("username", properties.getTestUsername())
                .param("password", properties.getTestUserPassword())
                .param("grant_type", "password"));

//        MockHttpServletResponse response = perform.andReturn().getResponse();
//        String responseBody = response.getContentAsString();
        String responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser jsonParser = new Jackson2JsonParser();
        return "Bearer" + jsonParser.parseMap(responseBody).get("access_token").toString();
    }

}
