package net.gentledot.demorestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;


import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    /* 입력 값을 전달 시 JSON 응답으로 status=201 나오는지 확인 */
    @Test
    public void createEvent() throws Exception {
        // 요청 입력
        Event event = Event.eventBuilder()
                .name("Spring")
                .description("REST API Dev. with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 11, 19, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 11, 20, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2019, 11, 21, 14, 21))
                .endEventDateTime(LocalDateTime.of(2019, 11, 26, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 5번 출구")
                .build();

        // 요청 동작
        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                // org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
                .andExpect(jsonPath("id").exists());
    }
}
