package com.ylkkx.crtadd.utils;

import com.teamderpy.shouldersurfing.client.ShoulderInstance;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenRegister
@ZenExpansion("crafttweaker.player.IPlayer")
public class CrtAddIPlayer {
    //获取玩家视角
    @ZenGetter("viewType")
    public static int getViewType(IPlayer player) {
        // crt类转java类
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        // 获取游戏客户端
        Minecraft mc = Minecraft.getMinecraft();

        // 仅允许操作当前客户端控制的玩家
        if (mc.player != mcPlayer) {
            return -1;
        }

        // 返回原生视角值，与接口中其他getter方法保持简洁风格
        return mc.gameSettings.thirdPersonView;
    }
    //设置玩家的视角
    @ZenMethod
    @SideOnly(Side.CLIENT)//客户端
    public static void togglePlayerView(IPlayer player, int viewType) {
        // crt类转java类
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        // 获取游戏客户端
        Minecraft mc = Minecraft.getMinecraft();

        // 仅允许操作当前客户端控制的玩家
        if (mc.player != mcPlayer) {
            return;
        }

        // 设置视角
        mc.gameSettings.thirdPersonView = viewType;
        // 越肩支持
        if (Loader.isModLoaded("shouldersurfing")) {
            if(viewType == 1) {
                ShoulderInstance.getInstance().setShoulderSurfing(true);
            }
        }

    }

}
