package com.ylkkx.crtadd.mods.projecte.MC;

import com.ylkkx.crtadd.mods.projecte.PlayersAttemptLearnEvent;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import moze_intel.projecte.api.event.PlayerAttemptLearnEvent;
import crafttweaker.api.item.IItemStack;

@ZenRegister
public class MCPlayersAttemptLearnEvent implements PlayersAttemptLearnEvent {
    private final PlayerAttemptLearnEvent evt;

    public MCPlayersAttemptLearnEvent(PlayerAttemptLearnEvent evt) {
        this.evt = evt;
    }

    public IItemStack getItem() {
        return CraftTweakerMC.getIItemStack(evt.getStack());
    }

    public IPlayer getPlayer() {
        return CraftTweakerMC.getIPlayer(evt.getPlayer());
    }

    public boolean isCanceled() {
        return evt.isCanceled();
    }

    public void setCanceled(boolean canceled) {
        this.evt.setCanceled(canceled);
    }
}

