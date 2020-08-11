package miskyle.villagedefender.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import miskyle.villagedefender.command.kitcreator.KitCreator;
import miskyle.villagedefender.data.kit.DIYKitManager;

public class KitCreatorCMD {
  @Cmd(subCmd = {"start"},
      args = {""},
      des = "开始制造Kit",
      permission = "VillageDefemderMiSkYle.Admin")
  public void start(Player p, String[] args) {
    KitCreator.start(p);
  }
  @Cmd(subCmd = {"save"},
      args = {""},
      des = "保存制造Kit",
      permission = "VillageDefemderMiSkYle.Admin")
  public void save(Player p, String[] args) {
    KitCreator.save(p);
  }
  @Cmd(subCmd = {"stop"},
      args = {""},
      des = "停止制造Kit",
      permission = "VillageDefemderMiSkYle.Admin")
  public void stop(Player p, String[] args) {
    KitCreator.stop(p);
  }
  
  @Cmd(subCmd = {"setArmor"},
      args = {""},
      des = "设置Kit的装备为自己所穿装备",
      permission = "VillageDefemderMiSkYle.Admin")
  public void setArmor(Player p, String[] args) {
    KitCreator.setArmor(p);
  }
  
  @Cmd(subCmd = {"setItems"},
      args = {""},
      des = "设置Kit的物品为自己仓库中的物品",
      permission = "VillageDefemderMiSkYle.Admin")
  public void setItem(Player p, String[] args) {
    KitCreator.setInv(p);
  }
  
  @Cmd(subCmd = {"setReStockInv"},
      args = {""},
      des = "设置Kit的没波次增加物品为自己仓库中的物品",
      permission = "VillageDefemderMiSkYle.Admin")
  public void setrItems(Player p, String[] args) {
    KitCreator.setReStockInv(p);
  }
  
  @Cmd(subCmd = {"setMaterial"},
      args = {""},
      des = "设置Kit的材质(唯一性)",
      permission = "VillageDefemderMiSkYle.Admin")
  public void setMaterial(Player p, String[] args) {
    KitCreator.setMetarial(p);
  }
  
  @Cmd(subCmd = {"setLevel"},
      args = {"","等级"},
      des = "设置Kit的等级(当权限不为null时也需要想应等级生效)",
      permission = "VillageDefemderMiSkYle.Admin")
  public void setLevel(Player p, String[] args) {
    KitCreator.setLevel(p, Integer.parseInt(args[1]));
  }
  
  @Cmd(subCmd = {"setGold"},
      args = {"","价格"},
      des = "设置Kit的价格",
      permission = "VillageDefemderMiSkYle.Admin")
  public void setGold(Player p, String[] args) {
    KitCreator.setGold(p, Double.parseDouble(args[1]));
  }
  
  @Cmd(subCmd = {"setHealth"},
      args = {"","生命值"},
      des = "设置Kit的生命值",
      permission = "VillageDefemderMiSkYle.Admin")
  public void setHealth(Player p, String[] args) {
    KitCreator.setHealth(p, Double.parseDouble(args[1]));
  }
  
  @Cmd(subCmd = {"setPermission"},
      args = {"","权限"},
      des = "设置Kit的权限",
      permission = "VillageDefemderMiSkYle.Admin")
  public void setPermission(Player p, String[] args) {
    KitCreator.setPermission(p, args[1]);
  }
  
  @Cmd(subCmd = {"setId"},
      args = {"","ID"},
      des = "设置Kit的ID",
      permission = "VillageDefemderMiSkYle.Admin")
  public void setID(Player p, String[] args) {
    KitCreator.setId(p, args[1]);
  }
  
  @Cmd(subCmd = {"unloadDefaultKits"},
      args = {""},
      des = "卸载已加载的插件原版Kit",
      permission = "VillageDefemderMiSkYle.Admin")
  public void unload(CommandSender p, String[] args) {
    DIYKitManager.unloadDefaultKit();
  }
}
