package com.ylkkx.crtadd.vanilla.village;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.Village;

import java.util.UUID;


@ZenRegister
public class MCIVillage implements IVillage {
    private final Village village;
    private final EntityPlayer mcPlayer;


    public MCIVillage(Village village,EntityPlayer mcPlayer) {
        this.village = village;
        this.mcPlayer = mcPlayer;

    }

    @Override
    public int getPlayerReputation() {
        return village.getPlayerReputation(mcPlayer.getUniqueID());
    }
}
