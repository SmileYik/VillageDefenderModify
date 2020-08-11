package miskyle.villagedefender.command;

import org.bukkit.command.CommandSender;

import miskyle.villagedefender.VillageDefender;
import miskyle.villagedefender.data.player.PlayerData;
import miskyle.villagedefender.data.player.PlayerManager;

public class VillageDefemderMiSkYleCMD {
  
  @Cmd(subCmd = {"take"},
      args = {"","玩家","数额"},
      des = "减少玩家账户金额",
      permission = "VillageDefemderMiSkYle.Admin",
      needPlayer =  false)
  public void takeMoney(CommandSender sender, String[] args) {
    PlayerData pd = PlayerManager.getPlayerData(args[1]);
    if (pd == null) {
      sender.sendMessage("未找到相对应玩家. 请确认玩家名是否正确或玩家是否在线.");
      return;
    }
    Double value = null;
    try {
      value = Double.parseDouble(args[2]);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    if (value == null) {
      sender.sendMessage("请输入正确的金额.");
      return;
    }
    if (value > pd.getGold()) {
      sender.sendMessage("该玩家账户中金额不足.");
      return;
    }
    pd.modifyGold(-value);
  }
  
  @Cmd(subCmd = {"give"},
      args = {"","玩家","数额"},
      des = "增加玩家账户金额",
      permission = "VillageDefemderMiSkYle.Admin",
      needPlayer = false)
  public void giveMoney(CommandSender sender, String[] args) {
    PlayerData pd = PlayerManager.getPlayerData(args[1]);
    if (pd == null) {
      sender.sendMessage("未找到相对应玩家. 请确认玩家名是否正确或玩家是否在线.");
      return;
    }
    Double value = null;
    try {
      value = Double.parseDouble(args[2]);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
    if (value == null) {
      sender.sendMessage("请输入正确的金额.");
      return;
    }
    pd.modifyGold(value);
  }
  
  @Cmd(subCmd = {"check"},
      args = {"","玩家"},
      des = "查看玩家账户金额",
      permission = "VillageDefemderMiSkYle.Admin",
      needPlayer = false)
  public void checkMoney(CommandSender sender, String[] args) {
    PlayerData pd = PlayerManager.getPlayerData(args[1]);
    if (pd == null) {
      sender.sendMessage("未找到相对应玩家. 请确认玩家名是否正确或玩家是否在线.");
      return;
    }
    sender.sendMessage("该玩家账户金额为: " + pd.getGold());
  }
  
  @Cmd(subCmd = {"reload"},
      args = {""},
      des = "重载插件",
      permission = "VillageDefemderMiSkYle.Admin",
      needPlayer = false)
  public void reload(CommandSender sender, String[] args) {
    new VillageDefender(VillageDefender.getPlugin());
  }
  
}
