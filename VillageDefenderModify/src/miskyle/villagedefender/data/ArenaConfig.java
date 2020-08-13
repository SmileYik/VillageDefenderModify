package miskyle.villagedefender.data;

import java.io.File;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import miskyle.villagedefender.VillageDefender;
import miskyle.villagedefender.data.area.AreaConfig;
import miskyle.villagedefender.data.area.WaveConfig;
import miskyle.villagedefender.utils.SimpleEntry;

public class ArenaConfig {
  private static ArenaConfig ac;
  private HashMap<String, AreaConfig> areas;
  
  public ArenaConfig() {
    ac = this;
    loadAreas();
  }
  
  private void loadAreas() {
    areas = new HashMap<>();
    if (!new File(VillageDefender.getPlugin().getDataFolder(), "ArenaConfig").exists()) {
      new File(VillageDefender.getPlugin().getDataFolder(), "ArenaConfig").mkdirs();
      return;
    }
    
    for (File file : new File(VillageDefender.getPlugin().getDataFolder(), "ArenaConfig").listFiles()) {
      if (file.isFile()) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        AreaConfig area = new AreaConfig(new HashMap<>());
        for (String wave : config.getStringList("wave-list")) {
          String key = "waves." + wave;
          String[] waveString = wave.split("-");
          String[] amount = config.getString(key + ".mob-amount").split("-");
          int coin = config.getInt(key + ".coin");
          HashMap<String, Double> mmMob = new HashMap<>();
          config.getStringList(key + ".mm-mob").forEach(str -> {
            String[] temp = str.split(":");
            mmMob.put(temp[0], Double.parseDouble(temp[1]));
          });
          HashMap<String, Integer> mustSpawn = new HashMap<>();
          config.getStringList(key + ".must-spawn").forEach(str -> {
            String[] temp = str.split(":");
            mustSpawn.put(temp[0], Integer.parseInt(temp[1]));
          });
          SimpleEntry<Integer, Integer> mobAmount = 
              new SimpleEntry<Integer, Integer>(Integer.parseInt(amount[0]), Integer.parseInt(amount[1]));
          SimpleEntry<Integer, Integer> waveEntry = 
              new SimpleEntry<Integer, Integer>(Integer.parseInt(waveString[0]), Integer.parseInt(waveString[1]));
          WaveConfig waveC = new WaveConfig(mobAmount, mmMob, mustSpawn, coin);
          area.getWaves().put(waveEntry, waveC);
        }
        area.setFinalWave(config.getInt("final-wave",999));
        area.setJoinMidway(config.getBoolean("join-midway", false));
        String id = config.getString("id");
        areas.put(id, area);
        VillageDefender.getPlugin().getLogger().info("[miSkYle] => 成功加载 "+id+" 设定");
      }
    }
  }
  
  public static AreaConfig getArea(String id) {
    return ac.areas.get(id);
  }
  
}
