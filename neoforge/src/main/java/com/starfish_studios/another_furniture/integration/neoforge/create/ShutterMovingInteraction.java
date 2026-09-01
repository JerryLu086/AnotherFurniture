package com.starfish_studios.another_furniture.integration.neoforge.create;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.SimpleBlockMovingInteraction;
import com.starfish_studios.another_furniture.block.ShutterBlock;
import com.starfish_studios.another_furniture.block.properties.VerticalConnectionType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class ShutterMovingInteraction extends SimpleBlockMovingInteraction {
    @Override
    protected BlockState handle(Player player, Contraption contraption, BlockPos pos, BlockState currentState) {
        currentState = currentState.cycle(ShutterBlock.OPEN);

        BlockState origin = currentState;
        BlockPos currentPos = pos;
        boolean isolate = player.isShiftKeyDown();

        int heightUp = (int) contraption.bounds.maxY - pos.getY();
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
                                = ShutterBlock.updateConnection(currentState.cycle(ShutterBlock.OPEN), currentState, neighbor, true);
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

        currentPos = pos;
        currentState = origin;
        isolate = player.isShiftKeyDown();

        int heightDown = (int) contraption.bounds.minY - pos.getY();
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
                                = ShutterBlock.updateConnection(currentState.cycle(ShutterBlock.OPEN), currentState, neighbor, false);
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

        playSound(player, ShutterBlock.shutterSound(origin.getValue(ShutterBlock.OPEN)),
                player.level().getRandom().nextFloat() * 0.1F + 0.9F);

        return origin;
    }

    @Override
    protected boolean updateColliders() {
        return true;
    }
}
