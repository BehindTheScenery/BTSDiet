package com.illusivesoulworks.diet.common.network.server;

import com.illusivesoulworks.diet.api.type.IDietGroup;
import com.illusivesoulworks.diet.common.impl.group.DietGroups;
import com.illusivesoulworks.diet.common.util.DietValueGenerator;
import com.illusivesoulworks.diet.platform.Services;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record SPacketGroups(CompoundTag groups, Map<Item, Set<String>> generated) {

  public static void encode(SPacketGroups msg, FriendlyByteBuf buf) {
    CompoundTag compoundNBT = new CompoundTag();

    for (Map.Entry<Item, Set<String>> entry : msg.generated().entrySet()) {
      ListTag listNBT = new ListTag();

      for (String s : entry.getValue()) {
        listNBT.add(StringTag.valueOf(s));
      }
      compoundNBT.put(
          Objects.requireNonNull(Services.REGISTRY.getItemKey(entry.getKey())).toString(), listNBT);
    }
    buf.writeNbt(compoundNBT);
    buf.writeNbt(msg.groups());
  }

  public static SPacketGroups decode(FriendlyByteBuf buf) {
    CompoundTag compoundNBT = buf.readNbt();
    Map<Item, Set<String>> generated = new HashMap<>();

    if (compoundNBT != null) {

      for (String name : compoundNBT.getAllKeys()) {
        Item item = Services.REGISTRY.getItem(new ResourceLocation(name)).orElse(null);

        if (item != null) {
          ListTag listNBT = compoundNBT.getList(name, Tag.TAG_STRING);
          Set<String> found = new HashSet<>();

          for (Tag nbt : listNBT) {
            String entry = nbt.getAsString();
            found.add(entry);
          }
          generated.put(item, found);
        }
      }
    }
    return new SPacketGroups(buf.readNbt(), generated);
  }

  public static void handle(SPacketGroups msg) {
    DietGroups.CLIENT.load(msg.groups());
    DietValueGenerator.load(msg.generated());
  }
}