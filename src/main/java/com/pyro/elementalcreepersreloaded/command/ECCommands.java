package com.pyro.elementalcreepersreloaded.command;

import com.pyro.elementalcreepersreloaded.ElementalCreepers;
import com.pyro.lomlibreloaded.nitea.NiteaSupport;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;

/**
 * {@code /elementalcreepers report bug <what happened>} and {@code /elementalcreepers report suggestion <idea>}: player
 * feedback straight to the mod's Nitea dashboard.
 */
public final class ECCommands {
    private ECCommands() {
    }

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("elementalcreepers")
                .then(NiteaSupport.reportCommand(ElementalCreepers::nitea)));
    }
}
