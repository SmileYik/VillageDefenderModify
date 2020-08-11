package miskyle.villagedefender.command;

import pl.plajer.villagedefense.Main;

public class CommandManager {
	public CommandManager(Main plugin){
		regesitCommands(plugin);
	}
	
	private void regesitCommands(Main plugin) {
        RSCommand cmdRecipe = new RSCommand();
        cmdRecipe.initialization(
                VillageDefemderMiSkYleCMD.class.getDeclaredMethods(), 
                new VillageDefemderMiSkYleCMD(),
                "vdmm");
        plugin.getCommand("vdmm").setExecutor(cmdRecipe);
        plugin.getCommand("vdmm").setTabCompleter(cmdRecipe);
        
        RSCommand cmd = new RSCommand();
        cmd.initialization(
            KitCreatorCMD.class.getDeclaredMethods(), 
                new KitCreatorCMD(),
                "vdmk");
        plugin.getCommand("vdmk").setExecutor(cmd);
        plugin.getCommand("vdmk").setTabCompleter(cmd);
	}
	
	public static boolean compareSubCommand(String[] args,String[] subCmd) {
		for(int i=0;i<subCmd.length;i++) {
			if(!subCmd[i].equalsIgnoreCase(args[i]))
				return false;
		}
		return true;
	}
}
