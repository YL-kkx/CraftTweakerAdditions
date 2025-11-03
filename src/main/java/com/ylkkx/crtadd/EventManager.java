package com.ylkkx.crtadd;

import com.ylkkx.crtadd.mods.projecte.MC.MCPlayersAttemptCondenserSetEvent;
import com.ylkkx.crtadd.mods.projecte.MC.MCPlayersAttemptLearnEvent;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventHandle;
import crafttweaker.api.event.IEventManager;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import moze_intel.projecte.api.event.PlayerAttemptCondenserSetEvent;
import moze_intel.projecte.api.event.PlayerAttemptLearnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenExpansion("crafttweaker.events.IEventManager")
@ZenRegister
public class EventManager {
    private static final EventList<MCPlayersAttemptLearnEvent> MCPlayersAttemptLearnEventList = new EventList<>();

    private static final EventList<MCPlayersAttemptCondenserSetEvent> MCPlayersAttemptCondenserSetEventList = new EventList<>();

    @ZenMethod
    public static IEventHandle onPlayerAttemptLearn(IEventManager manager, IEventHandler<MCPlayersAttemptLearnEvent> event) {
        return MCPlayersAttemptLearnEventList.add(event);
    }
    @ZenMethod
    public static IEventHandle onPlayerAttemptCondenserSet(IEventManager manager, IEventHandler<MCPlayersAttemptCondenserSetEvent> event) {
        return MCPlayersAttemptCondenserSetEventList.add(event);
    }
    public static final class EventHandler {
        @SubscribeEvent
        public static void PlayersAttemptLearnEvent(PlayerAttemptLearnEvent evt ) {
            if(MCPlayersAttemptLearnEventList.hasHandlers()) {
                MCPlayersAttemptLearnEventList.publish(new MCPlayersAttemptLearnEvent(evt));
            }
        }

        @SubscribeEvent
        public static void PlayersAttemptCondenserSetEvent(PlayerAttemptCondenserSetEvent evt ) {
            if(MCPlayersAttemptCondenserSetEventList.hasHandlers()) {
                MCPlayersAttemptCondenserSetEventList.publish(new MCPlayersAttemptCondenserSetEvent(evt));
            }
        }
    }
}
