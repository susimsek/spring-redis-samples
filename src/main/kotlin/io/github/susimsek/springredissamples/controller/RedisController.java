package io.github.susimsek.springredissamples.controller;

import io.github.susimsek.springredissamples.dto.PostDTO;
import io.github.susimsek.springredissamples.ratelimiter.RateLimiter;
import io.github.susimsek.springredissamples.service.PostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

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

    @GetMapping("/rateLimited")
    @RateLimiter(name = "sampleOperation")
    public String rateLimitedEndpoint() {
        return "Demo response";
    }

    @GetMapping("/rateLimited/{id}")
    @RateLimiter(name = "sampleOperation", key = "#id")
    public String getRateLimitedCustomKey(@PathVariable("id") String id) {
        return "Demo response with id: " + id;
    }
}
