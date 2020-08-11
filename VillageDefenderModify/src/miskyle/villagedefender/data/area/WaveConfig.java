package miskyle.villagedefender.data.area;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import miskyle.villagedefender.SimpleEntry;

public class WaveConfig {
  private SimpleEntry<Integer, Integer> mobAmount;
  private HashMap<String, Double> spawnChance;
  private HashMap<String, Integer> mustSpawn;
  
  private int coin;
  
  public WaveConfig(SimpleEntry<Integer, Integer> mobAmount, HashMap<String, Double> spawnChance,
      HashMap<String, Integer> mustSpawn, int coin) {
    super();
    this.mobAmount = mobAmount;
    this.spawnChance = spawnChance;
    this.mustSpawn = mustSpawn;
    this.coin = coin;
  }
  public HashMap<String, Integer> getMustSpawn() {
    return mustSpawn;
  }
  public void setMustSpawn(HashMap<String, Integer> mustSpawn) {
    this.mustSpawn = mustSpawn;
  }
  public SimpleEntry<Integer, Integer> getMobAmount() {
    return mobAmount;
  }
  public void setMobAmount(SimpleEntry<Integer, Integer> mobAmount) {
    this.mobAmount = mobAmount;
  }
  public HashMap<String, Double> getSpawnChance() {
    return spawnChance;
  }
  public int getCoin() {
    return coin;
  }
  public void setCoin(int coin) {
    this.coin = coin;
  }
  public void setSpawnChance(HashMap<String, Double> spawnChance) {
    this.spawnChance = spawnChance;
  }
  public ArrayList<String> getRandomSpawn(){
    ArrayList<String> list = new ArrayList<>();
    list.addAll(spawnChance.keySet());
    list.sort(new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        double temp = Math.random();
        if (temp < 0.5) {
          return -1;
        } else {
          return 1;
        }
      }
    });
    return list;
  }
  public boolean checkSpawn(String name) {
    return Math.random()<spawnChance.get(name);
  }
  public int randomMobAmount() {
    return (int)(Math.random()*(mobAmount.getRight()-mobAmount.getLeft()+1)+mobAmount.getLeft());
  }
  
}
