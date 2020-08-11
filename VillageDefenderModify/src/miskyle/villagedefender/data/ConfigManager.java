package miskyle.villagedefender.data;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.miskyle.mcpt.mysql.MySQLManager;

import pl.plajer.villagedefense.Main;

public class ConfigManager {
  public static final String ADD_GOLD_MESSAGE = "message.add-gold";
  public static final String NO_ENOUGH_GOLD = "message.no-enough-gold";
  public static final String CAN_NOT_DROP = "message.can-not-drop";
  
  private static ConfigManager cm;

  private Main plugin;
  private YamlConfiguration config;
  private HashMap<String, Double> golds;

  public ConfigManager(Main plugin) {
    cm = this;
    this.plugin = plugin;

    loadConfig();
    setupMySQL();
    loadLootGold();
    DIYKitManager.init(plugin);
  }

  private void loadConfig() {
    File file = new File(plugin.getDataFolder(), "config.miskyle.yml");
    config = YamlConfiguration.loadConfiguration(file);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void setupMySQL() {
    String host = config.getString("mysql.host");
    String database = config.getString("mysql.database");
    String username = config.getString("mysql.username");
    String password = config.getString("mysql.password");
    int port = config.getInt("mysql.port");
    MySQLManager.setupMySQl(host, port, database, username, password);

    MySQLManager.connect();
    try {
      if (!MySQLManager.execute("show tables like 'VillageDefenderMiskyle'").executeQuery().next()) {
        // Create Table
        MySQLManager.execute("create table VillageDefenderMiskyle(\r\n" + "Name VARCHAR(50),\r\n" + "Gold DOUBLE\r\n"
            + ")default charset=utf8;").execute();
      }
    } catch (SQLException e) {
      MySQLManager.disconnect();
      e.printStackTrace();
    }
    MySQLManager.disconnect();
  }

  private void loadLootGold() {
    golds = new HashMap<>();
    config.getStringList("gold").forEach(line -> {
      String[] temp = line.split(":");
      golds.put(temp[0], Double.parseDouble(temp[1]));
    });
  }

  public static double getLootGold(String mmType) {
    if (cm.golds.containsKey(mmType)) {
      return cm.golds.get(mmType);
    }
    return 0;
  }
  
  public static String getMsg(String key, Object ... orgs) {
    return MessageFormat.format(cm.config.getString(key), orgs);
  }
  
  public static String getMsg(String key) {
    return cm.config.getString(key);
  }
  
  public static String getLore(String key) {
    return cm.config.getString(key);
  }
}
