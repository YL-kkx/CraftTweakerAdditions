package com.ylkkx.crtadd.mods.everlastingabilities;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import stanhebben.zenscript.annotations.ZenClass;

public class Functions {
    @ZenClass("mods.crtadd.everlastingabilities.onTick")
    @ZenRegister
    @ModOnly("everlastingabilities")
    public interface onTick {
        void handle(IPlayer player, int level);
    }

    @ZenClass("mods.crtadd.everlastingabilities.onChangedLevel")
    @ZenRegister
    @ModOnly("everlastingabilities")
    public interface onChangedLevel {
        void handle(IPlayer player, int oldLevel, int newLevel);
    }
}
