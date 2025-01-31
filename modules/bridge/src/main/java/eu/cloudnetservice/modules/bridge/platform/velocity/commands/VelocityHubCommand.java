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

package eu.cloudnetservice.modules.bridge.platform.velocity.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.cloudnetservice.ext.adventure.AdventureSerializerUtil;
import eu.cloudnetservice.modules.bridge.platform.PlatformBridgeManagement;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;

public final class VelocityHubCommand implements SimpleCommand {

  private final ProxyServer proxyServer;
  private final PlatformBridgeManagement<Player, ?> management;

  public VelocityHubCommand(@NonNull ProxyServer server, @NonNull PlatformBridgeManagement<Player, ?> management) {
    this.proxyServer = server;
    this.management = management;
  }

  @Override
  public void execute(@NonNull Invocation invocation) {
    if (invocation.source() instanceof Player player) {
      // check if the player is on a fallback already
      if (this.management.isOnAnyFallbackInstance(player)) {
        player.sendMessage(AdventureSerializerUtil.serialize(this.management.configuration().message(
          player.getEffectiveLocale(),
          "command-hub-already-in-hub")));
      } else {
        // try to get a fallback for the player
        var hub = this.management.fallback(player)
          .flatMap(service -> this.proxyServer.getServer(service.name()))
          .orElse(null);
        // check if a fallback was found
        if (hub != null) {
          player.createConnectionRequest(hub).connectWithIndication().whenComplete((result, ex) -> {
            // check if the connection was successful
            if (result && ex == null) {
              player.sendMessage(AdventureSerializerUtil.serialize(this.management.configuration().message(
                player.getEffectiveLocale(),
                "command-hub-success-connect"
              ).replace("%server%", hub.getServerInfo().getName())));
            } else {
              // the connection was not successful
              player.sendMessage(AdventureSerializerUtil.serialize(this.management.configuration().message(
                player.getEffectiveLocale(),
                "command-hub-no-server-found")));
            }
          });
        }
      }
    }
  }

  @Override
  public @NonNull CompletableFuture<List<String>> suggestAsync(@NonNull Invocation invocation) {
    return CompletableFuture.completedFuture(List.copyOf(this.management.configuration().hubCommandNames()));
  }
}
