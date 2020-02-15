package io.github.classgraph.test.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.classgraph.test.external.ExternalAnnotation;
import io.github.classgraph.test.external.ExternalInterface;
import io.github.classgraph.test.external.ExternalSuperclass;

public class InternalExternalTest {
    @Test
    public void testWhitelistingExternalClasses() {
        final ScanResult scanResult = new ClassGraph().whitelistPackages(
                InternalExternalTest.class.getPackage().getName(), ExternalAnnotation.class.getName()).scan();
        assertThat(scanResult.getAllStandardClasses().getNames()).containsOnly(InternalExternalTest.class.getName(),
                InternalExtendsExternal.class.getName(), InternalImplementsExternal.class.getName(),
                InternalAnnotatedByExternal.class.getName());
    }

    @Test
    public void testEnableExternalClasses() {
        final ScanResult scanResult = new ClassGraph()
                .whitelistPackages(InternalExternalTest.class.getPackage().getName(),
                        ExternalAnnotation.class.getName())
                .enableExternalClasses().scan();
        assertThat(scanResult.getAllStandardClasses().getNames()).containsOnly(ExternalSuperclass.class.getName(),
                InternalExternalTest.class.getName(), InternalExtendsExternal.class.getName(),
                InternalImplementsExternal.class.getName(), InternalAnnotatedByExternal.class.getName());
    }

    @Test
    public void testWhitelistingExternalClassesWithoutEnablingExternalClasses() {
        final ScanResult scanResult = new ClassGraph()
                .whitelistPackages(InternalExternalTest.class.getPackage().getName(),
                        ExternalAnnotation.class.getName())
                .enableAllInfo().scan();
        assertThat(scanResult.getAllStandardClasses().getNames()).containsOnly(InternalExternalTest.class.getName(),
                InternalExtendsExternal.class.getName(), InternalImplementsExternal.class.getName(),
                InternalAnnotatedByExternal.class.getName());
        assertThat(scanResult.getSubclasses(ExternalSuperclass.class.getName()).getNames())
                .containsOnly(InternalExtendsExternal.class.getName());
        assertThat(scanResult.getAllInterfaces()).isEmpty();
        assertThat(scanResult.getClassesImplementing(ExternalInterface.class.getName()).getNames())
                .containsOnly(InternalImplementsExternal.class.getName());
        assertThat(scanResult.getAllAnnotations().getNames()).isEmpty();
        assertThat(scanResult.getClassesWithAnnotation(ExternalAnnotation.class.getName()).getNames())
                .containsOnly(InternalAnnotatedByExternal.class.getName());
    }

    @Test
    public void testIncludeReferencedClasses() {
        final ScanResult scanResult = new ClassGraph()
                .whitelistPackages(InternalExternalTest.class.getPackage().getName()).enableAllInfo().scan();
        assertThat(scanResult.getAllStandardClasses().getNames())
                .doesNotContain(ExternalSuperclass.class.getName());
        assertThat(scanResult.getSubclasses(ExternalSuperclass.class.getName()).getNames())
                .containsOnly(InternalExtendsExternal.class.getName());
        assertThat(scanResult.getAllInterfaces().getNames()).doesNotContain(ExternalInterface.class.getName());
        assertThat(scanResult.getClassesImplementing(ExternalInterface.class.getName()).getNames())
                .containsOnly(InternalImplementsExternal.class.getName());
        assertThat(scanResult.getAllAnnotations().getNames()).doesNotContain(ExternalAnnotation.class.getName());
        assertThat(scanResult.getClassesWithAnnotation(ExternalAnnotation.class.getName()).getNames())
                .containsOnly(InternalAnnotatedByExternal.class.getName());
    }
}
