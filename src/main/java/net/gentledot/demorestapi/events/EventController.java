package net.gentledot.demorestapi.events;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.ControllerLinkBuilder.methodOn;

@Controller
public class EventController {
    public ResponseEntity createEvent(){
        URI createUri = linkTo(methodOn(EventController.class).createEvent()).slash("{id}").toUri();
        return ResponseEntity.created(createUri).build();
    }
}
