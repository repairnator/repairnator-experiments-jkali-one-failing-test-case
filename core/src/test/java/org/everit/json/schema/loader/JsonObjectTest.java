package org.everit.json.schema.loader;

import org.everit.json.schema.ResourceLoader;
import org.everit.json.schema.SchemaException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author erosb
 */
public class JsonObjectTest {

    @SuppressWarnings("unchecked")
    static <R> Consumer<R> mockConsumer() {
        return (Consumer<R>) mock(Consumer.class);
    }

    public static final JSONObject RAW_OBJECTS = ResourceLoader.DEFAULT.readObj("objecttestcases.json");

    public static final JSONObject TESTSCHEMAS = ResourceLoader.DEFAULT.readObj("testschemas.json");

    private Map<String, Object> storage() {
        Map<String, Object> rval = new HashMap<>();
        rval.put("a", true);
        rval.put("b", new JSONObject());
        return rval;
    }

    private static final LoadingState emptyLs = JsonValueTest.emptyLs;

    @Rule
    public ExpectedException expExc = ExpectedException.none();

    @Test
    public void testHasKey() {
        assertTrue(subject().containsKey("a"));
    }

    private JsonObject subject() {
        return new JsonObject(storage(), JsonValueTest.emptyLs);
    }

    @Test
    public void testRequireWithConsumer() {
        Consumer<JsonValue> consumer = mockConsumer();
        subject().require("a", consumer);
        LoadingState lsForPath = emptyLs.childFor("a");
        verify(consumer).accept(JsonValue.of(true, lsForPath));
    }

    @Test
    public void testRequireWithConsumerFailure() {
        expExc.expect(SchemaException.class);
        expExc.expectMessage("#: required key [aaa] not found");
        Consumer<JsonValue> consumer = mockConsumer();
        subject().require("aaa", consumer);
        verify(consumer, never()).accept(any());
    }

    @Test
    public void testRequireWithFunction() {
        Function<JsonValue, Boolean> fn = val -> false;
        assertFalse(subject().requireMapping("a", fn));
    }

    @Test
    public void testRequireWithFunctionFailure() {
        expExc.expect(SchemaException.class);
        expExc.expectMessage("#: required key [aaa] not found");
        subject().requireMapping("aaa", val -> false);
    }

    @Test
    public void testMaybeWithConsumer() {
        Consumer<JsonValue> consumer = mockConsumer();
        subject().maybe("a", consumer);
        LoadingState lsForPath = emptyLs.childFor("a");
        verify(consumer).accept(JsonValue.of(true, lsForPath));
    }

    @Test
    public void testMaybeWithConsumerMiss() {
        Consumer<JsonValue> consumer = mockConsumer();
        subject().maybe("aaa", consumer);
        verify(consumer, never()).accept(any());
    }

    @Test
    public void testForEach() {
        JsonObjectIterator iterator = mock(JsonObjectIterator.class);
        subject().forEach(iterator);
        LoadingState aChild = emptyLs.childFor("a"), bChild = emptyLs.childFor("b");
        verify(iterator).apply("a", JsonValue.of(true, aChild));
        verify(iterator).apply("b", JsonValue.of(new JSONObject(), bChild));
    }

    @Test
    public void testMaybeWithFn() {
        assertEquals(Integer.valueOf(42), subject().maybeMapping("a", obj -> 42).get());
    }

    @Test
    public void testMaybeWithFnMiss() {
        assertEquals(Optional.empty(), subject().maybeMapping("aaaa", ls -> 42));
    }

    @Test
    public void idHandling() {
        JSONObject schema = RAW_OBJECTS.getJSONObject("idInRoot");
        URI actual = JsonValue.of(schema, emptyLs).ls.id;
        assertEquals(schema.get("id"), actual.toString());
    }

    @Test
    public void nullId() {
        JSONObject schema = new JSONObject();
        URI actual = JsonValue.of(schema, emptyLs).ls.id;
        assertNull(actual);
    }

    @Test
    public void nestedId() {
        JSONObject schema = RAW_OBJECTS.getJSONObject("nestedId");
        URI actual = JsonValue.of(schema, emptyLs).requireObject()
                .require("properties")
                .requireObject()
                .require("prop").ls.id;
        assertEquals("http://x.y/z#zzz", actual.toString());
    }

    @Test
    public void childForConsidersIdAttr() {
        JSONObject input = TESTSCHEMAS.getJSONObject("remotePointerResolution");
        JsonObject root = new JsonObject(input.toMap());
        System.out.println("root.ls.id = " +root.ls.id);
        JsonObject fc = root.require("properties").requireObject().require("folderChange").requireObject();
        System.out.println("fc.ls.id = " + fc.ls.id);
        JsonObject sIF = fc.require("properties").requireObject().require("schemaInFolder").requireObject();
        System.out.println("sIF.ls.id = " + sIF.ls.id);
    }

}
