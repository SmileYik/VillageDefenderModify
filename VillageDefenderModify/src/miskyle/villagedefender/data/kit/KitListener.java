package miskyle.villagedefender.data.kit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import miskyle.villagedefender.VillageDefender;
import miskyle.villagedefender.data.DIYKitManager;
import pl.plajer.villagedefense.arena.Arena;
import pl.plajer.villagedefense.arena.ArenaRegistry;
import pl.plajer.villagedefense.handlers.language.Messages;
import pl.plajer.villagedefense.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajer.villagedefense.plajerlair.commonsbox.minecraft.item.ItemUtils;
import pl.plajer.villagedefense.user.User;
import pl.plajer.villagedefense.utils.constants.CompatMaterialConstants;

public class KitListener implements Listener {

  public KitListener() {
    VillageDefender.getPlugin().getLogger().info("[miSkYle] => DIYKit 监听......");
  }
  
  //TODO 更多经验
  
  //TODO 更多硬币
  
  //TODO 更多腐肉

  // 禁止穿装备 开始
  @EventHandler
  public void onCloseInvToDropArmor(InventoryCloseEvent e) {
    if ((e.getPlayer() instanceof Player) && ArenaRegistry.isInArena((Player) e.getPlayer())) {
      User user = VillageDefender.getPlugin().getUserManager().getUser((Player) e.getPlayer());
      String kitId = (user.getKit() instanceof DIYKit) ? ((DIYKit) user.getKit()).getId() : null;
      if (kitId != null && DIYKitManager.getIdList(KitType.NO_ARMOR.name()).contains(kitId)) {
        ItemStack[] armors = e.getPlayer().getEquipment().getArmorContents();
        e.getPlayer().getEquipment().setArmorContents(new ItemStack[] { null, null, null, null });
        boolean flag = false;
        for (ItemStack item : armors) {
          if (item != null) {
            flag = true;
            e.getPlayer().getWorld().dropItem(e.getPlayer().getLocation(), item);            
          }
        }
        if (flag) {
          e.getPlayer().sendMessage("&e&l真的勇士, 敢于直面惨淡的人生, 果体对抗恐怖的僵尸.");
        }
        return;
      }
    }
  }
  //禁止穿装备 停止

  // 种地 开始
  @SuppressWarnings("deprecation")
  @EventHandler(priority = EventPriority.HIGHEST)
  private void onUseHoe(PlayerInteractEvent e) {
    if (!ArenaRegistry.isInArena(e.getPlayer())) {
      return;
    }
    if (e.getAction() != Action.RIGHT_CLICK_BLOCK || !e.hasBlock()) {
      return;
    }

    User user = VillageDefender.getPlugin().getUserManager().getUser(e.getPlayer());
    String kitId = (user.getKit() instanceof DIYKit) ? ((DIYKit) user.getKit()).getId() : null;

    if (e.hasItem()) {
      if (e.getItem().getType().name().contains("HOE") && (e.getClickedBlock().getType() == Material.GRASS_BLOCK
          || e.getClickedBlock().getType() == Material.DIRT)) {
        // 犁地
        if (kitId == null || !DIYKitManager.getIdList(KitType.FARMER.name()).contains(kitId)) {
          // e.setCancelled(true);
          return;
        } else {
          e.getClickedBlock().setType(Material.FARMLAND);
          e.getItem().setDurability((short) (e.getItem().getDurability() - 1));
          return;
        }
      }

      // 播种
      if (e.getClickedBlock().getType() == Material.FARMLAND) {
        if (kitId == null || !DIYKitManager.getIdList(KitType.FARMER.name()).contains(kitId)) {
          // e.setCancelled(true);
          return;
        } else {
          Block b = e.getClickedBlock().getRelative(BlockFace.UP);
          switch (e.getItem().getType()) {
          case WHEAT_SEEDS:
            b.setType(Material.WHEAT);
            break;
          case CARROT:
            b.setType(Material.CARROTS);
            break;
          case POTATO:
            b.setType(Material.POTATOES);
            break;
          default:
            break;
          }
          return;
        }
      }

      // 加速
      if (e.getItem().getType() == Material.BONE_MEAL
          && e.getClickedBlock().getRelative(BlockFace.DOWN).getType() == Material.FARMLAND) {
        if (kitId == null || !DIYKitManager.getIdList(KitType.FARMER.name()).contains(kitId)) {
          // e.setCancelled(true);
          return;
        } else {
          e.setCancelled(false);
          return;
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void harvest(BlockBreakEvent e) {
    if (!ArenaRegistry.isInArena(e.getPlayer())) {
      return;
    }

    if (e.getBlock().getRelative(BlockFace.DOWN).getType() == Material.FARMLAND) {
      User user = VillageDefender.getPlugin().getUserManager().getUser(e.getPlayer());
      String kitId = (user.getKit() instanceof DIYKit) ? ((DIYKit) user.getKit()).getId() : null;
      if (kitId == null || !DIYKitManager.getIdList(KitType.FARMER.name()).contains(kitId)) {
        // e.setCancelled(true);
        return;
      } else {
        e.setCancelled(false);
        return;
      }
    }
  }

  // 种地结束

  // 放门
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDoorPlace(BlockPlaceEvent e) {
    Arena arena = ArenaRegistry.getArena(e.getPlayer());
    if (arena == null) {
      return;
    }
    User user = VillageDefender.getPlugin().getUserManager().getUser(e.getPlayer());
    ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
    if (stack == null || user.isSpectator()
        || !arena.getMapRestorerManager().getGameDoorLocations().containsKey(e.getBlock().getLocation())) {
      e.setCancelled(true);
      return;
    }
    if (stack.getType() != CompatMaterialConstants.getOakDoorItem()) {
      e.setCancelled(true);
      return;
    }
    String kitId = (user.getKit() instanceof DIYKit) ? ((DIYKit) user.getKit()).getId() : null;
    if (kitId == null || (kitId != null && !DIYKitManager.getIdList(KitType.DOOR.name()).contains(kitId))) {
      e.setCancelled(true);
      return;
    }

    // to override world guard protection
    e.setCancelled(false);
    e.getPlayer().sendMessage(
        VillageDefender.getPlugin().getChatManager().colorMessage(Messages.KITS_WORKER_GAME_ITEM_PLACE_MESSAGE));
  }

  // 放栅栏
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBarrierPlace(PlayerInteractEvent event) {
    if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    Player player = event.getPlayer();
    ItemStack stack = player.getInventory().getItemInMainHand();
    if (!ArenaRegistry.isInArena(player) || !ItemUtils.isItemStackNamed(stack)
        || !stack.getItemMeta().getDisplayName().equalsIgnoreCase(
            VillageDefender.getPlugin().getChatManager().colorMessage(Messages.KITS_BLOCKER_GAME_ITEM_NAME))) {
      return;
    }
    Block block = null;
    for (Block blocks : player.getLastTwoTargetBlocks(null, 5)) {
      if (blocks.getType() == Material.AIR) {
        block = blocks;
      }
    }
    if (block == null) {
      event.getPlayer().sendMessage(
          VillageDefender.getPlugin().getChatManager().colorMessage(Messages.KITS_BLOCKER_GAME_ITEM_PLACE_FAIL));
      return;
    }
    User user = VillageDefender.getPlugin().getUserManager().getUser(player);
    if (!DIYKitManager.getIdList(KitType.BARRIER.name()).contains(((DIYKit) user.getKit()).getId())) {
      return;
    }

    pl.plajer.villagedefense.utils.Utils.takeOneItem(player, stack);
    event.setCancelled(false);

    event.getPlayer().sendMessage(
        VillageDefender.getPlugin().getChatManager().colorMessage(Messages.KITS_BLOCKER_GAME_ITEM_PLACE_MESSAGE));
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
          zombieBarrier.getLocation().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, zombieBarrier.getLocation(),
              20);
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
