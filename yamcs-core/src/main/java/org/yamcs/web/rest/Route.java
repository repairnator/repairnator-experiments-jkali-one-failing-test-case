package org.yamcs.web.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.yamcs.web.rest.archive.ArchiveTableRestHandler;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Routes.class)
public @interface Route {

    /**
     * Currently must be an absolute path. Specify route params by preceding them with a colon, followed by their
     * identifying name.
     */
    String path();

    /**
     * HTTP method or methods by which this rule is available. By default set to "GET".
     * <p>
     * Implementation note: can't use netty's HttpMethod because it's not an enum
     */
    String[] method() default { "GET" };

    /**
     * Whether this route must be checked before any other route. Used to differentiate a route when it's identifying
     * part overlaps with the route params of another route.
     * <p>
     * When matching the request URI, the first rule with the highest weight that matches is responsible for handling
     * the request.
     * <p>
     * FYI: The order of java methods is non-deterministic when accessing them through reflection. We implement some
     * tricks to get the desired result. This priority qualifier is one of them, another is that paths are reverse
     * sorted on string length before matching. This to make suffixes of otherwise identical rules stand out.
     * <p>
     * Try to architect non-overlapping paths such that you don't require this extra parameter, we have it here mostly
     * due to some legacy paths, that may need rework.
     */
    boolean priority() default false;

    /**
     * Data load routes expect to receive a large body and they receive it piece by piece in HttpContent objects.
     * 
     * See {@link ArchiveTableRestHandler } for an example on how to implement this.
     * 
     * For the normal routes (where dataLoad=false) the body is limited to {@link #maxBodySize()} bytes provided to the
     * HttpObjectAgregator.
     */
    boolean dataLoad() default false;
    
    /**
     * Set true if the execution of the route is expected to take a long time (more than 0.5 seconds).
     * It will be executed on another thread.
     * 
     * Leave false if the execution uses its own off thread mechanism (most of the routes should do that).
     * @return
     */
    boolean offThread() default false;

    int maxBodySize() default Router.MAX_BODY_SIZE;
}
