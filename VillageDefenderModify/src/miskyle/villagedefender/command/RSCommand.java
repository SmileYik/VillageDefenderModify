package miskyle.villagedefender.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class RSCommand implements CommandExecutor, TabExecutor {
  //private RSCommand comm
  protected ArrayList<Method> methods;
  protected Object commands;
  private ArrayList<String[]> subCmds = new ArrayList<String[]>();
  private ArrayList<ArrayList<String>> subCmdsNoTab = new ArrayList<>();
  private String aliases;
  
  public void initialization(Method[] temps,Object commands,String aliases) {
    methods = new ArrayList<Method>();
    subCmds.clear();
    subCmdsNoTab.clear();
    this.aliases = aliases.toLowerCase();
    
    this.commands = commands;
    
    ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
    for(Method method : temps) {
      if(!method.isAnnotationPresent(Cmd.class)) continue;
      methods.add(method);
      Cmd cmd = method.getAnnotation(Cmd.class);
      if(cmd.subCmd().length>0) {
        int index = 0;
        for(String subCmd : cmd.subCmd()) {
          if(subCmd!=null && !subCmd.isEmpty()) {
            if(temp.size()<=index) {
              ArrayList<String> temp2 = new ArrayList<String>();
              temp2.add(subCmd);
              temp.add(index,temp2);
            }else {
              ArrayList<String> temp2 = temp.get(index);
              if(!temp2.contains(subCmd))
                temp2.add(subCmd);
            }
          }
          if(subCmdsNoTab.size()<=index) {
            subCmdsNoTab.add(index, new ArrayList<String>());
          }
          index++;
        }
        subCmdsNoTab.get(index-1).add(cmd.subCmd()[index-1].toLowerCase());
      }
    }
    for(int i = 0;i<temp.size();i++) {
      ArrayList<String> temp2 = temp.get(i);
      subCmds.add(i,temp2.toArray(new String[temp2.size()]));
    }
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String topCmd, String[] args) {
    if(!(aliases.contains(topCmd.toLowerCase()))) {
      return false;
    }
    if(args.length==0||args[0].equalsIgnoreCase("help")) {
      getHelp(sender, topCmd);
      return true;
    }
    for(Method method : methods) {
      Cmd cmd = method.getAnnotation(Cmd.class);
      if(args.length < cmd.args().length)continue;
      if(!CommandManager.compareSubCommand(args, cmd.subCmd())) continue;
      if(!cmd.permission().isEmpty() && !sender.hasPermission(cmd.permission()))continue;
      if(!cmd.unlimitedLength() && args.length!=cmd.args().length)continue;
      if(cmd.needPlayer() && (sender instanceof Player)) {
        try {
          method.invoke(commands, (Player)sender,args);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }else if(cmd.needPlayer()) {
        sender.sendMessage("需要玩家执行此指令.");
      }else {
        try {
          method.invoke(commands, sender,args);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
      return true;
    }
    sender.sendMessage("指令错误.");
    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender commandSender, Command command, String a, String[] args) {
    if(args.length>subCmds.size())return new ArrayList<>();
    if(args.length == 0 )return Arrays.asList(subCmds.get(0));
    if(args.length>=2&&subCmdsNoTab.get(args.length-2).contains(args[args.length-2].toLowerCase()))
      return new ArrayList<String>();
    return Arrays.stream(subCmds.get(args.length-1)).filter(
        s -> s.startsWith(args[args.length-1])).collect(Collectors.toList()); 
  }
  
  private void getHelp(CommandSender sender,String top) {
    StringBuilder sb = new StringBuilder();
    sb.append("\n");
    sb.append("&b=============HELP=============\n");
    for(Method method : methods) {
      Cmd cmd = method.getAnnotation(Cmd.class);
      if(!cmd.permission().isEmpty() && !sender.hasPermission(cmd.permission()))continue;
      boolean canSee = cmd.permission().isEmpty() || (sender.isOp() || sender.hasPermission(cmd.permission()));
      if(canSee) {
        sb.append("&9/"+top+" ");
        for(String subCmd:cmd.subCmd()) {
          sb.append("&3"+subCmd+" ");
        }
        for(int i = cmd.subCmd().length;i<cmd.args().length;i++) {
          sb.append("&2[&a"+cmd.args()[i]+"&2] ");
        }
        sb.append("&7- ");
        sb.append("&b"+cmd.des());
        sb.append("\n");
      }
    }
    sb.append("==============================");
    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',sb.toString()));
  }

}
