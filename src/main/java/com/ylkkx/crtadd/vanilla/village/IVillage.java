package com.ylkkx.crtadd.vanilla.village;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.UUID;

@ZenClass("mods.crtadd.vanilla.IVillage")
@ZenRegister
public interface IVillage {
    //获取玩家的声望
    @ZenGetter("reputation")
    int getPlayerReputation();
}
