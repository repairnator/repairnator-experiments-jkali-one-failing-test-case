package org.zalando.logbook;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class FilteredHttpResponseTest {

    private final HttpResponse unit = new FilteredHttpResponse(MockHttpResponse.create()
            .withHeaders(MockHeaders.of(
                    "Authorization", "Bearer 9b7606a6-6838-11e5-8ed4-10ddb1ee7671",
                    "Accept", "text/plain"))
            .withBodyAsString("My secret is s3cr3t"),
            HeaderFilters.authorization(),
            (contentType, body) -> body.replace("s3cr3t", "f4k3"));

    @Test
    public void shouldFilterAuthorizationHeader() {
        assertThat(unit.getHeaders(), hasEntry(equalTo("Authorization"), contains("XXX")));
    }

    @Test
    public void shouldNotFilterAcceptHeader() {
        assertThat(unit.getHeaders(), hasEntry(equalTo("Accept"), contains("text/plain")));
    }

    @Test
    public void shouldFilterBody() throws IOException {
        assertThat(unit.getBodyAsString(), is("My secret is f4k3"));
    }

    @Test
    public void shouldFilterBodyContent() throws IOException {
        assertThat(new String(unit.getBody(), unit.getCharset()), is("My secret is f4k3"));
    }

}
