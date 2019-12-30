package net.gentledot.demorestapi.events;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.gentledot.demorestapi.accounts.Account;
import net.gentledot.demorestapi.accounts.CurrentUser;
import net.gentledot.demorestapi.common.ErrorEntityModel;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

// import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo << deprecated

@Tag(name = "이벤트", description = "이벤트 API")
@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final EventService eventService;

    // Spring 4.2 ~ 파라미터가 하나만 있는 상태는 autowired 생략 가능)
//    @Autowired
    public EventController(ModelMapper modelMapper, EventValidator eventValidator, EventService eventService) {
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
        this.eventService = eventService;
    }

    @Operation(summary = "이벤트 생성", description = "이 기능은 로그인하여 권한이 주어져야 작동합니다.")
    @ApiResponses(value = {@ApiResponse(description = "성공적인 동작", content = {@Content(mediaType = "application/hal+json;charset=UTF-8", schema = @Schema(implementation = Event.class))})})
    @PostMapping
    public ResponseEntity createEvent(@Parameter(description = "이벤트 객체 생성") @RequestBody @Valid EventDto eventDto,
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

    @Operation(summary = "이벤트 목록 확인", description = "생성된 이벤트 목록을 paging 하여 출력합니다.")
    @GetMapping
    public ResponseEntity getEvents(@Parameter(hidden = true) Pageable pageable, @Parameter(hidden = true) PagedResourcesAssembler<Event> resourcesAssembler
            , @Parameter(hidden = true) @CurrentUser Account currentUser) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User principal = (org.springframework.security.core.userdetails.User)authentication.getPrincipal();

        Page<Event> page = this.eventService.getEventsWithPaging(pageable);

        // Resource -> Model
        PagedModel<EntityModel<Event>> entityModels = resourcesAssembler.toModel(page, entity -> new EventEntityModel(entity));
        entityModels.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        if (currentUser != null) {
            entityModels.add(linkTo(EventController.class).withRel("create-event"));
        }
        return ResponseEntity.ok(entityModels);
    }

    @Operation(summary = "이벤트 조회", description = "/{id}의 Event를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity getEvent(@Parameter(required = true) @PathVariable Integer id,
                                   @Parameter(hidden = true) @CurrentUser Account currentUser) {
        Optional<Event> optionalEvent = this.eventService.getEvent(id);

        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventEntityModel eventEntityModel = new EventEntityModel(event);
        eventEntityModel.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        if (event.getManager().equals(currentUser)) {
            eventEntityModel.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }

        return ResponseEntity.ok(eventEntityModel);
    }

    @Operation(summary = "이벤트 수정", description = "/{id}의 Event를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@Parameter(required = true) @PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      @Parameter(hidden = true) Errors errors,
                                      @Parameter(hidden = true) @CurrentUser Account currentUser) {
        Optional<Event> optionalEvent = this.eventService.getEvent(id);

        if (optionalEvent.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else if (errors.hasErrors()) {
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);

        if (errors.hasErrors()) {
            return badRequest(errors);
        }

        Event targetEvent = optionalEvent.get();
        /* 강의와 다른 부분은 주석 처리 : 20191130_gentledot */
//        Event eventData = this.modelMapper.map(eventDto, Event.class);
//        eventData = eventService.updateEventEntities(eventData);

        if (currentUser == null) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        } else if (!targetEvent.getManager().equals(currentUser)) {
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        }

        this.modelMapper.map(eventDto, targetEvent);
        Event eventData = eventService.updateEventEntities(targetEvent);

        /* 강의와 다른 부분은 주석 처리 : 20191130_gentledot */
//        Event updatedEvent = eventService.updateEvent_Deprecated(targetEvent, eventData);
        Event updatedEvent = eventService.updateEvent(targetEvent);

        EventEntityModel eventEntityModel = new EventEntityModel(updatedEvent);
        eventEntityModel.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventEntityModel);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorEntityModel(errors));
    }
}
