/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2020  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.villagedefense.creatures.v1_15_R1;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import pl.plajer.villagedefense.creatures.CreatureUtils;

import java.util.Random;

/**
 * Created by Tom on 15/08/2014.
 */
public class RidableVillager extends EntityVillager {

  public RidableVillager(org.bukkit.World world) {
    this(((CraftWorld) world).getHandle());
  }

  public RidableVillager(World world) {
    super(EntityTypes.VILLAGER, world);

    GoalSelectorCleaner.clearSelectors(this);

    //todo this.setSize(0.6F, 1.8F);
    getNavigation().q().b(true);
    ((Navigation) getNavigation()).a(true);
    this.getBukkitEntity().setCustomName(CreatureUtils.getVillagerNames()[new Random().nextInt(CreatureUtils.getVillagerNames().length)]);
    this.setCustomNameVisible(true);
  }

}
