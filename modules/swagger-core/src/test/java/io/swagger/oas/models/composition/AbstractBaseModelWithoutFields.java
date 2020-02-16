package io.swagger.oas.models.composition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.swagger.oas.annotations.media.Schema;

@JsonSubTypes({@JsonSubTypes.Type(value = Thing3.class, name = "thing3")})
@Schema(description = "I am an Abstract Base Model without any declared fields and with Sub-Types")
public abstract class AbstractBaseModelWithoutFields {
}
