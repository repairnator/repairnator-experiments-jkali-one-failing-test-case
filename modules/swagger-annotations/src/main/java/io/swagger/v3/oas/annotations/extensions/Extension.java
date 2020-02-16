package io.swagger.v3.oas.annotations.extensions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An optionally named list of extension properties.
 *
 * @see <a target="_new" href="https://github.com/OAI/OpenAPI-Specification/blob/OpenAPI.next/versions/3.0.0.md#specificationExtensions">Specification extensions (OpenAPI specification)</a>
 */
@Target({ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
        ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Extensions.class)
public @interface Extension {

    /**
     * An option name for these extensions.
     *
     * @return an option name for these extensions - will be prefixed with "x-"
     */
    String name() default "";

    /**
     * The extension properties.
     *
     * @return the actual extension properties
     * @see ExtensionProperty
     */
    ExtensionProperty[] properties();
}