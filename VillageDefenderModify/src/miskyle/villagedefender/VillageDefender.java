package miskyle.villagedefender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.bukkit.configuration.file.YamlConfiguration;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import miskyle.villagedefender.command.CommandManager;
import miskyle.villagedefender.data.ConfigManager;
import miskyle.villagedefender.data.area.AreaConfig;
import miskyle.villagedefender.data.area.ArenaConfig;
import miskyle.villagedefender.data.player.PlayerManager;
import pl.plajer.villagedefense.Main;
public class VillageDefender {
  private static VillageDefender vd;
  
  private Main plugin;
  private BukkitAPIHelper mmApi;
  
  public VillageDefender(Main plugin) {
    vd = this;
    this.plugin = plugin;
    if (!plugin.getDataFolder().exists()) {
      plugin.getDataFolder().mkdir();
    }
    new ConfigManager(plugin);
    new PlayerManager(plugin);
    new CommandManager(plugin);
    new ArenaConfig();
    mmApi = ((MythicMobs)plugin.getServer().getPluginManager().getPlugin("MythicMobs")).getAPIHelper();
    
    plugin.getLogger().info("[miSkYle] => VillageDefender修改 已成功注入!");
    
    change();
  }
  
  private void change() {
    File p = new File(plugin.getDataFolder(),"input.properties");
    if (!p.exists()) {
      return;
    }
    File q = new File(plugin.getDataFolder(),"language.yml");
    File f = new File(plugin.getDataFolder(),"output.yml");
    Properties prop = new Properties();
    YamlConfiguration config = YamlConfiguration.loadConfiguration(q);
    try {
      prop.load(new BufferedReader(new InputStreamReader(new FileInputStream(p),"utf8")));
      for (Object keyO : prop.keySet()) {
        String key = keyO.toString();
        config.set(key, prop.get(keyO).toString());
      }
      config.save(f);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  
//  private void loadAreaConfig() {
//    areas = new HashMap<>();
//    File file = new File(plugin.getDataFolder(),"area-setting.yml");
//    if (!file.exists()) {
//      try {
//        file.createNewFile();
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//      return;
//    }
//    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
//    HashMap<SimpleEntry<Integer, Integer>, WaveConfig> waves = new HashMap<>();
//    SimpleEntry<String, String> nowAreaId = new SimpleEntry<String, String>(null, null);
//    config.getKeys(true).forEach(s->{
//      if (s.replace(".", "@").split("@").length<3) {
//        return;
//      }
//      String areaId = s.replace(".", "@").split("@")[0];
//      String wave = s.split("wave_")[1].replace(".", "@").split("@")[0];
//      String key = areaId+".wave_"+wave;
//      if (nowAreaId.getLeft() == null) {
//        nowAreaId.setLeft(areaId);
//      } else if (!nowAreaId.getLeft().equalsIgnoreCase(areaId)) {
//        HashMap<SimpleEntry<Integer, Integer>, WaveConfig> temp = new HashMap<>();
//        temp.putAll(waves);
//        areas.put(nowAreaId.getLeft(), new AreaConfig(temp));
//        waves.clear();
//      }
//      String[] waveTemp = wave.split("-");
//      String[] mobAmountTemp = config.getString(key+".mob-amount").split("-");
//      SimpleEntry<Integer, Integer> mobAmount = new SimpleEntry<>(Integer.parseInt(mobAmountTemp[0]), Integer.parseInt(mobAmountTemp[1]));
//      HashMap<String, Double> spawnChance = new HashMap<>();
//      config.getStringList(key+".mm-mob").forEach( line -> {
//        String[] temp = line.split(":");
//        spawnChance.put(temp[0], Double.parseDouble(temp[1]));
//      });
//      HashMap<String, Integer> mustSpawn = new HashMap<>();
//      config.getStringList(key+".must-spawn").forEach( line -> {
//        String[] temp = line.split(":");
//        mustSpawn.put(temp[0], Integer.parseInt(temp[1]));
//      });
//      WaveConfig waveConfig = new WaveConfig(mobAmount, spawnChance, mustSpawn, config.getInt(key+".coin",0));
//      waves.put(new SimpleEntry<Integer, Integer>(Integer.parseInt(waveTemp[0]), Integer.parseInt(waveTemp[1])), waveConfig);
//    });
//    areas.put(nowAreaId.getLeft(), new AreaConfig(waves));
//  }
  
  public static AreaConfig getArea(String id) {
    return ArenaConfig.getArea(id);
  }
  
  public static Main getPlugin() {
    return vd.plugin;
  }
  
  public static BukkitAPIHelper getMMApi() {
    return vd.mmApi;
  }
}
