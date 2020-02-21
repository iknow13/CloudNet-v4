package eu.cloudnetservice.cloudnet.ext.npcs.bukkit.command;


import com.destroystokyo.paper.profile.PlayerProfile;
import de.dytanic.cloudnet.ext.bridge.WorldPosition;
import eu.cloudnetservice.cloudnet.ext.npcs.CloudNPC;
import eu.cloudnetservice.cloudnet.ext.npcs.bukkit.BukkitNPCManagement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class CloudNPCCommand implements CommandExecutor {

    private static final Random RANDOM = new Random();

    private static final String DEFAULT_INFO_LINE = "§8• §7%online_players% of %max_players% players online §8•";

    private BukkitNPCManagement npcManagement;

    public CloudNPCCommand(BukkitNPCManagement npcManagement) {
        this.npcManagement = npcManagement;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length >= 7 && args[0].equalsIgnoreCase("create")) {
                this.createNPC(player, args);
            } else if (args.length == 2 && args[0].equalsIgnoreCase("editInfoLine")) {
                this.editInfoLine(player, args);
            } else if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
                this.removeNPC(player, args);
            } else {
                player.sendMessage("§7/cloudnpc create <targetGroup> <skinUUID> <itemInHand> <shouldLookAtPlayer> <shouldImitatePlayer> <displayName>");
                player.sendMessage("§7/cloudnpc editInfoLine <newInfoLine>");
                player.sendMessage("§7/cloudnpc remove");
            }

            return true;
        }

        return false;
    }

    private void createNPC(Player player, String[] args) {
        UUID skinUUID;
        try {
            skinUUID = UUID.fromString(args[2]);
        } catch (IllegalArgumentException exception) {
            player.sendMessage(this.npcManagement.getNPCConfiguration().getMessages().get("command-create-invalid-uuid"));
            return;
        }

        Material itemInHandMaterial = Material.getMaterial(args[3].toUpperCase());
        if (itemInHandMaterial == null) {
            player.sendMessage(this.npcManagement.getNPCConfiguration().getMessages().get("command-create-invalid-material"));
            return;
        }

        boolean lookAtPlayer = args[4].equalsIgnoreCase("true") || args[5].equalsIgnoreCase("yes");
        boolean imitatePlayer = args[5].equalsIgnoreCase("true") || args[6].equalsIgnoreCase("yes");

        PlayerProfile skinProfile = Bukkit.createProfile(skinUUID);
        if (!skinProfile.complete()) {
            player.sendMessage(this.npcManagement.getNPCConfiguration().getMessages().get("command-create-texture-fetch-fail"));
            return;
        }

        StringBuilder displayNameBuilder = new StringBuilder();
        for (int i = 6; i < args.length; i++) {
            displayNameBuilder.append(args[i]).append(" ");
        }

        Location location = player.getLocation();

        CloudNPC cloudNPC = new CloudNPC(
                new UUID(RANDOM.nextLong(), 0),
                ChatColor.translateAlternateColorCodes('&', displayNameBuilder.substring(0, displayNameBuilder.length() - 1)),
                DEFAULT_INFO_LINE,
                skinProfile.getProperties().stream()
                        .map(property -> new CloudNPC.NPCProfileProperty(property.getName(), property.getValue(), property.getSignature()))
                        .collect(Collectors.toSet()),
                new WorldPosition(
                        location.getX(),
                        location.getY(),
                        location.getZ(),
                        location.getYaw(),
                        location.getPitch(),
                        location.getWorld().getName(),
                        this.npcManagement.getOwnNPCConfigurationEntry().getTargetGroup()
                ),
                args[1],
                itemInHandMaterial.name(),
                lookAtPlayer,
                imitatePlayer
        );

        this.npcManagement.sendNPCAddUpdate(cloudNPC);
        player.sendMessage(this.npcManagement.getNPCConfiguration().getMessages().get("command-create-success"));
    }

    private void editInfoLine(Player player, String[] args) {

    }

    private void removeNPC(Player player, String[] args) {

    }

}
