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

package eu.cloudnetservice.cloudnet.driver.module;

import lombok.NonNull;

/**
 * An exception thrown when a required property was not found in a module.json file.
 * <p>
 * Required properties:
 * <ul>
 *   <li>group
 *   <li>name
 *   <li>version
 *   <li>main
 * </ul>
 *
 * @see ModuleConfiguration
 * @since 4.0
 */
public class ModuleConfigurationPropertyNotFoundException extends RuntimeException {

  /**
   * Creates a new instance of this class.
   *
   * @param field the field which was missing in the module configuration.
   */
  public ModuleConfigurationPropertyNotFoundException(@NonNull String field) {
    super(String.format("Required property in module configuration not found: %s", field));
  }
}
