/*
 * Copyright 2019-2022 CloudNetService team & contributors
 *
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

package eu.cloudnetservice.modules.cloudperms.velocity;

import com.velocitypowered.api.permission.PermissionFunction;
import com.velocitypowered.api.permission.Tristate;
import eu.cloudnetservice.cloudnet.driver.permission.Permission;
import eu.cloudnetservice.cloudnet.driver.permission.PermissionManagement;
import java.util.UUID;

final class VelocityCloudPermissionFunction implements PermissionFunction {

  private final UUID uniqueId;
  private final PermissionManagement permissionsManagement;

  public VelocityCloudPermissionFunction(UUID uniqueId, PermissionManagement permissionsManagement) {
    this.uniqueId = uniqueId;
    this.permissionsManagement = permissionsManagement;
  }

  @Override
  public Tristate getPermissionValue(String permission) {
    if (permission == null) {
      return Tristate.FALSE;
    }

    var permissionUser = this.permissionsManagement.user(this.uniqueId);
    return
      (permissionUser != null && this.permissionsManagement.hasPermission(permissionUser, Permission.of(permission))) ?
        Tristate.TRUE : Tristate.FALSE;
  }

  public UUID uniqueId() {
    return this.uniqueId;
  }
}
