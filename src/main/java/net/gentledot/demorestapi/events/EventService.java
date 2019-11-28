package net.gentledot.demorestapi.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    Event setNewEvent(Event event);
    Event updateEvent(Event event);
    Page<Event> getEventsWithPaging(Pageable pageable);
}
