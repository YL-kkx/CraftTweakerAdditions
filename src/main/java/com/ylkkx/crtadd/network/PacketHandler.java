package com.ylkkx.crtadd.network;

import com.ylkkx.crtadd.Tags;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class PacketHandler {
    private static final SimpleNetworkWrapper HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MOD_ID);

    public static void register() {
        int disc = 0;
        HANDLER.registerMessage(KnowledgeSyncWithClearPKT.Handler.class, KnowledgeSyncWithClearPKT.class, disc++, Side.CLIENT);
    }

    public static void sendTo(IMessage msg, EntityPlayerMP player) {
        HANDLER.sendTo(msg, player);
    }
}