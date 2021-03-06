package nju.edu.cn.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nju.edu.cn.backend.vo.LectureVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Transactional
public class LectureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void createLecture() throws Exception {
        LectureVO lectureVO = LectureVO.builder()
                .title("lecture")
                .start(LocalDateTime.of(2019, 5, 26, 0, 0, 0, 0))
                .validityDays(1)
                .build();
        MvcResult result = mockMvc.perform(post("/lectures")
                .content(mapper.writeValueAsString(lectureVO))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn();

        JSONAssert.assertEquals("{\"title\":\"lecture\",\"start\":\"2019-05-26 00:00\",\"validityDays\":1,\"expire\":\"2019-05-27 00:00\"}",
                result.getResponse().getContentAsString(), false);
    }

    @Test
    public void shouldReturnLectureComment() throws Exception {
        MvcResult result = mockMvc.perform(get("/lectures/{id}", 1))
                .andExpect(status().isOk())
                .andReturn();

        JSONAssert.assertEquals("{\"lecture\":{\"id\":1,\"title\":\"hackathon\",\"speaker\":\"gcm\",\"start\":\"2019-05-26 19:56\",\"validityDays\":365},\"editable\":true,\"lastCommentTime\":\"2019-05-26 19:58:00.200\"}",
                result.getResponse().getContentAsString(), false);
    }

    @Test
    public void shouldReturnLectureEmptyComments() throws Exception {
        MvcResult result = mockMvc.perform(get("/lectures/{id}", 5))
                .andExpect(status().isOk())
                .andReturn();

        JSONAssert.assertEquals("{\"lecture\":{\"id\":5,\"title\":\"test for empty comments\",\"speaker\":\"gcm\",\"start\":\"2019-05-26 12:12\",\"validityDays\":1},\"editable\":false,\"comments\":[],\"lastCommentTime\":\"2019-05-26 12:12:00.000\"}",
                result.getResponse().getContentAsString(), false);
    }


    @Test
    public void shouldNotFoundLecture() throws Exception {
        mockMvc.perform(get("/lectures/{id}", 3))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}