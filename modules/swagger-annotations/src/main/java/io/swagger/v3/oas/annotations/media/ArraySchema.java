package io.swagger.v3.oas.annotations.media;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation may be used to define a schema of type "array" for a set of elements of the OpenAPI spec, and/or to define additional
 * properties for the schema. It is applicable e.g. to parameters, schema classes (aka "models"), properties of such
 * models, request and response content, header.
 *
 * <p>swagger-core resolver and swagger-jaxrs2 reader engine consider this annotation along with JAX-RS annotations,
 * element type and context as input to resolve the annotated element into an OpenAPI schema definition for such element.</p>
 *
 * <p>The annotation {@link Schema} shall be used for non array elements; {@link ArraySchema} and {@link Schema} cannot
 * coexist</p>
 *
 * @see <a target="_new" href="https://github.com/OAI/OpenAPI-Specification/blob/OpenAPI.next/versions/3.0.0.md#schemaObject">Schema (OpenAPI specification)</a>
 * @see Schema
 **/
@Target({ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
        ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ArraySchema {

    /**
     * The schema of the items in the array
     *
     * @return schema
     */
    Schema schema() default @Schema;

    /**
     * sets the maximum number of items in an array.  Ignored if value is Integer.MIN_VALUE.
     *
     * @return integer representing maximum number of items in array
     **/
    int maxItems() default Integer.MIN_VALUE;

    /**
     * sets the minimum number of items in an array.  Ignored if value is Integer.MAX_VALUE.
     *
     * @return integer representing minimum number of items in array
     **/
    int minItems() default Integer.MAX_VALUE;

    /**
     * determines whether an array of items will be unique
     *
     * @return boolean - whether items in an array are unique or repeating
     **/
    boolean uniqueItems() default false;
}