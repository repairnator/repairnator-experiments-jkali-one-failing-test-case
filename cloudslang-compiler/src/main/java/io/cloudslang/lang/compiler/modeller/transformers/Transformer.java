package io.cloudslang.lang.compiler.modeller.transformers;
/*******************************************************************************
* (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Apache License v2.0 which accompany this distribution.
*
* The Apache License is available at
* http://www.apache.org/licenses/LICENSE-2.0
*
*******************************************************************************/


/*
 * Created by orius123 on 05/11/14.
 */

import java.util.List;

//TODO: Eliya - add Java Doc
public interface Transformer<F, T> {

    T transform(F rawData);

    List<Scope> getScopes();

    String keyToTransform();

    enum Scope {
        BEFORE_TASK,
        AFTER_TASK,
        BEFORE_EXECUTABLE,
        AFTER_EXECUTABLE,
        ACTION
    }

}
