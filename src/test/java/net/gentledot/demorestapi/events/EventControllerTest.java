package net.gentledot.demorestapi.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    /* 입력 값을 전달 시 JSON 응답으로 status=201 나오는지 확인 */
    @Test
    public void createEvent() throws Exception {
        mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
//                .content()
        )   // 요청
                .andDo(print())
                .andExpect(status().isCreated());
    }
}
