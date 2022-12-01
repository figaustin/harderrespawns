package com.etsuni.harderspawns;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;

import static com.etsuni.harderspawns.HarderSpawns.plugin;

public class Events implements Listener {

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        FileConfiguration config = plugin.getCustomConfig();
        Player player  = event.getPlayer();

        if(player.hasPlayedBefore()) {
            return;
        }

        player.setHealth(config.getDouble("first_join_health"));
        player.setFoodLevel(config.getInt("first_join_hunger"));

        if(config.getStringList("first_join_potion_effects").isEmpty()) {
            return;
        }

        for(String effect : config.getStringList("first_join_potion_effects")) {
            player.addPotionEffect(parseEffect(effect));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {

        FileConfiguration config = plugin.getCustomConfig();
        Player player = event.getPlayer();

        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        //This is here to put a delay on setting the health and hunger since the default playerRespawnEvent takes priority
        // and will set the players health and hunger to full
        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                player.setHealth(config.getDouble("respawn_health"));
                player.setFoodLevel(config.getInt("respawn_hunger"));

                for(String effect : config.getStringList("respawn_potion_effects")) {
                    player.addPotionEffect(parseEffect(effect));
                }
            }
        }, 1);

    }

    public PotionEffect parseEffect(String effect) {
        String[] arrStr = effect.split(" ");
        PotionEffectType type = null;
        int duration = 0;
        int amplifier = 0;

        for(int i = 0; i < arrStr.length; i++) {
            switch(i) {
                case 0:
                    type = PotionEffectType.getByName(arrStr[i]);
                    break;
                case 1:
                    duration = Integer.parseInt(arrStr[i]);
                    break;
                case 2:
                    amplifier = Integer.parseInt(arrStr[i]);
                    break;
                default:
                    return null;
            }
        }

        return new PotionEffect(Objects.requireNonNull(type), duration, amplifier);
    }

}
