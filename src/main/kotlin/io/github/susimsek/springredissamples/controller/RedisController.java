package io.github.susimsek.springredissamples.controller;

import io.github.susimsek.springredissamples.dto.PostDTO;
import io.github.susimsek.springredissamples.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "redis",
    description = "Spring Redis REST APIs"
)
@RestController
@RequestMapping(value = "/api/v1/redis")
@RequiredArgsConstructor
public class RedisController {

    private final PostService postService;

    @GetMapping("/posts")
    @Cacheable("postsCache")
    public Set<PostDTO> getPostList(){
        return postService.getPostSet();
    }
}