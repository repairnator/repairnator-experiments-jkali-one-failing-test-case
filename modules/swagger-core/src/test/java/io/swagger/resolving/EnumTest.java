package io.swagger.resolving;

import com.google.common.base.Functions;
import com.google.common.collect.Collections2;
import io.swagger.converter.ModelConverterContextImpl;
import io.swagger.jackson.ModelResolver;
import io.swagger.oas.models.media.Schema;
import io.swagger.oas.models.media.StringSchema;
import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class EnumTest extends SwaggerTestBase {

    @Test
    public void testEnum() throws Exception {
        final ModelResolver modelResolver = new ModelResolver(mapper());
        final ModelConverterContextImpl context = new ModelConverterContextImpl(modelResolver);

        final Schema model = context.resolve(Currency.class);
        assertNull(model);

        final Schema property = context.resolve(Currency.class, new Annotation[]{});
        assertNotNull(property);
        assertTrue(property instanceof StringSchema);
        final StringSchema strProperty = (StringSchema) property;
        assertNotNull(strProperty.getEnum());
        final Collection<String> values =
                new ArrayList<String>(Collections2.transform(Arrays.asList(Currency.values()), Functions.toStringFunction()));
        assertEquals(strProperty.getEnum(), values);
    }

    public enum Currency {
        USA, CANADA
    }
}
