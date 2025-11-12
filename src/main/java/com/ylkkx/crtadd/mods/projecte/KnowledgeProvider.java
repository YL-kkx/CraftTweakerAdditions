package com.ylkkx.crtadd.mods.projecte;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;
import java.util.List;

@ZenClass("mods.crtadd.projecte.KnowledgeProvider")
@ZenRegister
@ModOnly("projecte")
public interface KnowledgeProvider {
    @ZenGetter("hasFullKnowledge")
     boolean hasFullKnowledge();
    @ZenSetter("hasFullKnowledge")
    void setFullKnowledge(boolean fullKnowledge);
    @ZenMethod
    void clearKnowledge();
    @ZenMethod
    boolean addKnowledge(IItemStack stack);
    @ZenMethod
    boolean hasKnowledge(IItemStack stack);
    @ZenMethod
    boolean removeKnowledge(IItemStack stack);
    @ZenGetter("knowledge")
    List<IItemStack> getKnowledge();
    @ZenGetter("emc")
    long getEmc();
    @ZenSetter("emc")
    void setEmc(long emc);

}
