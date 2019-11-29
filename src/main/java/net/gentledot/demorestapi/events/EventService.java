package net.gentledot.demorestapi.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EventService {
    Event setNewEvent(Event event);
    Event updateEvent(Event event);
    Optional<Event> getEvent(Integer id);
    Page<Event> getEventsWithPaging(Pageable pageable);
}
