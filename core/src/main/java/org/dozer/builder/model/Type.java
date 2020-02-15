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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

@XmlType(name = "type")
@XmlEnum
public enum Type {

    @XmlEnumValue("one-way")
    ONE_WAY("one-way"),

    @XmlEnumValue("bi-directional")
    BI_DIRECTIONAL("bi-directional");

    private final String value;

    Type(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Type fromValue(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        for (Type c : Type.values()) {
            if (c.value.equals(value)) {
                return c;
            }
        }

        throw new IllegalArgumentException("type should be bi-directional or one-way. Found: " + value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("value", value)
            .toString();
    }
}
