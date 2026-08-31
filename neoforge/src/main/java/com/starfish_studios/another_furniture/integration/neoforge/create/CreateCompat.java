package com.starfish_studios.another_furniture.integration.neoforge.create;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.contraptions.actors.seat.SeatMovementBehaviour;
import com.starfish_studios.another_furniture.registry.AFBlockTags;

public class CreateCompat {
    public static void setup() {
        MovingInteractionBehaviour.REGISTRY.registerProvider(SimpleRegistry.Provider.forBlockTag(AFBlockTags.SHUTTERS, new ShutterMovingInteraction()));
        MovementBehaviour.REGISTRY.registerProvider(SimpleRegistry.Provider.forBlockTag(AFBlockTags.SEATS, new SeatMovementBehaviour()));
        BlockMovementChecks.registerAttachedCheck(new ShutterAttachedCheck());
    }
}