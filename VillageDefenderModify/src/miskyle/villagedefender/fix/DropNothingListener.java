package miskyle.villagedefender.fix;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import miskyle.villagedefender.VillageDefender;
import pl.plajer.villagedefense.Main;

public class DropNothingListener implements Listener {
  private static DropNothingListener dnl;
  
  public DropNothingListener(Main plugin) {
    if (dnl != null) {
      HandlerList.unregisterAll(dnl);
    }
    dnl = this;
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }
  
  
  @EventHandler
  public void onHitFriend(MythicMobDeathEvent e) {
    if (e.getKiller() != null && VillageDefender.getMMApi().isMythicMob(e.getKiller())) {
      e.setDrops(new ArrayList<>());
    }
  }
  
  @EventHandler
  public void killByIronGolem(MythicMobDeathEvent e) {
    if (e.getKiller() != null && e.getKiller().getType() == EntityType.IRON_GOLEM) {
      e.setDrops(new ArrayList<>());
    }
  }
}
