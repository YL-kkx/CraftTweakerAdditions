package com.ylkkx.crtadd.vanilla.event;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ZenRegister
@SideOnly(Side.CLIENT)
public class MCClientChatReceivedEvent implements IClientChatReceivedEvent {
    private final ClientChatReceivedEvent evt;

    public MCClientChatReceivedEvent(ClientChatReceivedEvent evt) {
        this.evt = evt;
    }

    @Override
    public String getMessage() {
        return evt.getMessage().getFormattedText();
    }

    @Override
    public void setMessage(String message) {
        evt.setMessage(new TextComponentString(message));
    }

    @Override
    public boolean isCanceled() {
        return evt.isCanceled();
    }

    @Override
    public void setCanceled(boolean canceled) {
        evt.setCanceled(canceled);
    }
}
