package com.ylkkx.crtadd.mods.everlastingabilities;

import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import org.cyclops.everlastingabilities.ability.AbilityTypeDefault;
import org.cyclops.everlastingabilities.api.AbilityType;

public class Abilities extends AbilityTypeDefault{
    String translationKey = null;
    String unlocalizedDescription = null;
    int rarity = 0;
    int maxLevel = 1;
    int baseXpPerLevel = 0;

    Functions.onTick onTick = null;
    Functions.onChangedLevel onChangedLevel = null;

    public Abilities(String id, int rarity, int maxLevel, int baseXpPerLevel) {
        super(id, rarity, maxLevel, baseXpPerLevel);
    }

    @Override
    public void onTick(EntityPlayer player, int level) {
        if (onTick != null) {
            onTick.handle(CraftTweakerMC.getIPlayer(player),level);
        } else {
            super.onTick(player, level);
        }
    }
    @Override
    public void onChangedLevel(EntityPlayer player, int oldLevel, int newLevel) {
        if (onChangedLevel != null) {
            onChangedLevel.handle(CraftTweakerMC.getIPlayer(player),oldLevel,newLevel);
        } else {
            super.onChangedLevel(player,oldLevel,newLevel);
        }
    }
}
