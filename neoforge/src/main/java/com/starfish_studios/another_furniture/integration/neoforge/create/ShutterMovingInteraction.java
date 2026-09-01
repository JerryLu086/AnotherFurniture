package com.starfish_studios.another_furniture.integration.neoforge.create;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.starfish_studios.another_furniture.block.ShutterBlock;
import com.starfish_studios.another_furniture.block.properties.VerticalConnectionType;
import com.starfish_studios.another_furniture.registry.AFItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

// Can't detect whether the player is using a hammer or not with SimpleBlockMovingInteraction, had to make this instead.
public class ShutterMovingInteraction extends MovingInteractionBehaviour {
    @Override
    public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos,
                                           AbstractContraptionEntity contraptionEntity) {
        Contraption contraption = contraptionEntity.getContraption();
        BlockState origin = contraption.getBlocks().get(localPos).state().cycle(ShutterBlock.OPEN),
                currentState = origin;
        BlockPos currentPos = localPos;
        boolean withHammer = player.getItemInHand(activeHand).is(AFItemTags.FURNITURE_HAMMER),
                isolate = player.isShiftKeyDown();

        int heightUp = (int) contraption.bounds.maxY - localPos.getY();
        for (int i = 0; i < heightUp; i++) {

            isolate |= currentState.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.SINGLE
                               || currentState.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.TOP;

            BlockPos offset = currentPos.above();
            StructureTemplate.StructureBlockInfo neighborInfo = contraption.getBlocks().get(offset);
            if (neighborInfo != null) {
                BlockState neighbor = neighborInfo.state();
                if (neighbor.is(currentState.getBlock())
                            && neighbor.getValue(ShutterBlock.FACING) == currentState.getValue(ShutterBlock.FACING)
                            && neighbor.getValue(ShutterBlock.HINGE) == currentState.getValue(ShutterBlock.HINGE)) {

                    if (isolate) {

                        Pair<BlockState, BlockState> updated
                                = updateConnection(currentState.cycle(ShutterBlock.OPEN), currentState, neighbor, true);
                        currentState = updated.getFirst();
                        neighbor = updated.getSecond();

                        this.setContraptionBlockData(contraption.entity, offset,
                                new StructureTemplate.StructureBlockInfo(neighborInfo.pos(), neighbor, neighborInfo.nbt()));

                        if (i > 0) {
                            StructureTemplate.StructureBlockInfo currentInfo = contraption.getBlocks().get(currentPos);
                            this.setContraptionBlockData(contraption.entity, currentPos,
                                    new StructureTemplate.StructureBlockInfo(currentInfo.pos(), currentState, currentInfo.nbt()));
                        } else {
                            origin = currentState;
                        }

                        break;

                    } else {

                        currentState = neighbor.cycle(ShutterBlock.OPEN);

                        if (neighbor.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.TOP
                                    || neighbor.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.MIDDLE) {
                            this.setContraptionBlockData(contraption.entity, offset,
                                    new StructureTemplate.StructureBlockInfo(neighborInfo.pos(), currentState, neighborInfo.nbt()));
                        }
                    }
                }
            }

            currentPos = offset;

        }

        currentPos = localPos;
        currentState = origin;
        isolate = player.isShiftKeyDown();

        int heightDown = (int) contraption.bounds.minY - localPos.getY();
        heightDown = Math.abs(heightDown);
        for (int i = 0; i < heightDown; i++) {

            isolate |= currentState.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.SINGLE
                               || currentState.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.BOTTOM;

            BlockPos offset = currentPos.below();
            StructureTemplate.StructureBlockInfo neighborInfo = contraption.getBlocks().get(offset);
            if (neighborInfo != null) {
                BlockState neighbor = neighborInfo.state();
                if (neighbor.is(currentState.getBlock())
                            && neighbor.getValue(ShutterBlock.FACING) == currentState.getValue(ShutterBlock.FACING)
                            && neighbor.getValue(ShutterBlock.HINGE) == currentState.getValue(ShutterBlock.HINGE)) {

                    if (isolate) {

                        Pair<BlockState, BlockState> updated
                                = updateConnection(currentState.cycle(ShutterBlock.OPEN), currentState, neighbor, false);
                        currentState = updated.getFirst();
                        neighbor = updated.getSecond();

                        this.setContraptionBlockData(contraption.entity, offset,
                                new StructureTemplate.StructureBlockInfo(neighborInfo.pos(), neighbor, neighborInfo.nbt()));

                        if (i > 0) {
                            StructureTemplate.StructureBlockInfo currentInfo = contraption.getBlocks().get(currentPos);
                            this.setContraptionBlockData(contraption.entity, currentPos,
                                    new StructureTemplate.StructureBlockInfo(currentInfo.pos(), currentState, currentInfo.nbt()));
                        } else {
                            origin = currentState;
                        }

                        break;

                    } else {

                        currentState = neighbor.cycle(ShutterBlock.OPEN);

                        if (neighbor.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.MIDDLE
                                    || neighbor.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.BOTTOM) {
                            this.setContraptionBlockData(contraption.entity, offset,
                                    new StructureTemplate.StructureBlockInfo(neighborInfo.pos(), currentState, neighborInfo.nbt()));
                        }
                    }
                }
            }

            currentPos = offset;

        }

        StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(localPos);
        this.setContraptionBlockData(contraption.entity, localPos,
                new StructureTemplate.StructureBlockInfo(info.pos(), origin, info.nbt()));

        player.level().playSound(null, player.blockPosition(), ShutterBlock.shutterSound(origin.getValue(ShutterBlock.OPEN)),
                SoundSource.BLOCKS, 0.3F, player.level().getRandom().nextFloat() * 0.1F + 0.9F);
        contraption.invalidateColliders();

        return true;
    }

    public static Pair<BlockState, BlockState> updateConnection(BlockState state, BlockState newState, BlockState neighbor, boolean above) {
        if (canConnectTo(state, neighbor) != canConnectTo(newState, neighbor)) {
            newState = cycleConnection(newState, above);
            neighbor = cycleConnection(neighbor, !above);
        }

        return Pair.of(newState, neighbor);
    }

    public static BlockState cycleConnection(BlockState state, boolean top) {
        return state.setValue(ShutterBlock.VERTICAL, cycleConnection(state.getValue(ShutterBlock.VERTICAL), top));
    }

    public static VerticalConnectionType cycleConnection(VerticalConnectionType type, boolean top) {
        return VerticalConnectionType.values()[type.ordinal() ^ (top ? 1 : 3)];
    }

    public static boolean canConnectTo(BlockState state, BlockState other) {
        return other.is(state.getBlock())
                       //&& other.getValue(VERTICAL) == state.getValue(VERTICAL)
                       && other.getValue(ShutterBlock.FACING) == state.getValue(ShutterBlock.FACING)
                       && other.getValue(ShutterBlock.OPEN) == state.getValue(ShutterBlock.OPEN)
                       && other.getValue(ShutterBlock.HINGE) == state.getValue(ShutterBlock.HINGE)
                       && other.getValue(ShutterBlock.VARIANT).equals(state.getValue(ShutterBlock.VARIANT));
    }
}
