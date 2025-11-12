package com.ylkkx.crtadd;



import com.ylkkx.crtadd.mods.everlastingabilities.AbilitiesBuilder;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION, dependencies = "required-after:crafttweaker;after:projecte;after:shoulder-surfing-reloaded;after:everlastingabilities;after:cyclops-core")
public class CraftTweakerAdditions {
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // 检查ProjectE模组并注册相关事件
        if (Loader.isModLoaded("projecte")) {
            MinecraftForge.EVENT_BUS.register(EventManager.EventHandler.class);
        }
    }
}
