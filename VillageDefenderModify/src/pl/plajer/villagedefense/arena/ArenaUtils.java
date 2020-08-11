/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2020  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.villagedefense.arena;

import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import miskyle.villagedefender.data.player.PlayerData;
import miskyle.villagedefender.data.player.PlayerManager;
import pl.plajer.villagedefense.ConfigPreferences;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.arena.initializers.*;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.plajerlair.commonsbox.minecraft.serialization.InventorySerializer;

/**
 * @author Plajer
 * <p>
 * Created at 13.03.2018
 */
@SuppressWarnings("deprecation")
public class ArenaUtils {

  private static Main plugin;

  private ArenaUtils() {
  }

  public static void init(Main plugin) {
    ArenaUtils.plugin = plugin;
  }

  public static void hidePlayer(Player p, Arena arena) {
    for (Player player : arena.getPlayers()) {
      player.hidePlayer(p);
    }
  }

  public static void showPlayer(Player p, Arena arena) {
    for (Player player : arena.getPlayers()) {
      player.showPlayer(p);
    }
  }

  public static void resetPlayerAfterGame(Player player) {
    for (Player players : plugin.getServer().getOnlinePlayers()) {
      players.showPlayer(player);
      player.showPlayer(players);
    }
    player.setGlowing(false);
    player.setGameMode(GameMode.SURVIVAL);
    for (PotionEffect effect : player.getActivePotionEffects()) {
      player.removePotionEffect(effect.getType());
    }
    player.setFlying(false);
    player.setAllowFlight(false);
    player.getInventory().clear();
    player.getInventory().setArmorContents(null);
    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
    player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
    player.setFireTicks(0);
    player.setFoodLevel(20);
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
      InventorySerializer.loadInventory(plugin, player);
    }
  }

  public static void bringDeathPlayersBack(Arena arena) {
    for (Player player : arena.getPlayers()) {
      if (arena.getPlayersLeft().contains(player)) {
        continue;
      }

      User user = plugin.getUserManager().getUser(player);
      if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INGAME_JOIN_RESPAWN) && user.isPermanentSpectator()){
        continue;
      }
      user.setSpectator(false);

      player.teleport(arena.getStartLocation());
      player.setFlying(false);
      player.setAllowFlight(false);
      //the default fly speed
      player.setFlySpeed(0.1f);
      player.setGameMode(GameMode.SURVIVAL);
      player.removePotionEffect(PotionEffectType.NIGHT_VISION);
      player.removePotionEffect(PotionEffectType.SPEED);
      player.getInventory().clear();
      ArenaUtils.showPlayer(player, arena);
      PlayerData pd = PlayerManager.getPlayerData(player.getName());
      if (pd == null || pd.getDeathInv() == null) {
        user.getKit().giveKitItems(player);        
      } else {
        pd.respawn(player);
      }
      player.updateInventory();
      player.sendMessage(plugin.getChatManager().colorMessage(Messages.BACK_IN_GAME));
    }
  }

  public static Arena initializeArena(String id) {
    Arena arena;
    if (plugin.is1_11_R1()) {
      arena = new ArenaInitializer1_11_R1(id, plugin);
    } else if (plugin.is1_12_R1()) {
      arena = new ArenaInitializer1_12_R1(id, plugin);
    } else if (plugin.is1_13_R1()) {
      arena = new ArenaInitializer1_13_R1(id, plugin);
    } else if (plugin.is1_13_R2()) {
      arena = new ArenaInitializer1_13_R2(id, plugin);
    } else if (plugin.is1_14_R1()) {
      arena = new ArenaInitializer1_14_R1(id, plugin);
    } else if (plugin.is1_15_R1()){
      arena = new ArenaInitializer1_15_R1(id, plugin);
    } else {
      arena = new ArenaInitializer1_16_R1(id, plugin);
    }
    return arena;
  }

  public static void setWorld(Arena arena) {
    if (plugin.is1_11_R1()) {
      ((ArenaInitializer1_11_R1) arena).setWorld(arena.getStartLocation());
    } else if (plugin.is1_12_R1()) {
      ((ArenaInitializer1_12_R1) arena).setWorld(arena.getStartLocation());
    } else if (plugin.is1_13_R1()) {
      ((ArenaInitializer1_13_R1) arena).setWorld(arena.getStartLocation());
    } else if (plugin.is1_13_R2()) {
      ((ArenaInitializer1_13_R2) arena).setWorld(arena.getStartLocation());
    } else if (plugin.is1_14_R1()) {
      ((ArenaInitializer1_14_R1) arena).setWorld(arena.getStartLocation());
    } else if (plugin.is1_15_R1()) {
      ((ArenaInitializer1_15_R1) arena).setWorld(arena.getStartLocation());
    } else if (plugin.is1_16_R1()) {
      ((ArenaInitializer1_16_R1) arena).setWorld(arena.getStartLocation());
    }
  }

  public static void removeSpawnedZombies(Arena arena) {
    boolean eachThree = arena.getZombies().size() > 70;
    int i = 0;
    for (Zombie zombie : arena.getZombies()) {
      if (eachThree && (i % 3) == 0) {
        zombie.getWorld().spawnParticle(Particle.LAVA, zombie.getLocation(), 20);
      }
      zombie.remove();
      i++;
    }
  }

}
