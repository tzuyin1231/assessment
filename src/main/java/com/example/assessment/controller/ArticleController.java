package com.example.assessment.controller;

import com.example.assessment.model.Article;
import com.example.assessment.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ArticleController {

    @Autowired
    ArticleRepository articleRepository;

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
        articleRepository.save(article);
        return article;
    }
}