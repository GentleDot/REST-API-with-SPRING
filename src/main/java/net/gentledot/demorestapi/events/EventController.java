package net.gentledot.demorestapi.events;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;

/* deprecated 이므로 대체 함수를 확인해야 함 : 20191119_gentledot*/

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @Autowired
    EventService eventService;


    @Autowired   // Spring 4.2 ~ 파라미터가 하나만 있는 상태는 autowired 생략 가능)
    public EventController(ModelMapper modelMapper, EventValidator eventValidator) {
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }


    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);

        if (errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        // ModelMapper를 통해 Entity로 변환하는 과정을 생략할 수 있음.
        /*
        Event event = Event.eventBuilder()
                .name(eventDto.getName())
                .description(eventDto.getDescription())
                .build();
       */
        Event event = this.modelMapper.map(eventDto, Event.class);
        event = eventService.updateEvent(event);

        Event newEvent = eventService.setNewEvent(event);
        URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        return ResponseEntity.created(createUri).body(event);
    }
}
