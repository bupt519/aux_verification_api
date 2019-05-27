package cn.edu.bupt.controller;

import cn.edu.bupt.bean.po.User;
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
public class OauthControllerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;
    private MockHttpSession session;
    private ObjectMapper objectMapper;

    @Before
    public void setupMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        session = new MockHttpSession();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testOauthError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/oauth/error/203")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(203))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Non-Authoritative Information"))
                .andDo(MockMvcResultHandlers.print());

        mvc.perform(MockMvcRequestBuilders.get("/api/oauth/error/404")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(500))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Internal Server Error"))
                .andDo(MockMvcResultHandlers.print());

        mvc.perform(MockMvcRequestBuilders.get("/api/user/list/entities")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("/api/oauth/error/*"));
    }

    @Test
    public void testSubmitRegister() throws Exception {
        String noEmail = "{\"password\":\"123\"}";
        String noPsd = "{\"email\":\"123@gmail.com\"}";
        String emptyEmail = "{\"email\":\"\",\"password\":\"123\"}";
        String emptyPsd = "{\"email\":\"123@gmail.com\",\"password\":\"\"}";
        String normal = "{\"email\":\"123@gmail.com\",\"password\":\"123\"}";
        mvc.perform(MockMvcRequestBuilders.post("/api/oauth/register")
                .content(noEmail)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("注册信息不完整"));

        mvc.perform(MockMvcRequestBuilders.post("/api/oauth/register")
                .content(noPsd)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("注册信息不完整"));

        mvc.perform(MockMvcRequestBuilders.post("/api/oauth/register")
                .content(emptyEmail)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("注册信息不完整"));

        mvc.perform(MockMvcRequestBuilders.post("/api/oauth/register")
                .content(emptyPsd)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("注册信息不完整"));

        mvc.perform(MockMvcRequestBuilders.post("/api/oauth/register")
                .content(normal)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("注册成功"));

        mvc.perform(MockMvcRequestBuilders.post("/api/oauth/register")
                .content(normal)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(500))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("邮箱已被注册"));
    }

    @Test
    public void testSubmitLogin() throws Exception {
        String noEmail = "{\"password\":\"123\"}";
        String noPsd = "{\"email\":\"123@gmail.com\"}";
        String emptyEmail = "{\"email\":\"\",\"password\":\"123\"}";
        String emptyPsd = "{\"email\":\"123@gmail.com\",\"password\":\"\"}";
        String success = "{\"email\":\"123@gmail.com\",\"password\":\"123\"}";
        String fail = "{\"email\":\"123@gmail.com\",\"password\":\"124\"}";

        mvc.perform(MockMvcRequestBuilders.post("/api/oauth/login")
                .content(noEmail)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("登录信息不完整"));

        mvc.perform(MockMvcRequestBuilders.post("/api/oauth/login")
                .content(noPsd)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("登录信息不完整"));

        mvc.perform(MockMvcRequestBuilders.post("/api/oauth/login")
                .content(emptyEmail)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("登录信息不完整"));

        mvc.perform(MockMvcRequestBuilders.post("/api/oauth/login")
                .content(emptyPsd)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("登录信息不完整"));

        mvc.perform(MockMvcRequestBuilders.post("/api/oauth/login")
                .content(success)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("登录成功"))
                .andDo(MockMvcResultHandlers.print());

        mvc.perform(MockMvcRequestBuilders.post("/api/oauth/login")
                .content(fail)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(500))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("用户名或密码错误"));
    }

    @Test
    public void generateJson() throws Exception {
        User user = new User();
        user.setEmail("123@gmail.com");
        user.setPassword("123");
        log.info("{}", objectMapper.writeValueAsString(user));
    }
}
