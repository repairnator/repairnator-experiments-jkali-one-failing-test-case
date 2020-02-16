/*
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
package com.facebook.presto.sql.planner.iterative.rule;

import com.facebook.presto.matching.Pattern;
import com.facebook.presto.sql.planner.PlanNodeIdAllocator;
import com.facebook.presto.sql.planner.Symbol;
import com.facebook.presto.sql.planner.iterative.Rule;
import com.facebook.presto.sql.planner.plan.PlanNode;
import com.facebook.presto.sql.planner.plan.ProjectNode;
import com.google.common.collect.ImmutableList;

import java.util.Optional;
import java.util.Set;

import static com.facebook.presto.sql.planner.iterative.rule.Util.pruneInputs;

/**
 * @param <N> The node type to look for under the ProjectNode
 * Looks for a Project parent over a N child, such that the parent doesn't use all the output columns of the child.
 * Given that situation, invokes the pushDownProjectOff helper to possibly rewrite the child to produce fewer outputs.
 */
public abstract class ProjectOffPushDownRule<N extends PlanNode>
        implements Rule
{
    private static final Pattern PATTERN = Pattern.typeOf(ProjectNode.class);
    private final Class<N> targetNodeClass;

    protected ProjectOffPushDownRule(Class<N> targetNodeClass)
    {
        this.targetNodeClass = targetNodeClass;
    }

    @Override
    public Pattern getPattern()
    {
        return PATTERN;
    }

    @Override
    public Optional<PlanNode> apply(PlanNode node, Context context)
    {
        ProjectNode parent = (ProjectNode) node;

        PlanNode child = context.getLookup().resolve(parent.getSource());
        if (!targetNodeClass.isInstance(child)) {
            return Optional.empty();
        }

        N targetNode = targetNodeClass.cast(child);

        return pruneInputs(child.getOutputSymbols(), parent.getAssignments().getExpressions())
                .flatMap(prunedOutputs -> this.pushDownProjectOff(context.getIdAllocator(), targetNode, prunedOutputs))
                .map(newChild -> parent.replaceChildren(ImmutableList.of(newChild)));
    }

    protected abstract Optional<PlanNode> pushDownProjectOff(PlanNodeIdAllocator idAllocator, N targetNode, Set<Symbol> referencedOutputs);
}
