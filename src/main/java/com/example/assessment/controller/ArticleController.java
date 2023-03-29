package com.example.assessment.controller;

import com.example.assessment.configuration.security.ResourceServerConfig;
import com.example.assessment.model.Article;
import com.example.assessment.model.User;
import com.example.assessment.repository.ArticleRepository;
import com.example.assessment.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ArticleController {
    private static final Logger log = LoggerFactory.getLogger(ArticleController.class);
    private ArticleRepository articleRepository;
    private UserRepository userRepository;

    public ArticleController(UserRepository userRepository, ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
    }
//    首頁查詢所有文章
    @QueryMapping
    public List<Article> findAllArticles() {
        return articleRepository.findAll();
    }

//    查詢特定文章ID
    @QueryMapping
    public Article findArticleByArticleId(@Argument Integer articleId){
        return articleRepository.findById(articleId).orElse(null);
    }

//    查詢特定作者的所有文章
    @QueryMapping
    public List<Article> findAllArticlesByNickname(@Argument String nickname){
        return articleRepository.findByNicknameLike("%"+nickname+"%");
    }

//    查詢標題含有關鍵字的所有文章
    @QueryMapping
    public List<Article> findAllArticlesByArticleTitle(@Argument String articleTitle){
        return articleRepository.findByArticleTitleLike("%"+articleTitle+"%");
    }

//    新增文章
    @MutationMapping
    public Article addNewArticle(
            @Argument Integer userId,
            @Argument String articleTitle,
            @Argument String articleContent) {
        Article article = new Article();
        User aimuser = new User();
        aimuser.setUserId(userId);
        article.setUser(aimuser);
        article.setArticleTitle(articleTitle);
        article.setArticleContent(articleContent);
        article.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        article.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));
        return articleRepository.save(article);
    }

    @SchemaMapping
//    根據文章id去查作者的資訊
    public User user(Article article) {
        return userRepository.findById(article.getUser().getUserId()).orElse(null);
    }

    @MutationMapping
    public Article updateArticle(
            @Argument Integer articleId,
            @Argument String articleTitle,
            @Argument String articleContent
    ) {
        Article aimArticle = articleRepository.findById(articleId).orElse(null);
        if (aimArticle != null) { //   如果有找到目標
            aimArticle.setArticleTitle(articleTitle);
            aimArticle.setArticleContent(articleContent);
            aimArticle.setUpdateTime(Timestamp.valueOf(LocalDateTime.now()));

            articleRepository.save(aimArticle);
        } else {
            return null;
        }
        return aimArticle;
    }
//    updateArticle(articleId: ID!, articleTitle: String!, articleContent: String!): Article!
//    deleteArticle(articleId: ID!): User

    @MutationMapping
    public Boolean deleteArticle(
            @Argument Integer articleId
    ) {
        articleRepository.deleteById(articleId);
        return true;
    }
}