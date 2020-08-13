package miskyle.villagedefender.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import miskyle.villagedefender.VillageDefender;
import miskyle.villagedefender.data.kit.DIYKit;
import miskyle.villagedefender.data.kit.KitListener;
import pl.plajer.villagedefense.Main;
import pl.plajer.villagedefense.kits.KitRegistry;
import pl.plajer.villagedefense.kits.basekits.Kit;
import pl.plajer.villagedefense.kits.free.KnightKit;

public class DIYKitManager {
  private static String path;
  private static String defaultKit;
  private static HashMap<String, List<String>> kitsManager;
  private static HashMap<String, DIYKit> kits;
  private static KitListener listener;

  public static void init(Main plugin) {
    path = plugin.getDataFolder() + "/DIYKits";
    firstLoad();
    loadKits();
    loadConfig();
  }
  
  private static void firstLoad() {
    if (!new File(path).exists()) {
      new File(path).mkdir();
    }
    File file = new File(VillageDefender.getPlugin().getDataFolder(), "DIYKitManager.yml");
    if (!file.exists()) {
      setDefualtConfig(file);
    }
  }
  
  private static void loadConfig() {
    File file = new File(VillageDefender.getPlugin().getDataFolder(), "DIYKitManager.yml");
    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
    //加载默认Kit
    defaultKit = config.getString("default-kit", "");
    if (!defaultKit.isEmpty()) {
      KitRegistry.setDefaultKit(kits.get(defaultKit));
    }
    
    //设定
    kitsManager = new HashMap<>();
    config.getKeys(false).forEach(key -> {
      if (key.equalsIgnoreCase("default-kit") 
          || key.equalsIgnoreCase("unload-default-kits")) {
        return;
      }
      List<String> list = config.getStringList(key);
      if (list.size() == 1 && list.get(0).equalsIgnoreCase("all")) {
        ArrayList<String> temp = new ArrayList<>();
        temp.addAll(kits.keySet());
        list = temp;
      }
      kitsManager.put(key, list);
    });
    
    if (config.getBoolean("unload-default-kits")) {
      Bukkit.getScheduler().runTaskLaterAsynchronously(VillageDefender.getPlugin(), () -> {
        unloadDefaultKit();        
      }, 100L);
      if (listener != null) {
        HandlerList.unregisterAll(listener);
      }
      listener = new KitListener();
      VillageDefender.getPlugin().getServer().getPluginManager().registerEvents(listener, VillageDefender.getPlugin());
    }
  }
  
  /**
   * 卸载插件自带Kit
   */
  public static void unloadDefaultKit() {
    Iterator<Kit> iterator = KitRegistry.getKits().iterator();
    while (iterator.hasNext()) {
      Kit kit = iterator.next();
      if (defaultKit.isEmpty() && kit instanceof KnightKit) {
        continue;
      }
      if (!(kit instanceof DIYKit)) {
        if (kit instanceof Listener) {
          HandlerList.unregisterAll((Listener)kit);
        }
        iterator.remove();
        KitRegistry.getKits().remove(kit);
      }
    }
    VillageDefender.getPlugin().getLogger().info("[miSkYle] => 原版Kit已卸载.");
  }
  
  private static void setDefualtConfig(File file) {
    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
    config.set("default-kit", "");
    config.set("unload-default-kits", true);
    
    try {
      config.save(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
  private static void loadKits() {
    kits = new HashMap<>();
    Iterator<Kit> iterator = KitRegistry.getKits().iterator();
    while (iterator.hasNext()) {
      Kit kit = iterator.next();
      if (kit instanceof DIYKit) {
        iterator.remove();
        KitRegistry.getKits().remove(kit);
      }
    }

    for (File file : new File(path).listFiles()) {
      if (file.isFile()) {
        loadDIYKits(file.getName());
      }
    }
  }

  public static void loadDIYKits(String fileName) {
    File file = new File(path + "/" + fileName);
    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

    for (String id : config.getKeys(false)) {
      DIYKit kit = new DIYKit();
      kit.setId(id);
      kit.setDescription(config.getString(id + ".des").split("%n"));
      kit.setName(config.getString(id + ".name"));
      kit.setPermission(config.getString(id + ".permission", null));
      kit.setMaterial(config.getString(id + ".material"));
      kit.setLevel(config.getInt(id + ".level", -1));
      kit.setHealth(config.getDouble(id + ".health", 20));
      kit.setGold(config.getDouble(id + ".gold", 0));

      List<ItemStack> items = new LinkedList<ItemStack>();
      HashMap<Integer, List<ItemStack>> reStockItems = new HashMap<>();
      HashMap<Integer, ItemStack> armors = new HashMap<Integer, ItemStack>();

      int index = 0;
      while (config.contains(id + ".item.items." + index)) {
        items.add(config.getItemStack(id + ".item.items." + index++));
      }

      if (config.contains(id + ".item.armors.1")) {
        armors.put(1, config.getItemStack(id + ".item.armors.1"));
      }
      if (config.contains(id + ".item.armors.2")) {
        armors.put(2, config.getItemStack(id + ".item.armors.2"));
      }
      if (config.contains(id + ".item.armors.3")) {
        armors.put(3, config.getItemStack(id + ".item.armors.3"));
      }
      if (config.contains(id + ".item.armors.4")) {
        armors.put(4, config.getItemStack(id + ".item.armors.4"));
      }

      index = 0;
      while (config.contains(id + ".item.re-sock-items." + index)) {
        String key = id + ".item.re-sock-items." + index++;
        int wave = config.getInt(key + ".wave",1);
        ItemStack item = config.getItemStack(key + ".item");
        if (reStockItems.containsKey(wave)) {
          reStockItems.get(wave).add(item);
        } else {
          ArrayList<ItemStack> itemsList = new ArrayList<>();
          itemsList.add(item);
          reStockItems.put(wave, itemsList);
        }
      }

      kit.setArmors(armors);
      kit.setReStockItems(reStockItems);
      kit.setItems(items);
      kits.put(kit.getId(), kit);
      KitRegistry.registerKit(kit);
      VillageDefender.getPlugin().getLogger().info("[miSkYle] => 加载DIYKit: " + kit.getId());
    }
  }
  
  public static List<String> getIdList(String str){
    return kitsManager.get(str);
  }
}
