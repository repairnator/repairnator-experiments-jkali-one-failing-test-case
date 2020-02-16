/*
 * Copyright 2018 The Error Prone Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.errorprone.bugpatterns;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.errorprone.BugPattern.Category.JDK;
import static com.google.errorprone.BugPattern.ProvidesFix.REQUIRES_HUMAN_ATTENTION;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.matchers.Matchers.anyOf;
import static com.google.errorprone.matchers.Matchers.staticMethod;
import static com.google.errorprone.matchers.method.MethodMatchers.constructor;
import static com.google.errorprone.matchers.method.MethodMatchers.instanceMethod;
import static com.google.errorprone.util.ASTHelpers.getReceiver;
import static com.google.errorprone.util.ASTHelpers.getSymbol;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker.VariableTreeMatcher;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.IsSubtypeOf;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.predicates.type.DescendantOfAny;
import com.google.errorprone.suppliers.Suppliers;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Matches creation of new collections/proto builders which are modified but never used.
 *
 * @author ghm@google.com (Graeme Morgan)
 */
@BugPattern(
    name = "ModifiedButNotUsed",
    summary = "A collection or proto builder was created, but its values were never accessed.",
    category = JDK,
    providesFix = REQUIRES_HUMAN_ATTENTION,
    severity = WARNING)
public class ModifiedButNotUsed extends BugChecker implements VariableTreeMatcher {

  private static final ImmutableSet<String> GUAVA_IMMUTABLES =
      ImmutableSet.of(
          "com.google.common.collect.ImmutableCollection",
          "com.google.common.collect.ImmutableMap",
          "com.google.common.collect.ImmutableMultimap");

  private static final ImmutableSet<String> COLLECTIONS =
      Streams.concat(
              GUAVA_IMMUTABLES.stream().map(i -> i + ".Builder"),
              Stream.of(
                  "java.util.Collection", "java.util.Map", "com.google.common.collect.Multimap"))
          .collect(toImmutableSet());

  private static final Matcher<ExpressionTree> COLLECTION_SETTER =
      instanceMethod()
          .onDescendantOfAny(COLLECTIONS)
          .withNameMatching(
              Pattern.compile(
                  "add|addAll|clear|put|putAll|remove|removeAll|removeIf|replaceAll|"
                      + "retainAll|set|sort"));

  private static final ImmutableSet<String> PROTO_CLASSES =
      ImmutableSet.of(
          "com.google.protobuf.GeneratedMessage", "com.google.protobuf.GeneratedMessageLite");

  private static final Matcher<ExpressionTree> FLUENT_SETTER =
      anyOf(
          instanceMethod()
              .onDescendantOfAny(
                  PROTO_CLASSES.stream().map(p -> p + ".Builder").collect(toImmutableList()))
              .withNameMatching(Pattern.compile("(add|set|clear|remove|merge).+")),
          instanceMethod()
              .onDescendantOfAny(
                  GUAVA_IMMUTABLES.stream().map(c -> c + ".Builder").collect(toImmutableSet()))
              .withNameMatching(Pattern.compile("(add|put)(All)?")));

  private static final Matcher<Tree> COLLECTION_TYPE =
      anyOf(COLLECTIONS.stream().map(IsSubtypeOf::new).collect(toImmutableList()));

  private static final Matcher<Tree> PROTO_TYPE =
      anyOf(
          PROTO_CLASSES
              .stream()
              .map(p -> new IsSubtypeOf<>(p + ".Builder"))
              .collect(toImmutableList()));

  private static final Matcher<ExpressionTree> FLUENT_CONSTRUCTOR =
      anyOf(
          constructor()
              .forClass(
                  new DescendantOfAny(
                      GUAVA_IMMUTABLES
                          .stream()
                          .map(i -> Suppliers.typeFromString(i + ".Builder"))
                          .collect(toImmutableList()))),
          staticMethod()
              .onClass(
                  new DescendantOfAny(
                      GUAVA_IMMUTABLES
                          .stream()
                          .map(Suppliers::typeFromString)
                          .collect(toImmutableList())))
              .withNameMatching(Pattern.compile("builder(WithExpectedSize)?")),
          constructor()
              .forClass(
                  new DescendantOfAny(
                      PROTO_CLASSES
                          .stream()
                          .map(c -> Suppliers.typeFromString(c + ".Builder"))
                          .collect(toImmutableList()))),
          staticMethod()
              .onClass(
                  new DescendantOfAny(
                      PROTO_CLASSES
                          .stream()
                          .map(Suppliers::typeFromString)
                          .collect(toImmutableList())))
              .named("newBuilder"),
          instanceMethod()
              .onClass(
                  new DescendantOfAny(
                      PROTO_CLASSES
                          .stream()
                          .map(Suppliers::typeFromString)
                          .collect(toImmutableList())))
              .withNameMatching(Pattern.compile("toBuilder|newBuilderForType")));

  private static final Matcher<ExpressionTree> NEW_COLLECTION =
      anyOf(
          constructor()
              .forClass(
                  new DescendantOfAny(
                      COLLECTIONS
                          .stream()
                          .map(Suppliers::typeFromString)
                          .collect(toImmutableList()))),
          staticMethod()
              .onClassAny(
                  "com.google.common.collect.Lists",
                  "com.google.common.collect.Maps",
                  "com.google.common.collect.Sets")
              .withNameMatching(Pattern.compile("new.+")));

  private static boolean newFluentChain(ExpressionTree tree, VisitorState state) {
    while (tree instanceof MethodInvocationTree && FLUENT_SETTER.matches(tree, state)) {
      tree = getReceiver(tree);
    }
    return FLUENT_CONSTRUCTOR.matches(tree, state);
  }

  private static boolean collectionUsed(VisitorState state) {
    TreePath path = state.getPath();
    return !(path.getParentPath().getLeaf() instanceof MemberSelectTree)
        || !(path.getParentPath().getParentPath().getLeaf() instanceof MethodInvocationTree)
        || !COLLECTION_SETTER.matches(
            (MethodInvocationTree) path.getParentPath().getParentPath().getLeaf(), state)
        || ASTHelpers.targetType(state.withPath(path.getParentPath().getParentPath())) != null;
  }

  private static boolean fluentUsed(VisitorState state) {
    for (TreePath path = state.getPath();
        path != null;
        path = path.getParentPath().getParentPath()) {
      if (path.getParentPath().getLeaf() instanceof ExpressionStatementTree) {
        return false;
      }
      if (!(path.getParentPath().getLeaf() instanceof MemberSelectTree
          && path.getParentPath().getParentPath().getLeaf() instanceof MethodInvocationTree
          && FLUENT_SETTER.matches(
              (MethodInvocationTree) path.getParentPath().getParentPath().getLeaf(), state))) {
        break;
      }
    }
    return true;
  }

  @Override
  public Description matchVariable(VariableTree tree, VisitorState state) {
    VarSymbol symbol = getSymbol(tree);
    if (!effectivelyFinal(symbol)) {
      return NO_MATCH;
    }
    if (state.getPath().getParentPath().getLeaf() instanceof ClassTree) {
      return NO_MATCH;
    }
    if (!COLLECTION_TYPE.matches(tree, state) && !PROTO_TYPE.matches(tree, state)) {
      return NO_MATCH;
    }
    List<ExpressionTree> initializers = new ArrayList<>();
    if (tree.getInitializer() == null) {
      new TreeScanner<Void, Void>() {
        @Override
        public Void visitAssignment(AssignmentTree node, Void aVoid) {
          if (symbol.equals(getSymbol(node.getVariable()))) {
            initializers.add(node.getExpression());
          }
          return super.visitAssignment(node, aVoid);
        }
      }.scan(state.getPath().getParentPath().getLeaf(), null);
    } else {
      initializers.add(tree.getInitializer());
    }
    initializers.removeIf(i -> !NEW_COLLECTION.matches(i, state) && !newFluentChain(i, state));
    if (initializers.isEmpty()) {
      return NO_MATCH;
    }
    UnusedScanner isUnusedScanner = new UnusedScanner(symbol, state, getMatcher(tree, state));
    isUnusedScanner.scan(state.getPath().getParentPath(), null);
    return isUnusedScanner.isUnused ? describeMatch(initializers.get(0)) : NO_MATCH;
  }

  private static Matcher<IdentifierTree> getMatcher(Tree tree, VisitorState state) {
    return COLLECTION_TYPE.matches(tree, state)
        ? (t, s) -> collectionUsed(s)
        : (t, s) -> fluentUsed(s);
  }

  private static class UnusedScanner extends TreePathScanner<Void, Void> {
    private final Symbol symbol;
    private final VisitorState state;
    private final Matcher<IdentifierTree> matcher;

    private boolean isUnused = true;

    private UnusedScanner(Symbol symbol, VisitorState state, Matcher<IdentifierTree> matcher) {
      this.symbol = symbol;
      this.state = state;
      this.matcher = matcher;
    }

    @Override
    public Void visitIdentifier(IdentifierTree identifierTree, Void aVoid) {
      if (!Objects.equals(getSymbol(identifierTree), symbol)) {
        return null;
      }
      if (matcher.matches(identifierTree, state.withPath(getCurrentPath()))) {
        isUnused = false;
        return null;
      }
      return null;
    }

    @Override
    public Void visitVariable(VariableTree variableTree, Void aVoid) {
      // Don't count the declaration of the variable as a usage.
      if (Objects.equals(getSymbol(variableTree), symbol)) {
        return null;
      }
      return super.visitVariable(variableTree, aVoid);
    }

    @Override
    public Void visitAssignment(AssignmentTree assignmentTree, Void aVoid) {
      // Don't count the LHS of the assignment to the variable as a usage.
      if (Objects.equals(getSymbol(assignmentTree.getVariable()), symbol)) {
        return scan(assignmentTree.getExpression(), aVoid);
      }
      return super.visitAssignment(assignmentTree, aVoid);
    }
  }

  private static boolean effectivelyFinal(Symbol symbol) {
    return (symbol.flags() & (Flags.FINAL | Flags.EFFECTIVELY_FINAL)) != 0;
  }
}
