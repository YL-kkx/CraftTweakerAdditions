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
    //检查玩家是否拥有全部的知识
    @ZenGetter("hasFullKnowledge")
     boolean hasFullKnowledge();
    //设置玩家是否拥有全部的知识
    @ZenSetter("hasFullKnowledge")
    void setFullKnowledge(boolean fullKnowledge);
    //清除玩家的所有知识
    @ZenMethod
    void clearKnowledge();
    //向玩家添加知识
    @ZenMethod
    boolean addKnowledge(IItemStack stack);
    //检查玩家是否拥有特定物品的知识
    @ZenMethod
    boolean hasKnowledge(IItemStack stack);
    //移除玩家指定知识
    @ZenMethod
    boolean removeKnowledge(IItemStack stack);
    //获取玩家拥有的所有知识
    @ZenGetter("knowledge")
    List<IItemStack> getKnowledge();
    //获取玩家的emc值
    @ZenGetter("emc")
    long getEmc();
    //设置玩家的emc值
    @ZenSetter("emc")
    void setEmc(long emc);

}
