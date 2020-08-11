package miskyle.villagedefender.command.kitcreator;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import miskyle.villagedefender.data.kit.DIYKit;

public class KitCreator {
  private static HashMap<String, KitCreator> creators = new HashMap<>();
  
  private DIYKit kit = new DIYKit();
  
  public static void start(Player p) {
    if (creators.containsKey(p.getName())) {
      p.sendMessage("你正在创造一个Kit");
      return;
    } else {
      creators.put(p.getName(), new KitCreator());
    }
  }
  
  public static void stop(Player p) {
    if (!creators.containsKey(p.getName())) {
      p.sendMessage("你并未正在创造一个Kit");
      return;
    } else {
      creators.remove(p.getName());
    }
  }
  
  public static void setArmor(Player p) {
    if (!creators.containsKey(p.getName())) {
      p.sendMessage("你并未正在创造一个Kit");
      return;
    }
    KitCreator kc = creators.get(p.getName());
    kc.kit.setArmors(new HashMap<>());
    if (p.getEquipment().getHelmet() != null) {
      kc.kit.getArmors().put(1, p.getEquipment().getHelmet());      
    }
    if (p.getEquipment().getBoots() != null) {
      kc.kit.getArmors().put(4, p.getEquipment().getBoots());      
    }
    if (p.getEquipment().getChestplate() != null) {
      kc.kit.getArmors().put(2, p.getEquipment().getChestplate());      
    }
    if (p.getEquipment().getLeggings() != null) {
      kc.kit.getArmors().put(3, p.getEquipment().getLeggings());      
    }
    p.sendMessage("设定装备成功");
  }
  
  public static void setInv(Player p) {
    if (!creators.containsKey(p.getName())) {
      p.sendMessage("你并未正在创造一个Kit");
      return;
    }
    KitCreator kc = creators.get(p.getName());
    kc.kit.setItems(new ArrayList<>());
    for (ItemStack item : p.getInventory().getContents()) {
      if (item == null) {
        continue;
      }
      kc.kit.getItems().add(item.clone());
    }
    p.sendMessage("设定初始物品成功, 请注意装备槽及副手槽物品是否为空, 若不为空则此物品也会算进");
  }
  
  public static void setReStockInv(Player p) {
    if (!creators.containsKey(p.getName())) {
      p.sendMessage("你并未正在创造一个Kit");
      return;
    }
    KitCreator kc = creators.get(p.getName());
    kc.kit.setReStockItems(new HashMap<>());
    for (ItemStack item : p.getInventory()) {
      if (item != null) {
        if (kc.kit.getReStockItems().containsKey(1)) {
          kc.kit.getReStockItems().get(1).add(item.clone());
        } else {
          ArrayList<ItemStack> items = new ArrayList<>();
          items.add(item.clone());
          kc.kit.getReStockItems().put(1, items);
        }
      }
    }
    p.sendMessage("设定恢复物品成功, 请注意装备槽及副手槽物品是否为空, 若不为空则此物品也会算进");
  }
  
  public static void  setMetarial(Player p) {
    if (!creators.containsKey(p.getName())) {
      p.sendMessage("你并未正在创造一个Kit");
      return;
    }
    if (p.getInventory().getItemInMainHand() == null 
        || p.getInventory().getItemInMainHand().getType() == Material.AIR) {
      p.sendMessage("你手中什么物品都没拿哎.");
    }
    KitCreator kc = creators.get(p.getName());
    kc.kit.setMaterial(p.getInventory().getItemInMainHand().getType().name());
    p.sendMessage("设定材质成功, 材质具有唯一性, 若已有Kit有相同材质则此kit可能不会显示出来.");
  }
  
  public static void setLevel(Player p, int level) {
    if (!creators.containsKey(p.getName())) {
      p.sendMessage("你并未正在创造一个Kit");
      return;
    }
    KitCreator kc = creators.get(p.getName());
    kc.kit.setLevel(level);
    p.sendMessage("设定等级成功");
  }
  
  public static void setGold(Player p, double gold) {
    if (!creators.containsKey(p.getName())) {
      p.sendMessage("你并未正在创造一个Kit");
      return;
    }
    KitCreator kc = creators.get(p.getName());
    kc.kit.setGold(gold);
    p.sendMessage("设定价格成功");
  }
  
  public static void setPermission(Player p, String permission) {
    if (!creators.containsKey(p.getName())) {
      p.sendMessage("你并未正在创造一个Kit");
      return;
    }
    KitCreator kc = creators.get(p.getName());
    kc.kit.setPermission(permission);
    p.sendMessage("设置权限成功成功");
  }
  
  public static void setId(Player p, String id) {
    if (!creators.containsKey(p.getName())) {
      p.sendMessage("你并未正在创造一个Kit");
      return;
    }
    KitCreator kc = creators.get(p.getName());
    kc.kit.setId(id);
    p.sendMessage("设定ID成功, 默认显示名称也为该ID(请去配置文件修改)");
  }
  
  public static void setHealth(Player p, double health) {
    if (!creators.containsKey(p.getName())) {
      p.sendMessage("你并未正在创造一个Kit");
      return;
    }
    KitCreator kc = creators.get(p.getName());
    kc.kit.setHealth(health);
    p.sendMessage("设定生命值成功");
  }
  
  public static void save(Player p) {
    if (!creators.containsKey(p.getName())) {
      p.sendMessage("你并未正在创造一个Kit");
      return;
    }
    KitCreator kc = creators.get(p.getName());
    kc.kit.save();
    creators.remove(p.getName());
    p.sendMessage("保存成功, 若有需求请修改配置文件, 修改完后请使用/vdmm reload进行重载");
  }
}
