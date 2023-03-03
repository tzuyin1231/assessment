package com.example.assessment.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
@Entity
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "articleId")
    private Integer articleId;

    @Column(name = "userId")
    private Integer userId;

    @Column(name = "articleTitle")
    private String articleTitle;

    @Column(name = "articleContent")
    private String articleContent;

    @Column(name = "status")
    private String status;

    @Column(name = "createTime")
    private String createTime;

    @Column(name = "updateTime")
    private String updateTime;

}
