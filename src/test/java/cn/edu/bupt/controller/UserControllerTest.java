package cn.edu.bupt.controller;

import cn.edu.bupt.constant.OauthConsts;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class UserControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;
    private MockHttpSession session;
    private ObjectMapper objectMapper;
    // 调试时通过登录接口重新获取
    private String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIzIiwiaWF0IjoxNTU4OTUzOTQ3LCJzdWIiOiIzLzEyM0BnbWFpbC5jb20iLCJpc3MiOiJhdXhfdmVyaWZpY2F0aW9uIiwiZXhwIjoxNTU4OTU5OTQ3fQ.-hKzzJhXVrq_qnEZ_ViWYqY3LvKhgoFMjXk_Im8cMjY";

    @Before
    public void setupMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        session = new MockHttpSession();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testReviewedEntities() throws Exception {
        String passedStr = "{\"passed\":true}";
        String deniedStr = "{\"passed\":false}";
        mvc.perform(MockMvcRequestBuilders.get("/api/user/entities")
                .content(passedStr)
                .header(OauthConsts.KEY_ACCESS_TOKEN, token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        mvc.perform(MockMvcRequestBuilders.get("/api/user/entities")
                .content(deniedStr)
                .header(OauthConsts.KEY_ACCESS_TOKEN, token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testReviewedRelations() throws Exception {
        String passedStr = "{\"passed\":true}";
        String deniedStr = "{\"passed\":false}";
        mvc.perform(MockMvcRequestBuilders.get("/api/user/relations")
                .content(passedStr)
                .header(OauthConsts.KEY_ACCESS_TOKEN, token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        mvc.perform(MockMvcRequestBuilders.get("/api/user/relations")
                .content(deniedStr)
                .header(OauthConsts.KEY_ACCESS_TOKEN, token)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

}
