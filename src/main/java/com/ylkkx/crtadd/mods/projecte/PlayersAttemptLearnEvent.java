package com.ylkkx.crtadd.mods.projecte;


import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventCancelable;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.crtadd.projecte.PlayerAttemptLearnEvent")
@ZenRegister
@ModOnly("projecte")
public interface PlayersAttemptLearnEvent extends IEventCancelable {
    @ZenGetter("item")
    @ZenMethod
    IItemStack getItem();
    @ZenGetter("player")
    @ZenMethod
    IPlayer getPlayer();
}
