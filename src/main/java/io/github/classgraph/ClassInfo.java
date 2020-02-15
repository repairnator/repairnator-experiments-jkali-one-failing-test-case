/*
 * This file is part of ClassGraph.
 *
 * Author: Luke Hutchison
 *
 * Hosted at: https://github.com/lukehutch/fast-classpath-scanner
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Luke Hutchison
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.classgraph;

import java.io.File;
import java.lang.annotation.Inherited;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.classgraph.json.Id;
import io.github.classgraph.utils.JarUtils;
import io.github.classgraph.utils.LogNode;
import io.github.classgraph.utils.Parser.ParseException;
import io.github.classgraph.utils.TypeUtils;
import io.github.classgraph.utils.URLPathEncoder;

/** Holds metadata about a class encountered during a scan. */
public class ClassInfo extends ScanResultObject implements Comparable<ClassInfo> {
    /** Name of the class. */
    private @Id String name;

    /** Class modifier flags, e.g. Modifier.PUBLIC */
    private int modifiers;

    /** True if the classfile indicated this is an interface (or an annotation, which is an interface). */
    private boolean isInterface;

    /** True if the classfile indicated this is an annotation. */
    private boolean isAnnotation;

    /**
     * This annotation has the {@link Inherited} meta-annotation, which means that any class that this annotation is
     * applied to also implicitly causes the annotation to annotate all subclasses too.
     */
    boolean isInherited;

    /** The class type signature string. */
    private String typeSignatureStr;

    /** The class type signature, parsed. */
    private transient ClassTypeSignature typeSignature;

    /** The fully-qualified defining method name, for anonymous inner classes. */
    private String fullyQualifiedDefiningMethodName;

    /**
     * If true, this class is only being referenced by another class' classfile as a superclass / implemented
     * interface / annotation, but this class is not itself a whitelisted (non-blacklisted) class, or in a
     * whitelisted (non-blacklisted) package.
     * 
     * If false, this classfile was matched during scanning (i.e. its classfile contents read), i.e. this class is a
     * whitelisted (and non-blacklisted) class in a whitelisted (and non-blacklisted) package.
     */
    private boolean isExternalClass;

    /**
     * The classpath element file (classpath root dir or jar) that this class was found within, or null if this
     * class was found in a module.
     */
    transient File classpathElementFile;

    /**
     * The package root within a jarfile (e.g. "BOOT-INF/classes"), or the empty string if this is not a jarfile, or
     * the package root is the classpath element path (as opposed to within a subdirectory of the classpath
     * element).
     */
    private transient String jarfilePackageRoot = "";

    /**
     * The classpath element module that this class was found within, or null if this class was found within a
     * directory or jar.
     */
    private transient ModuleRef moduleRef;

    /** The classpath element URL (classpath root dir or jar) that this class was found within. */
    private transient URL classpathElementURL;

    /** The classloaders to try to load this class with before calling a MatchProcessor. */
    transient ClassLoader[] classLoaders;

    /** Info on class annotations, including optional annotation param values. */
    AnnotationInfoList annotationInfo;

    /** Info on fields. */
    FieldInfoList fieldInfo;

    /** Reverse mapping from field name to FieldInfo. */
    private transient Map<String, FieldInfo> fieldNameToFieldInfo;

    /** Info on fields. */
    MethodInfoList methodInfo;

    /** For annotations, the default values of parameters. */
    List<AnnotationParameterValue> annotationDefaultParamValues;

    /** The set of classes related to this one. */
    private final Map<RelType, Set<ClassInfo>> relatedClasses = new HashMap<>();

    // -------------------------------------------------------------------------------------------------------------

    /** Default constructor for deserialization. */
    ClassInfo() {
    }

    private ClassInfo(final String name, final int classModifiers, final boolean isExternalClass) {
        this();
        this.name = name;
        if (name.endsWith(";")) {
            // Spot check to make sure class names were parsed from descriptors
            throw new RuntimeException("Bad class name");
        }
        this.modifiers = classModifiers;
        this.isExternalClass = isExternalClass;
    }

    // -------------------------------------------------------------------------------------------------------------

    /** How classes are related. */
    private static enum RelType {

        // Classes:

        /**
         * Superclasses of this class, if this is a regular class.
         *
         * <p>
         * (Should consist of only one entry, or null if superclass is java.lang.Object or unknown).
         */
        SUPERCLASSES,

        /** Subclasses of this class, if this is a regular class. */
        SUBCLASSES,

        /** Indicates that an inner class is contained within this one. */
        CONTAINS_INNER_CLASS,

        /** Indicates that an outer class contains this one. (Should only have zero or one entries.) */
        CONTAINED_WITHIN_OUTER_CLASS,

        // Interfaces:

        /**
         * Interfaces that this class implements, if this is a regular class, or superinterfaces, if this is an
         * interface.
         *
         * <p>
         * (May also include annotations, since annotations are interfaces, so you can implement an annotation.)
         */
        IMPLEMENTED_INTERFACES,

        /**
         * Classes that implement this interface (including sub-interfaces), if this is an interface.
         */
        CLASSES_IMPLEMENTING,

        // Class annotations:

        /**
         * Annotations on this class, if this is a regular class, or meta-annotations on this annotation, if this is
         * an annotation.
         */
        CLASS_ANNOTATIONS,

        /** Classes annotated with this annotation, if this is an annotation. */
        CLASSES_WITH_ANNOTATION,

        // Method annotations:

        /** Annotations on one or more methods of this class. */
        METHOD_ANNOTATIONS,

        /**
         * Classes that have one or more methods annotated with this annotation, if this is an annotation.
         */
        CLASSES_WITH_METHOD_ANNOTATION,

        // Field annotations:

        /** Annotations on one or more fields of this class. */
        FIELD_ANNOTATIONS,

        /**
         * Classes that have one or more fields annotated with this annotation, if this is an annotation.
         */
        CLASSES_WITH_FIELD_ANNOTATION,
    }

    /**
     * Add a class with a given relationship type. Return whether the collection changed as a result of the call.
     */
    private boolean addRelatedClass(final RelType relType, final ClassInfo classInfo) {
        Set<ClassInfo> classInfoSet = relatedClasses.get(relType);
        if (classInfoSet == null) {
            relatedClasses.put(relType, classInfoSet = new HashSet<>(4));
        }
        return classInfoSet.add(classInfo);
    }

    private static final int ANNOTATION_CLASS_MODIFIER = 0x2000;

    /**
     * Get a ClassInfo object, or create it if it doesn't exist. N.B. not threadsafe, so ClassInfo objects should
     * only ever be constructed by a single thread.
     */
    private static ClassInfo getOrCreateClassInfo(final String className, final int classModifiers,
            final Map<String, ClassInfo> classNameToClassInfo) {
        ClassInfo classInfo = classNameToClassInfo.get(className);
        if (classInfo == null) {
            classNameToClassInfo.put(className,
                    classInfo = new ClassInfo(className, classModifiers, /* isExternalClass = */ true));
        }
        classInfo.modifiers |= classModifiers;
        if ((classModifiers & ANNOTATION_CLASS_MODIFIER) != 0) {
            classInfo.isAnnotation = true;
        }
        if ((classModifiers & Modifier.INTERFACE) != 0) {
            classInfo.isInterface = true;
        }
        return classInfo;
    }

    /** Add a superclass to this class. */
    void addSuperclass(final String superclassName, final Map<String, ClassInfo> classNameToClassInfo) {
        if (superclassName != null && !superclassName.equals("java.lang.Object")) {
            final ClassInfo superclassClassInfo = getOrCreateClassInfo(superclassName, /* classModifiers = */ 0,
                    classNameToClassInfo);
            this.addRelatedClass(RelType.SUPERCLASSES, superclassClassInfo);
            superclassClassInfo.addRelatedClass(RelType.SUBCLASSES, this);
        }
    }

    /** Add an implemented interface to this class. */
    void addImplementedInterface(final String interfaceName, final Map<String, ClassInfo> classNameToClassInfo) {
        final ClassInfo interfaceClassInfo = getOrCreateClassInfo(interfaceName,
                /* classModifiers = */ Modifier.INTERFACE, classNameToClassInfo);
        interfaceClassInfo.isInterface = true;
        interfaceClassInfo.modifiers |= Modifier.INTERFACE;
        this.addRelatedClass(RelType.IMPLEMENTED_INTERFACES, interfaceClassInfo);
        interfaceClassInfo.addRelatedClass(RelType.CLASSES_IMPLEMENTING, this);
    }

    /** Add class containment info */
    static void addClassContainment(final List<SimpleEntry<String, String>> classContainmentEntries,
            final Map<String, ClassInfo> classNameToClassInfo) {
        for (final SimpleEntry<String, String> ent : classContainmentEntries) {
            final String innerClassName = ent.getKey();
            final ClassInfo innerClassInfo = ClassInfo.getOrCreateClassInfo(innerClassName,
                    /* classModifiers = */ 0, classNameToClassInfo);
            final String outerClassName = ent.getValue();
            final ClassInfo outerClassInfo = ClassInfo.getOrCreateClassInfo(outerClassName,
                    /* classModifiers = */ 0, classNameToClassInfo);
            innerClassInfo.addRelatedClass(RelType.CONTAINED_WITHIN_OUTER_CLASS, outerClassInfo);
            outerClassInfo.addRelatedClass(RelType.CONTAINS_INNER_CLASS, innerClassInfo);
        }
    }

    /** Add containing method name, for anonymous inner classes */
    void addFullyQualifiedDefiningMethodName(final String fullyQualifiedDefiningMethodName) {
        this.fullyQualifiedDefiningMethodName = fullyQualifiedDefiningMethodName;
    }

    /** Add an annotation to this class. */
    void addClassAnnotation(final AnnotationInfo classAnnotationInfo,
            final Map<String, ClassInfo> classNameToClassInfo) {
        final ClassInfo annotationClassInfo = getOrCreateClassInfo(classAnnotationInfo.getName(),
                ANNOTATION_CLASS_MODIFIER, classNameToClassInfo);
        if (this.annotationInfo == null) {
            this.annotationInfo = new AnnotationInfoList(2);
        }
        this.annotationInfo.add(classAnnotationInfo);

        this.addRelatedClass(RelType.CLASS_ANNOTATIONS, annotationClassInfo);
        annotationClassInfo.addRelatedClass(RelType.CLASSES_WITH_ANNOTATION, this);

        // Record use of @Inherited meta-annotation
        if (classAnnotationInfo.getName().equals(Inherited.class.getName())) {
            isInherited = true;
        }
    }

    /** Add field info. */
    void addFieldInfo(final FieldInfoList fieldInfoList, final Map<String, ClassInfo> classNameToClassInfo) {
        for (final FieldInfo fieldInfo : fieldInfoList) {
            final AnnotationInfoList fieldAnnotationInfoList = fieldInfo.annotationInfo;
            if (fieldAnnotationInfoList != null) {
                for (final AnnotationInfo fieldAnnotationInfo : fieldAnnotationInfoList) {
                    final ClassInfo annotationClassInfo = getOrCreateClassInfo(fieldAnnotationInfo.getName(),
                            ANNOTATION_CLASS_MODIFIER, classNameToClassInfo);
                    // Mark this class as having a field with this annotation
                    this.addRelatedClass(RelType.FIELD_ANNOTATIONS, annotationClassInfo);
                    annotationClassInfo.addRelatedClass(RelType.CLASSES_WITH_FIELD_ANNOTATION, this);
                }
            }
        }
        if (this.fieldInfo == null) {
            this.fieldInfo = fieldInfoList;
        } else {
            this.fieldInfo.addAll(fieldInfoList);
        }
    }

    /** Add method info. */
    void addMethodInfo(final MethodInfoList methodInfoList, final Map<String, ClassInfo> classNameToClassInfo) {
        for (final MethodInfo methodInfo : methodInfoList) {
            final AnnotationInfoList methodAnnotationInfoList = methodInfo.annotationInfo;
            if (methodAnnotationInfoList != null) {
                for (final AnnotationInfo methodAnnotationInfo : methodAnnotationInfoList) {
                    final ClassInfo annotationClassInfo = getOrCreateClassInfo(methodAnnotationInfo.getName(),
                            ANNOTATION_CLASS_MODIFIER, classNameToClassInfo);
                    // Mark this class as having a method with this annotation
                    this.addRelatedClass(RelType.METHOD_ANNOTATIONS, annotationClassInfo);
                    annotationClassInfo.addRelatedClass(RelType.CLASSES_WITH_METHOD_ANNOTATION, this);
                }
            }
            //    // Currently it is not possible to find methods by annotation parameter annotation
            //    final AnnotationInfo[][] methodParamAnnotationInfoList = methodInfo.parameterAnnotationInfo;
            //    if (methodParamAnnotationInfoList != null) {
            //        for (int i = 0; i < methodParamAnnotationInfoList.length; i++) {
            //            final AnnotationInfo[] paramAnnotationInfoArr = methodParamAnnotationInfoList[i];
            //            if (paramAnnotationInfoArr != null) {
            //                for (int j = 0; j < paramAnnotationInfoArr.length; j++) {
            //                    final AnnotationInfo methodParamAnnotationInfo = paramAnnotationInfoArr[j];
            //                    final ClassInfo annotationClassInfo = getOrCreateClassInfo(
            //                            methodParamAnnotationInfo.getName(), ANNOTATION_CLASS_MODIFIER,
            //                            classNameToClassInfo);
            //                    // Index parameter annotations here
            //                }
            //            }
            //        }
            //    }
        }
        if (this.methodInfo == null) {
            this.methodInfo = methodInfoList;
        } else {
            this.methodInfo.addAll(methodInfoList);
        }
    }

    /** Add the class type signature, including type params */
    void addTypeSignature(final String typeSignatureStr) {
        if (this.typeSignatureStr == null) {
            this.typeSignatureStr = typeSignatureStr;
        } else {
            if (typeSignatureStr != null && !this.typeSignatureStr.equals(typeSignatureStr)) {
                throw new RuntimeException("Trying to merge two classes with different type signatures for class "
                        + name + ": " + this.typeSignatureStr + " ; " + typeSignatureStr);
            }
        }
    }

    /**
     * Add annotation default values. (Only called in the case of annotation class definitions, when the annotation
     * has default parameter values.)
     */
    void addAnnotationParamDefaultValues(final List<AnnotationParameterValue> paramNamesAndValues) {
        if (this.annotationDefaultParamValues == null) {
            this.annotationDefaultParamValues = paramNamesAndValues;
        } else {
            this.annotationDefaultParamValues.addAll(paramNamesAndValues);
        }
    }

    /**
     * Add a class that has just been scanned (as opposed to just referenced by a scanned class). Not threadsafe,
     * should be run in single threaded context.
     */
    static ClassInfo addScannedClass(final String className, final int classModifiers, final boolean isInterface,
            final boolean isAnnotation, final Map<String, ClassInfo> classNameToClassInfo,
            final ClasspathElement classpathElement, final ScanSpec scanSpec, final LogNode log) {
        boolean classEncounteredMultipleTimes = false;
        ClassInfo classInfo = classNameToClassInfo.get(className);
        if (classInfo == null) {
            // This is the first time this class has been seen, add it
            classNameToClassInfo.put(className,
                    classInfo = new ClassInfo(className, classModifiers, /* isExternalClass = */ false));
        } else {
            if (!classInfo.isExternalClass) {
                classEncounteredMultipleTimes = true;
            }
        }

        // Remember which classpath element (zipfile / classpath root directory / module) the class was found in
        final ModuleRef modRef = classpathElement.getClasspathElementModuleRef();
        final File file = modRef != null ? null : classpathElement.getClasspathElementFile(log);
        if ((classInfo.moduleRef != null && modRef != null && !classInfo.moduleRef.equals(modRef))
                || (classInfo.classpathElementFile != null && file != null
                        && !classInfo.classpathElementFile.equals(file))) {
            classEncounteredMultipleTimes = true;
        }

        if (classEncounteredMultipleTimes) {
            // The same class was encountered more than once in a single jarfile -- should not happen. However,
            // actually there is no restriction for paths within a zipfile to be unique (!!), and in fact
            // zipfiles in the wild do contain the same classfiles multiple times with the same exact path,
            // e.g.: xmlbeans-2.6.0.jar!org/apache/xmlbeans/xml/stream/Location.class
            if (log != null) {
                log.log("Class " + className + " is defined in multiple different classpath elements or modules -- "
                        + "ClassInfo#getClasspathElementFile() and/or ClassInfo#getClasspathElementModuleRef "
                        + "will only return the first of these; attempting to merge info from all copies of "
                        + "the classfile");
            }
        }
        if (classInfo.classpathElementFile == null) {
            // If class was found in more than one classpath element, keep the first classpath element reference 
            classInfo.classpathElementFile = file;
            // Save jarfile package root, if any
            classInfo.jarfilePackageRoot = classpathElement.getJarfilePackageRoot();
        }
        if (classInfo.moduleRef == null) {
            // If class was found in more than one module, keep the first module reference 
            classInfo.moduleRef = modRef;
        }

        // Remember which classloader handles the class was found in, for classloading
        final ClassLoader[] classLoaders = classpathElement.getClassLoaders();
        if (classInfo.classLoaders == null) {
            classInfo.classLoaders = classLoaders;
        } else if (classLoaders != null && !Arrays.equals(classInfo.classLoaders, classLoaders)) {
            // Merge together ClassLoader list (concatenate and dedup)
            final LinkedHashSet<ClassLoader> allClassLoaders = new LinkedHashSet<>(
                    Arrays.asList(classInfo.classLoaders));
            for (final ClassLoader classLoader : classLoaders) {
                allClassLoaders.add(classLoader);
            }
            final List<ClassLoader> classLoaderOrder = new ArrayList<>(allClassLoaders);
            classInfo.classLoaders = classLoaderOrder.toArray(new ClassLoader[0]);
        }

        // Mark the classfile as scanned
        classInfo.isExternalClass = false;

        // Merge modifiers
        classInfo.modifiers |= classModifiers;
        classInfo.isInterface |= isInterface;
        classInfo.isAnnotation |= isAnnotation;

        return classInfo;
    }

    // -------------------------------------------------------------------------------------------------------------

    /** The class type to return. */
    private static enum ClassType {
        /** Get all class types. */
        ALL,
        /** A standard class (not an interface or annotation). */
        STANDARD_CLASS,
        /**
         * An interface (this is named "implemented interface" rather than just "interface" to distinguish it from
         * an annotation.)
         */
        IMPLEMENTED_INTERFACE,
        /** An annotation. */
        ANNOTATION,
        /** An interface or annotation (used since you can actually implement an annotation). */
        INTERFACE_OR_ANNOTATION,
    }

    /**
     * Filter classes according to scan spec and class type.
     * 
     * @param strictWhitelist
     *            If true, exclude class if it is is external, blacklisted, or a system class.
     */
    private static Set<ClassInfo> filterClassInfo(final Collection<ClassInfo> classes, final ScanSpec scanSpec,
            final boolean strictWhitelist, final ClassType... classTypes) {
        if (classes == null) {
            return null;
        }
        boolean includeAllTypes = classTypes.length == 0;
        boolean includeStandardClasses = false;
        boolean includeImplementedInterfaces = false;
        boolean includeAnnotations = false;
        for (final ClassType classType : classTypes) {
            switch (classType) {
            case ALL:
                includeAllTypes = true;
                break;
            case STANDARD_CLASS:
                includeStandardClasses = true;
                break;
            case IMPLEMENTED_INTERFACE:
                includeImplementedInterfaces = true;
                break;
            case ANNOTATION:
                includeAnnotations = true;
                break;
            case INTERFACE_OR_ANNOTATION:
                includeImplementedInterfaces = includeAnnotations = true;
                break;
            default:
                throw new RuntimeException("Unknown ClassType: " + classType);
            }
        }
        if (includeStandardClasses && includeImplementedInterfaces && includeAnnotations) {
            includeAllTypes = true;
        }
        final Set<ClassInfo> classInfoSetFiltered = new LinkedHashSet<>(classes.size());
        for (final ClassInfo classInfo : classes) {
            // Check class type against requested type(s)
            if (includeAllTypes //
                    || includeStandardClasses && classInfo.isStandardClass()
                    || includeImplementedInterfaces && classInfo.isImplementedInterface()
                    || includeAnnotations && classInfo.isAnnotation()) {
                if (
                // Always check blacklist 
                !scanSpec.classIsBlacklisted(classInfo.name)
                        // If not blacklisted, and strictWhitelist is false, add class
                        && (!strictWhitelist || (
                        // Don't include external classes unless enableExternalClasses is true
                        (!classInfo.isExternalClass || scanSpec.enableExternalClasses)
                                // If this is a system class, ignore blacklist unless the blanket blacklisting of
                                // all system jars or modules has been disabled, and this system class was specifically
                                // blacklisted by name
                                && (!scanSpec.blacklistSystemJarsOrModules
                                        || !JarUtils.isInSystemPackageOrModule(classInfo.name))))) {
                    // Class passed strict whitelist criteria
                    classInfoSetFiltered.add(classInfo);
                }
            }
        }
        return classInfoSetFiltered;
    }

    /**
     * A set of classes that indirectly reachable through a directed path, for a given relationship type, and a set
     * of classes that is directly related (only one relationship step away).
     */
    static class ReachableAndDirectlyRelatedClasses {
        final Set<ClassInfo> reachableClasses;
        final Set<ClassInfo> directlyRelatedClasses;

        private ReachableAndDirectlyRelatedClasses(final Set<ClassInfo> reachableClasses,
                final Set<ClassInfo> directlyRelatedClasses) {
            this.reachableClasses = reachableClasses;
            this.directlyRelatedClasses = directlyRelatedClasses;
        }
    }

    private static final ReachableAndDirectlyRelatedClasses NO_REACHABLE_CLASSES = //
            new ReachableAndDirectlyRelatedClasses(Collections.<ClassInfo> emptySet(),
                    Collections.<ClassInfo> emptySet());

    /**
     * Get the classes related to this one (the transitive closure) for the given relationship type, and those
     * directly related.
     */
    private ReachableAndDirectlyRelatedClasses filterClassInfo(final RelType relType, final boolean strictWhitelist,
            final ClassType... classTypes) {
        final Set<ClassInfo> directlyRelatedClasses = this.relatedClasses.get(relType);
        if (directlyRelatedClasses == null) {
            return NO_REACHABLE_CLASSES;
        }
        final Set<ClassInfo> reachableClasses = new LinkedHashSet<>(directlyRelatedClasses);
        if (relType == RelType.METHOD_ANNOTATIONS || relType == RelType.FIELD_ANNOTATIONS) {
            // For method and field annotations, need to change the RelType when finding meta-annotations
            for (final ClassInfo annotation : directlyRelatedClasses) {
                reachableClasses.addAll(
                        annotation.filterClassInfo(RelType.CLASS_ANNOTATIONS, strictWhitelist).reachableClasses);
            }
        } else if (relType == RelType.CLASSES_WITH_METHOD_ANNOTATION
                || relType == RelType.CLASSES_WITH_FIELD_ANNOTATION) {
            // If looking for meta-annotated methods or fields, need to find all meta-annotated annotations, then
            // look for the methods or fields that they annotate
            for (final ClassInfo subAnnotation : this.filterClassInfo(RelType.CLASSES_WITH_ANNOTATION,
                    strictWhitelist, ClassType.ANNOTATION).reachableClasses) {
                final Set<ClassInfo> annotatedClasses = subAnnotation.relatedClasses.get(relType);
                if (annotatedClasses != null) {
                    reachableClasses.addAll(annotatedClasses);
                }
            }
        } else {
            // For other relationship types, the reachable type stays the same over the transitive closure. Find the
            // transitive closure, breaking cycles where necessary.
            final LinkedList<ClassInfo> queue = new LinkedList<>();
            queue.addAll(directlyRelatedClasses);
            while (!queue.isEmpty()) {
                final ClassInfo head = queue.removeFirst();
                final Set<ClassInfo> headRelatedClasses = head.relatedClasses.get(relType);
                if (headRelatedClasses != null) {
                    for (final ClassInfo directlyReachableFromHead : headRelatedClasses) {
                        // Don't get in cycle
                        if (reachableClasses.add(directlyReachableFromHead)) {
                            queue.add(directlyReachableFromHead);
                        }
                    }
                }
            }
        }
        if (reachableClasses.isEmpty()) {
            return NO_REACHABLE_CLASSES;
        }

        // Special case -- don't inherit java.lang.annotation.* meta-annotations as related meta-annotations
        // (but still return them as direct meta-annotations on annotation classes).
        Set<ClassInfo> javaLangAnnotationRelatedClasses = null;
        for (final ClassInfo classInfo : reachableClasses) {
            if (classInfo.getName().startsWith("java.lang.annotation.")) {
                if (javaLangAnnotationRelatedClasses == null) {
                    javaLangAnnotationRelatedClasses = new HashSet<>();
                }
                javaLangAnnotationRelatedClasses.add(classInfo);
            }
        }
        if (javaLangAnnotationRelatedClasses != null) {
            // Remove all java.lang.annotation annotations that are not directly related to this class
            Set<ClassInfo> javaLangAnnotationDirectlyRelatedClasses = null;
            for (final ClassInfo classInfo : directlyRelatedClasses) {
                if (classInfo.getName().startsWith("java.lang.annotation.")) {
                    if (javaLangAnnotationDirectlyRelatedClasses == null) {
                        javaLangAnnotationDirectlyRelatedClasses = new HashSet<>();
                    }
                    javaLangAnnotationDirectlyRelatedClasses.add(classInfo);
                }
            }
            if (javaLangAnnotationDirectlyRelatedClasses != null) {
                javaLangAnnotationRelatedClasses.removeAll(javaLangAnnotationDirectlyRelatedClasses);
            }
            reachableClasses.removeAll(javaLangAnnotationRelatedClasses);
        }

        return new ReachableAndDirectlyRelatedClasses(
                filterClassInfo(reachableClasses, scanResult.scanSpec, strictWhitelist),
                filterClassInfo(directlyRelatedClasses, scanResult.scanSpec, strictWhitelist));
    }

    // -------------------------------------------------------------------------------------------------------------

    /**
     * Get all classes found during the scan.
     *
     * @return A list of all classes found during the scan, or the empty list if none.
     */
    static ClassInfoList getAllClasses(final Collection<ClassInfo> classes, final ScanSpec scanSpec,
            final ScanResult scanResult) {
        return new ClassInfoList(
                ClassInfo.filterClassInfo(classes, scanSpec, /* strictWhitelist = */ true, ClassType.ALL),
                /* sortByName = */ true);
    }

    /**
     * Get all standard classes found during the scan.
     *
     * @return A list of all standard classes found during the scan, or the empty list if none.
     */
    static ClassInfoList getAllStandardClasses(final Collection<ClassInfo> classes, final ScanSpec scanSpec,
            final ScanResult scanResult) {
        return new ClassInfoList(ClassInfo.filterClassInfo(classes, scanSpec, /* strictWhitelist = */ true,
                ClassType.STANDARD_CLASS), /* sortByName = */ true);
    }

    /**
     * Get all implemented interface (non-annotation interface) classes found during the scan.
     *
     * @return A list of all annotation classes found during the scan, or the empty list if none.
     */
    static ClassInfoList getAllImplementedInterfaceClasses(final Collection<ClassInfo> classes,
            final ScanSpec scanSpec, final ScanResult scanResult) {
        return new ClassInfoList(ClassInfo.filterClassInfo(classes, scanSpec, /* strictWhitelist = */ true,
                ClassType.IMPLEMENTED_INTERFACE), /* sortByName = */ true);
    }

    /**
     * Get all annotation classes found during the scan. See also {@link #getAllInterfaceOrAnnotationClasses()}.
     *
     * @return A list of all annotation classes found during the scan, or the empty list if none.
     */
    static ClassInfoList getAllAnnotationClasses(final Collection<ClassInfo> classes, final ScanSpec scanSpec,
            final ScanResult scanResult) {
        return new ClassInfoList(
                ClassInfo.filterClassInfo(classes, scanSpec, /* strictWhitelist = */ true, ClassType.ANNOTATION),
                /* sortByName = */ true);
    }

    /**
     * Get all interface or annotation classes found during the scan. (Annotations are technically interfaces, and
     * they can be implemented.)
     *
     * @return A list of all whitelisted interfaces found during the scan, or the empty list if none.
     */
    static ClassInfoList getAllInterfacesOrAnnotationClasses(final Collection<ClassInfo> classes,
            final ScanSpec scanSpec, final ScanResult scanResult) {
        return new ClassInfoList(ClassInfo.filterClassInfo(classes, scanSpec, /* strictWhitelist = */ true,
                ClassType.INTERFACE_OR_ANNOTATION), /* sortByName = */ true);
    }

    // -------------------------------------------------------------------------------------------------------------
    // Predicates

    /** @return The name of the class. */
    public String getName() {
        return name;
    }

    /**
     * @return true if this class is an external class, i.e. was referenced by a whitelisted class as a superclass,
     *         interface, or annotation, but is not itself a whitelisted class.
     */
    public boolean isExternalClass() {
        return isExternalClass;
    }

    /**
     * @return The class modifier bits, e.g. {@link Modifier#PUBLIC}.
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * @return The field modifiers as a string, e.g. "public static final". For the modifier bits, call
     *         {@link #getModifiers()}.
     */
    public String getModifiersStr() {
        return TypeUtils.modifiersToString(modifiers, /* isMethod = */ false);
    }

    /**
     * @return true if this class is a public class.
     */
    public boolean isPublic() {
        return (modifiers & Modifier.PUBLIC) != 0;
    }

    /**
     * @return true if this class is an abstract class.
     */
    public boolean isAbstract() {
        return (modifiers & 0x400) != 0;
    }

    /**
     * @return true if this class is a synthetic class.
     */
    public boolean isSynthetic() {
        return (modifiers & 0x1000) != 0;
    }

    /**
     * @return true if this class is a final class.
     */
    public boolean isFinal() {
        return (modifiers & Modifier.FINAL) != 0;
    }

    /**
     * @return true if this class is static.
     */
    public boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }

    /**
     * @return true if this class is an annotation class.
     */
    public boolean isAnnotation() {
        return isAnnotation;
    }

    /**
     * @return true if this class is an interface and is not an annotation (annotations are interfaces, and can be
     *         implemented).
     */
    public boolean isInterface() {
        return isInterface && !isAnnotation;
    }

    /**
     * @return true if this class is an interface or an annotation (annotations are interfaces, and can be
     *         implemented).
     */
    public boolean isInterfaceOrAnnotation() {
        return isInterface;
    }

    /**
     * @return true if this class is an {@link Enum}.
     */
    public boolean isEnum() {
        return (modifiers & 0x4000) != 0;
    }

    /**
     * @return true if this class is a standard class (i.e. is not an annotation or interface).
     */
    public boolean isStandardClass() {
        return !(isAnnotation || isInterface);
    }

    /**
     * @param superclassName
     *            The name of a superclass.
     * @return true if this class extends the named superclass.
     */
    public boolean extendsSuperclass(final String superclassName) {
        return getSuperclasses().containsName(superclassName);
    }

    /**
     * @return true if this is an inner class (call {@link #isAnonymousInnerClass()} to test if this is an anonymous
     *         inner class). If true, the containing class can be determined by calling {@link #getOuterClasses()}.
     */
    public boolean isInnerClass() {
        return !getOuterClasses().isEmpty();
    }

    /**
     * @return true if this class contains inner classes. If true, the inner classes can be determined by calling
     *         {@link #getInnerClasses()}.
     */
    public boolean isOuterClass() {
        return !getInnerClasses().isEmpty();
    }

    /**
     * @return true if this is an anonymous inner class. If true, the name of the containing method can be obtained
     *         by calling {@link #getFullyQualifiedDefiningMethodName()}.
     */
    public boolean isAnonymousInnerClass() {
        return fullyQualifiedDefiningMethodName != null;
    }

    /**
     * Return whether this class is an implemented interface (meaning a standard, non-annotation interface, or an
     * annotation that has also been implemented as an interface by some class).
     *
     * <p>
     * Annotations are interfaces, but you can also implement an annotation, so to we return whether an interface
     * (even an annotation) is implemented by a class or extended by a subinterface, or (failing that) if it is not
     * an interface but not an annotation.
     *
     * @return true if this class is an implemented interface.
     */
    public boolean isImplementedInterface() {
        return relatedClasses.get(RelType.CLASSES_IMPLEMENTING) != null || (isInterface && !isAnnotation);
    }

    /**
     * @param interfaceName
     *            The name of an interface.
     * @return true if this class implements the named interface.
     */
    public boolean implementsInterface(final String interfaceName) {
        return getInterfaces().containsName(interfaceName);
    }

    /**
     * @param annotationName
     *            The name of an annotation.
     * @return true if this class has the named annotation.
     */
    public boolean hasAnnotation(final String annotationName) {
        return getAnnotations().containsName(annotationName);
    }

    /**
     * @param fieldName
     *            The name of a field.
     * @return true if this class has the named field.
     */
    public boolean hasField(final String fieldName) {
        return getFieldInfo().containsName(fieldName);
    }

    /**
     * @param fieldAnnotationName
     *            The name of a field annotation.
     * @return true if this class has a field with the named annotation.
     */
    public boolean hasFieldAnnotation(final String fieldAnnotationName) {
        for (final FieldInfo fieldInfo : getFieldInfo()) {
            if (fieldInfo.getAnnotationInfo().containsName(fieldAnnotationName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param methodName
     *            The name of a method.
     * @return true if this class has a method of the requested name.
     */
    public boolean hasMethod(final String methodName) {
        return getMethodInfo().containsName(methodName);
    }

    /**
     * @param methodAnnotationName
     *            The name of a mehtod annotation.
     * @return true if this class has a method with the named annotation.
     */
    public boolean hasMethodAnnotation(final String methodAnnotationName) {
        for (final MethodInfo methodInfo : getMethodInfo()) {
            if (methodInfo.getAnnotationInfo().containsName(methodAnnotationName)) {
                return true;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------------------------------------------
    // Standard classes

    /**
     * Get the subclasses of this class, sorted in order of name. Call {@link ClassInfoList#directOnly()} to get
     * direct subclasses.
     *
     * @return the list of subclasses of this class, or the empty list if none.
     */
    public ClassInfoList getSubclasses() {
        if (getName().equals("java.lang.Object")) {
            // Make an exception for querying all subclasses of java.lang.Object
            return scanResult.getAllClasses();
        } else {
            return new ClassInfoList(this.filterClassInfo(RelType.SUBCLASSES, /* strictWhitelist = */ true),
                    /* sortByName = */ true);
        }
    }

    /**
     * Get all superclasses of this class, in ascending order in the class hierarchy. Does not include
     * superinterfaces, if this is an interface (use {@link #getInterfaces()} to get superinterfaces of an
     * interface.}
     *
     * @return the list of all superclasses of this class, or the empty list if none.
     */
    public ClassInfoList getSuperclasses() {
        return new ClassInfoList(this.filterClassInfo(RelType.SUPERCLASSES, /* strictWhitelist = */ false),
                /* sortByName = */ false);
    }

    /**
     * Get the single direct superclass of this class, or null if none. Does not return the superinterfaces, if this
     * is an interface (use {@link #getInterfaces()} to get superinterfaces of an interface.}
     *
     * @return the superclass of this class, or null if none.
     */
    public ClassInfo getSuperclass() {
        final Set<ClassInfo> superClasses = relatedClasses.get(RelType.SUPERCLASSES);
        if (superClasses == null || superClasses.isEmpty()) {
            return null;
        } else if (superClasses.size() > 2) {
            throw new IllegalArgumentException("More than one superclass: " + superClasses);
        } else {
            final ClassInfo superclass = superClasses.iterator().next();
            if (superclass.getName().equals("java.lang.Object")) {
                return null;
            } else {
                return superclass;
            }
        }
    }

    /**
     * @return A list of the containing outer classes, if this is an inner class, otherwise the empty list. Note
     *         that all containing outer classes are returned, not just the innermost of the containing outer
     *         classes.
     */
    public ClassInfoList getOuterClasses() {
        return new ClassInfoList(
                this.filterClassInfo(RelType.CONTAINED_WITHIN_OUTER_CLASS, /* strictWhitelist = */ false),
                /* sortByName = */ false);
    }

    /**
     * @return A list of the inner classes contained within this class, or the empty list if none.
     */
    public ClassInfoList getInnerClasses() {
        return new ClassInfoList(this.filterClassInfo(RelType.CONTAINS_INNER_CLASS, /* strictWhitelist = */ false),
                /* sortByName = */ true);
    }

    /**
     * @return The fully-qualified method name (i.e. fully qualified classname, followed by dot, followed by method
     *         name) for the defining method, if this is an anonymous inner class, or null if not.
     */
    public String getFullyQualifiedDefiningMethodName() {
        return fullyQualifiedDefiningMethodName;
    }

    // -------------------------------------------------------------------------------------------------------------
    // Interfaces

    /**
     * @return The list of interfaces implemented by this class or by one of its superclasses, if this is a standard
     *         class, or the superinterfaces extended by this interface, if this is an interface. Returns the empty
     *         list if none.
     */
    public ClassInfoList getInterfaces() {
        // Classes also implement the interfaces of their superclasses
        final ReachableAndDirectlyRelatedClasses implementedInterfaces = this
                .filterClassInfo(RelType.IMPLEMENTED_INTERFACES, /* strictWhitelist = */ false);
        final Set<ClassInfo> allInterfaces = new HashSet<>(implementedInterfaces.reachableClasses);
        for (final ClassInfo superclass : this.filterClassInfo(RelType.SUPERCLASSES,
                /* strictWhitelist = */ false).reachableClasses) {
            final Set<ClassInfo> superclassImplementedInterfaces = superclass.filterClassInfo(
                    RelType.IMPLEMENTED_INTERFACES, /* strictWhitelist = */ false).reachableClasses;
            allInterfaces.addAll(superclassImplementedInterfaces);
        }
        return new ClassInfoList(allInterfaces, implementedInterfaces.directlyRelatedClasses,
                /* sortByName = */ true);
    }

    /**
     * @return the list of the classes (and their subclasses) that implement this interface, if this is an
     *         interface, otherwise returns the empty list.
     */
    public ClassInfoList getClassesImplementing() {
        if (!isInterface) {
            throw new IllegalArgumentException("Class is not an interface: " + getName());
        }
        // Subclasses of implementing classes also implement the interface
        final ReachableAndDirectlyRelatedClasses implementingClasses = this
                .filterClassInfo(RelType.CLASSES_IMPLEMENTING, /* strictWhitelist = */ true);
        final Set<ClassInfo> allImplementingClasses = new HashSet<>(implementingClasses.reachableClasses);
        for (final ClassInfo implementingClass : implementingClasses.reachableClasses) {
            final Set<ClassInfo> implementingSubclasses = implementingClass.filterClassInfo(RelType.SUBCLASSES,
                    /* strictWhitelist = */ true).reachableClasses;
            allImplementingClasses.addAll(implementingSubclasses);
        }
        return new ClassInfoList(allImplementingClasses, implementingClasses.directlyRelatedClasses,
                /* sortByName = */ true);
    }

    // -------------------------------------------------------------------------------------------------------------
    // Annotations

    /**
     * Get the annotations and meta-annotations on this class. (Call {@link #getAnnotationInfo()} instead, if you
     * need the parameter values of annotations, rather than just the annotation classes.)
     * 
     * <p>
     * Also handles the {@link Inherited} meta-annotation, which causes an annotation to annotate a class and all of
     * its subclasses.
     *
     * @return the list of annotations and meta-annotations on this class.
     */
    public ClassInfoList getAnnotations() {
        if (!scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
        }

        // Get all annotations on this class
        final ReachableAndDirectlyRelatedClasses annotationClasses = this.filterClassInfo(RelType.CLASS_ANNOTATIONS,
                /* strictWhitelist = */ false);

        // Check for any @Inherited annotations on superclasses
        Set<ClassInfo> inheritedSuperclassAnnotations = null;
        for (final ClassInfo superclass : getSuperclasses()) {
            for (final ClassInfo superclassAnnotationClass : superclass.filterClassInfo(RelType.CLASS_ANNOTATIONS,
                    /* strictWhitelist = */ false).reachableClasses) {
                final Set<ClassInfo> superclassAnnotations = superclassAnnotationClass.relatedClasses
                        .get(RelType.CLASS_ANNOTATIONS);
                if (superclassAnnotations != null) {
                    // Check if any of the meta-annotations on this annotation are @Inherited,
                    // which causes an annotation to annotate a class and all of its subclasses.
                    if (isInherited) {
                        // inheritedSuperclassAnnotations is an inherited annotation
                        if (inheritedSuperclassAnnotations == null) {
                            inheritedSuperclassAnnotations = new HashSet<>();
                        }
                        inheritedSuperclassAnnotations.add(superclassAnnotationClass);
                    }
                }
            }
        }

        if (inheritedSuperclassAnnotations == null) {
            // No inherited superclass annotations
            return new ClassInfoList(annotationClasses, /* sortByName = */ true);
        } else {
            // Merge inherited superclass annotations and annotations on this class
            inheritedSuperclassAnnotations.addAll(annotationClasses.reachableClasses);
            return new ClassInfoList(inheritedSuperclassAnnotations, annotationClasses.directlyRelatedClasses,
                    /* sortByName = */ true);
        }
    }

    /**
     * Get a list of direct annotations on this method, along with any annotation parameter values, as a list of
     * {@link AnnotationInfo} objects, or the empty list if none.
     * 
     * <p>
     * Also handles the {@link Inherited} meta-annotation, which causes an annotation to annotate a class and all of
     * its subclasses.
     * 
     * @return A list of {@link AnnotationInfo} objects for the annotations on this method, or the empty list if
     *         none.
     */
    public AnnotationInfoList getAnnotationInfo() {
        if (!scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
        }
        // Check for any @Inherited annotations on superclasses
        AnnotationInfoList inheritedSuperclassAnnotations = null;
        for (final ClassInfo superclass : getSuperclasses()) {
            for (final AnnotationInfo superclassAnnotationInfo : superclass.getAnnotationInfo()) {
                if (superclassAnnotationInfo.isInherited()) {
                    // inheritedSuperclassAnnotations is an inherited annotation
                    if (inheritedSuperclassAnnotations == null) {
                        inheritedSuperclassAnnotations = new AnnotationInfoList();
                    }
                    inheritedSuperclassAnnotations.add(superclassAnnotationInfo);
                }
            }
        }
        if (inheritedSuperclassAnnotations == null) {
            // No inherited superclass annotations
            return annotationInfo == null ? AnnotationInfoList.EMPTY_LIST : annotationInfo;
        } else {
            // Merge inherited superclass annotations and annotations on this class
            if (annotationInfo != null) {
                inheritedSuperclassAnnotations.addAll(annotationInfo);
            }
            Collections.sort(inheritedSuperclassAnnotations);
            return inheritedSuperclassAnnotations;
        }
    }

    /**
     * @return A list of {@link AnnotationParameterValue} objects for each of the default parameter values for this
     *         annotation, if this is an annotation class with default parameter values, otherwise the empty list.
     */
    public List<AnnotationParameterValue> getAnnotationDefaultParameterValues() {
        if (!scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
        }
        if (!isAnnotation) {
            throw new IllegalArgumentException("Class is not an annotation: " + getName());
        }
        return annotationDefaultParamValues == null ? Collections.<AnnotationParameterValue> emptyList()
                : annotationDefaultParamValues;
    }

    /**
     * @return A list of standard classes and non-annotation interfaces that are annotated by this class, if this is
     *         an annotation class, or the empty list if none. Also handles the {@link Inherited} meta-annotation,
     *         which causes an annotation on a class to be inherited by all of its subclasses.
     */
    public ClassInfoList getClassesWithAnnotation() {
        if (!scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableAnnotationInfo() before #scan()");
        }
        if (!isAnnotation) {
            throw new IllegalArgumentException("Class is not an annotation: " + getName());
        }

        // Get classes that have this annotation
        final ReachableAndDirectlyRelatedClasses classesWithAnnotation = this
                .filterClassInfo(RelType.CLASSES_WITH_ANNOTATION, /* strictWhitelist = */ true);

        if (isInherited) {
            // If this is an inherited annotation, add into the result all subclasses of the annotated classes. 
            final Set<ClassInfo> classesWithAnnotationAndTheirSubclasses = new HashSet<>(
                    classesWithAnnotation.reachableClasses);
            for (final ClassInfo classWithAnnotation : classesWithAnnotation.reachableClasses) {
                classesWithAnnotationAndTheirSubclasses.addAll(classWithAnnotation.getSubclasses());
            }
            return new ClassInfoList(classesWithAnnotationAndTheirSubclasses,
                    classesWithAnnotation.directlyRelatedClasses, /* sortByName = */ true);
        } else {
            // If not inherited, only return the annotated classes
            return new ClassInfoList(classesWithAnnotation, /* sortByName = */ true);
        }
    }

    /**
     * @return The list of classes that are directly (i.e. are not meta-annotated) annotated with the requested
     *         annotation, or the empty list if none.
     */
    ClassInfoList getClassesWithAnnotationDirectOnly() {
        return new ClassInfoList(
                this.filterClassInfo(RelType.CLASSES_WITH_ANNOTATION, /* strictWhitelist = */ true),
                /* sortByName = */ true);
    }

    // -------------------------------------------------------------------------------------------------------------
    // Methods

    /**
     * Returns information on visible methods of the class that are not constructors. (Call
     * {@link #getMethodAndConstructorInfo()} if you need methods and constructors.) There may be more than one
     * method of a given name with different type signatures, due to overloading.
     *
     * <p>
     * Requires that {@link ClassGraph#enableMethodInfo()} be called before scanning, otherwise throws
     * {@link IllegalArgumentException}.
     *
     * <p>
     * By default only returns information for public methods, unless {@link ClassGraph#ignoreMethodVisibility()}
     * was called before the scan.
     *
     * @return the list of {@link MethodInfo} objects for visible methods of this class, or the empty list if no
     *         methods were found.
     * @throws IllegalArgumentException
     *             if {@link ClassGraph#enableMethodInfo()} was not called prior to initiating the scan.
     */
    public MethodInfoList getMethodInfo() {
        if (!scanResult.scanSpec.enableMethodInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableMethodInfo() before #scan()");
        }
        if (methodInfo == null) {
            return MethodInfoList.EMPTY_LIST;
        } else {
            final MethodInfoList nonConstructorMethods = new MethodInfoList();
            for (final MethodInfo mi : methodInfo) {
                final String methodName = mi.getName();
                if (!methodName.equals("<init>") && !methodName.equals("<clinit>")) {
                    nonConstructorMethods.add(mi);
                }
            }
            return nonConstructorMethods;
        }
    }

    /**
     * Returns information on visible constructors of the class. Constructors have the method name of
     * {@code "<init>"}. There may be more than one constructor of a given name with different type signatures, due
     * to overloading.
     *
     * <p>
     * Requires that {@link ClassGraph#enableMethodInfo()} be called before scanning, otherwise throws
     * {@link IllegalArgumentException}.
     *
     * <p>
     * By default only returns information for public constructors, unless
     * {@link ClassGraph#ignoreMethodVisibility()} was called before the scan.
     *
     * @return the list of {@link MethodInfo} objects for visible constructors of this class, or the empty list if
     *         no constructors were found or visible.
     * @throws IllegalArgumentException
     *             if {@link ClassGraph#enableMethodInfo()} was not called prior to initiating the scan.
     */
    public MethodInfoList getConstructorInfo() {
        if (!scanResult.scanSpec.enableMethodInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableMethodInfo() before #scan()");
        }
        if (methodInfo == null) {
            return MethodInfoList.EMPTY_LIST;
        } else {
            final MethodInfoList nonConstructorMethods = new MethodInfoList();
            for (final MethodInfo mi : methodInfo) {
                final String methodName = mi.getName();
                if (methodName.equals("<init>")) {
                    nonConstructorMethods.add(mi);
                }
            }
            return nonConstructorMethods;
        }
    }

    /**
     * Returns information on visible methods and constructors of the class. There may be more than one method or
     * constructor or method of a given name with different type signatures, due to overloading. Constructors have
     * the method name of {@code "<init>"} and static initializer blocks have the name of {@code "<clinit>"}.
     *
     * <p>
     * Requires that {@link ClassGraph#enableMethodInfo()} be called before scanning, otherwise throws
     * {@link IllegalArgumentException}.
     *
     * <p>
     * By default only returns information for public methods and constructors, unless
     * {@link ClassGraph#ignoreMethodVisibility()} was called before the scan. If method visibility is ignored, the
     * result may include a reference to a private static class initializer block, with a method name of
     * {@code "<clinit>"}.
     *
     * @return the list of {@link MethodInfo} objects for visible methods and constructors of this class, or the
     *         empty list if no methods or constructors were found or visible.
     * @throws IllegalArgumentException
     *             if {@link ClassGraph#enableMethodInfo()} was not called prior to initiating the scan.
     */
    public MethodInfoList getMethodAndConstructorInfo() {
        if (!scanResult.scanSpec.enableMethodInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableMethodInfo() before #scan()");
        }
        return methodInfo == null ? MethodInfoList.EMPTY_LIST : methodInfo;
    }

    /**
     * Returns information on the method(s) or constructor(s) of the class with the given method name. Constructors
     * have the method name of {@code "<init>"}.
     *
     * <p>
     * Requires that {@link ClassGraph#enableMethodInfo()} be called before scanning, otherwise throws
     * {@link IllegalArgumentException}.
     *
     * <p>
     * By default only returns information for public methods, unless {@link ClassGraph#ignoreMethodVisibility()}
     * was called before the scan.
     *
     * <p>
     * May return info for multiple methods with the same name (with different type signatures).
     *
     * @param methodName
     *            The method name to query.
     * @return a list of {@link MethodInfo} objects for the method(s) with the given name, or the empty list if the
     *         method was not found in this class (or is not visible).
     * @throws IllegalArgumentException
     *             if {@link ClassGraph#enableMethodInfo()} was not called prior to initiating the scan.
     */
    public MethodInfoList getMethodInfo(final String methodName) {
        if (!scanResult.scanSpec.enableMethodInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableMethodInfo() before #scan()");
        }
        if (methodInfo == null) {
            return MethodInfoList.EMPTY_LIST;
        }
        boolean hasMethodWithName = false;
        for (final MethodInfo f : methodInfo) {
            if (f.getName().equals(methodName)) {
                hasMethodWithName = true;
                break;
            }
        }
        if (!hasMethodWithName) {
            return MethodInfoList.EMPTY_LIST;
        }
        final MethodInfoList methodInfoList = new MethodInfoList();
        for (final MethodInfo f : methodInfo) {
            if (f.getName().equals(methodName)) {
                methodInfoList.add(f);
            }
        }
        return methodInfoList;
    }

    /**
     * @return A list of method annotations or meta-annotations on this class, as a list of {@link ClassInfo}
     *         objects, or the empty list if none. N.B. these annotations do not contain specific annotation
     *         parameters -- call {@link MethodInfo#getAnnotationInfo()} to get details on specific method
     *         annotation instances.
     */
    public ClassInfoList getMethodAnnotations() {
        if (!scanResult.scanSpec.enableMethodInfo || !scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException(
                    "Please call ClassGraph#enableMethodInfo() and " + "#enableAnnotationInfo() before #scan()");
        }
        final ReachableAndDirectlyRelatedClasses methodAnnotations = this
                .filterClassInfo(RelType.METHOD_ANNOTATIONS, /* strictWhitelist = */ false, ClassType.ANNOTATION);
        final Set<ClassInfo> methodAnnotationsAndMetaAnnotations = new HashSet<>(
                methodAnnotations.reachableClasses);
        for (final ClassInfo methodAnnotation : methodAnnotations.reachableClasses) {
            methodAnnotationsAndMetaAnnotations.addAll(methodAnnotation.filterClassInfo(RelType.CLASS_ANNOTATIONS,
                    /* strictWhitelist = */ false).reachableClasses);
        }
        return new ClassInfoList(methodAnnotationsAndMetaAnnotations, methodAnnotations.directlyRelatedClasses,
                /* sortByName = */ true);
    }

    /**
     * @return A list of classes that have a method with this annotation or meta-annotation, or the empty list if
     *         none.
     */
    public ClassInfoList getClassesWithMethodAnnotation() {
        if (!scanResult.scanSpec.enableMethodInfo || !scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException(
                    "Please call ClassGraph#enableMethodInfo() and " + "#enableAnnotationInfo() before #scan()");
        }
        final ReachableAndDirectlyRelatedClasses classesWithDirectlyAnnotatedMethods = this
                .filterClassInfo(RelType.CLASSES_WITH_METHOD_ANNOTATION, /* strictWhitelist = */ true);
        final ReachableAndDirectlyRelatedClasses annotationsWithThisMetaAnnotation = this.filterClassInfo(
                RelType.CLASSES_WITH_ANNOTATION, /* strictWhitelist = */ false, ClassType.ANNOTATION);
        if (annotationsWithThisMetaAnnotation.reachableClasses.isEmpty()) {
            // This annotation does not meta-annotate another annotation that annotates a method
            return new ClassInfoList(classesWithDirectlyAnnotatedMethods, /* sortByName = */ true);
        } else {
            // Take the union of all classes with methods directly annotated by this annotation,
            // and classes with methods meta-annotated by this annotation
            final Set<ClassInfo> allClassesWithAnnotatedOrMetaAnnotatedMethods = new HashSet<>(
                    classesWithDirectlyAnnotatedMethods.reachableClasses);
            for (final ClassInfo metaAnnotatedAnnotation : annotationsWithThisMetaAnnotation.reachableClasses) {
                allClassesWithAnnotatedOrMetaAnnotatedMethods
                        .addAll(metaAnnotatedAnnotation.filterClassInfo(RelType.CLASSES_WITH_METHOD_ANNOTATION,
                                /* strictWhitelist = */ true).reachableClasses);
            }
            return new ClassInfoList(allClassesWithAnnotatedOrMetaAnnotatedMethods,
                    classesWithDirectlyAnnotatedMethods.directlyRelatedClasses, /* sortByName = */ true);
        }
    }

    /**
     * @return A list of classes that have methods that are directly annotated (i.e. are not meta-annotated) with
     *         the requested method annotation, or the empty list if none.
     */
    ClassInfoList getClassesWithMethodAnnotationDirectOnly() {
        return new ClassInfoList(
                this.filterClassInfo(RelType.CLASSES_WITH_METHOD_ANNOTATION, /* strictWhitelist = */ true),
                /* sortByName = */ true);
    }

    // -------------------------------------------------------------------------------------------------------------
    // Fields

    /**
     * Returns information on all visible fields of the class.
     *
     * <p>
     * Requires that {@link ClassGraph#enableFieldInfo()} be called before scanning, otherwise throws
     * {@link IllegalArgumentException}.
     *
     * <p>
     * By default only returns information for public methods, unless {@link ClassGraph#ignoreFieldVisibility()} was
     * called before the scan.
     *
     * @return the list of FieldInfo objects for visible fields of this class, or the empty list if no fields were
     *         found or visible.
     * @throws IllegalArgumentException
     *             if {@link ClassGraph#enableFieldInfo()} was not called prior to initiating the scan.
     */
    public FieldInfoList getFieldInfo() {
        if (!scanResult.scanSpec.enableFieldInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableFieldInfo() before #scan()");
        }
        return fieldInfo == null ? FieldInfoList.EMPTY_LIST : fieldInfo;
    }

    /**
     * Returns information on a given visible field of the class.
     *
     * <p>
     * Requires that {@link ClassGraph#enableFieldInfo()} be called before scanning, otherwise throws
     * {@link IllegalArgumentException}.
     *
     * <p>
     * By default only returns information for public fields, unless {@link ClassGraph#ignoreFieldVisibility()} was
     * called before the scan.
     *
     * @param fieldName
     *            The field name to query.
     * @return the {@link FieldInfo} object for the named field, or null if the field was not found in this class
     *         (or is not visible).
     * @throws IllegalArgumentException
     *             if {@link ClassGraph#enableFieldInfo()} was not called prior to initiating the scan.
     */
    public FieldInfo getFieldInfo(final String fieldName) {
        if (!scanResult.scanSpec.enableFieldInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableFieldInfo() before #scan()");
        }
        if (fieldInfo == null) {
            return null;
        }
        if (fieldNameToFieldInfo == null) {
            // Lazily build reverse mapping cache
            fieldNameToFieldInfo = new HashMap<>();
            for (final FieldInfo f : fieldInfo) {
                fieldNameToFieldInfo.put(f.getName(), f);
            }
        }
        return fieldNameToFieldInfo.get(fieldName);
    }

    /**
     * @return A list of field annotations on this class, or the empty list if none. N.B. these annotations do not
     *         contain specific annotation parameters -- call {@link FieldInfo#getAnnotationInfo()} to get details
     *         on specific field annotation instances.
     */
    public ClassInfoList getFieldAnnotations() {
        if (!scanResult.scanSpec.enableFieldInfo || !scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableFieldInfo() and "
                    + "ClassGraph#enableAnnotationInfo() before #scan()");
        }
        final ReachableAndDirectlyRelatedClasses fieldAnnotations = this.filterClassInfo(RelType.FIELD_ANNOTATIONS,
                /* strictWhitelist = */ false, ClassType.ANNOTATION);
        final Set<ClassInfo> fieldAnnotationsAndMetaAnnotations = new HashSet<>(fieldAnnotations.reachableClasses);
        for (final ClassInfo fieldAnnotation : fieldAnnotations.reachableClasses) {
            fieldAnnotationsAndMetaAnnotations.addAll(fieldAnnotation.filterClassInfo(RelType.CLASS_ANNOTATIONS,
                    /* strictWhitelist = */ false).reachableClasses);
        }
        return new ClassInfoList(fieldAnnotationsAndMetaAnnotations, fieldAnnotations.directlyRelatedClasses,
                /* sortByName = */ true);
    }

    /**
     * @return A list of classes that have a field with this annotation or meta-annotation, or the empty list if
     *         none.
     */
    public ClassInfoList getClassesWithFieldAnnotation() {
        if (!scanResult.scanSpec.enableFieldInfo || !scanResult.scanSpec.enableAnnotationInfo) {
            throw new IllegalArgumentException("Please call ClassGraph#enableFieldInfo() and "
                    + "ClassGraph#enableAnnotationInfo() before #scan()");
        }
        final ReachableAndDirectlyRelatedClasses classesWithDirectlyAnnotatedFields = this
                .filterClassInfo(RelType.CLASSES_WITH_FIELD_ANNOTATION, /* strictWhitelist = */ true);
        final ReachableAndDirectlyRelatedClasses annotationsWithThisMetaAnnotation = this.filterClassInfo(
                RelType.CLASSES_WITH_ANNOTATION, /* strictWhitelist = */ false, ClassType.ANNOTATION);
        if (annotationsWithThisMetaAnnotation.reachableClasses.isEmpty()) {
            // This annotation does not meta-annotate another annotation that annotates a field
            return new ClassInfoList(classesWithDirectlyAnnotatedFields, /* sortByName = */ true);
        } else {
            // Take the union of all classes with fields directly annotated by this annotation,
            // and classes with fields meta-annotated by this annotation
            final Set<ClassInfo> allClassesWithAnnotatedOrMetaAnnotatedFields = new HashSet<>(
                    classesWithDirectlyAnnotatedFields.reachableClasses);
            for (final ClassInfo metaAnnotatedAnnotation : annotationsWithThisMetaAnnotation.reachableClasses) {
                allClassesWithAnnotatedOrMetaAnnotatedFields
                        .addAll(metaAnnotatedAnnotation.filterClassInfo(RelType.CLASSES_WITH_FIELD_ANNOTATION,
                                /* strictWhitelist = */ true).reachableClasses);
            }
            return new ClassInfoList(allClassesWithAnnotatedOrMetaAnnotatedFields,
                    classesWithDirectlyAnnotatedFields.directlyRelatedClasses, /* sortByName = */ true);
        }
    }

    /**
     * @return A list of classes that have fields that are directly annotated (i.e. are not meta-annotated) with the
     *         requested method annotation, or the empty list if none.
     */
    ClassInfoList getClassesWithFieldAnnotationDirectOnly() {
        return new ClassInfoList(
                this.filterClassInfo(RelType.CLASSES_WITH_FIELD_ANNOTATION, /* strictWhitelist = */ true),
                /* sortByName = */ true);
    }

    // -------------------------------------------------------------------------------------------------------------

    /** @return The class type signature, if available, otherwise returns null. */
    public ClassTypeSignature getTypeSignature() {
        if (typeSignatureStr == null) {
            return null;
        }
        if (typeSignature == null) {
            try {
                typeSignature = ClassTypeSignature.parse(typeSignatureStr, this);
                typeSignature.setScanResult(scanResult);
            } catch (final ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return typeSignature;
    }

    // -------------------------------------------------------------------------------------------------------------

    /**
     * @return The URL of the classpath element that this class was found within.
     */
    public URL getClasspathElementURL() {
        if (classpathElementURL == null) {
            try {
                if (moduleRef != null) {
                    classpathElementURL = moduleRef.getLocation().toURL();
                } else if (!jarfilePackageRoot.isEmpty()) {
                    final File classpathEltFile = getClasspathElementFile();
                    final boolean classpathEltIsJar = classpathEltFile.isFile();
                    String classpathEltURL;
                    try {
                        classpathEltURL = classpathEltFile.toURI().toURL().toString();
                    } catch (final MalformedURLException e) {
                        // Should not happen
                        throw new RuntimeException(e);
                    }
                    final String relativePathEncoded = URLPathEncoder.encodePath(jarfilePackageRoot);
                    final String urlStr = classpathEltIsJar //
                            ? "jar:" + classpathEltURL + "!" + relativePathEncoded
                            : classpathEltURL + (!classpathEltURL.endsWith("/") ? "/" : "")
                                    + (relativePathEncoded.startsWith("/") ? relativePathEncoded.substring(1)
                                            : relativePathEncoded);
                    try {
                        classpathElementURL = new URL(urlStr);
                    } catch (final MalformedURLException e) {
                        // Should not happen
                        throw new RuntimeException(e);
                    }

                } else {
                    classpathElementURL = getClasspathElementFile().toURI().toURL();
                }
            } catch (final MalformedURLException e) {
                // Shouldn't happen
                throw new IllegalArgumentException(e);
            }
        }
        return classpathElementURL;
    }

    /**
     * @return The {@link File} for the classpath element package root dir or jar that this class was found within,
     *         or null if this class was found in a module. (See also {@link #getModuleRef}.)
     */
    public File getClasspathElementFile() {
        return classpathElementFile;
    }

    /**
     * @return The module in the module path that this class was found within, as a {@link ModuleRef}, or null if
     *         this class was found in a directory or jar in the classpath.
     */
    public ModuleRef getModuleRef() {
        return moduleRef;
    }

    // -------------------------------------------------------------------------------------------------------------

    /**
     * Obtain a {@code Class<?>} reference for the class named by this {@link ClassInfo} object, casting it to the
     * requested interface or superclass type. Causes the ClassLoader to load the class, if it is not already
     * loaded.
     * 
     * <p>
     * <b>Important note:</b> since {@code superclassOrInterfaceType} is a class reference for an already-loaded
     * class, it is critical that {@code superclassOrInterfaceType} is loaded by the same classloader as the class
     * referred to by this {@code ClassInfo} object, otherwise the class cast will fail.
     * 
     * @param superclassOrInterfaceType
     *            The type to cast the loaded class to.
     * @param ignoreExceptions
     *            If true, return null if any exceptions or errors thrown during classloading, or if attempting to
     *            cast the resulting {@code Class<?>} reference to the requested superclass or interface type fails.
     *            If false, {@link IllegalArgumentException} is thrown if the class could not be loaded or could not
     *            be cast to the requested type.
     * @return The class reference, or null, if ignoreExceptions is true and there was an exception or error loading
     *         the class.
     * @throws IllegalArgumentException
     *             if ignoreExceptions is false and there were problems loading the class, or casting it to the
     *             requested type.
     */
    @Override
    public <T> Class<T> loadClass(final Class<T> superclassOrInterfaceType, final boolean ignoreExceptions) {
        return super.loadClass(superclassOrInterfaceType, ignoreExceptions);
    }

    /**
     * Obtain a {@code Class<?>} reference for the class named by this {@link ClassInfo} object, casting it to the
     * requested interface or superclass type. Causes the ClassLoader to load the class, if it is not already
     * loaded.
     * 
     * <p>
     * <b>Important note:</b> since {@code superclassOrInterfaceType} is a class reference for an already-loaded
     * class, it is critical that {@code superclassOrInterfaceType} is loaded by the same classloader as the class
     * referred to by this {@code ClassInfo} object, otherwise the class cast will fail.
     * 
     * @param superclassOrInterfaceType
     *            The type to cast the loaded class to.
     * @return The class reference.
     * @throws IllegalArgumentException
     *             if there were problems loading the class or casting it to the requested type.
     */
    @Override
    public <T> Class<T> loadClass(final Class<T> superclassOrInterfaceType) {
        return super.loadClass(superclassOrInterfaceType, /* ignoreExceptions = */ false);
    }

    /**
     * Obtain a {@code Class<?>} reference for the class named by this {@link ClassInfo} object. Causes the
     * ClassLoader to load the class, if it is not already loaded.
     * 
     * @return The class reference, or null, if ignoreExceptions is true and there was an exception or error loading
     *         the class.
     * @throws IllegalArgumentException
     *             if ignoreExceptions is false and there were problems loading the class.
     */
    @Override
    public Class<?> loadClass(final boolean ignoreExceptions) {
        return super.loadClass(ignoreExceptions);
    }

    /**
     * Obtain a {@code Class<?>} reference for the class named by this {@link ClassInfo} object. Causes the
     * ClassLoader to load the class, if it is not already loaded.
     * 
     * @return The class reference.
     * @throws IllegalArgumentException
     *             if there were problems loading the class.
     */
    @Override
    public Class<?> loadClass() {
        return super.loadClass(/* ignoreExceptions = */ false);
    }

    // -------------------------------------------------------------------------------------------------------------

    @Override
    protected String getClassName() {
        return name;
    }

    @Override
    protected ClassInfo getClassInfo() {
        return this;
    }

    @Override
    void setScanResult(final ScanResult scanResult) {
        super.setScanResult(scanResult);
        if (this.typeSignature != null) {
            this.typeSignature.setScanResult(scanResult);
        }
        if (annotationInfo != null) {
            for (final AnnotationInfo ai : annotationInfo) {
                ai.setScanResult(scanResult);
            }
        }
        if (fieldInfo != null) {
            for (final FieldInfo fi : fieldInfo) {
                fi.setScanResult(scanResult);
            }
        }
        if (methodInfo != null) {
            for (final MethodInfo mi : methodInfo) {
                mi.setScanResult(scanResult);
            }
        }
        if (annotationDefaultParamValues != null) {
            for (final AnnotationParameterValue apv : annotationDefaultParamValues) {
                apv.setScanResult(scanResult);
            }
        }
    }

    /**
     * Get the names of any classes referenced in this class' type descriptor, or the type descriptors of fields,
     * methods or annotations.
     */
    @Override
    protected void getClassNamesFromTypeDescriptors(final Set<String> classNames) {
        final Set<String> referencedClassNames = new HashSet<>();
        if (methodInfo != null) {
            for (final MethodInfo mi : methodInfo) {
                mi.getClassNamesFromTypeDescriptors(classNames);
            }
        }
        if (fieldInfo != null) {
            for (final FieldInfo fi : fieldInfo) {
                fi.getClassNamesFromTypeDescriptors(classNames);
            }
        }
        if (annotationInfo != null) {
            for (final AnnotationInfo ai : annotationInfo) {
                ai.getClassNamesFromTypeDescriptors(referencedClassNames);
            }
        }
        if (annotationDefaultParamValues != null) {
            for (final AnnotationParameterValue paramValue : annotationDefaultParamValues) {
                paramValue.getClassNamesFromTypeDescriptors(referencedClassNames);
            }
        }
        final ClassTypeSignature classSig = getTypeSignature();
        if (classSig != null) {
            classSig.getClassNamesFromTypeDescriptors(referencedClassNames);
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    /** Compare based on class name. */
    @Override
    public int compareTo(final ClassInfo o) {
        return this.name.compareTo(o.name);
    }

    /** Use class name for equals(). */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ClassInfo other = (ClassInfo) obj;
        return name.equals(other.name);
    }

    /** Use hash code of class name. */
    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 33;
    }

    @Override
    public String toString() {
        final ClassTypeSignature typeSig = getTypeSignature();
        if (typeSig != null) {
            return typeSig.toString();
        } else {
            final StringBuilder buf = new StringBuilder();
            TypeUtils.modifiersToString(modifiers, /* isMethod = */ false, buf);
            if (buf.length() > 0) {
                buf.append(' ');
            }
            buf.append(isAnnotation ? "@interface "
                    : isInterface ? "interface " : (modifiers & 0x4000) != 0 ? "enum " : "class ");
            buf.append(name);
            return buf.toString();
        }
    }
}
