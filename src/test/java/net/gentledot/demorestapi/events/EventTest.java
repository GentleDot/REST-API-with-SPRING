package net.gentledot.demorestapi.events;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EventTest {

    @Test
    public void builder() {
        Event event = Event.eventBuilder()
                .name("my Spring REST API")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        // Given
        String name = "Event";
        String description = "Spring";

        // When
        Event event = new Event();
        event.setName("Event");
        event.setDescription(description);

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }

    @Test
    public void testSetFree() {
        // given
        Event event = Event.eventBuilder()
                .basePrice(0)
                .maxPrice(0)
                .build();

        // when
        event.updateFree();

        // then
        assertThat(event.isFree()).isTrue();
    }

    @Test
    public void testSetNotFree() {
        // given
        Event event = Event.eventBuilder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        // when
        event.updateFree();

        // then
        assertThat(event.isFree()).isFalse();

        // given
        event = Event.eventBuilder()
                .basePrice(0)
                .maxPrice(100)
                .build();

        // when
        event.updateFree();

        // then
        assertThat(event.isFree()).isFalse();
    }


    @Test
    public void testSetOffline() {
        // given
        Event event = Event.eventBuilder()
                .location("location Test")
                .build();

        // when
        event.updateOffline();

        // then
        assertThat(event.isOffline()).isTrue();
    }


    @Test
    public void testSetNotOffline() {
        // given
        Event event = Event.eventBuilder()
                .build();

        // when
        event.updateOffline();

        // then
        assertThat(event.isOffline()).isFalse();
    }

}