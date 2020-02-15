/*
 * Copyright 2005-2017 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dozer.builder.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "variables")
public class VariablesDefinition {

    @XmlElement(name = "variable", required = true)
    private List<VariableDefinition> variables;

    public List<VariableDefinition> getVariable() {
        return variables;
    }

    public void setVariable(List<VariableDefinition> variables) {
        this.variables = variables;
    }

    // Fluent API
    //-------------------------------------------------------------------------
    public VariablesDefinition addVariable(VariableDefinition variable) {
        if (getVariable() == null) {
            setVariable(new ArrayList<VariableDefinition>());
        }

        getVariable().add(variable);

        return this;
    }

    public void build() {
        for (VariableDefinition current : variables) {
            current.build();
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("variable", variables)
            .toString();
    }
}
