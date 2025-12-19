package com.ylkkx.crtadd.vanilla.village;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.world.IBlockPos;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("mods.crtadd.vanilla.IVillage")
@ZenRegister
public interface IVillage {
    //获取玩家的声望
    @ZenGetter("reputation")
    int getPlayerReputation();
    //设置玩家的声望
    @ZenSetter("reputation")
    void setPlayerReputation(int reputation);
    //获取玩家的声望是否过低(铁傀儡攻击)
    @ZenGetter("isPlayerReputationTooLow")
    boolean isPlayerReputationTooLow();
    //获取村庄中心位置
    @ZenGetter("center")
    IBlockPos getCenter();
    //获取村庄半径
    @ZenGetter("villageRadius")
    int getVillageRadius();
    //获取村庄有效门的数量
    @ZenGetter("numVillageDoors")
    int getNumVillageDoors();
    //获取村庄内村民的数量
    @ZenGetter("numVillagers")
    int getNumVillagers();
    //获取村庄是否已被摧毁
    @ZenGetter("isAnnihilated")
    boolean isAnnihilated();
    //检查指定位置是否存在有效门
    @ZenMethod
    boolean getExistedDoor(IBlockPos blockPos);
    //向村庄添加袭击者
    @ZenMethod
    void addOrRenewAgressor(IEntityLivingBase entity);
    //获取距离指定实体最近的村庄袭击者
    @ZenMethod
    IEntityLivingBase findNearestVillageAggressor(IEntityLivingBase entity);
    //获取指定位置是否在村庄范围
    @ZenMethod
    boolean isBlockPosWithinSqVillageRadius(IBlockPos blockPos);
}
