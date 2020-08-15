package miskyle.villagedefender.data.player;

import java.util.HashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import miskyle.villagedefender.data.ConfigManager;
import miskyle.villagedefender.data.ReJoinSystem;
import miskyle.villagedefender.data.kit.DIYKit;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.api.event.game.VillageGameStartEvent;
import pl.plajer.villagedefense.api.event.player.VillagePlayerChooseKitEvent;
import pl.plajer.villagedefense.user.User;

public class PlayerManager implements Listener{
  private static PlayerManager pm;
  
  private HashMap<String, PlayerData> playerDatas;
  private Main plugin;
  
  public PlayerManager(Main plugin) {
    if (pm != null) {
      HandlerList.unregisterAll(pm);
    }
    pm = this;
    this.plugin = plugin;
    playerDatas = new HashMap<>();
    loadPlayer(plugin);
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  private void loadPlayer(Main plugin) {
    plugin.getServer().getOnlinePlayers().forEach(p->{
      playerDatas.put(p.getName(), PlayerData.load(p.getName()));
    });
  }
  
  public static PlayerData getPlayerData(String name) {
    return pm.playerDatas.get(name);
  }
  
  @EventHandler
  public void playerJoin(PlayerJoinEvent e) {
    playerDatas.put(e.getPlayer().getName(), PlayerData.load(e.getPlayer().getName()));
    ReJoinSystem.reJoin(e.getPlayer());
  }
  
  @EventHandler
  public void playerQuit(PlayerQuitEvent e) {
    playerDatas.remove(e.getPlayer().getName());
  }
  
  @EventHandler
  public void playerKick(PlayerKickEvent e) {
    playerDatas.remove(e.getPlayer().getName());
  }
  
  @EventHandler
  public void playerChooseKit(VillagePlayerChooseKitEvent e) {
    if (e.getKit() instanceof DIYKit) {
      DIYKit kit = (DIYKit) e.getKit();
      PlayerData pd = getPlayerData(e.getPlayer().getName());
      if (pd == null) {
        return;
      }
      if(kit.getGold() != 0) {
        if(pd.getGold() < kit.getGold()) {
          e.setCancelled(true);
          e.getPlayer().sendMessage(ConfigManager.getMsg(ConfigManager.NO_ENOUGH_GOLD, _2f(kit.getGold()),_2f(pd.getGold())));
        }
      }
    }
  }
  
  @EventHandler
  public void gameStart(VillageGameStartEvent e) {
    e.getArena().getPlayers().forEach(p->{
      User user = plugin.getUserManager().getUser(p);
      PlayerData pd = getPlayerData(p.getName());
      if (pd == null || user == null) {
        return;
      }
      pd.startGame();
      if (user.getKit() == null) {
        return;
      }
      if (user.getKit() instanceof DIYKit) {
        double gold = ((DIYKit)user.getKit()).getGold();
        if (gold != 0) {
          pd.modifyGold(-gold);        
        }        
      }
    });
  }

  private String _2f(double d) {
    return String.format("%.2f", d);
  }

}
