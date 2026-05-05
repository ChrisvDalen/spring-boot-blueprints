package com.example.restconventions.controller;

import com.example.restconventions.model.Article;
import com.example.restconventions.model.CreateArticleRequest;
import com.example.restconventions.model.UpdateArticleRequest;
import com.example.restconventions.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Demonstrates REST naming conventions:
 * - Plural nouns for collection resources (/articles not /getArticles)
 * - HTTP verbs carry the action (GET/POST/PATCH/DELETE)
 * - 201 + Location header on creation
 * - 204 No Content on deletion
 * - 404 on missing resource, not 200 with null body
 */
@RestController
@RequestMapping("/articles")
@Tag(name = "Articles", description = "Demonstrates proper REST resource naming and HTTP verb usage")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    @Operation(summary = "List all articles", description = "Collection resource — GET on plural noun, returns 200 with array")
    public List<Article> listArticles() {
        return articleService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single article", description = "Singleton sub-resource — 200 on found, 404 on missing")
    @ApiResponse(responseCode = "200", description = "Article found")
    @ApiResponse(responseCode = "404", description = "Article not found")
    public Article getArticle(@PathVariable Long id) {
        return articleService.findById(id);
    }

    @PostMapping
    @Operation(
            summary = "Create an article",
            description = "POST to collection creates a new resource. Returns 201 with Location header pointing to the new resource."
    )
    @ApiResponse(responseCode = "201", description = "Article created — Location header contains the URI")
    public ResponseEntity<Article> createArticle(@Valid @RequestBody CreateArticleRequest request) {
        Article created = articleService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    // PATCH vs PUT:
    // PATCH = partial update (only fields present in the body are changed)
    // PUT   = full replacement (absent fields are cleared/defaulted)
    // Use PATCH when clients should be able to update individual fields.
    @PatchMapping("/{id}")
    @Operation(
            summary = "Partially update an article",
            description = "PATCH for partial updates — only provided fields change. Use PUT only for full replacement."
    )
    @ApiResponse(responseCode = "200", description = "Article updated")
    @ApiResponse(responseCode = "404", description = "Article not found")
    public Article updateArticle(@PathVariable Long id, @Valid @RequestBody UpdateArticleRequest request) {
        return articleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an article", description = "204 No Content on success — no body needed")
    @ApiResponse(responseCode = "204", description = "Article deleted")
    @ApiResponse(responseCode = "404", description = "Article not found")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
