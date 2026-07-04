package com.ylkkx.crtadd;

import com.ylkkx.crtadd.network.PacketHandler;
import com.ylkkx.crtadd.vanilla.event.MCClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies = "required-after:crafttweaker;after:projecte;after:shoulder-surfing-reloaded;after:everlastingabilities;after:cyclops-core;after:nutrition;after:tinkersplannerantique")
public class CraftTweakerAdditions {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ClientChatEventHandler());
        PacketHandler.register();
        if (Loader.isModLoaded("projecte")) {
            MinecraftForge.EVENT_BUS.register(EventManager.EventHandler.class);
        }
    }

    public static class ClientChatEventHandler {
        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public void onClientChatReceived(ClientChatReceivedEvent evt) {
            if (EventManager.MCClientChatReceivedEventList.hasHandlers()) {
                EventManager.MCClientChatReceivedEventList.publish(new MCClientChatReceivedEvent(evt));
            }
        }
    }
}
