// Copyright (C) 2016 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.gerrit.extensions.api.access;

import com.google.common.base.Objects;

import java.util.Map;

public class AccessSectionInfo {

  public Map<String, PermissionInfo> permissions;

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof AccessSectionInfo) {
      return Objects.equal(permissions, ((AccessSectionInfo) obj).permissions);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(permissions);
  }
}
