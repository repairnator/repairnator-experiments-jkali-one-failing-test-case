package org.zalando.logbook;

import org.junit.Test;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class RawResponseFiltersTest {

    @Test
    public void shouldReplaceImageBodyByDefault() throws IOException {
        final RawResponseFilter filter = RawResponseFilters.defaultValue();

        final RawHttpResponse response = filter.filter(MockRawHttpResponse.create()
                .withContentType("image/png")
                .withBodyAsString("this is an image"));

        assertThat(response.getContentType(), is("image/png"));
        assertThat(response.withBody().getContentType(), is("image/png"));
        assertThat(response.withBody().getBody(), is("<binary>".getBytes(UTF_8)));
        assertThat(response.withBody().getBodyAsString(), is("<binary>"));
    }

    @Test
    public void shouldNotReplaceTextBodyByDefault() throws IOException {
        final RawResponseFilter filter = RawResponseFilters.defaultValue();

        final RawHttpResponse response = filter.filter(MockRawHttpResponse.create()
                .withContentType("text/plain")
                .withBodyAsString("Hello"));

        assertThat(response.withBody().getBody(), is("Hello".getBytes(UTF_8)));
        assertThat(response.withBody().getBodyAsString(), is("Hello"));
    }

}