# REST-API 실습

## 이력
| 날짜 | 내용 |
| :---: | --- |
| 2019.10.26 | 프로젝트 최초 생성 |
| 2019.12.06 | 완강, 1차완성 |

## 목차
- [목표](#목표)
- [출처](#출처)
- 학습 내용
    - [REST API는 무엇인가?](#REST-API)
    - [EVENT API](#EVENT-API)
    - [Project 생성](#Project-생성)
    - [EVENT API 구현](#EVENT-API-구현)

## 목표
1. 스프링부트 기본기 습득
2. 테스트 주도 개발(TDD) 익히기
3. Self-Describtive Message와 HATEOAS를 만족하는 REST API 이해
4. OAuth2 활용 이해 (SPRING SECURITY) 

## 출처 
* 강좌
    1. [스프링 기반 REST-API 개발 / 백기선](https://www.inflearn.com/course/spring_rest-api)

* 문서
    1. [Spring HATEOAS Reference](https://docs.spring.io/spring-hateoas/docs/current/reference/html)
    1. [MAVEN WINDOWS 설치 - 제타위키](https://zetawiki.com/wiki/%EC%9C%88%EB%8F%84%EC%9A%B0_%EB%A9%94%EC%9D%B4%EB%B8%90_%EC%84%A4%EC%B9%98)
    1. [What is REST?](https://midnightcow.tistory.com/entry/REST-What-is-REST-1)
    1. [REST API 제대로 알고 사용하기](https://meetup.toast.com/posts/92)
    1. [REST API란?](https://jcon.tistory.com/88)
    1. [Common Application properties](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html)
    

## 학습내용

### REST API
REST(<b>Re</b>presentational <b>S</b>tate <b>T</b>ransfer)  

- 인터넷 상에서 시스템 간의 상호 운용성(interoperability)을 제공하는 방법중 하나

- 시스템 제각각의 독립적인 진화를 보장하기
위한 방법

- REST API: REST 아키텍처 스타일을 따르는 API

- REST Architecture  
    출처 : [What is REST?](https://midnightcow.tistory.com/entry/REST-What-is-REST-1)
    >  REST는 철저히 Resource 중심적 설계를 중시하고, Create / Read / Update / Delete (CURD)에 해당하는 HTTP의 4 가지 Method POST / GET / PUT / DELECT 를 그 용도에 맞게 정확하게 사용함으로써, 기존의 Service 중심의 Web Application(SOA - Service, Verbs 중심)을 Resource 중심의 Web Application(ROA - Resource, Nouns)으로의 변화를 일으켰다. REST는 Protocol이 아닌 설계론이라는 것을 이해하자.

    - Client-Server
    - Stateless
    - Cache
    - __Uniform Interface__
        - identification of resources
        - manipulation of resources through represenations
        - _Self-descriptive messages_
        - _hypermedia as the engine of appliaction state (HATEOAS)_
    - Layered System
    - Code-On-Demand (optional)

- Self-descriptive messages 와 HATEOAS 를 만족시키려면?
1. Self-descriptive message
    - 요건
        - 메시지 스스로 메시지에 대한 설명이 가능해야 한다.
            - 메시지의 State, Information 등을 HTTP method, HTTP status code, HTTP Header 등을 활용하여 전달
        - 서버가 변동이 있더라도 클라이언트는 메시지를 통해 해석이 가능해야 한다(코드만 보고도 온전한 해석이 가능해야)
        - 확장 가능한 커뮤니케이션
    
    - 방법
        1. 리소스를 리턴할 때 IANA(The Internet Assigned Numbers Authority)에 미디어 타입을 등록하고 이 정의된 미디어 타입을 Content-Type으로 사용한다.
            - 단점 : 매번 media type을 정의해야 한다.
        1. profile 링크 헤더에 의미를 정의한 명세를 추가한다. (대안으로 HAL의 링크 데이터에 profile 링크를 추가)
    
1. HATEOAS
    - 요건
        - 하이퍼미디어(links)를 통해 application 상태 변화가 가능해야 한다.(다음 상태로 전이가 가능) 
        - 링크 정보를 동적으로 바꿀 수 있다.
    
    - 방법
        > 출처 : [REST API란?](https://jcon.tistory.com/88)
        1. 데이터에 링크를 제공한다.
            - 단점 : 링크를 표현하는 방법을 profile 또는 media type에 직접 정의해야 한다.
        
        1. 데이터에 JSON으로 hyperlink를 표현하는 방법을 정의한 명세들을 활용
            - JSON API
            - HAL
            - UBER
            - Siren
            - Collection+json 등
            - 단점 : 기존 API에서 추가 필요 (침투적) 
        
        1. HTTP Header로 link, location 등을 표현
            - 단점 : 정의된 relation만 활용한다면 표현에 한계가 있음.

1. (필자가 이해한)결론
    - 리턴 시 HTTP Header에 Content-type 제공
    - HAL 링크 데이터를 통해 profile, self 등을 추가
    - 명세는 Spring REST Docs를 통해 문서화하여 링크 제공
  
  
### EVENT API
- 문서
    - Spring REST Docs로 제작
        - [생성한 Spring REST Docs](./rest-doc-sample.html)

- 이벤트 
1. GET /api/events (이벤트 목록 조회)
    - 로그인 없이 접속한 상태
        - 이벤트 목록
        - 링크
            - self : 리턴하는 자신의 Request URI 
            - profile : 이벤트 목록 조회 API 문서로 링크
            - get-event : 이벤트 하나를 조회하는 API 링크
            - next : 다음 페이지 (optional)
            - prev : 이전 페이지 (optional)
         
    - 로그인 상태 (Bearer Header에 유효한 AccessToken이 들어있는 경우)
        - 이벤트 목록
        - 링크
            - self  
            - profile
            - get-event
            - create-event : 이벤트를 생성할 수 있는 API 링크
            - next 
            - prev

1. POST /api/events (이벤트 생성)
    - 로그인 없이 접속한 상태
        - 인증 필요
    
    - 로그인 상태
        - 생성된 이벤트 정보
        - 링크
            - self
            - update-event : 생성된 이벤트를 수정할 수 있는 API 링크
            - query-events : 이벤트 목록 조회할 수 있는 API 링크
            - profile

1. GET /api/events/{id} (특정 이벤트 하나 조회)
    - 로그인 여부와 상관없이
        - 조회한 이벤트 정보
        - 링크
            - self
            - profile

1. PUT /api/events/{id} (이벤트 수정)
    - 로그인 필요 
        - 수정된 이벤트 정보
        - 링크
            - self
            - profile
            
            
### Project 생성
- Spring Boot로 생성
    - 의존성 설정 (maven, gradle)
    - 자동 설정 (@EnableAutoConfiguration)
    - 내장 웹 서버 (servlet, tomcat)
    - 독립적으로 실행 가능한 JAR (pom.xml의 플러그인)

- Java 11 버전

- 의존성 설정 (Maven : [pom.xml](pom.xml))
    - Web
    - JPA
    - HATEOAS
    - REST Docs
    - H2
    - PostgreSQL
    - Lombok
    - test (junit)

- 추가 기능에 대한 의존성 추가
    - modelmapper
    - spring-security-oauth2-autoconfigure
    - spring-boot-configuration-processor
    - spring-security-test
    
### EVENT API 구현
-> 비즈니스 로직 구현

#### 생성 API 구현
- 이벤트 Entity 설정
    ```
    package net.gentledot.demorestapi.events;
    
    import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
    @EqualsAndHashCode(of = "id")
    @Entity // JPA 설정
    @DynamicUpdate
    public class Event {
    
        @Id
        @GeneratedValue     // JPA에서 자동 생성
        private Integer id;
        private String name;    // 이벤트 이름
        private String description;     // 이벤트 설명
        // JPA 3.2 ~ LocalDateTime 매핑 지원
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
        // enum은 default가 EnumType.ORDINAL (순서에 따라 0, 1, n)
        @Enumerated(EnumType.STRING)
        private EventStatus eventStatus = EventStatus.DRAFT;
    ```
    
    - basePrice와 maxPrice 경우의 수와 각각의 로직
    
    | basePrice | maxPrice | 경우의 수 |
    | :---: | :---: | --- |
    | 0 | 100 | 선착순 등록
    | 0 | 0 | 무료
    | 100 | 0 | 무제한 경매(높은 금액을 낸 사람이 등록)
    | 100 | 200 | 제한가 선착순 등록, 더 많이 낸 사림이 우선순위 높음 |
    
    - 이벤트 상태
        - 등록 대기
        - 이벤트 개설
        - 인원 모집 시작
        - 인원 모집 종료
        - 이벤트 시작
        - 이벤트 종료
        
    ```
    package net.gentledot.demorestapi.events;
    
    public enum EventStatus {
        DRAFT, PUBLISHED, BEGAN_ENROLLMENT, CLOSED_ENROLLMENT, STARTED, ENDED
    }
    ```
    
    - 장소가 없으면 온라인 모임

- Lombok Annotation (*** 정리 필요)
```
@Builder(builderMethodName = "eventBuilder") // 빌더패턴으로 객체 생성
    @AllArgsConstructor // 생성자(모든 변수를 파라미터로)
    @NoArgsConstructor // 생성자(파라미터 없음)
    @Getter // (getXXX 생성)
    @Setter // (setXXX 생성)
    @EqualsAndHashCode(of = "id")
    @Entity // JPA 설정
    @DynamicUpdate
    public class Event {
```
   
#### Controller 생성 및 테스트(ControllerTest) 개요

- Spring Boot Slice Test
    - @WebMvcTest
        - MockMvc 빈을 자동 설정 해준다. 따라서 그냥 가져와서 쓰면 됨.
        - 웹 관련 빈만 등록해 준다. (슬라이스)
    
    - MockMvc
        - 웹서버를 띄우지 않고도 Spring MVC (DispatcherServlet)가 요청을 처리하는 과정을 설정할 수 있기 때문에 Controller 테스트용으로 자주 쓰임.
    
- 테스트 할 내용
    1. 입력값들을 전달하면 JSON 응답으로 201 (Status OK) 확인
        - Location Header에 생성된 이벤트를 조회할 수 있는 URI 확인
        - ID는 DB에 들어갈 때 자동생성된 값으로 나오는지 확인
        
    1. 입력 값으로 id, eventStatus, offline, free 등의 데이터를 전달하는 경우
        1. Bad_Request로 응답하는 방법
        1. 받기로 한 값 이외에는 무시하는 방법
        
        - a 방법 사용. Event(모델)과는 다른 객체 EventDto 생성(프레임워크 내에서 전달)
    
    1. 입력 데이터가 이상한 경우 Bad_Request로 응답
        - 입력값이 이상한 경우 에러
        - 비즈니스 로직으로 검사할 수 있는 에러
            - 전달받은 일자들에 오류가 있는 경우
            - basePrice 보다 maxPrice가 더 적은 경우
            - ...
        - 에러 응답 메시지에 에러에 대한 정보 필요
    
    1. 비즈니스 로직이 적용되었는지 응답 메시지 확인 (출력해서 확인) 
        - 장소 유무에 따른 offline 설정
        - 가격 유무에 따른 유무료 설정
    
    1. 응답에 HATEOAS와 link가 있는지 확인
        - self (view 주소)
        - update (event manager는 수정 가능)
        - events (event 목록)
    
    1. Spring Docs로 API 문서 만들기
        - request 문서화
        - response 문서화
        - link 문서화
        - profile link 추가 (API 상세)


#### API 구현 : 이벤트 생성 요청 -> 201 응답 받기

- @RestController
    - class 내 모든 method에 @ResponseBody를 적용한 것과 동일함.
    - @Controller vs @RestController
        - Http Response Body가 생성되는 방식이 다름
        > [@Controller vs @RestController](https://doublesprogramming.tistory.com/105)
    
- String이 아닌 ResponseEntity 사용
    - String : View 사용
    - ResponseEntity : Data + HTTP Status Code
    - 응답코드, header, body 모두 다루기 편한 API
    - 별도의 View를 제공하지 않는 형태로 서비스를 실행하므로 때로는 결과데이터가 예외적인 상황에서 문제가 발생할 수 있음
    
    ```
    @PostMapping
    public ResponseEntity createEvent(@RequestBody EventDto eventDto,
                                          Errors errors) {
    ```

- 전달받는 객체와 domain 객체 분리
    - 전달받는 event 객체는 EventDto
    - Entity(domain)는 Event
    - modelMapper : 두 객체를 결합하여 하나의 객체로 생성시킴
    
    ```
    // 전달받은 eventDto를 Event 객체에 결합시킴.
    Event event = this.modelMapper.map(eventDto, Event.class);
    ```

- Location URI 만들기
    - HATEOAS가 제공하는 linkTo(), methodOn() 사용 (WebMvcLinkBuilder)
        - linkTo(Class) : 지정한 Class에 Mapping된 URL을 가져옴.
        - methodOn(Class.method()) : 지정한 Class 내 Method에 Mapping된 URL을 가져옴.
          
    ```
    WebMvcLinkBuilder eventLinkUrl = linkTo(EventController.class);
    WebMvcLinkBuilder selfLinkUrl = linkTo(EventController.class).slash(newEvent.getId());
    URI createUri = selfLinkUrl.toUri();

    EventEntityModel eventEntityModel = new EventEntityModel(event);
    eventEntityModel.add(selfLinkUrl.withRel("update-event"));
    eventEntityModel.add(eventLinkUrl.withRel("query-events"));
    eventEntityModel.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
    ```
    
- 객체를 JSON으로 변환
    - ObjectMapper 사용
    
```
@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventService eventService;

    public EventController(ModelMapper modelMapper, EventValidator eventValidator, EventService eventService) {
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
        this.eventService = eventService;
    }
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) {
        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        // ModelMapper를 통해 Entity로 변환하는 과정을 생략할 수 있음.
        /*
        Event event = Event.eventBuilder()
                .name(eventDto.getName())
                .description(eventDto.getDescription())
                .build();
       */
        Event event = this.modelMapper.map(eventDto, Event.class);
        event = eventService.updateEventEntities(event);
        event.setManager(currentUser);
        Event newEvent = eventService.createNewEvent(event);

        WebMvcLinkBuilder eventLinkUrl = linkTo(EventController.class);
        WebMvcLinkBuilder selfLinkUrl = linkTo(EventController.class).slash(newEvent.getId());
        URI createUri = selfLinkUrl.toUri();

        EventEntityModel eventEntityModel = new EventEntityModel(event);
        eventEntityModel.add(selfLinkUrl.withRel("update-event"));
        eventEntityModel.add(eventLinkUrl.withRel("query-events"));
        eventEntityModel.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createUri).body(eventEntityModel);
    }
}
```

- 테스트 내용
    - 입력값들을 전달하면 JSON 응답으로 status code 201이 나오는지 확인
        - Location Header에 생성된 event를 조회할 수 있는 URI가 담겨 있는지 확인.
        - id는 DB에 저장될 때 자동생성된 값으로 나오는지 확인
    
    - Json 객체 내 해당하는 jsonPath가 있는지 확인 
        - org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
         
    - MediaTypes 내 UTF-8이 명시된 객체는 deprecated.
        - MediaTypes.HAL_JSON_VALUE = "application/hal+json"
        - 따라서 UTF-8 설정하는 @Bean을 추가하여 진행하였음.
          
            ```
            // Charset = UTF-8 설정 (WebMvcTest)
            // The bean 'characterEncodingFilter', defined in class path resource [org/springframework/boot/autoconfigure/web/servlet/HttpEncodingAutoConfiguration.class], could not be registered. A bean with that name has already been defined in net.gentledot.demorestapi.DemoRestApiApplication and overriding is disabled.
            // Consider renaming one of the beans or enabling overriding by setting spring.main.allow-bean-definition-overriding=true
            // ****** Spring boot 2.1 이후부터는 bean definition overriding이 false ******
            // spring.main.allow-bean-definition-overriding=true 임에도 해당 bean 설정이 MockHttpServletResponse에 반영되지 않음.
            // 해당 옵션을 주어 charset 설정은 bean에서 설정하도록 하면 통과는 되지만....
            // @SpringBootApplication(exclude = HttpEncodingAutoConfiguration.class)
            // application.properties 상에서 설정으로 해결하였음.
            /*
            @Bean
            public Filter characterEncodingFilter() {
                CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
                characterEncodingFilter.setEncoding("UTF-8");
                characterEncodingFilter.setForceEncoding(true);
        
                return characterEncodingFilter;
            }
            */
            ```
        
        ```
        @Test
        @TestDescription("정상적인 이벤트를 생성하는 테스트 (201 응답)")
        public void createEvent() throws Exception{
            ...
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
        //                .andExpect(header().string("Content-Type", "application/hal+json;charset=UTF-8"))
                        .andExpect(header().exists(HttpHeaders.LOCATION))
                        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_HAL_JSON))
                        .andExpect(jsonPath("id").value(Matchers.not(100)))
                        .andExpect(jsonPath("free").value(false))
                        .andExpect(jsonPath("offline").value(true))
                        .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        } 
        ```
      
#### API 구현 : EventRepository 구현
- Spring Data JPA
    - JpaRepository를 상속받은 interface를 만들기

- Enum을 JPA Mapping시 주의
    - @Enumerated(EnumType.STRING)
    - enum은 default가 EnumType.ORDINAL (순서에 따라 0, 1, ..., n)

- @MockBean
    - Mockito를 사용해서 mock 객체를 만들고 Bean으로 등록해줌. (Stubbing)
    - 주의) 기존 Bean을 테스트용 Bean이 대체
    
    ```
    // @WebMvcTest는 MVC 관련 Bean을 테스트배드에 올리는 것이므로 Transaction 처리 결과는 모두 Null
    // 따라서 Transaction에 대한 처리 방식이 어떻게 되는지 구현 필요 (Stubbing)
    // Bean 전체를 테스트배드에 올리는 @SpringBootTest 를 설정하면 Stubbing이 불필요 (통합 테스트)
    //    @MockBean
    //    EventRepository eventRepository;
  
    // @WebMvcTest 에서 JPA 테스트가 없으므로 save에 대한 Mocking이 필요
    // event는 eventBuilder로 생성된 객체가 아니고 ModelMapper를 통해 생성된 객체
    // Mocking이 성립되지 않으므로 Null을 반환하게 된다.
    //    when(eventRepository.save(event)).thenReturn(event);
    ```

- 테스트 내용
    - 입력값을 전달하면 JSON 응답으로 201(Status OK)이 나오는지 확인
        - Location Header내 URI 확인.
        - id가 Auto Generated 확인.
        
#### API 구현 : 입력값 제한하기
- 입력값 제한
    - id 또는 입력 받은 데이터로 계산해야 하는 값들은 입력을 받지 않아야 한다.
    - EventDto 적용
    
- Domain 객체로 값을 복사하기 위해 ModelMapper 사용
    
    ```
    // pom.xml
    <dependency>
        <groupId>org.modelmapper</groupId>
        <artifactId>modelmapper</artifactId>
        <version>2.3.5</version>
    </dependency>
    ```
  
- MvcTest에서 통합 Test로 전환
    - @WebMvcTest 빼고 다음 애노테이션 추가
        - @SpringBootTest
        - @AutoConfigureMockMvc
    - Repository @MockBean 코드 제거
    
- ObjectMapper 커스터마이징
    - spring.jackson.deserialization.fail-on-unknown-properties=true
    - jackson Library
        > [Java Json library jackson 사용법](https://www.lesstif.com/pages/viewpage.action?pageId=24445183)
        [Java Config로 Jackson 설정하기](https://zepinos.tistory.com/32)

- 테스트 내용
    - 입력값으로 누가 id나 eventStatus, offline, free 이런 데이터까지 같이 주면?
        - BadRequest로 응답.
    
    ```
    @Test
    @TestDescription("입력 받기로 한 값 이외의 값이 들어올 때 에러가 발생하는 테스트 (400 error)")
    public void createEventWithBadRequestInputNotDtoData() throws Exception {}
    ```
