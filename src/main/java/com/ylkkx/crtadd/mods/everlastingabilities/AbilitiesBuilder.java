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
    @ZenProperty
    public String translationKey;

    @ZenProperty
    public String unlocalizedDescription;

    @ZenProperty
    public int rarity;

    @ZenProperty
    public int maxLevel;

    @ZenProperty
    public int baseXpPerLevel;

    @ZenProperty
    public Functions.onTick onTick = null;

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
