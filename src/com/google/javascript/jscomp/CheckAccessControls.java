/*
 * Copyright 2008 The Closure Compiler Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.javascript.jscomp;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.javascript.jscomp.NodeTraversal.AbstractPostOrderCallback;
import com.google.javascript.jscomp.NodeTraversal.ScopedCallback;
import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.JSDocInfo.Visibility;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.StaticSourceFile;
import com.google.javascript.rhino.jstype.FunctionType;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.JSTypeNative;
import com.google.javascript.rhino.jstype.JSTypeRegistry;
import com.google.javascript.rhino.jstype.ObjectType;
import java.util.ArrayDeque;
import javax.annotation.Nullable;

/**
 * A compiler pass that checks that the programmer has obeyed all the access
 * control restrictions indicated by JSDoc annotations, like
 * {@code @private} and {@code @deprecated}.
 *
 * Because access control restrictions are attached to type information, this pass must run
 * after TypeInference, and InferJSDocInfo.
 *
 * @author nicksantos@google.com (Nick Santos)
 */
class CheckAccessControls extends AbstractPostOrderCallback
    implements ScopedCallback, HotSwapCompilerPass {

  static final DiagnosticType DEPRECATED_NAME = DiagnosticType.disabled(
      "JSC_DEPRECATED_VAR",
      "Variable {0} has been deprecated.");

  static final DiagnosticType DEPRECATED_NAME_REASON = DiagnosticType.disabled(
      "JSC_DEPRECATED_VAR_REASON",
      "Variable {0} has been deprecated: {1}");

  static final DiagnosticType DEPRECATED_PROP = DiagnosticType.disabled(
      "JSC_DEPRECATED_PROP",
      "Property {0} of type {1} has been deprecated.");

  static final DiagnosticType DEPRECATED_PROP_REASON = DiagnosticType.disabled(
      "JSC_DEPRECATED_PROP_REASON",
      "Property {0} of type {1} has been deprecated: {2}");

  static final DiagnosticType DEPRECATED_CLASS = DiagnosticType.disabled(
      "JSC_DEPRECATED_CLASS",
      "Class {0} has been deprecated.");

  static final DiagnosticType DEPRECATED_CLASS_REASON = DiagnosticType.disabled(
      "JSC_DEPRECATED_CLASS_REASON",
      "Class {0} has been deprecated: {1}");

  static final DiagnosticType BAD_PACKAGE_PROPERTY_ACCESS =
      DiagnosticType.error(
          "JSC_BAD_PACKAGE_PROPERTY_ACCESS",
          "Access to package-private property {0} of {1} not allowed here.");

  static final DiagnosticType BAD_PRIVATE_GLOBAL_ACCESS =
      DiagnosticType.error(
          "JSC_BAD_PRIVATE_GLOBAL_ACCESS",
          "Access to private variable {0} not allowed outside file {1}.");

  static final DiagnosticType BAD_PRIVATE_PROPERTY_ACCESS =
      DiagnosticType.warning(
          "JSC_BAD_PRIVATE_PROPERTY_ACCESS",
          "Access to private property {0} of {1} not allowed here.");

  static final DiagnosticType BAD_PROTECTED_PROPERTY_ACCESS =
      DiagnosticType.warning(
          "JSC_BAD_PROTECTED_PROPERTY_ACCESS",
          "Access to protected property {0} of {1} not allowed here.");

  static final DiagnosticType
      BAD_PROPERTY_OVERRIDE_IN_FILE_WITH_FILEOVERVIEW_VISIBILITY =
      DiagnosticType.error(
          "JSC_BAD_PROPERTY_OVERRIDE_IN_FILE_WITH_FILEOVERVIEW_VISIBILITY",
          "Overridden property {0} in file with fileoverview visibility {1}"
              + " must explicitly redeclare superclass visibility");

  static final DiagnosticType PRIVATE_OVERRIDE =
      DiagnosticType.warning(
          "JSC_PRIVATE_OVERRIDE",
          "Overriding private property of {0}.");

  static final DiagnosticType EXTEND_FINAL_CLASS =
      DiagnosticType.error(
          "JSC_EXTEND_FINAL_CLASS",
          "{0} is not allowed to extend final class {1}.");

  static final DiagnosticType VISIBILITY_MISMATCH =
      DiagnosticType.warning(
          "JSC_VISIBILITY_MISMATCH",
          "Overriding {0} property of {1} with {2} property.");

  static final DiagnosticType CONST_PROPERTY_REASSIGNED_VALUE =
      DiagnosticType.warning(
        "JSC_CONSTANT_PROPERTY_REASSIGNED_VALUE",
        "constant property {0} assigned a value more than once");

  static final DiagnosticType CONST_PROPERTY_DELETED =
      DiagnosticType.warning(
        "JSC_CONSTANT_PROPERTY_DELETED",
        "constant property {0} cannot be deleted");

  static final DiagnosticType CONVENTION_MISMATCH =
      DiagnosticType.warning(
          "JSC_CONVENTION_MISMATCH",
          "Declared access conflicts with access convention.");

  private final AbstractCompiler compiler;
  private final JSTypeRegistry typeRegistry;
  private final boolean enforceCodingConventions;

  // State about the current traversal.
  private int deprecatedDepth = 0;
  private final ArrayDeque<JSType> currentClassStack = new ArrayDeque<>();
  private final JSType noTypeSentinel;

  private ImmutableMap<StaticSourceFile, Visibility> defaultVisibilityForFiles;
  private final Multimap<JSType, String> initializedConstantProperties;


  CheckAccessControls(
      AbstractCompiler compiler, boolean enforceCodingConventions) {
    this.compiler = compiler;
    this.typeRegistry = compiler.getTypeRegistry();
    this.initializedConstantProperties = HashMultimap.create();
    this.enforceCodingConventions = enforceCodingConventions;
    this.noTypeSentinel = typeRegistry.getNativeType(JSTypeNative.NO_TYPE);
  }

  @Override
  public void process(Node externs, Node root) {
    CollectFileOverviewVisibility collectPass =
        new CollectFileOverviewVisibility(compiler);
    collectPass.process(externs, root);
    defaultVisibilityForFiles = collectPass.getFileOverviewVisibilityMap();

    NodeTraversal.traverse(compiler, externs, this);
    NodeTraversal.traverse(compiler, root, this);
  }

  @Override
  public void hotSwapScript(Node scriptRoot, Node originalRoot) {
    CollectFileOverviewVisibility collectPass =
        new CollectFileOverviewVisibility(compiler);
    collectPass.hotSwapScript(scriptRoot, originalRoot);
    defaultVisibilityForFiles = collectPass.getFileOverviewVisibilityMap();

    NodeTraversal.traverse(compiler, scriptRoot, this);
  }

  @Override
  public void enterScope(NodeTraversal t) {
    Node n = t.getScopeRoot();
    if (n.isFunction()) {
      Node parent = n.getParent();
      if (isDeprecatedFunction(n)) {
        deprecatedDepth++;
      }
      JSType prevClass = getCurrentClass();
      JSType currentClass = prevClass == null
          ? getClassOfMethod(n, parent)
          : prevClass;
      // ArrayDeques can't handle nulls, so we reuse the bottom type
      // as a null sentinel.
      currentClassStack.addFirst(currentClass == null
          ? noTypeSentinel
          : currentClass);
    }
  }

  @Override
  public void exitScope(NodeTraversal t) {
    Node n = t.getScopeRoot();
    if (n.isFunction()) {
      if (isDeprecatedFunction(n)) {
        deprecatedDepth--;
      }
      currentClassStack.pop();
    }
  }

  /**
   * Gets the type of the class that "owns" a method, or null if
   * we know that its un-owned.
   */
  private JSType getClassOfMethod(Node n, Node parent) {
    checkState(n.isFunction(), n);
    if (parent.isAssign()) {
      Node lValue = parent.getFirstChild();
      if (NodeUtil.isGet(lValue)) {
        // We have an assignment of the form "a.b = ...".
        JSType lValueType = lValue.getJSType();
        if (lValueType != null && (lValueType.isConstructor() || lValueType.isInterface())) {
          // If a.b is a constructor, then everything in this function
          // belongs to the "a.b" type.
          return (lValueType.toMaybeFunctionType()).getInstanceType();
        } else if (NodeUtil.isPrototypeProperty(lValue)) {
          return normalizeClassType(
              NodeUtil.getPrototypeClassName(lValue).getJSType());
        } else {
          return normalizeClassType(lValue.getFirstChild().getJSType());
        }
      } else {
        // We have an assignment of the form "a = ...", so pull the
        // type off the "a".
        return normalizeClassType(lValue.getJSType());
      }
    } else if (NodeUtil.isFunctionDeclaration(n) || parent.isName()) {
      return normalizeClassType(n.getJSType());
    } else if (parent.isStringKey()
        || parent.isGetterDef()
        || parent.isSetterDef()
        || parent.isMemberFunctionDef()
        || parent.isComputedProp()) {
      Node objectLitParent = parent.getGrandparent();
      if (!objectLitParent.isAssign()) {
        return null;
      }
      Node className = NodeUtil.getPrototypeClassName(objectLitParent.getFirstChild());
      if (className != null) {
        return normalizeClassType(className.getJSType());
      }
    }

    return null;
  }

  /**
   * Normalize the type of a constructor, its instance, and its prototype
   * all down to the same type (the instance type).
   */
  private static JSType normalizeClassType(JSType type) {
    if (type == null || type.isUnknownType()) {
      return type;
    } else if (type.isConstructor() || type.isInterface()) {
      return type.toMaybeFunctionType().getInstanceType();
    } else if (type.isFunctionPrototypeType()) {
      return normalizePrototypeObject(type.toMaybeObjectType());
    }
    return type;
  }

  private static ObjectType normalizePrototypeObject(ObjectType type) {
    FunctionType owner = type.getOwnerFunction();
    if (owner.hasInstanceType()) {
      return owner.getInstanceType();
    }
    return type;
  }

  @Override
  public void visit(NodeTraversal t, Node n, Node parent) {
    switch (n.getToken()) {
      case NAME:
        checkNameDeprecation(t, n, parent);
        checkNameVisibility(t, n, parent);
        break;
      case GETPROP:
        checkPropertyDeprecation(t, n, parent);
        checkPropertyVisibility(t, n, parent);
        checkConstantProperty(t, n);
        break;
      case STRING_KEY:
      case GETTER_DEF:
      case SETTER_DEF:
      case MEMBER_FUNCTION_DEF:
        checkKeyVisibilityConvention(t, n, parent);
        break;
      case NEW:
        checkConstructorDeprecation(t, n, parent);
        break;
      case FUNCTION:
        checkFinalClassOverrides(t, n, parent);
        break;
      default:
        break;
    }
  }

  /**
   * Checks the given NEW node to ensure that access restrictions are obeyed.
   */
  private void checkConstructorDeprecation(NodeTraversal t, Node n,
      Node parent) {
    JSType type = n.getJSType();

    if (type != null) {
      String deprecationInfo = getTypeDeprecationInfo(type);

      if (deprecationInfo != null && shouldEmitDeprecationWarning(t, n, parent)) {

        if (!deprecationInfo.isEmpty()) {
            compiler.report(
                t.makeError(n, DEPRECATED_CLASS_REASON,
                    type.toString(), deprecationInfo));
        } else {
          compiler.report(
              t.makeError(n, DEPRECATED_CLASS, type.toString()));
        }
      }
    }
  }

  /**
   * Checks the given NAME node to ensure that access restrictions are obeyed.
   */
  private void checkNameDeprecation(NodeTraversal t, Node n, Node parent) {
    // Don't bother checking definitions or constructors.
    if (parent.isFunction() || NodeUtil.isNameDeclaration(parent) || parent.isNew()) {
      return;
    }

    Var var = t.getScope().getVar(n.getString());
    JSDocInfo docInfo = var == null ? null : var.getJSDocInfo();

    if (docInfo != null && docInfo.isDeprecated() && shouldEmitDeprecationWarning(t, n, parent)) {
      if (docInfo.getDeprecationReason() != null) {
        compiler.report(
            t.makeError(n, DEPRECATED_NAME_REASON, n.getString(),
                docInfo.getDeprecationReason()));
      } else {
        compiler.report(
            t.makeError(n, DEPRECATED_NAME, n.getString()));
      }
    }
  }

  /**
   * Checks the given GETPROP node to ensure that access restrictions are
   * obeyed.
   */
  private void checkPropertyDeprecation(NodeTraversal t, Node n, Node parent) {
    // Don't bother checking constructors.
    if (parent.isNew()) {
      return;
    }

    ObjectType objectType = castToObject(dereference(n.getFirstChild().getJSType()));
    String propertyName = n.getLastChild().getString();

    if (objectType != null) {
      String deprecationInfo
          = getPropertyDeprecationInfo(objectType, propertyName);

      if (deprecationInfo != null && shouldEmitDeprecationWarning(t, n, parent)) {

        if (!deprecationInfo.isEmpty()) {
          compiler.report(
              t.makeError(n, DEPRECATED_PROP_REASON, propertyName,
                  typeRegistry.getReadableTypeName(n.getFirstChild()),
                  deprecationInfo));
        } else {
          compiler.report(
              t.makeError(n, DEPRECATED_PROP, propertyName,
                  typeRegistry.getReadableTypeName(n.getFirstChild())));
        }
      }
    }
  }

  private boolean isPrivateByConvention(String name) {
    return enforceCodingConventions
        && compiler.getCodingConvention().isPrivate(name);
  }

  /**
   * Determines whether the given OBJECTLIT property visibility violates the coding convention.
   *
   * @param t The current traversal.
   * @param key The objectlit key node (STRING_KEY, GETTER_DEF, SETTER_DEF, MEMBER_FUNCTION_DEF).
   */
  private void checkKeyVisibilityConvention(NodeTraversal t, Node key, Node parent) {
    JSDocInfo info = key.getJSDocInfo();
    if (info == null) {
      return;
    }
    if (!isPrivateByConvention(key.getString())) {
      return;
    }
    Node assign = parent.getParent();
    if (assign == null || !assign.isAssign()) {
      return;
    }
    Node left = assign.getFirstChild();
    if (!left.isGetProp()
        || !left.getLastChild().getString().equals("prototype")) {
      return;
    }
    Visibility declaredVisibility = info.getVisibility();
    // Visibility is declared to be something other than private.
    if (declaredVisibility != Visibility.INHERITED
        && declaredVisibility != Visibility.PRIVATE) {
      compiler.report(t.makeError(key, CONVENTION_MISMATCH));
    }
  }

  /**
   * Reports an error if the given name is not visible in the current context.
   * @param t The current traversal.
   * @param name The name node.
   */
  private void checkNameVisibility(NodeTraversal t, Node name, Node parent) {
    Var var = t.getScope().getVar(name.getString());
    if (var == null) {
      return;
    }

    Visibility v = checkPrivateNameConvention(
        AccessControlUtils.getEffectiveNameVisibility(
            name, var, defaultVisibilityForFiles), name);

    switch (v) {
      case PACKAGE:
        if (!isPackageAccessAllowed(var, name)) {
          compiler.report(
              t.makeError(name, BAD_PACKAGE_PROPERTY_ACCESS,
                  name.getString(), var.getSourceFile().getName()));
        }
        break;
      case PRIVATE:
        if (!isPrivateAccessAllowed(var, name, parent)) {
          compiler.report(
              t.makeError(name, BAD_PRIVATE_GLOBAL_ACCESS,
                  name.getString(), var.getSourceFile().getName()));
        }
        break;
      default:
        // Nothing to do for PUBLIC and PROTECTED
        // (which is irrelevant for names).
        break;
    }
  }


  /**
   * Returns the effective visibility of the given name, reporting an error
   * if there is a contradiction in the various sources of visibility
   * (example: a variable with a trailing underscore that is declared
   * {@code @public}).
   */
  private Visibility checkPrivateNameConvention(Visibility v, Node name) {
    if (isPrivateByConvention(name.getString())) {
      if (v != Visibility.PRIVATE && v != Visibility.INHERITED) {
        compiler.report(JSError.make(name, CONVENTION_MISMATCH));
      }
      return Visibility.PRIVATE;
    }
    return v;
  }

  private static boolean isPrivateAccessAllowed(Var var, Node name, Node parent) {
    StaticSourceFile varSrc = var.getSourceFile();
    StaticSourceFile refSrc = name.getStaticSourceFile();
    JSDocInfo docInfo = var.getJSDocInfo();
    if (varSrc != null
        && refSrc != null
        && !varSrc.getName().equals(refSrc.getName())) {
      return docInfo != null && docInfo.isConstructor()
          && isValidPrivateConstructorAccess(parent);
    } else {
      return true;
    }
  }

  private boolean isPackageAccessAllowed(Var var, Node name) {
    StaticSourceFile varSrc = var.getSourceFile();
    StaticSourceFile refSrc = name.getStaticSourceFile();
    CodingConvention codingConvention = compiler.getCodingConvention();
    if (varSrc != null && refSrc != null) {
      String srcPackage = codingConvention.getPackageName(varSrc);
      String refPackage = codingConvention.getPackageName(refSrc);
      return srcPackage != null
          && refPackage != null
          && srcPackage.equals(refPackage);
    } else {
      // If the source file of either var or name is unavailable, conservatively
      // assume they belong to different packages.
      // TODO(brndn): by contrast, isPrivateAccessAllowed does allow
      // private access when a source file is unknown. I didn't change it
      // in order not to break existing code.
      return false;
    }
  }

  private void checkOverriddenPropertyVisibilityMismatch(
      Visibility overriding,
      Visibility overridden,
      @Nullable Visibility fileOverview,
      NodeTraversal t,
      Node getprop) {
    if (overriding == Visibility.INHERITED
        && overriding != overridden
        && fileOverview != null
        && fileOverview != Visibility.INHERITED) {
      String propertyName = getprop.getLastChild().getString();
      compiler.report(
          t.makeError(getprop,
              BAD_PROPERTY_OVERRIDE_IN_FILE_WITH_FILEOVERVIEW_VISIBILITY,
              propertyName,
              fileOverview.name()));
    }
  }

  @Nullable private static Visibility getOverridingPropertyVisibility(Node parent) {
    JSDocInfo overridingInfo = parent.getJSDocInfo();
    return overridingInfo == null || !overridingInfo.isOverride()
        ? null
        : overridingInfo.getVisibility();
  }



  /**
   * Checks if a constructor is trying to override a final class.
   */
  private void checkFinalClassOverrides(NodeTraversal t, Node fn, Node parent) {
    checkState(fn.isFunction(), fn);
    JSType type = fn.getJSType().toMaybeFunctionType();
    if (type != null && type.isConstructor()) {
      JSType finalParentClass = getSuperClassInstanceIfFinal(getClassOfMethod(fn, parent));
      if (finalParentClass != null) {
        compiler.report(
            t.makeError(fn, EXTEND_FINAL_CLASS,
                type.getDisplayName(), finalParentClass.getDisplayName()));
      }
    }
  }

  /**
   * Determines whether the given constant property got reassigned
   * @param t The current traversal.
   * @param getprop The getprop node.
   */
  private void checkConstantProperty(NodeTraversal t, Node getprop) {
    // Check whether the property is modified
    Node parent = getprop.getParent();
    boolean isDelete = parent.isDelProp();
    if (!(NodeUtil.isAssignmentOp(parent) && parent.getFirstChild() == getprop)
        && !parent.isInc() && !parent.isDec()
        && !isDelete) {
      return;
    }

    ObjectType objectType = castToObject(dereference(getprop.getFirstChild().getJSType()));

    String propertyName = getprop.getLastChild().getString();

    boolean isConstant = isPropertyDeclaredConstant(objectType, propertyName);

    // Check whether constant properties are reassigned
    if (isConstant) {
      JSDocInfo info = parent.getJSDocInfo();
      if (info != null && info.getSuppressions().contains("const")) {
        return;
      }

      if (isDelete) {
        compiler.report(t.makeError(getprop, CONST_PROPERTY_DELETED, propertyName));
        return;
      }

      // Can't check for constant properties on generic function types.
      // TODO(johnlenz): I'm not 100% certain this is necessary, or if
      // the type is being inspected incorrectly.
      if (objectType == null
          || (objectType.isFunctionType()
              && !objectType.toMaybeFunctionType().isConstructor())) {
        return;
      }

      ObjectType oType = objectType;
      while (oType != null) {
        if (initializedConstantProperties.containsEntry(oType, propertyName)
            || initializedConstantProperties.containsEntry(
                getCanonicalInstance(oType), propertyName)) {
          compiler.report(t.makeError(getprop, CONST_PROPERTY_REASSIGNED_VALUE, propertyName));
          break;
        }
        oType = oType.getPrototypeObject();
      }

      initializedConstantProperties.put(objectType, propertyName);

      // Add the prototype when we're looking at an instance object
      if (objectType.isInstanceType()) {
        ObjectType prototype = objectType.getPrototypeObject();
        if (prototype != null && prototype.hasProperty(propertyName)) {
          initializedConstantProperties.put(prototype, propertyName);
        }
      }
    }
  }

  /**
   * Return an object with the same nominal type as obj,
   * but without any possible extra properties that exist on obj.
   */
  static ObjectType getCanonicalInstance(ObjectType obj) {
    FunctionType ctor = obj.getConstructor();
    return ctor == null ? obj : ctor.getInstanceType();
  }

  private JSType typeOrUnknown(JSType type) {
    if (type == null) {
      return typeRegistry.getNativeType(JSTypeNative.UNKNOWN_TYPE);
    }
    return type;
  }

  /**
   * Reports an error if the given property is not visible in the current
   * context.
   * @param t The current traversal.
   * @param getprop The getprop node.
   */
  private void checkPropertyVisibility(NodeTraversal t,
      Node getprop, Node parent) {
    JSDocInfo jsdoc = NodeUtil.getBestJSDocInfo(getprop);
    if (jsdoc != null && jsdoc.getSuppressions().contains("visibility")) {
      return;
    }

    JSType rawReferenceType = typeOrUnknown(getprop.getFirstChild().getJSType()).autobox();
    ObjectType referenceType = castToObject(rawReferenceType);

    String propertyName = getprop.getLastChild().getString();
    boolean isPrivateByConvention = isPrivateByConvention(propertyName);

    if (isPrivateByConvention
        && propertyIsDeclaredButNotPrivate(getprop, parent)) {
      compiler.report(t.makeError(getprop, CONVENTION_MISMATCH));
      return;
    }

    StaticSourceFile definingSource = AccessControlUtils.getDefiningSource(
        getprop, referenceType, propertyName);

    boolean isClassType = false;

    // Is this a normal property access, or are we trying to override
    // an existing property?
    boolean isOverride =
        jsdoc != null
            && (parent.isExprResult() || (parent.isAssign() && parent.getFirstChild() == getprop));

    ObjectType objectType = AccessControlUtils.getObjectType(
        referenceType, isOverride, propertyName);

    Visibility fileOverviewVisibility =
        defaultVisibilityForFiles.get(definingSource);

    Visibility visibility = AccessControlUtils.getEffectivePropertyVisibility(
        getprop,
        referenceType,
        defaultVisibilityForFiles,
        enforceCodingConventions ? compiler.getCodingConvention() : null);

    if (isOverride) {
      Visibility overriding = getOverridingPropertyVisibility(parent);
      if (overriding != null) {
        checkOverriddenPropertyVisibilityMismatch(
            overriding, visibility, fileOverviewVisibility, t, getprop);
      }
    }

    JSType reportType = rawReferenceType;
    if (objectType != null) {
      Node node = objectType.getOwnPropertyDefSite(propertyName);
      if (node == null) {
        // Assume the property is public.
        return;
      }
      reportType = objectType;
      definingSource = node.getStaticSourceFile();
      isClassType = objectType.getOwnPropertyJSDocInfo(propertyName).isConstructor();
    } else if (!isPrivateByConvention && fileOverviewVisibility == null) {
      // We can only check visibility references if we know what file
      // it was defined in.
      // Otherwise just assume the property is public.
      return;
    }

    StaticSourceFile referenceSource = getprop.getStaticSourceFile();

    if (isOverride) {
      boolean sameInput = referenceSource != null
          && referenceSource.getName().equals(definingSource.getName());
      checkOverriddenPropertyVisibility(
          t,
          getprop,
          parent,
          visibility,
          fileOverviewVisibility,
          reportType,
          sameInput);
    } else {
      checkNonOverriddenPropertyVisibility(
          t,
          getprop,
          parent,
          visibility,
          isClassType,
          reportType,
          referenceSource,
          definingSource);
    }
  }

  private static boolean propertyIsDeclaredButNotPrivate(Node getprop, Node parent) {
    // This is a declaration with JSDoc
    JSDocInfo info = NodeUtil.getBestJSDocInfo(getprop);
    if ((parent.isAssign() || parent.isExprResult())
        && parent.getFirstChild() == getprop
        && info != null) {
      Visibility declaredVisibility = info.getVisibility();
      if (declaredVisibility != Visibility.PRIVATE
      && declaredVisibility != Visibility.INHERITED) {
          return true;
      }
    }
    return false;
  }

  private void checkOverriddenPropertyVisibility(
      NodeTraversal t,
      Node getprop,
      Node parent,
      Visibility visibility,
      Visibility fileOverviewVisibility,
      JSType objectType,
      boolean sameInput) {
    // Check an ASSIGN statement that's trying to override a property
    // on a superclass.
    JSDocInfo overridingInfo = parent.getJSDocInfo();
    Visibility overridingVisibility = overridingInfo == null
        ? Visibility.INHERITED
        : overridingInfo.getVisibility();

    // Check that:
    // (a) the property *can* be overridden,
    // (b) the visibility of the override is the same as the
    //     visibility of the original property,
    // (c) the visibility is explicitly redeclared if the override is in
    //     a file with default visibility in the @fileoverview block.
    if (visibility == Visibility.PRIVATE && !sameInput) {
      compiler.report(
          t.makeError(getprop, PRIVATE_OVERRIDE,
              objectType.toString()));
    } else if (overridingVisibility != Visibility.INHERITED
        && overridingVisibility != visibility
        && fileOverviewVisibility == null) {
      compiler.report(
          t.makeError(getprop, VISIBILITY_MISMATCH,
              visibility.name(), objectType.toString(),
              overridingVisibility.name()));
    }
  }

  private void checkNonOverriddenPropertyVisibility(
      NodeTraversal t,
      Node getprop,
      Node parent,
      Visibility visibility,
      boolean isClassType,
      JSType objectType,
      StaticSourceFile referenceSource,
      StaticSourceFile definingSource) {
    // private access is always allowed in the same file.
    if (referenceSource != null
        && definingSource != null
        && referenceSource.getName().equals(definingSource.getName())) {
      return;
    }

    JSType ownerType = normalizeClassType(objectType);

    switch (visibility) {
      case PACKAGE:
        checkPackagePropertyVisibility(t, getprop, referenceSource, definingSource);
        break;
      case PRIVATE:
        checkPrivatePropertyVisibility(t, getprop, parent, isClassType, ownerType);
        break;
      case PROTECTED:
        checkProtectedPropertyVisibility(t, getprop, ownerType);
        break;
      default:
        break;
    }
  }

  private void checkPackagePropertyVisibility(
      NodeTraversal t,
      Node getprop,
      StaticSourceFile referenceSource,
      StaticSourceFile definingSource) {
    CodingConvention codingConvention = compiler.getCodingConvention();
    String refPackage = codingConvention.getPackageName(referenceSource);
    String defPackage = codingConvention.getPackageName(definingSource);
    if (refPackage == null
        || defPackage == null
        || !refPackage.equals(defPackage)) {
      String propertyName = getprop.getLastChild().getString();
      compiler.report(
          t.makeError(getprop, BAD_PACKAGE_PROPERTY_ACCESS,
              propertyName,
              typeRegistry.getReadableTypeName(getprop.getFirstChild())));
      }
  }

  @Nullable private JSType getCurrentClass() {
    JSType cur = currentClassStack.peekFirst();
    return cur == noTypeSentinel
        ? null
        : cur;
  }

  private void checkPrivatePropertyVisibility(
      NodeTraversal t,
      Node getprop,
      Node parent,
      boolean isClassType,
      JSType ownerType) {

    if (isClassType && isValidPrivateConstructorAccess(parent)) {
      return;
    }

    // private access is not allowed outside the file from a different
    // enclosing class.
    JSType accessedType = getprop.getFirstChild().getJSType();
    String propertyName = getprop.getLastChild().getString();
    String readableTypeName = ownerType.equals(accessedType)
        ? typeRegistry.getReadableTypeName(getprop.getFirstChild())
        : ownerType.toString();
    // TODO(tbreisacher): Should we also include the filename where ownerType is defined?
    compiler.report(
        t.makeError(getprop,
            BAD_PRIVATE_PROPERTY_ACCESS,
            propertyName,
            readableTypeName));
  }

  private void checkProtectedPropertyVisibility(
      NodeTraversal t,
      Node getprop,
      JSType ownerType) {
    // There are 3 types of legal accesses of a protected property:
    // 1) Accesses in the same file
    // 2) Overriding the property in a subclass
    // 3) Accessing the property from inside a subclass
    // The first two have already been checked for.
    JSType currentClass = getCurrentClass();
    if (currentClass == null || !currentClass.isSubtypeOf(ownerType)) {
      String propertyName = getprop.getLastChild().getString();
      compiler.report(
          t.makeError(getprop,  BAD_PROTECTED_PROPERTY_ACCESS,
              propertyName,
              typeRegistry.getReadableTypeName(getprop.getFirstChild())));
    }
  }

  /**
   * Whether the given access of a private constructor is legal.
   *
   * For example,
   * new PrivateCtor_(); // not legal
   * PrivateCtor_.newInstance(); // legal
   * x instanceof PrivateCtor_ // legal
   *
   * This is a weird special case, because our visibility system is inherited
   * from Java, and JavaScript has no distinction between classes and
   * constructors like Java does.
   *
   * We may want to revisit this if we decide to make the restrictions tighter.
   */
  private static boolean isValidPrivateConstructorAccess(Node parent) {
    return !parent.isNew();
  }

  /**
   * Determines whether a deprecation warning should be emitted.
   * @param t The current traversal.
   * @param n The node which we are checking.
   * @param parent The parent of the node which we are checking.
   */
  private boolean shouldEmitDeprecationWarning(
      NodeTraversal t, Node n, Node parent) {
    // In the global scope, there are only two kinds of accesses that should
    // be flagged for warnings:
    // 1) Calls of deprecated functions and methods.
    // 2) Instantiations of deprecated classes.
    // For now, we just let everything else by.
    if (t.inGlobalScope()) {
      if (!((parent.isCall() && parent.getFirstChild() == n) || n.isNew())) {
        return false;
      }
    }

    // We can always assign to a deprecated property, to keep it up to date.
    if (n.isGetProp() && n == parent.getFirstChild() && NodeUtil.isAssignmentOp(parent)) {
      return false;
    }

    // Don't warn if the node is just declaring the property, not reading it.
    if (n.isGetProp() && parent.isExprResult() && n.getJSDocInfo().isDeprecated()) {
      return false;
    }

    return !canAccessDeprecatedTypes(t);
  }

  /**
   * Returns whether it's currently OK to access deprecated names and
   * properties.
   *
   * There are 3 exceptions when we're allowed to use a deprecated
   * type or property:
   * 1) When we're in a deprecated function.
   * 2) When we're in a deprecated class.
   * 3) When we're in a static method of a deprecated class.
   */
  private boolean canAccessDeprecatedTypes(NodeTraversal t) {
    Node scopeRoot = t.getClosestHoistScopeRoot();
    if (NodeUtil.isFunctionBlock(scopeRoot)) {
      scopeRoot = scopeRoot.getParent();
    }
    Node scopeRootParent = scopeRoot.getParent();

    return
    // Case #1
    (deprecatedDepth > 0)
        // Case #2
        || (getTypeDeprecationInfo(getTypeOfThis(scopeRoot)) != null)
        // Case #3
        || (scopeRootParent != null
            && scopeRootParent.isAssign()
            && getTypeDeprecationInfo(getClassOfMethod(scopeRoot, scopeRootParent)) != null);
  }

  /**
   * Returns whether this is a function node annotated as deprecated.
   */
  private static boolean isDeprecatedFunction(Node n) {
    checkState(n.isFunction(), n);
    return getDeprecationReason(NodeUtil.getBestJSDocInfo(n)) != null;
  }

  /**
   * Returns the deprecation reason for the type if it is marked
   * as being deprecated. Returns empty string if the type is deprecated
   * but no reason was given. Returns null if the type is not deprecated.
   */
  private static String getTypeDeprecationInfo(JSType type) {
    if (type == null) {
      return null;
    }

    String depReason = getDeprecationReason(type.getJSDocInfo());
    if (depReason != null) {
      return depReason;
    }

    ObjectType objType = castToObject(type);
    if (objType != null) {
      ObjectType implicitProto = objType.getPrototypeObject();
      if (implicitProto != null) {
        return getTypeDeprecationInfo(implicitProto);
      }
    }
    return null;
  }

  private static String getDeprecationReason(JSDocInfo info) {
    if (info != null && info.isDeprecated()) {
      if (info.getDeprecationReason() != null) {
        return info.getDeprecationReason();
      }
      return "";
    }
    return null;
  }

  /**
   * Returns if a property is declared constant.
   */
  private boolean isPropertyDeclaredConstant(
      ObjectType objectType, String prop) {
    if (enforceCodingConventions
        && compiler.getCodingConvention().isConstant(prop)) {
      return true;
    }
    for (;
         objectType != null;
         objectType = objectType.getPrototypeObject()) {
      JSDocInfo docInfo = objectType.getOwnPropertyJSDocInfo(prop);
      if (docInfo != null && docInfo.isConstant()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the deprecation reason for the property if it is marked
   * as being deprecated. Returns empty string if the property is deprecated
   * but no reason was given. Returns null if the property is not deprecated.
   */
  @Nullable
  private static String getPropertyDeprecationInfo(ObjectType type, String prop) {
    String depReason = getDeprecationReason(type.getOwnPropertyJSDocInfo(prop));
    if (depReason != null) {
      return depReason;
    }

    ObjectType implicitProto = type.getPrototypeObject();
    if (implicitProto != null) {
      return getPropertyDeprecationInfo(implicitProto, prop);
    }
    return null;
  }

  /**
   * Dereference a type, autoboxing it and filtering out null.
   */
  @Nullable
  private static ObjectType dereference(JSType type) {
    return type == null ? null : type.dereference();
  }

  /**
   * If the superclass is final, this method returns an instance of the superclass.
   */
  @Nullable
  private static ObjectType getSuperClassInstanceIfFinal(@Nullable JSType type) {
    if (type != null) {
      ObjectType obj = castToObject(type);
      FunctionType ctor = obj == null ? null : obj.getSuperClassConstructor();
      JSDocInfo doc = ctor == null ? null : ctor.getJSDocInfo();
      if (doc != null && doc.isFinal()) {
        return ctor.getInstanceType();
      }
    }
    return null;
  }

  @Nullable
  private static ObjectType castToObject(@Nullable JSType type) {
    return type == null ? null : type.toMaybeObjectType();
  }

  @Nullable
  private JSType getTypeOfThis(Node scopeRoot) {
    if (scopeRoot.isRoot()) {
      return castToObject(scopeRoot.getJSType());
    }

    checkState(scopeRoot.isFunction(), scopeRoot);
    JSType nodeType = scopeRoot.getJSType();
    if (nodeType != null && nodeType.isFunctionType()) {
      return nodeType.toMaybeFunctionType().getTypeOfThis();
    } else {
      // Executed when the current scope has not been typechecked.
      return null;
    }
  }
}
