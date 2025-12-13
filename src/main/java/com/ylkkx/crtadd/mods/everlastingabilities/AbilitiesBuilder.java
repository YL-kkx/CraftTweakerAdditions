package com.ylkkx.crtadd.mods.everlastingabilities;

import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import org.cyclops.everlastingabilities.ability.AbilityTypeRegistry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenClass("mods.crtadd.everlastingabilities.AbilitiesBuilder")
@ZenRegister
@ModOnly("everlastingabilities")
public class AbilitiesBuilder {
    //能力的ID
    @ZenProperty
    public String translationKey;
    //能力的描述
    @ZenProperty
    public String unlocalizedDescription;
    //能力的稀有度
    @ZenProperty
    public int rarity;
    //能力的最大等级
    @ZenProperty
    public int maxLevel;
    //能力每级所需的基础经验值
    @ZenProperty
    public int baseXpPerLevel;
    //能力的tick事件处理函数
    @ZenProperty
    public Functions.onTick onTick = null;
    //能力的等级变化事件处理函数
    @ZenProperty
    public Functions.onChangedLevel onChangedLevel = null;


    public AbilitiesBuilder(String translationKey) {
        this.translationKey=translationKey;
    }

    @ZenMethod
    public static AbilitiesBuilder create(String translationKey) {
        return new AbilitiesBuilder(translationKey);
    }
    @ZenMethod
    public void register() {

        // 创建能力实例
        Abilities ability = new Abilities(translationKey, rarity, maxLevel, baseXpPerLevel);
        ability.onTick = this.onTick;
        ability.onChangedLevel = this.onChangedLevel;

        // 注册能力到注册表
        AbilityTypeRegistry.getInstance().register(ability);
    }
}
