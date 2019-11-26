package net.gentledot.demorestapi.events;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/*
// ResourceSupport -> RepresentationModel
public class EventResource extends RepresentationModel{

    // event 의 속성으로 event 객체 내용을 감싸 출력되지 않도록 설정
    @JsonUnwrapped
    private Event event;

    public EventResource(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }
}
*/

// Resource -> EntityModel
public class EventResource extends EntityModel<Event> {
    // getContent 에서 @JsonUnwrapped가 미리 적용되어 있음.
    public EventResource(Event event, Link... links) {
        super(event, links);
        WebMvcLinkBuilder eventLink = linkTo(EventController.class);
        add(eventLink.slash(event.getId()).withSelfRel());
        add(eventLink.slash(event.getId()).withRel("update-event"));
        add(eventLink.withRel("query-events"));
    }
}
