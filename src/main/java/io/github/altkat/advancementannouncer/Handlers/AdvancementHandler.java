package io.github.altkat.advancementannouncer.Handlers;

import io.github.altkat.advancementannouncer.AdvancementAnnouncer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AdvancementHandler {
    private final NamespacedKey key;
    private final String icon;
    private final String message;
    private final Style style;
    private AdvancementAnnouncer plugin;

    private AdvancementHandler(String icon, String message, Style style) {
        this.plugin = AdvancementAnnouncer.getInstance();
        this.key = new NamespacedKey(plugin, UUID.randomUUID().toString());
        this.icon = icon;
        this.message = message;
        this.style = style;
    }

    private void start(Player player){
        createAdvancement();
        grantAdvancement(player);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            revokeAdvancement(player);
        }, 10);
    }

    private void createAdvancement() {
        String itemKey;
        int version = plugin.getVersion();
        if (version <= 20) {
            itemKey = "item";
        }else {
            itemKey = "id";
        }

        String advancementJson = "{\n" +
                "    \"criteria\": {\n" +
                "        \"trigger\": {\n" +
                "            \"trigger\": \"minecraft:impossible\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"display\": {\n" +
                "        \"icon\": {\n" +
                "            \"" + itemKey + "\": \"minecraft:" + icon + "\"\n" +
                "        },\n" +
                "        \"title\": {\n" +
                "            \"text\": \"" + message.replace("|", "\n") + "\"\n" +
                "        },\n" +
                "        \"description\": {\n" +
                "            \"text\": \"\"\n" +
                "        },\n" +
                "        \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" +
                "        \"frame\": \"" + style.toString().toLowerCase() + "\",\n" +
                "        \"announce_to_chat\": false,\n" +
                "        \"show_toast\": true,\n" +
                "        \"hidden\": true\n" +
                "    },\n" +
                "    \"requirements\": [\n" +
                "        [\n" +
                "            \"trigger\"\n" +
                "        ]\n" +
                "    ]\n" +
                "}";
        Bukkit.getUnsafe().loadAdvancement(key, advancementJson);
    }



    private void grantAdvancement(Player player) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).awardCriteria("trigger");
    }

    private void revokeAdvancement(Player player) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).revokeCriteria("trigger");
    }

    public static void displayTo(Player player, String icon, String message, Style style) {
        String coloredMessage = ChatColor.translateAlternateColorCodes('&', message);
        if(AdvancementAnnouncer.getInstance().isPAPIEnabled()) {
            String parsedMessage = ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, coloredMessage));
            new AdvancementHandler(icon, parsedMessage, style).start(player);
        }else {
            new AdvancementHandler(icon, coloredMessage, style).start(player);
        }
    }

    public static enum Style {
        GOAL,
        TASK,
        CHALLENGE
    }

}
