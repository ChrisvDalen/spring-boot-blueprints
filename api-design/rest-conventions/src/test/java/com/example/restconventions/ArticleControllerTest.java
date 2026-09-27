package com.example.restconventions;

import com.example.restconventions.model.CreateArticleRequest;
import com.example.restconventions.model.UpdateArticleRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ArticleControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void createArticle_returns201WithLocationHeader() throws Exception {
        var request = new CreateArticleRequest("The Force Awakens", "A long time ago...", "author-1");

        MvcResult result = mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("The Force Awakens"))
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assert location != null && location.contains("/articles/");
    }

    @Test
    void getArticle_notFound_returns404() throws Exception {
        mockMvc.perform(get("/articles/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteArticle_returns204NoContent() throws Exception {
        var request = new CreateArticleRequest("To Be Deleted", "body", "author-2");
        MvcResult created = mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String location = created.getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf('/') + 1);

        mockMvc.perform(delete("/articles/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void createArticle_missingTitle_returns400() throws Exception {
        String badJson = """
                {"body": "some body", "authorId": "a1"}
                """;
        mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchArticle_updatesOnlyProvidedFields() throws Exception {
        var request = new CreateArticleRequest("Original Title", "original body", "author-x");
        MvcResult created = mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();
        String location = created.getResponse().getHeader("Location");
        String id = location.substring(location.lastIndexOf('/') + 1);

        // Partial update: only title provided — body must be preserved
        var patch = new UpdateArticleRequest("Patched Title", null);
        mockMvc.perform(patch("/articles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Patched Title"))
                .andExpect(jsonPath("$.body").value("original body"));
    }

    @Test
    void patchArticle_notFound_returns404ProblemDetail() throws Exception {
        var patch = new UpdateArticleRequest("New title", null);
        mockMvc.perform(patch("/articles/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteArticle_notFound_returns404ProblemDetail() throws Exception {
        mockMvc.perform(delete("/articles/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listArticles_returnsCollectionArray() throws Exception {
        var request = new CreateArticleRequest("Listed Article", "body", "author-l");
        mockMvc.perform(post("/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasItem(
                        org.hamcrest.Matchers.hasEntry("title", "Listed Article"))));
    }
}
