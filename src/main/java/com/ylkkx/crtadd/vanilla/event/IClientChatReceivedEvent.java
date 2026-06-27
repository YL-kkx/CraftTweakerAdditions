package com.ylkkx.crtadd.vanilla.event;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventCancelable;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("mods.crtadd.vanilla.ClientChatReceivedEvent")
@ZenRegister
public interface IClientChatReceivedEvent extends IEventCancelable {
    @ZenGetter("message")
    @ZenMethod
    String getMessage();

    @ZenSetter("message")
    @ZenMethod
    void setMessage(String message);
}
