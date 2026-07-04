package com.ylkkx.crtadd.mods.projecte.MC;

import com.ylkkx.crtadd.mods.projecte.KnowledgeProvider;
import com.ylkkx.crtadd.network.KnowledgeSyncWithClearPKT;
import com.ylkkx.crtadd.network.PacketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
public class MCKnowledgeProvider implements KnowledgeProvider {
    private final IKnowledgeProvider internal;
    private final EntityPlayer player;

    public MCKnowledgeProvider(IKnowledgeProvider internal, EntityPlayer player) {
        this.internal = internal;
        this.player = player;
    }

    public IKnowledgeProvider getInternal() {
        return internal;
    }

    /**
     * 同步知识到客户端
     */
    private void syncToClient() {
        if (player instanceof EntityPlayerMP) {
            internal.sync((EntityPlayerMP) player);
        }
    }

    @Override
    public boolean hasFullKnowledge() {
        return internal.hasFullKnowledge();
    }
    @Override
    public void setFullKnowledge(boolean fullKnowledge) {
        internal.setFullKnowledge(fullKnowledge);
        syncToClient();
    }
    @Override
    public void clearKnowledge() {
        internal.clearKnowledge();
        syncToClient();
    }
    @Override
    public boolean hasKnowledge(IItemStack stack) {
        return internal.hasKnowledge(CraftTweakerMC.getItemStack(stack));
    }
    @Override
    public boolean addKnowledge(IItemStack stack) {
        boolean result = internal.addKnowledge(CraftTweakerMC.getItemStack(stack));
        if (result) {
            syncToClient();
        }
        return result;
    }
    @Override
    public boolean removeKnowledge(IItemStack stack) {
        boolean result = internal.removeKnowledge(CraftTweakerMC.getItemStack(stack));
        if (result && player instanceof EntityPlayerMP) {
            PacketHandler.sendTo(new KnowledgeSyncWithClearPKT(internal.serializeNBT()), (EntityPlayerMP) player);
        }
        return result;
    }
    @Override
    public List<IItemStack> getKnowledge() {
        return internal.getKnowledge().stream()
                .map(CraftTweakerMC::getIItemStack)
                .collect(Collectors.toList());
    }
    @Override
    public long getEmc() {
        return internal.getEmc();
    }
    @Override
    public void setEmc(long emc) {
        internal.setEmc(emc);
        syncToClient();
    }

}
