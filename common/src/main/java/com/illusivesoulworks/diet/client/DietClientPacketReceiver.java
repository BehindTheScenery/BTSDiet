/*
 * Copyright (C) 2021 C4
 *
 * This file is part of Diet, a mod made for Minecraft.
 *
 * Diet is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Diet is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Diet.
 * If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.illusivesoulworks.diet.client;

import com.illusivesoulworks.diet.common.network.server.SPacketActivate;
import com.illusivesoulworks.diet.common.network.server.SPacketDiet;
import com.illusivesoulworks.diet.common.network.server.SPacketEaten;
import com.illusivesoulworks.diet.platform.Services;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class DietClientPacketReceiver {

  public static void handleActivate(SPacketActivate msg) {
    Player player = Minecraft.getInstance().player;

    if (player != null) {
      Services.CAPABILITY.get(player).ifPresent(diet -> diet.setActive(msg.flag()));
    }
  }

  public static void handleDiet(SPacketDiet msg) {
    Player player = Minecraft.getInstance().player;

    if (player != null) {
      Services.CAPABILITY.get(player).ifPresent(diet -> {
        diet.setSuite(msg.suite());

        for (Map.Entry<String, Float> entry : msg.groups().entrySet()) {
          diet.setValue(entry.getKey(), entry.getValue());
        }
      });
    }
  }

  public static void handleEaten(SPacketEaten msg) {
    Player player = Minecraft.getInstance().player;

    if (player != null) {
      Services.CAPABILITY.get(player).ifPresent(diet -> {

        for (Item item : msg.items()) {
          diet.addEaten(item);
        }
      });
    }
  }
}