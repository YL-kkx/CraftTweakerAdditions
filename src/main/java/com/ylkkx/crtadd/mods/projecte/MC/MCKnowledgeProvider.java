package com.ylkkx.crtadd.mods.projecte.MC;

import com.ylkkx.crtadd.mods.projecte.KnowledgeProvider;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;

import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
public class MCKnowledgeProvider implements KnowledgeProvider {
    private final IKnowledgeProvider internal;

    public MCKnowledgeProvider(IKnowledgeProvider internal) {
        this.internal = internal;
    }
    public IKnowledgeProvider getInternal() {
        return internal;
    }

    @Override
    public boolean hasFullKnowledge() {
        return internal.hasFullKnowledge();
    }
    @Override
    public void setFullKnowledge(boolean fullKnowledge) {
        internal.setFullKnowledge(fullKnowledge);
    }
    @Override
    public void clearKnowledge() {
        internal.clearKnowledge();
    }
    @Override
    public boolean hasKnowledge(IItemStack stack) {
        return internal.hasKnowledge(CraftTweakerMC.getItemStack(stack));
    }
    @Override
    public boolean addKnowledge(IItemStack stack) {
        return internal.addKnowledge(CraftTweakerMC.getItemStack(stack));
    }
    @Override
    public boolean removeKnowledge(IItemStack stack) {
        return internal.removeKnowledge(CraftTweakerMC.getItemStack(stack));
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
    }

}
