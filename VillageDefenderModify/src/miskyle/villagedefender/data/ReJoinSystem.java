package miskyle.villagedefender.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

import pl.plajer.villagedefense.arena.ArenaManager;
import pl.plajer.villagedefense.arena.ArenaRegistry;

public class ReJoinSystem {
  private static HashMap<String, ArrayList<String>> player = new HashMap<>();
  
  public static void joinGame(Player p, String arena) {
    if (player.containsKey(arena)) {
      if (!player.containsKey(p.getName())) {
        player.get(arena).add(p.getName());        
      }
    } else {
      ArrayList<String> list = new ArrayList<>();
      list.add(p.getName());
      player.put(arena, list);
    }
  }
  
  public static boolean canJoin(Player p, String arena) {
    if (player.containsKey(arena)) {
      return player.get(arena).contains(p.getName());
    }
    return false;
  }
  
  public static void leaveGame(Player p, String arena) {
    if (player.containsKey(arena)) {
      player.get(arena).remove(p.getName());
    }
  }
  
  public static void gameStop(String arena) {
    player.remove(arena);
  }
  
  public static void reJoin(Player p) {
    player.forEach((k, v) -> {
      if (v.contains(p.getName())) {
        ArenaManager.joinAttempt(p, ArenaRegistry.getArena(k));
      } else {
        return;
      }
    });
  }
}
