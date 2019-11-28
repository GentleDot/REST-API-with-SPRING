package net.gentledot.demorestapi.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service(value = "EventService")
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event setNewEvent(Event event) {
        return this.eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Event event) {
        event.updateFree();
        event.updateOffline();
        return event;
    }

    @Override
    public Page<Event> getEventsWithPaging(Pageable pageable) {
        return this.eventRepository.findAll(pageable);
    }
}
