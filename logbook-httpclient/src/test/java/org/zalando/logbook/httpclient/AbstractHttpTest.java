package org.zalando.logbook.httpclient;

import com.github.restdriver.clientdriver.ClientDriverRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.zalando.logbook.Correlation;
import org.zalando.logbook.HttpLogWriter;
import org.zalando.logbook.Precorrelation;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static java.lang.String.format;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractHttpTest {

    @Rule
    public final ClientDriverRule driver = new ClientDriverRule();

    protected final HttpLogWriter writer = mock(HttpLogWriter.class);

    @Before
    public void defaultBehaviour() throws IOException {
        when(writer.isActive(any())).thenReturn(true);
    }

    @Before
    public void start() {

    }

    @After
    public void stop() throws IOException {

    }

    @Test
    public void shouldLogRequest() throws IOException, ExecutionException, InterruptedException {
        sendAndReceive();

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<Precorrelation<String>> captor = ArgumentCaptor.forClass(Precorrelation.class);
        verify(writer).writeRequest(captor.capture());
        final String request = captor.getValue().getRequest();

        assertThat(request, startsWith("Outgoing Request:"));
        assertThat(request, containsString(format("GET http://localhost:%d HTTP/1.1", driver.getPort())));
    }

    @Test
    public void shouldNotLogRequestIfInactive() throws IOException, ExecutionException, InterruptedException {
        when(writer.isActive(any())).thenReturn(false);

        sendAndReceive();

        verify(writer, never()).writeRequest(any());
    }

    @Test
    public void shouldLogResponse() throws IOException, ExecutionException, InterruptedException {
        sendAndReceive();

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<Correlation<String, String>> captor = ArgumentCaptor.forClass(Correlation.class);
        verify(writer).writeResponse(captor.capture());
        final String response = captor.getValue().getResponse();

        assertThat(response, startsWith("Incoming Response:"));
        assertThat(response, containsString("HTTP/1.1 200"));
        assertThat(response, containsString("Content-Type: text/plain"));
        assertThat(response, containsString("Hello, world!"));
    }

    @Test
    public void shouldNotLogResponseIfInactive() throws IOException, ExecutionException, InterruptedException {
        when(writer.isActive(any())).thenReturn(false);

        sendAndReceive();

        verify(writer, never()).writeResponse(any());
    }

    protected abstract void sendAndReceive() throws IOException, ExecutionException, InterruptedException;
}
