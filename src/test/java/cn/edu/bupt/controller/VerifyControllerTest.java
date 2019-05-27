package cn.edu.bupt.controller;

import cn.edu.bupt.bean.jo.EntityParam;
import cn.edu.bupt.bean.jo.RelationParam;
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
public class VerifyControllerTest {

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
    public void testNextVerData() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/api/ver/next")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .header(OauthConsts.KEY_ACCESS_TOKEN, token)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testDealEntity() throws Exception {
        String passedStr = "{\"id\":1,\"content\":\"此外，<e1>公司</e1>遵循“生产一代、研发一代、储备一代”的持续发展目标，不断拓展<e2>语音技术应用领域</e2>，前瞻性、针对性地进行研发\",\"passed\":true,\"statId\":1}";
        String deniedStr = "{\"id\":4,\"content\":\"12345\",\"passed\":false,\"statId\":1}";
        mvc.perform(MockMvcRequestBuilders.put("/api/ver/entity")
                .header(OauthConsts.KEY_ACCESS_TOKEN, token)
                .content(passedStr)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("审批成功"))
                .andDo(MockMvcResultHandlers.print());

        mvc.perform(MockMvcRequestBuilders.put("/api/ver/entity")
                .header(OauthConsts.KEY_ACCESS_TOKEN, token)
                .content(deniedStr)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("审批成功"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testDealRelation() throws Exception{
        String passedStr = "{\"id\":1,\"content\":\"此外，<e1>公司</e1>遵循“生产一代、研发一代、储备一代”的持续发展目标，不断拓展<e2>语音技术应用领域</e2>，前瞻性、针对性地进行研发\",\"passed\":true,\"relationId\":10,\"statId\":1}";
        String deniedStr = "{\"id\":4,\"content\":\"今天天气不错\",\"passed\":false,\"relationId\":19,\"statId\":1}";
        mvc.perform(MockMvcRequestBuilders.put("/api/ver/relation")
                .header(OauthConsts.KEY_ACCESS_TOKEN, token)
                .content(passedStr)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("审批成功"))
                .andDo(MockMvcResultHandlers.print());

        mvc.perform(MockMvcRequestBuilders.put("/api/ver/relation")
                .header(OauthConsts.KEY_ACCESS_TOKEN, token)
                .content(deniedStr)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("审批成功"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void generateJson() throws Exception {
        EntityParam param = new EntityParam();
        param.setId(4);
        param.setContent("12345");
        param.setPassed(false);
        param.setStatId(1);
        log.info("{}", objectMapper.writeValueAsString(param));

        RelationParam param1=new RelationParam();
        param1.setId(4);
        param1.setContent("今天天气不错");
        param1.setPassed(false);
        param1.setRelationId(19);
        param1.setStatId(1);
        log.info("{}", objectMapper.writeValueAsString(param1));
    }

}
