package com.example.restconventions.service;

import com.example.restconventions.exception.ArticleNotFoundException;
import com.example.restconventions.model.Article;
import com.example.restconventions.model.CreateArticleRequest;
import com.example.restconventions.model.UpdateArticleRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ArticleService {

    private final Map<Long, Article> store = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public List<Article> findAll() {
        return new ArrayList<>(store.values());
    }

    public Article findById(Long id) {
        Article article = store.get(id);
        if (article == null) {
            throw new ArticleNotFoundException(id);
        }
        return article;
    }

    public Article create(CreateArticleRequest request) {
        long id = sequence.getAndIncrement();
        Article article = new Article(id, request.title(), request.body(), request.authorId(), Instant.now());
        store.put(id, article);
        return article;
    }

    public Article update(Long id, UpdateArticleRequest request) {
        Article existing = findById(id);
        Article updated = new Article(
                existing.id(),
                request.title() != null ? request.title() : existing.title(),
                request.body() != null ? request.body() : existing.body(),
                existing.authorId(),
                existing.publishedAt()
        );
        store.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        if (!store.containsKey(id)) {
            throw new ArticleNotFoundException(id);
        }
        store.remove(id);
    }
}
