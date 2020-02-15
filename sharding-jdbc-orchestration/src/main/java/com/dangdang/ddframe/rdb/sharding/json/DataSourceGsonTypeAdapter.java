/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
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
 * </p>
 */

package com.dangdang.ddframe.rdb.sharding.json;

import com.dangdang.ddframe.rdb.sharding.jdbc.core.datasource.NamedDataSource;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 作业配置的Json转换适配器.
 *
 * @author zhangliang
 */
public final class DataSourceGsonTypeAdapter extends TypeAdapter<NamedDataSource> {
    
    private static Collection<Class<?>> generalClassType;
    
    static {
        generalClassType = Sets.<Class<?>>newHashSet(boolean.class, Boolean.class, int.class, Integer.class, long.class, Long.class, String.class);
    }
    
    @Override
    public NamedDataSource read(final JsonReader in) throws IOException {
        String name = "";
        String clazz = "";
        Map<String, String> properties = new HashMap<>(64);
        in.beginObject();
        while (in.hasNext()) {
            String jsonName = in.nextName();
            if ("name".equals(jsonName)) {
                name = in.nextString();
            } else if ("clazz".equals(jsonName)) {
                clazz = in.nextString();
            } else {
                properties.put(jsonName, in.nextString());
            }
        }
        in.endObject();
        DataSource dataSource = null;
        try {
            dataSource = (DataSource) Class.forName(clazz).newInstance();
            for (Entry<String, String> entry : properties.entrySet()) {
                callSetterMethod(dataSource, getSetterMethodName(entry.getKey()), entry.getValue());
            }
        } catch (final ReflectiveOperationException ex) {
        }
        return new NamedDataSource(name, dataSource);
    }
    
    @Override
    public void write(final JsonWriter out, final NamedDataSource value) throws IOException {
        out.beginObject();
        out.name("name").value(value.getName());
        out.name("clazz").value(value.getDataSource().getClass().getName());
        Method[] methods = value.getDataSource().getClass().getDeclaredMethods();
        Map<String, Method> getterMethods = new HashMap<>(methods.length, 1);
        Map<String, Method> setterMethods = new HashMap<>(methods.length, 1);
        for (Method each : methods) {
            if (isGetterMethod(each)) {
                getterMethods.put(getPropertyName(each), each);
            } else if (isSetterMethod(each)) {
                setterMethods.put(getPropertyName(each), each);
            }
        }
        Map<String, Method> getterPairedGetterMethods = getPairedGetterMethods(getterMethods, setterMethods);
        for (Entry<String, Method> entry : getterPairedGetterMethods.entrySet()) {
            Object getterResult = null;
            try {
                getterResult = entry.getValue().invoke(value.getDataSource());
            // CHECKSTYLE:OFF
            } catch (final Exception ex) {
            }
            // CHECKSTYLE:ON
            if (null != getterResult) {
                out.name(entry.getKey()).value(getterResult.toString());
            }
        }
        out.endObject();
    }
    
    private boolean isGetterMethod(final Method method) {
        return method.getName().startsWith("get") && 0 == method.getParameterTypes().length && isGeneralClassType(method.getReturnType());
    }
    
    private boolean isSetterMethod(final Method method) {
        return method.getName().startsWith("set") && 1 == method.getParameterTypes().length && isGeneralClassType(method.getParameterTypes()[0]) && isVoid(method.getReturnType());
    }
    
    private boolean isGeneralClassType(final Class<?> clazz) {
        return generalClassType.contains(clazz);
    }
    
    private boolean isVoid(final Class<?> clazz) {
        return void.class == clazz || Void.class == clazz;
    }
    
    private String getPropertyName(final Method method) {
        return String.valueOf(method.getName().charAt(3)).toLowerCase() + method.getName().substring(4, method.getName().length());
    }
    
    private Map<String, Method> getPairedGetterMethods(final Map<String, Method> getterMethods, final Map<String, Method> setterMethods) {
        Map<String, Method> result = new HashMap<>(getterMethods.size());
        for (Entry<String, Method> entry : getterMethods.entrySet()) {
            if (setterMethods.containsKey(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
    
    private String getSetterMethodName(final String propertyName) {
        return "set" + String.valueOf(propertyName.charAt(0)).toUpperCase() + propertyName.substring(1, propertyName.length());
    }
    
    private void callSetterMethod(final DataSource dataSource, final String methodName, final String setterValue) {
        for (Class<?> each : generalClassType) {
            try {
                Method method = dataSource.getClass().getDeclaredMethod(methodName, each);
                if (boolean.class == each || Boolean.class == each) {
                    method.invoke(dataSource, Boolean.valueOf(setterValue));
                } else if (int.class == each || Integer.class == each) {
                    method.invoke(dataSource, Integer.parseInt(setterValue));
                } else if (long.class == each || Long.class == each) {
                    method.invoke(dataSource, Long.parseLong(setterValue));
                } else {
                    method.invoke(dataSource, setterValue);
                }
                return;
            } catch (final ReflectiveOperationException ex) {
            }
        }
    }
}
