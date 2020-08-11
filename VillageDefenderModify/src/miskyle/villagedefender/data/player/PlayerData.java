package miskyle.villagedefender.data.player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.miskyle.mcpt.mysql.MySQLManager;

import miskyle.villagedefender.VillageDefender;
import miskyle.villagedefender.data.ConfigManager;
import miskyle.villagedefender.data.kit.DIYKit;
import pl.plajer.villagedefense.user.User;

public class PlayerData {
  private String name;
  private double gold;
  private double inGameGold;
  private ArrayList<ItemStack> deathInv;
  public PlayerData(String name, double gold) {
    super();
    this.name = name;
    this.gold = gold;
  }
  protected static PlayerData load(String playerName) {
    MySQLManager.connect();
    try {
      ResultSet rs = MySQLManager.execute(
          "SELECT * FROM VillageDefenderMiskyle WHERE Name='"+playerName+"'").executeQuery();
      if (rs.next()) {
        return new PlayerData(playerName, rs.getDouble("Gold"));
      } else {
        return new PlayerData(playerName, 0);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    MySQLManager.disconnect();
    return new PlayerData(playerName, 0);
  }
  
  @Override
  public String toString() {
    return "PlayerData [name=" + name + ", gold=" + gold + "]";
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public double getGold() {
    return gold;
  }
  public void setGold(double gold) {
    this.gold = gold;
    update();
  }
  public void modifyGold(double value) {
    gold += value;
    update();
    Bukkit.getServer().getPlayer(name).sendMessage(ConfigManager.getMsg(ConfigManager.ADD_GOLD_MESSAGE, _2f(value), _2f(gold)));
  }
  
  public void modifyInGameGold(double value) {
    inGameGold += value;
    Bukkit.getServer().getPlayer(name).sendMessage(ConfigManager.getMsg(ConfigManager.ADD_GOLD_MESSAGE, _2f(value), _2f(inGameGold)));
  }
  
  public void gameOver() {
    modifyGold(inGameGold);
    inGameGold = 0;
    deathInv = null;
  }
  
  public void startGame() {
    inGameGold = 0;
    deathInv = null;
  }
  
  public void death(Player p) {
    deathInv = new ArrayList<>();
    for (ItemStack item : p.getInventory()) {
      if (item != null) {
        deathInv.add(item.clone());
      }
    }
    
    User user = VillageDefender.getPlugin().getUserManager().getUser(p);
    if (user.getKit() instanceof DIYKit) {
      p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(((DIYKit)user.getKit()).getHealth());
    } else {
      p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
    }
  }
  
  public void respawn(Player p) {
    deathInv.forEach(item->{
      p.getInventory().addItem(item.clone()).values().forEach(i->{
        p.getWorld().dropItem(p.getLocation(), i);
      });
    });
    deathInv = null;
  }
  
  public void stopGame() {
    modifyGold(0);
    inGameGold = 0;
    deathInv = null;
  }
  
  
  
  public double getInGameGold() {
    return inGameGold;
  }
  public void setInGameGold(double inGameGold) {
    this.inGameGold = inGameGold;
  }
  public ArrayList<ItemStack> getDeathInv() {
    return deathInv;
  }
  private String _2f(double d) {
    return String.format("%.2f", d);
  }
  
  public void update() {
    MySQLManager.connect();
    try {
      ResultSet rs = MySQLManager.execute(
          "SELECT * FROM VillageDefenderMiskyle WHERE Name='"+name+"'").executeQuery();
      if(rs.next()) {
        String sql = "UPDATE `villagedefendermiskyle` SET `Gold`="+gold+" WHERE Name = '"+name+"'";
        MySQLManager.execute(sql).execute();
      }else {
        String sql = "INSERT INTO VillageDefenderMiskyle VALUES ('"
              +name+"','"
              +gold+"')";
        MySQLManager.execute(sql).execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try {
      ResultSet rs = MySQLManager.execute(
          "SELECT * FROM VillageDefenderMiskyle WHERE Name='"+name+"'").executeQuery();
      if (rs.next()) {
        gold = rs.getDouble("Gold");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    MySQLManager.disconnect();
  }
  
}
