package com.ylkkx.crtadd.vanilla.village;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.world.IBlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.village.Village;

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

    @Override
    public void setPlayerReputation(int reputation) {
        village.modifyPlayerReputation(mcPlayer.getUniqueID(),reputation);
    }

    @Override
    public boolean isPlayerReputationTooLow() {
        return village.isPlayerReputationTooLow(mcPlayer.getUniqueID());
    }

    @Override
    public IBlockPos getCenter() {
        return CraftTweakerMC.getIBlockPos(village.getCenter());
    }

    @Override
    public int getVillageRadius() {
        return village.getVillageRadius();
    }

    @Override
    public int getNumVillageDoors() {
        return village.getNumVillageDoors();
    }

    @Override
    public int getNumVillagers() {
        return village.getNumVillagers();
    }

    @Override
    public boolean isAnnihilated() {
        return village.isAnnihilated();
    }

    @Override
    public boolean getExistedDoor(IBlockPos blockPos) {
        return village.getExistedDoor(CraftTweakerMC.getBlockPos(blockPos)) != null;
    }

    @Override
    public void addOrRenewAgressor(IEntityLivingBase entity) {
        village.addOrRenewAgressor(CraftTweakerMC.getEntityLivingBase(entity));
    }

    @Override
    public IEntityLivingBase findNearestVillageAggressor(IEntityLivingBase entity) {
        return CraftTweakerMC.getIEntityLivingBase(village.findNearestVillageAggressor(CraftTweakerMC.getEntityLivingBase(entity)));
    }

    @Override
    public boolean isBlockPosWithinSqVillageRadius(IBlockPos blockPos) {
        return village.isBlockPosWithinSqVillageRadius(CraftTweakerMC.getBlockPos(blockPos));
    }
}
