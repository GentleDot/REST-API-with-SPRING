package net.gentledot.demorestapi.events;


import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
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
    @Parameters
    public void testSetFree(int basePrice, int maxPrice, boolean isFree) {
        // given
        Event event = Event.eventBuilder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        // when
        event.updateFree();

        // then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private Object[] parametersForTestSetFree() {
        return new Object[]{
                new Object[]{0, 0, true},
                new Object[]{100, 0, false},
                new Object[]{0, 100, false},
                new Object[]{100, 200, false}
        };
    }

    @Test
    @Parameters(method = "paramsForTestSetOffline")
    public void testSetOffline(String location, boolean isOffline) {
        // given
        Event event = Event.eventBuilder()
                .location(location)
                .build();

        // when
        event.updateOffline();

        // then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    private Object[] paramsForTestSetOffline() {
        return new Object[]{
                new Object[]{"location Test", true},
                new Object[]{"강남역 2번출구 앞 강남빌딩", true},
                new Object[]{"   ", false},
                new Object[]{null, false},
        };
    }

}