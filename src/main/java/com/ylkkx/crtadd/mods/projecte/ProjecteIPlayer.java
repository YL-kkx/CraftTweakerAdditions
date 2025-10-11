package com.ylkkx.crtadd.mods.projecte;

import com.ylkkx.crtadd.mods.projecte.MC.MCKnowledgeProvider;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.player.IPlayer;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenGetter;

@ZenRegister
@ZenExpansion("crafttweaker.player.IPlayer")
public class ProjecteIPlayer {

    @ZenGetter("knowledge")
    public static KnowledgeProvider getKnowledge(IPlayer player) {
        EntityPlayer mcPlayer = CraftTweakerMC.getPlayer(player);
        if (mcPlayer != null) {
            IKnowledgeProvider provider = mcPlayer.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null);
            if (provider != null) {
                return new MCKnowledgeProvider(provider);
            }
        }
        return null;
    }
}
