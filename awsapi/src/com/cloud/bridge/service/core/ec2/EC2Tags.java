// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.cloud.bridge.service.core.ec2;

import java.util.ArrayList;
import java.util.List;

public class EC2Tags {

    private List<EC2TagTypeId> resourceTypeSet = new ArrayList<EC2TagTypeId>();
    private List<EC2TagKeyValue> resourceTagSet = new ArrayList<EC2TagKeyValue>();

    public void addResourceType(EC2TagTypeId param) {
        resourceTypeSet.add(param);
    }

    public EC2TagTypeId[] getResourceTypeSet() {
        return resourceTypeSet.toArray(new EC2TagTypeId[0]);
    }

    public void addResourceTag(EC2TagKeyValue param) {
        resourceTagSet.add(param);
    }

    public EC2TagKeyValue[] getResourceTags() {
        return resourceTagSet.toArray(new EC2TagKeyValue[0]);
    }
}
