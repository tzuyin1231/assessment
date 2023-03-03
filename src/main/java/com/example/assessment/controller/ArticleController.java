package com.example.assessment.controller;

import com.example.assessment.model.Article;
import com.example.assessment.model.User;
import com.example.assessment.repository.ArticleRepository;
import com.example.assessment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ArticleController {
    private ArticleRepository articleRepository;
    private UserRepository userRepository;
    public ArticleController(UserRepository userRepository, ArticleRepository articleRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
    }
    @QueryMapping
    public List<Article> findAllArticles() {
        System.out.println("hi");
        return articleRepository.findAll();
    }

    @MutationMapping
    public Article addNewArticle(
            @Argument Integer userId,
            @Argument String articleTitle,
            @Argument String articleContent) {
        Article article = new Article();
        article.setUserId(userId);
        article.setArticleTitle(articleTitle);
        article.setArticleContent(articleContent);
        article.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        articleRepository.save(article);
        return article;
    }

    @SchemaMapping
    public User user(Article article) {
        return userRepository.getById(article.getUserId());
    }

}