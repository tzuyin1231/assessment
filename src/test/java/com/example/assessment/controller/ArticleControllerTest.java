package com.example.assessment.controller;

import com.example.assessment.repository.ArticleRepository;
import com.example.assessment.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.val;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
public class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebTestClient webTestClient;

    private static final Logger log = LoggerFactory.getLogger(ArticleControllerTest.class);
    private static final String GRAPHQL_PATH = "/graphql";

    public String getToken() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/token/long")).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();
        log.info("token value should be {}", mvcResultString);
        return mvcResultString;
    }

    private static String toJSON(String query) {
        try{
            log.info(query);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("query", query);
            return jsonObject.toString();
        }catch(Exception e){
            log.info(String.valueOf(e));
            return null;
        }
    }

    @Test
    public void testAddNewArticle() throws Exception {
//        參數宣告
//        String articleTitle = "日常生活範式的轉變：從紙筆到 AI";
//        String articleContent = "技術的進步是基於讓它適應你，因此你可能根本不會真正注意到它，所以它是日常生活的一部分。\n" +
//                "——比爾．蓋茨（微軟公司創辦人之一）";
//        String requestBody = String.format("mutation{\n" +
//                "addNewArticle(userId:1,articleTitle:%s,articleContent:%s){\n" +
//                "articleTitle,articleContent,user{nickname}\n" +
//                "}\n" +
//                "}", articleTitle, articleContent);
    }

    @Test
    @Transactional
    public void testFindAllArticlesController() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/graphql")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken())
                        .content("{\"query\":\"{ findAllArticles { articleId articleTitle } }\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        log.info("{}", mvcResult.getResponse().getContentAsString());
    }



    @Test
    public void whenFindAllArticles() throws Exception {
        String findAllArticlesQuery = "{ findAllArticles { articleId articleTitle } }";
        val result = webTestClient.post().uri(GRAPHQL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h-> {
                    try {
                        h.setBearerAuth(getToken());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .body(Mono.just(toJSON(findAllArticlesQuery)),String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.findAllArticles").isNotEmpty()
                .returnResult().getResponseBodyContent();
        log.info("whenFindAllArticles : "+String.valueOf(result));
    }

    @Test
    public void whenFindArticleByArticleId() throws Exception {
        String findAllArticlesQuery = "{ findArticleByArticleId(articleId:3) { articleId articleTitle } }";
        val result = webTestClient.post().uri(GRAPHQL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h-> {
                    try {
                        h.setBearerAuth(getToken());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .body(Mono.just(toJSON(findAllArticlesQuery)),String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.findArticleByArticleId.articleTitle").isEqualTo("dwevvdweerwe")
                .returnResult().getResponseBodyContent();
        log.info(String.valueOf(result));
    }

    @Test
    @Transactional
    public void testFindAllArticlesControllerSpringDoc() throws Exception {
//        WebApplicationContext context = "{\"query\":\"{ findAllArticles { articleId articleTitle } }\"}" ;

//        WebTestClient client =
//                MockMvcWebTestClient.bindToApplicationContext(context)
//                        .configureClient()
//                        .baseUrl("/graphql")
//                        .build();
//
//        WebGraphQlTester tester = WebGraphQlTester.builder(client).build();
    }

    @Test
    public void testDeleteArticle() throws Exception {
//        String query = new StringBuilder().append("query findAllArticles {")
//                .append(" articleId articleTitle }").toString();
//        JSONObject variables = new JSONObject();
//        variables.put("id", 1);
//        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/graphql")
//                        .header("Authorization", "Bearer " + getToken())
////                        .content("{\"query\":\"mutation { deleteArticle ( articleId: 3 })  }\"}")
//                        .content(generateRequest(query, variables))
//                        .contentType(MediaType.APPLICATION_JSON))
////                        .accept(MediaType.APPLICATION_JSON)
//                .andExpect(request().asyncStarted())
//                .andExpect(request().asyncResult(notNullValue()))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andReturn();
//        log.info(mvcResult.getResponse().getContentAsString());
    }


}

