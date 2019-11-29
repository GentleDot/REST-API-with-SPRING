package net.gentledot.demorestapi.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service(value = "EventService")
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event createNewEvent(Event event) {
        return this.eventRepository.save(event);
    }

    @Override
    public Event updateEvent(Event targetEvent) {
        return this.eventRepository.save(targetEvent);
    }

    /* 강의와 다른 부분은 주석 처리 : 20191130_gentledot */
    /*
    @Override
    public Event updateEvent_Deprecated(Event targetEvent, Event eventData) {
        eventData.setId(targetEvent.getId());
        return this.eventRepository.save(eventData);
    }
    */

    @Override
    public Optional<Event> getEvent(Integer id) {
        return this.eventRepository.findById(id);
    }

    @Override
    public Page<Event> getEventsWithPaging(Pageable pageable) {
        return this.eventRepository.findAll(pageable);
    }

    @Override
    public Event updateEventEntities(Event event) {
        event.updateFree();
        event.updateOffline();
        return event;
    }
}
