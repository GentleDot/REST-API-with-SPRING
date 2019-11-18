package net.gentledot.demorestapi.events;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.methodOn;

@Controller
public class EventController {

    @PostMapping("/api/events")
    public ResponseEntity createEvent() {
        URI createUri = linkTo(methodOn(EventController.class).createEvent()).slash("{id}").toUri();

        return  ResponseEntity.created(createUri).build();
    }
}
