package io.github.susimsek.springredissamples.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.susimsek.springredissamples.dto.PostDTO;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PostServiceTest {

    private PostService postService;

    private PostDTO post;

    @BeforeEach
    public void setUp() {
        postService = new PostService();
        post =  new PostDTO("1", 1L,
            "sunt aut facere repellat provident occaecati excepturi optio reprehenderit",
            """
        quia et suscipit
        suscipit recusandae consequuntur expedita et cum
        reprehenderit molestiae ut ut quas totam
        nostrum rerum est autem sunt rem eveniet architecto""");
    }

    @Test
    public void whenCheckingForElement_shouldSearchForElement() {
        // Arrange

        // Act
        Set<PostDTO> postSet = postService.getPostSet();
        assertTrue(postSet.contains(post));
    }

    @Test
    public void whenRemovingElement_shouldRemoveElement() {
        Set<PostDTO> postSet = postService.getPostSet();
        assertTrue(postSet.remove(post));
    }

    @Test
    public void whenClearingHashSet_shouldClearHashSet() {
        Set<PostDTO> postSet = postService.getPostSet();
        postSet.clear();
        assertTrue(postSet.isEmpty());
    }

    @Test
    public void whenCheckingTheSizeOfHashSet_shouldReturnThesize() {
        Set<PostDTO> postSet = postService.getPostSet();
        assertEquals(2, postSet.size());
    }

    @Test
    public void whenCheckingForEmptyHashSet_shouldCheckForEmpty() {
        Set<PostDTO> postSet = postService.getPostSet();
        assertFalse(postSet.isEmpty());
    }
}