package miskyle.villagedefender.cleaner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import pl.plajer.villagedefense.Main;

public class MobCleaner implements Listener {
  private static MobCleaner mc;
  private List<String> cleanList;
  private ArrayList<Entity> entitys;
  
  public MobCleaner(Main plugin, List<String> cleanList) {
    this.cleanList = cleanList;
    if (entitys == null) {
      entitys = new ArrayList<>();
    } else {
      entitys.forEach(e -> {
        e.remove();
      });
      entitys.clear();
    }
    if (mc != null) {
      HandlerList.unregisterAll(mc);
    }
    mc = this;
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }
  
  @EventHandler
  public void onMobSpawn(MythicMobSpawnEvent e) {
    if (cleanList.contains(e.getMob().getMobType())) {
      entitys.add(e.getEntity());
    }
  }
  
  public static void clear() {
    
    Iterator<Entity> iterator = mc.entitys.iterator();
    while (iterator.hasNext()) {
      Entity e = iterator.next();
      if (e.getPassengers().isEmpty()) {
        e.remove();
        iterator.remove();
        mc.entitys.remove(e);
      }
    }
  }
  
}
