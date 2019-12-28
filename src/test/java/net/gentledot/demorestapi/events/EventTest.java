package net.gentledot.demorestapi.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
public class EventTest {

    @Test
    @DisplayName("Event entity를 builder로 생성.")
    public void builder() {
        Event event = Event.eventBuilder()
                .name("my Spring REST API")
                .description("REST API development with Spring")
                .build();
        assertThat(event).isNotNull();
    }

    @Test
    @DisplayName("@Getter, @Setter annotation test (Event entity가 lombok annotation으로 만들었기 때문에)")
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

    @ParameterizedTest(name = "[{index}] {displayName} - basePrice = {0}, maxPrice = {1}, isFree = {2}")
    @DisplayName("Event 참가비 테스트")
    @MethodSource("parametersForTestSetFree")
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

    private static Object[] parametersForTestSetFree() {
        return new Object[]{
                new Object[]{0, 0, true},
                new Object[]{100, 0, false},
                new Object[]{0, 100, false},
                new Object[]{100, 200, false}
        };
    }

    @ParameterizedTest(name = "[{index}] {displayName} - location = {0}, isOffline = {1}")
    @DisplayName("Event 장소 설정 테스트")
    @MethodSource("paramsForTestSetOffline")
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

    private static Object[] paramsForTestSetOffline() {
        return new Object[]{
                new Object[]{"location Test", true},
                new Object[]{"강남역 2번출구 앞 강남빌딩", true},
                new Object[]{"   ", false},
                new Object[]{null, false},
        };
    }

}