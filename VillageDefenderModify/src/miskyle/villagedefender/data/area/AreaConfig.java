package miskyle.villagedefender.data.area;

import java.util.HashMap;
import java.util.Map;

import miskyle.villagedefender.utils.SimpleEntry;

public class AreaConfig {
  private HashMap<SimpleEntry<Integer, Integer>, WaveConfig> waves;
  private boolean mustSpawn = false;
  private int finalWave;

  public AreaConfig(HashMap<SimpleEntry<Integer, Integer>, WaveConfig> waves) {
    super();
    this.waves = waves;
  }

  public HashMap<SimpleEntry<Integer, Integer>, WaveConfig> getWaves() {
    return waves;
  }

  public void setWaves(HashMap<SimpleEntry<Integer, Integer>, WaveConfig> waves) {
    this.waves = waves;
  }
  
  public WaveConfig getWave(int wave) {
    for (Map.Entry<SimpleEntry<Integer, Integer>, WaveConfig> entry : waves.entrySet()) {
      if (wave >= entry.getKey().getLeft() && wave <= entry.getKey().getRight()) {
        return entry.getValue();
      }
    }
    return null;
  }
  
  public void startWave() {
    mustSpawn = false;
  }
  
  public void startSpawn() {
    mustSpawn = true;
  }
  
  public boolean isMustSpawn() {
    return mustSpawn;
  }

  public int getFinalWave() {
    return finalWave;
  }

  public void setFinalWave(int finalWave) {
    this.finalWave = finalWave;
  }
  
}
