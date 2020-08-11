package miskyle.villagedefender.data.kit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import miskyle.villagedefender.VillageDefender;
import miskyle.villagedefender.data.LoreString;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajer.villagedefense.plajerlair.commonsbox.minecraft.item.ItemUtils;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.constants.CompatMaterialConstants;

public class KitListener implements Listener{
  
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDoorPlace(BlockPlaceEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    if (arena == null) {
      return;
    }
    User user = VillageDefender.getPlugin().getUserManager().getUser(e.getPlayer());
    ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
    if (stack == null || user.isSpectator() || !arena.getMapRestorerManager().getGameDoorLocations()
        .containsKey(e.getBlock().getLocation())) {
      e.setCancelled(true);
      return;
    }
    if (stack.getType() != CompatMaterialConstants.getOakDoorItem()) {
      e.setCancelled(true);
      return;
    }
    if (!DIYKitManager.getIdList(LoreString.DOOR).contains(((DIYKit)user.getKit()).getId())) {
      e.setCancelled(true);
      return;
    }
    
    //to override world guard protection
    e.setCancelled(false);
    e.getPlayer().sendMessage(VillageDefender.getPlugin().getChatManager().colorMessage(Messages.KITS_WORKER_GAME_ITEM_PLACE_MESSAGE));
  }
  
  //放栅栏
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBarrierPlace(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    Player player = event.getPlayer();
    ItemStack stack = player.getInventory().getItemInMainHand();
    if (!ArenaRegistry.isInArena(player) || !ItemUtils.isItemStackNamed(stack) || !stack.getItemMeta().getDisplayName()
        .equalsIgnoreCase(VillageDefender.getPlugin().getChatManager().colorMessage(Messages.KITS_BLOCKER_GAME_ITEM_NAME))) {
      return;
    }
    Block block = null;
    for (Block blocks : player.getLastTwoTargetBlocks(null, 5)) {
      if (blocks.getType() == Material.AIR) {
        block = blocks;
      }
    }
    if (block == null) {
      event.getPlayer().sendMessage(VillageDefender.getPlugin().getChatManager().colorMessage(Messages.KITS_BLOCKER_GAME_ITEM_PLACE_FAIL));
      return;
    }
    User user = VillageDefender.getPlugin().getUserManager().getUser(player);
    if (!DIYKitManager.getIdList(LoreString.DOOR).contains(((DIYKit)user.getKit()).getId())) {
      return;
    }
    
    pl.plajer.villagedefense.utils.Utils.takeOneItem(player, stack);
    event.setCancelled(false);

    event.getPlayer().sendMessage(VillageDefender.getPlugin().getChatManager().colorMessage(Messages.KITS_BLOCKER_GAME_ITEM_PLACE_MESSAGE));
    ZombieBarrier zombieBarrier = new ZombieBarrier();
    zombieBarrier.setLocation(block.getLocation());
    zombieBarrier.getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, zombieBarrier.getLocation(), 20);
    removeBarrierLater(zombieBarrier);
    block.setType(XMaterial.OAK_FENCE.parseMaterial());
  }

  private void removeBarrierLater(ZombieBarrier zombieBarrier) {
    new BukkitRunnable() {
      @Override
      public void run() {
        zombieBarrier.decrementSeconds();
        if (zombieBarrier.getSeconds() <= 0) {
          zombieBarrier.getLocation().getBlock().setType(Material.AIR);
          zombieBarrier.getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, zombieBarrier.getLocation(), 20);
          this.cancel();
        }
      }
    }.runTaskTimer(VillageDefender.getPlugin(), 20, 20);
  }

  private static class ZombieBarrier {
    private Location location;
    private int seconds = 10;

    Location getLocation() {
      return location;
    }

    void setLocation(Location location) {
      this.location = location;
    }

    int getSeconds() {
      return seconds;
    }

    void decrementSeconds() {
      this.seconds = seconds - 1;
    }
  }
}
