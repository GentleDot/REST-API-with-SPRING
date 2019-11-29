package net.gentledot.demorestapi.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.gentledot.demorestapi.events.EventRepository;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@Ignore // 테스트를 실행하지 않도록 설정
public class BaseControllerTest {

    // @SpringBootTest 에서 MockMvc 클래스를 사용하려면 @AutoConfigureMockMvc를 사용한다.
    // WebEnvironment webEnvironment() default WebEnvironment.MOCK; >> Mocking을 하는 DispatcherServlet를 생성
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;
}
