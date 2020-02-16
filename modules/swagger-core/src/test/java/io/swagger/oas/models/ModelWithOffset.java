package io.swagger.oas.models;

import io.swagger.oas.annotations.media.Schema;

public class ModelWithOffset {
    public String id;

    @Schema(implementation = java.time.OffsetDateTime.class)
    public String offset;
}
