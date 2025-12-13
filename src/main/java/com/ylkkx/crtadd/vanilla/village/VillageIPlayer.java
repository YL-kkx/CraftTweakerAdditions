package com.ylkkx.crtadd.vanilla.village;

import com.ylkkx.crtadd.mods.projecte.KnowledgeProvider;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenRegister
@ZenExpansion("crafttweaker.player.IPlayer")
public class VillageIPlayer {
    //获取玩家所在位置的村庄
    @ZenGetter("village")
    public static IVillage getVillage(IPlayer player) {
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        BlockPos blockPos = mcPlayer.getPosition();
        World world = mcPlayer.world;
        VillageCollection villageCollection = world.villageCollection;
        if(villageCollection == null){
            return null;
        }
        Village village = villageCollection.getNearestVillage(blockPos,32);
        if(village == null){
            return null;
        }
        return new MCIVillage(village,mcPlayer);
    }
}
