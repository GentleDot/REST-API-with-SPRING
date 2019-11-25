package net.gentledot.demorestapi.events;

import org.springframework.stereotype.Service;

@Service(value = "EventService")
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event setNewEvent(Event event) {
        Event newEvent = this.eventRepository.save(event);
        return newEvent;
    }

    @Override
    public Event updateEvent(Event event) {
        event.updateFree();
        event.updateOffline();
        return event;
    }
}
