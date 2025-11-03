package com.ylkkx.crtadd.mods.projecte.MC;

import com.ylkkx.crtadd.mods.projecte.PlayersAttemptCondenserSetEvent;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import moze_intel.projecte.api.event.PlayerAttemptCondenserSetEvent;

@ZenRegister
public class MCPlayersAttemptCondenserSetEvent implements PlayersAttemptCondenserSetEvent {
    private final PlayerAttemptCondenserSetEvent evt;

    public MCPlayersAttemptCondenserSetEvent(PlayerAttemptCondenserSetEvent evt) {
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
