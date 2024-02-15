package io.github.susimsek.springredissamples.dto;

import org.jetbrains.annotations.NotNull;

public record PostDTO(
    String userId,
    Long id,
    String title,
    String body) implements  Comparable<PostDTO> {

    @Override
    public int compareTo(@NotNull PostDTO o) {
        return id.intValue() - o.id.intValue();
    }
}