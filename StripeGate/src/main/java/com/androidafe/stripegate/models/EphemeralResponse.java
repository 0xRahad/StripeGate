/*
 *
 *   Created Md Rahadul Islam on 12/16/23, 3:16 PM
 *   Copyright Ⓒ 2023. All rights reserved Ⓒ 2023 http://freefuninfo.com/
 *   Last modified: 12/15/23, 11:47 AM
 *
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 *   except in compliance with the License. You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENS... Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *    either express or implied. See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 *
 */

package com.androidafe.stripegate.models;

public class EphemeralResponse {

    private String id;

    public EphemeralResponse(String id) {
        this.id = id;
    }

    public String getEphemeralKey() {
        return id;
    }

    public void setEphemeralKey(String id) {
        this.id = id;
    }
}
