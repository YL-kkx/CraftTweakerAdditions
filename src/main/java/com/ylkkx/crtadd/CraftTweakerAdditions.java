package com.ylkkx.crtadd;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies = "required-after:crafttweaker;after:projecte;after:shoulder-surfing-reloaded")
public class CraftTweakerAdditions {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (Loader.isModLoaded("projecte")) {
            MinecraftForge.EVENT_BUS.register(EventManager.EventHandler.class);
        }

    }
}
