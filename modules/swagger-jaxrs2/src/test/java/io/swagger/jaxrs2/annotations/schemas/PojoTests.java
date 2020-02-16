package io.swagger.jaxrs2.annotations.schemas;

import io.swagger.jaxrs2.annotations.AbstractAnnotationTest;
import io.swagger.oas.annotations.media.Schema;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.HashMap;

import static org.testng.Assert.assertEquals;

public class PojoTests extends AbstractAnnotationTest {
    @Test(enabled = false)
    public void testModelWithTitle() {
        String yaml = readIntoYaml(ClassWithTitle.class);

        assertEquals(yaml,
            "type: object\n" +
            "title: 'My Pojo'\n" +
            "properties:\n" +
            "  id:\n" +
            "    type: string");
    }

    @Schema(title = "My Pojo")
    static class ClassWithTitle {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    @Test(enabled = false, description = "The @Schema annotation will only be adding additional sugar on the property")
    public void testModelWithAnnotatedPrivateMember() {
        String yaml = readIntoYaml(ClassWithAnnotatedProperty.class);

        assertEquals(yaml,
            "type: object\n" +
            "properties:\n" +
            "  id:\n" +
            "    description: 'a long descrxiption for this property\n" +
            "    type: string");
    }

    static class ClassWithAnnotatedProperty {
        @Schema(description = "a long description for this property")
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    @Test(enabled = false, description = "The @Schema annotation will only be adding additional sugar on the property")
    public void testModelWithAnnotatedPublicMethod() {
        String yaml = readIntoYaml(ClassWithAnnotatedMethod.class);

        assertEquals(yaml,
            "type: object\n" +
            "properties:\n" +
            "  id:\n" +
            "    description: 'a long descrxiption for this property\n" +
            "    type: string");
    }

    static class ClassWithAnnotatedMethod {
        private String id;

        @Schema(description = "a long description for this property")
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    @Test(enabled = false, description = "The @Schema annotation will override the type of the actual parameter")
    public void testModelWithOverriddenMemberType() {
        String yaml = readIntoYaml(ClassWithOverriddenMemberType.class);

        assertEquals(yaml,
            "type: object\n" +
            "properties:\n" +
            "  id:\n" +
            "    description: 'we are declaring a string implementation must be a valid long integer, even though " +
                    "the model backs it with a String implementation\n" +
            "    type: integer\n" +
            "    format: int64");
    }

    static class ClassWithOverriddenMemberType {
        private String id;

        @Schema(type = "integer", format = "int64", description = "we are declaring a string implementation must be " +
                "a valid long integer, even though the model backs it with a String implementation")
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    @Test(enabled = false, description = "@Schema is completely overriding the type for this model")
    public void testModelWithAlternateRepresentation () {
        String yaml = readIntoYaml(ClassWithAlternateRepresentation.class);

        assertEquals(yaml,
            "type: object\n" +
            "properties:\n" +
            "  id:\n" +
            "    description: 'a long descrxiption for this property\n" +
            "    type: string");
    }

    @Schema(implementation = ClassWithAnnotatedMethod.class)
    static class ClassWithAlternateRepresentation {
        private Date dateField;

        public Date getDateField() {
            return dateField;
        }

        public void setDateField(Date dateField) {
            this.dateField = dateField;
        }
    }

    @Test(enabled = false, description = "@Schema is allowing multiple definition interfaces to represent this model")
    public void testModelWithMultipleRepresentations () {
        String yaml = readIntoYaml(UberObject.class);

        assertEquals(yaml,
            "anyOf:\n" +
            "  - type: object\n" +
            "    description: 'A User Object'\n" +
            "    required:\n" +
            "      - id\n" +
            "    properties:\n" +
            "      id:\n" +
            "        type: string\n" +
            "        format: uuid\n" +
            "      name:\n" +
            "        type: string\n" +
            "  - type: object\n" +
            "    description: An Employee Object\n" +
            "    required:\n" +
            "      - department\n" +
            "    properties:\n" +
            "      id:\n" +
            "        type: string\n" +
            "        format: email\n" +
            "      department:\n" +
            "        type: string");
    }

    @Schema(anyOf = {UserObject.class, EmployeeObject.class})
    static class UberObject implements UserObject, EmployeeObject {
        private String id;
        private String name;
        private String department;

        @Override
        public String getDepartment() {
            return department;
        }
        @Override
        public String getId() {
            return id;
        }
        @Override
        public String getName() {
            return name;
        }
    }

    @Schema(description = "A User Object")
    interface UserObject {
        @Schema(format = "uuid", required = true)
        String getId();
        String getName();
    }

    @Schema(description = "An Employee Object", requiredProperties = {"department"})
    interface EmployeeObject {
        @Schema(format = "email")
        String getId();
        String getDepartment();
    }

    @Test(enabled = false, description = "Shows how @Schema can be used to allow only certain data formats")
    public void testModelWithSpecificFormat () {
        String yaml = readIntoYaml(classWithIdConstraints.class);

        assertEquals(yaml,
            "type: object\n" +
            "title: AuthorizedUser\n" +
            "properties:\n" +
            "  id:\n" +
            "    type: string\n" +
            "    description: 'A valid user social security'\n" +
            "    pattern: '^\\d{3}-?\\d{2}-?\\d{4}$'");
    }

    @Schema(title = "AuthorizedUser")
    static class classWithIdConstraints {
        @Schema(pattern = "^\\d{3}-?\\d{2}-?\\d{4}$", description = "A valid user social security")
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    // TODO verify that _not_ is an array or `not`
    @Test(enabled = false, description = "Shows how to restrict a particular schema")
    public void testExcludeSchema () {
        String yaml = readIntoYaml(ArbitraryDataReceiver.class);

        assertEquals(yaml,
            "type: object\n" +
            "additionalProperties: true\n" +
            "not:\n" +
            "  - type: object\n" +
            "    title: AuthorizedUser\n" +
            "    properties:\n" +
            "      id:\n" +
            "        type: string\n" +
            "        description: 'A valid user social security'\n" +
            "        pattern: '^\\d{3}-?\\d{2}-?\\d{4}$'");
    }

    @Schema(not = classWithIdConstraints.class, description = "We don't store social security numbers here!")
    static class ArbitraryDataReceiver extends HashMap<String, Object> {}

    @Test(enabled = false, description = "Shows how to override a definition with a schema reference")
    public void testSchemaReference () {
        String yaml = readIntoYaml(NotAPet.class);

        assertEquals(yaml, "$ref: http://petstore.swagger.io/v2/swagger.json#/definitions/Tag");
    }

    @Schema(ref = "http://petstore.swagger.io/v2/swagger.json#/definitions/Tag")
    static class NotAPet {}

    @Test(enabled = false, description = "Shows how to add a reference on a property")
    public void testPropertySchemaReference () {
        String yaml = readIntoYaml(ModelWithSchemaPropertyReference.class);

        assertEquals(yaml,
            "type: object\n" +
            "properties:\n" +
            "  notATag:\n" +
            "    $ref: 'http://petstore.swagger.io/v2/swagger.json#/definitions/Tag'");
    }

    static class ModelWithSchemaPropertyReference {
        @Schema(ref = "http://petstore.swagger.io/v2/swagger.json#/definitions/Tag")
        private String notATag;
    }

    @Test(enabled = false, description = "Shows how to override a property name")
    public void testPropertyNameOverride () {
        String yaml = readIntoYaml(ModelWithPropertyNameOverride.class);

        assertEquals(yaml,
            "type: object\n" +
            "properties:\n" +
            "  username:\n" +
            "    type: string");
    }

    static class ModelWithPropertyNameOverride {
        @Schema(name = "username")
        private String definitelyNotCalledUsername;
    }

    @Test(enabled = false, description = "Shows how to override a model name")
    public void testModelNameOverride () {
        String yaml = readIntoYaml(ModelWithNameOverride.class);

        assertEquals(yaml,
            "type: object\n" +
            "properties:\n" +
            "  id:\n" +
            "    type: string");
    }

    @Schema(name = "Employee")
    static class ModelWithNameOverride {
        private String id;
    }

    @Test(enabled = false, description = "Shows how to provide model examples")
    public void testModelPropertyExampleOverride () {
        String yaml = readIntoYaml(modelWithPropertyExampleOverride.class);

        assertEquals(yaml,
            "type: object\n" +
            "properties:\n" +
            "  id:\n" +
            "    type: string\n" +
            "    example: abc-123");
    }

    static class modelWithPropertyExampleOverride {
        @Schema(example = "abc-123")
        private String id;
    }

    @Test(enabled = false, description = "Shows how to provide multiple property examples")
    public void testMultipleModelPropertyExampleOverrides() {
        String yaml = readIntoYaml(modelWithMultiplePropertyExamples.class);

        assertEquals(yaml,
            "type: object\n" +
            "properties:\n" +
            "  id:\n" +
            "    type: string\n" +
            "    examples:\n" +
            "      - abc-123\n" +
            "      - zz-aa-bb\n");
    }

    static class modelWithMultiplePropertyExamples {
        @Schema(examples = {"abc-123", "zz-aa-bb"})
        private String id;
    }

    @Test(enabled = false, description = "Show how to completely override an object example")
    public void testModelExampleOverride() {
        String yaml = readIntoYaml(modelWithExampleOverride.class);

        assertEquals(yaml,
            "type: object\n" +
            "example: \n" +
            "  foo: bar\n" +
            "  baz: true\n" +
            "properties:\n" +
            "  id:\n" +
            "    type: string");
    }

    @Schema(example = "{\"foo\": \"bar\",\"baz\": true}")
    static class modelWithExampleOverride {
        private String id;
    }

    @Test(enabled = false, description = "shows how to model a string array schema")
    public void testStringArraySchema() {

    }

    @Schema(type = "string"/* container = "array" */)
    static class stringArraySchema {}

    @Test(enabled = false, description = "shows how to model a string map schema")
    public void testStringMapSchema() {

    }

    @Schema(type = "string"/* container = "map" */)
    static class stringMapSchema {}

    static class JavaSucks {
        @Schema(implementation = JavaSucks.class)
        public Object values;
    }
}
