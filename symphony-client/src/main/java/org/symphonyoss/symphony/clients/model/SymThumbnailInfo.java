/*
 *
 *
 * Copyright 2016 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.symphonyoss.symphony.clients.model;

import org.symphonyoss.symphony.agent.model.V4ThumbnailInfo;

/**
 * @author Frank Tarsillo on 8/2/17.
 */
public class SymThumbnailInfo {


    private String id = null;


    private String dimension = null;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }


    public static SymThumbnailInfo toSymThumbnailInfo(V4ThumbnailInfo v4ThumbnailInfo){

        if(v4ThumbnailInfo == null)
            return null;

        SymThumbnailInfo symThumbnailInfo = new SymThumbnailInfo();

        symThumbnailInfo.setDimension(v4ThumbnailInfo.getDimension());
        symThumbnailInfo.setId(v4ThumbnailInfo.getId());
        return symThumbnailInfo;

    }
}
