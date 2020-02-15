package edu.wisc.my.messages.data;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.env.MockEnvironment;

public class MessagesFromTextFileTest {

  /**
   * Test that an exception encountered in preparing to read the text file results in a throw from
   * the reader.
   */
  @Test(expected = RuntimeException.class)
  public void exceptionPropagates() {
    MessagesFromTextFile messageReader = new MessagesFromTextFile();

    Environment env = mock(Environment.class);
    when(env.getProperty("message.source")).thenThrow(new RuntimeException());
    messageReader.setEnv(env);

    ResourceLoader loader = mock(ResourceLoader.class);
    messageReader.setResourceLoader(loader);

    messageReader.allMessages();
  }

  /**
   * Test that an IOException encountered in reading the text file results in a throw from the
   * reader.
   */
  @Test(expected = RuntimeException.class)
  public void ioExceptionPropagates() throws IOException {
    MessagesFromTextFile messageReader = new MessagesFromTextFile();

    Environment env = mock(Environment.class);
    when(env.getProperty("message.source")).thenReturn("some-particular-message-file.json");
    messageReader.setEnv(env);

    Resource resource = mock(Resource.class);
    when(resource.getInputStream()).thenThrow(IOException.class);

    ResourceLoader loader = mock(ResourceLoader.class);
    when(loader.getResource("some-particular-message-file.json")).thenReturn(resource);
    messageReader.setResourceLoader(loader);

    messageReader.allMessages();
  }

}
