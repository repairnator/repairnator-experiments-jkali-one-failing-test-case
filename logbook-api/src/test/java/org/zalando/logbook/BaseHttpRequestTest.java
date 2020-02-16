package org.zalando.logbook;

import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public final class BaseHttpRequestTest {

    @Test
    public void shouldReconstructURI() {
        final BaseHttpRequest unit = spy(BaseHttpRequest.class);
        when(unit.getScheme()).thenReturn("http");
        when(unit.getHost()).thenReturn("localhost");
        when(unit.getPort()).thenReturn(Optional.empty());
        when(unit.getPath()).thenReturn("/test");
        when(unit.getQuery()).thenReturn("limit=1");

        assertThat(unit.getRequestUri(), is("http://localhost/test?limit=1"));
    }

}