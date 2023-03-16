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
        return mvcResultString;
    }

    private static String toJSON(String query) {
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("query", query);
            return jsonObject.toString();
        }catch(Exception e){
            return null;
        }
    }

    @Test
    @Transactional
    public void testAddNewArticle() throws Exception {

        String articleTitle = "日常生活範式的轉變：從紙筆到 AI";
        String articleContent = "技術的進步是基於讓它適應你，因此你可能根本不會真正注意到它，所以它是日常生活的一部分。" +
                "——比爾．蓋茨（微軟公司創辦人之一）";
        String addNewArticleMutation = String.format("mutation{" +
                "addNewArticle(userId:1,articleTitle:\"%s\",articleContent:\"%s\"){" +
                "articleTitle,articleContent,user{nickname} }  " +
                "}", articleTitle, articleContent);

        val result = webTestClient.post().uri(GRAPHQL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h-> {
                    try {
                        h.setBearerAuth(getToken());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .body(Mono.just(toJSON(addNewArticleMutation)),String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.addNewArticle").isNotEmpty()
                .jsonPath("$.data.addNewArticle.articleTitle").isEqualTo(articleTitle)
                .jsonPath("$.data.addNewArticle.articleContent").isEqualTo(articleContent)
                .returnResult();
    }

    @Test
    @Transactional
    public void testDeleteArticle() throws Exception {
        String deleteArticleMutation = "mutation { deleteArticle ( articleId: 3 )  }";
        val result = webTestClient.post().uri(GRAPHQL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h-> {
                    try {
                        h.setBearerAuth(getToken());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .body(Mono.just(toJSON(deleteArticleMutation)),String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.deleteArticle").isNotEmpty()
                .jsonPath("$.data.deleteArticle").isEqualTo(true)
                .returnResult();
        log.info(String.valueOf(result));
    }

    @Test
    public void testUpdateArticle() throws Exception {

        String articleTitle = "人工智慧崛起，人類從此俯首稱臣？《電腦簡史》 楔子";
        String articleContent = "曾經，電腦只是個計算工具，雖然計算能力遠勝過人類，卻缺乏人類的智慧。" +
                "但近來人工智慧崛起，在各種不同領域的表現已超越人類，以致於物理大師霍金與企業怪傑伊隆·馬斯克都憂心人類未來會受到威脅。" +
                "電影《魔鬼終結者》中的「天網」有一天會成真嗎？電腦究竟如何從簡單的計算機，一步步演進為人工智慧，超越自詡為「萬物之靈」的人類？" +
                "《電腦簡史：從齒輪到 AI》這本書將從齒輪時代、電腦時代、網路時代、AI時代，依序回顧電腦的演進。";

        String updateArticle = String.format("mutation{" +
                "updateArticle(articleId:1,articleTitle:\"%s\",articleContent:\"%s\"){" +
                "articleTitle,articleContent,user{nickname} }  " +
                "}", articleTitle, articleContent);
        log.info(updateArticle);

        val result = webTestClient.post().uri(GRAPHQL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h-> {
                    try {
                        h.setBearerAuth(getToken());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .body(Mono.just(toJSON(updateArticle)),String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.updateArticle").isNotEmpty()
                .jsonPath("$.data.updateArticle.articleTitle").isEqualTo(articleTitle)
                .jsonPath("$.data.updateArticle.articleContent").isEqualTo(articleContent)
                .returnResult();
        log.info(String.valueOf(result));
    }

    @Test
    public void testFindAllArticles() throws Exception {
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
                .returnResult();
        log.info("testFindAllArticles : "+ result);
    }

    @Test
    public void testFindArticleByArticleId() throws Exception {
        String findArticleByArticleIdQuery = "{ findArticleByArticleId(articleId:3) { articleId articleTitle } }";
        val result = webTestClient.post().uri(GRAPHQL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h-> {
                    try {
                        h.setBearerAuth(getToken());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .body(Mono.just(toJSON(findArticleByArticleIdQuery)),String.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.findArticleByArticleId.articleTitle").isEqualTo("dwevvdweerwe")
                .returnResult();
        log.info("testFindArticleByArticleId : "+String.valueOf(result));
    }



}

