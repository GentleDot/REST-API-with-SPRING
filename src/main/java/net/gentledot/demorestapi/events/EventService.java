package net.gentledot.demorestapi.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EventService {
    Event createNewEvent(Event event);
    Event updateEvent(Event targetEvent);
    /* 강의와 다른 부분은 주석 처리 : 20191130_gentledot */
//    Event updateEvent_Deprecated(Event targetEvent, Event eventData);
    Optional<Event> getEvent(Integer id);
    Page<Event> getEventsWithPaging(Pageable pageable);
    Event updateEventEntities(Event event);
}
