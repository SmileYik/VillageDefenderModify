package miskyle.villagedefender.data.kit;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import miskyle.villagedefender.VillageDefender;
import pl.plajer.villagedefense.api.StatsStorage.StatisticType;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.kits.basekits.Kit;

public class DIYKit extends Kit {

  private String id;
  private String permission;
  private int level;
  private double health;
  private double gold = 0;

  private String material;

  private List<ItemStack> items;
  private HashMap<Integer, List<ItemStack>> reStockItems;
  //Key: 1-头 2-身体 3-腿 4-脚
  private HashMap<Integer, ItemStack> armors;

  public DIYKit() {
    super(0);
  }
  
  public void save() {
    File file = new File(new File(VillageDefender.getPlugin().getDataFolder()+"/DIYKits"),id+".yml");
    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
    config.set(id+".des", "这个职业很懒%n还没有介绍自己");
    config.set(id+".name", id);
    config.set(id+".permission", permission);
    config.set(id+".level", level);
    config.set(id+".health", health);
    config.set(id+".gold", gold);
    config.set(id+".material", material);
    
    armors.forEach((k, v) -> {
      config.set(id+".item.armors."+k, v);
    });
    
    int index = 0;
    for (ItemStack item : items) {
      config.set(id+".item.items."+index++, item);
    }
    
    index = 0;
    
    for (Map.Entry<Integer, List<ItemStack>> entry : reStockItems.entrySet()) {
      for (ItemStack item : entry.getValue()) {
        config.set(id+".item.re-sock-items."+index+".item", item);
        config.set(id+".item.re-sock-items."+index+++".wave", entry.getKey());
      }
    }
    
    try {
      config.save(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }

  @Override
  public boolean isUnlockedByPlayer(Player p) {
    if (permission == null || permission.isEmpty()) {
      if (getPlugin().getUserManager().getUser(p).getStat(StatisticType.LEVEL) >= level) {
        return true;
      } else if (p.isOp()) {
        return true;
      } else {
        return false;
      }
    } else {
      if (p.isOp()) {
        return true;
      } else if (p.hasPermission(permission) && getPlugin().getUserManager().getUser(p).getStat(StatisticType.LEVEL) >= level) {
        return true;
      } else {
        return false;
      }
    }
  }

  @Override
  public void giveKitItems(Player player) {

    player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
    player.setHealth(health);
    
    for (ItemStack item : items) {
      player.getInventory().addItem(item.clone());
    }

    for (Map.Entry<Integer, ItemStack> entry : armors.entrySet()) {
      switch (entry.getKey()) {
      case 1: {
        player.getInventory().setHelmet(entry.getValue().clone());
        break;
      }
      case 2: {
        player.getInventory().setChestplate(entry.getValue().clone());
        break;
      }
      case 3: {
        player.getInventory().setLeggings(entry.getValue().clone());
        break;
      }
      case 4: {
        player.getInventory().setBoots(entry.getValue().clone());
        break;
      }
      }
    }
  }

  @Override
  public Material getMaterial() {
    return Material.valueOf(material);
  }

  /**
   * 每回合补给物品
   */
  @Override
  public void reStock(Player player) {
    Arena arena = ArenaRegistry.getArena(player);
    if (arena == null) {
      return;
    }
    for (Map.Entry<Integer, List<ItemStack>> entry : reStockItems.entrySet()) {
      if (arena.getWave() % entry.getKey() == 0) {
        for (ItemStack item : entry.getValue()) {
          player.getInventory().addItem(item.clone()).values().forEach( i -> {
            player.getWorld().dropItem(player.getLocation(), i);
          });
        }
      }
    }

  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public List<ItemStack> getItems() {
    return items;
  }

  public void setItems(List<ItemStack> items) {
    this.items = items;
  }

  public HashMap<Integer, List<ItemStack>> getReStockItems() {
    return reStockItems;
  }

  public void setReStockItems(HashMap<Integer, List<ItemStack>> reStockItems) {
    this.reStockItems = reStockItems;
  }

  public HashMap<Integer, ItemStack> getArmors() {
    return armors;
  }

  public void setArmors(HashMap<Integer, ItemStack> armors) {
    this.armors = armors;
  }

  public void setMaterial(String material) {
    this.material = material;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public double getHealth() {
    return health;
  }

  public void setHealth(double health) {
    this.health = health;
  }

  public double getGold() {
    return gold;
  }

  public void setGold(double gold) {
    this.gold = gold;
  }
  
}
