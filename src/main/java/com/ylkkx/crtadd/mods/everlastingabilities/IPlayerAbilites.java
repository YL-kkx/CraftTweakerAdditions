package com.ylkkx.crtadd.mods.everlastingabilities;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import org.cyclops.everlastingabilities.ability.AbilityHelpers;
import org.cyclops.everlastingabilities.ability.AbilityTypeRegistry;
import org.cyclops.everlastingabilities.api.Ability;
import org.cyclops.everlastingabilities.api.IAbilityType;
import org.cyclops.everlastingabilities.api.capability.IMutableAbilityStore;
import org.cyclops.everlastingabilities.capability.MutableAbilityStoreConfig;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.crtadd.everlastingabilities.Player")
@ModOnly("everlastingabilities")
public class IPlayerAbilites {
    //获取玩家是否有某项能力
    @ZenMethod
    public static boolean hasAbilites(IPlayer player, String abilitesname) {
        IAbilityType abilityType = AbilityTypeRegistry.getInstance().getAbilityType(abilitesname);
        if (abilityType == null) {
            return false;
        }
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        IMutableAbilityStore abilityStore = mcPlayer.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        return abilityStore.hasAbilityType(abilityType);
    }
    //获取玩家某项能力的等级
    @ZenMethod
    public static int getAbilitesLevel(IPlayer player, String abilitesname) {
        IAbilityType abilityType = AbilityTypeRegistry.getInstance().getAbilityType(abilitesname);
        if (abilityType == null) {
            return 0;
        }
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        IMutableAbilityStore abilityStore = mcPlayer.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        return abilityStore.getAbility(abilityType).getLevel();
    }
    //向玩家添加某项能力
    @ZenMethod
    public static void addAbilites(IPlayer player,String abilitesname ,int level,boolean modifyXp){
        IAbilityType abilityType = AbilityTypeRegistry.getInstance().getAbilityType(abilitesname);
        if (abilityType != null) {
            EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
            Ability newAbility = new Ability(abilityType, level);
            Ability result = AbilityHelpers.addPlayerAbility(mcPlayer, newAbility, true, modifyXp);
        }
    }
    //移除玩家某项能力
    @ZenMethod
    public static void removeAbilites(IPlayer player,String abilitesname ,int level,boolean modifyXp){
        IAbilityType abilityType = AbilityTypeRegistry.getInstance().getAbilityType(abilitesname);
        if (abilityType != null) {
            EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
            Ability newAbility = new Ability(abilityType, level);
            Ability result = AbilityHelpers.removePlayerAbility(mcPlayer, newAbility, true, modifyXp);
        }
    }
    //获取玩家能力数量
    @ZenMethod
    public static int getPlayerAbiliteCount(IPlayer player){
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        IMutableAbilityStore abilityStore = mcPlayer.getCapability(MutableAbilityStoreConfig.CAPABILITY, null);
        return abilityStore.getAbilities().size();
    }
}
