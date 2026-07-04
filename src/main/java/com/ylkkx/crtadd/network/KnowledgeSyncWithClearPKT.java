package com.ylkkx.crtadd.network;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.PECore;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class KnowledgeSyncWithClearPKT implements IMessage {
    private NBTTagCompound nbt;

    public KnowledgeSyncWithClearPKT() {}

    public KnowledgeSyncWithClearPKT(NBTTagCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nbt = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, nbt);
    }

    public static class Handler implements IMessageHandler<KnowledgeSyncWithClearPKT, IMessage> {
        @Override
        public IMessage onMessage(final KnowledgeSyncWithClearPKT message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                PECore.proxy.getClientTransmutationProps().clearKnowledge();
                PECore.proxy.getClientTransmutationProps().deserializeNBT(message.nbt);
            });
            return null;
        }
    }
}