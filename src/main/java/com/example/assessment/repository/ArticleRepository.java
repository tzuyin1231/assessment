package com.example.assessment.repository;

import com.example.assessment.model.Article;
import com.example.assessment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article,Integer> {
    @Query("SELECT a FROM Article a INNER JOIN a.user u WHERE u.nickname LIKE :nickname")
    List<Article> findByNicknameLike(String nickname);
    List<Article> findByArticleTitleLike(String articleTitle);
    List<Article> findByUser(User user);
}
