package com.ylkkx.crtadd.mods.nutrition;

import ca.wescook.nutrition.capabilities.INutrientManager;
import ca.wescook.nutrition.nutrients.Nutrient;
import ca.wescook.nutrition.nutrients.NutrientList;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import ca.wescook.nutrition.capabilities.CapabilityManager;

@ZenRegister
@ZenExpansion("crafttweaker.player.IPlayer")
@ModOnly("nutrition")
public class IPlayerNutrition {
    @CapabilityInject(INutrientManager.class)
    private static final Capability<INutrientManager> NUTRITION_CAPABILITY = null;
    //获取玩家某项营养的值
    @ZenMethod
    @SideOnly(Side.CLIENT)//客户端
    public static Float getNutrition(IPlayer player, String nutritionname) {
        Nutrient nutrient = NutrientList.getByName(nutritionname);
        if (nutrient == null) {
            return 0.0f;
        }
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        Float nutrientValue = mcPlayer.getCapability(NUTRITION_CAPABILITY, null).get(nutrient);
        return nutrientValue;
    }
    //设置玩家某项营养的值
    @ZenMethod
    @SideOnly(Side.CLIENT)//客户端
    public static void setNutrition(IPlayer player, String nutritionname, Float nutritionfloat){
        Nutrient nutrient = NutrientList.getByName(nutritionname);
        if (nutrient != null) {
            EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
            mcPlayer.getCapability(NUTRITION_CAPABILITY, null).set(nutrient,nutritionfloat);
        }
    }
}
