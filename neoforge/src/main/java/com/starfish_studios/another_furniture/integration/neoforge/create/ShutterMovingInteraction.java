package com.starfish_studios.another_furniture.integration.neoforge.create;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.behaviour.SimpleBlockMovingInteraction;
import com.starfish_studios.another_furniture.block.ShutterBlock;
import com.starfish_studios.another_furniture.block.properties.VerticalConnectionType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class ShutterMovingInteraction extends SimpleBlockMovingInteraction {

    protected BlockState handle(Player player, Contraption contraption, BlockPos pos, BlockState currentState) {
        currentState = currentState.cycle(ShutterBlock.OPEN);

        if (!player.isShiftKeyDown()) {
            toggleShutters(currentState, contraption, pos);
        } else {
            currentState = notifyNeighbors(currentState, contraption, pos);
        }

        this.playSound(player, ShutterBlock.shutterSound(currentState.getValue(ShutterBlock.OPEN)), 1.0F);
        return currentState;
    }

    public void toggleShutters(BlockState state, Contraption contraption, BlockPos pos) {
        boolean open = state.getValue(ShutterBlock.OPEN);
        BlockState updateState = state;
        BlockPos updatePos = pos;
        if (state.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.MIDDLE || state.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.BOTTOM) {
            int heightUp = (int)contraption.bounds.maxY - updatePos.getY();
            for (int i = 0; i < heightUp; i++) {
                StructureTemplate.StructureBlockInfo above = contraption.getBlocks().get(updatePos.above());
                if (above != null && above.state().is(state.getBlock()) && above.state().getValue(ShutterBlock.FACING) == updateState.getValue(ShutterBlock.FACING) && above.state().getValue(ShutterBlock.HINGE) == updateState.getValue(ShutterBlock.HINGE) && above.state().getValue(ShutterBlock.OPEN) != open) {
                    updateState = above.state();
                    updatePos = updatePos.above();
                    this.setContraptionBlockData(contraption.entity, updatePos, new StructureTemplate.StructureBlockInfo(above.pos(), updateState.setValue(ShutterBlock.OPEN, open), above.nbt()));
                } else {
                    break;
                }
            }
        }
        if (state.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.MIDDLE || state.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.TOP) {
            updateState = state;
            updatePos = pos;
            int heightDown = (int)contraption.bounds.minY - updatePos.getY();
            heightDown = (heightDown < 0) ? -heightDown : heightDown;
            for (int i = 0; i < heightDown; i++) {
                StructureTemplate.StructureBlockInfo below = contraption.getBlocks().get(updatePos.below());
                if (below != null && below.state().is(state.getBlock()) && below.state().getValue(ShutterBlock.FACING) == updateState.getValue(ShutterBlock.FACING) && below.state().getValue(ShutterBlock.HINGE) == updateState.getValue(ShutterBlock.HINGE) && below.state().getValue(ShutterBlock.OPEN) != open) {
                    updateState = below.state();
                    updatePos = updatePos.below();
                    this.setContraptionBlockData(contraption.entity, updatePos, new StructureTemplate.StructureBlockInfo(below.pos(), updateState.setValue(ShutterBlock.OPEN, open), below.nbt()));
                } else {
                    break;
                }
            }
        }
    }

    public BlockState notifyNeighbors(BlockState state, Contraption contraption, BlockPos pos) {
        StructureTemplate.StructureBlockInfo above = contraption.getBlocks().get(pos.above());
        if (above != null) {
            BlockState neighbor = above.state();

            if (neighbor.is(state.getBlock())) {
                boolean connectionChanged = canConnectTo(state, neighbor)
                                         != canConnectTo(state.cycle(ShutterBlock.OPEN), neighbor);

                if (connectionChanged) {
                    state = cycleConnection(state, true);
                    this.setContraptionBlockData(contraption.entity, pos.above(),
                            new StructureTemplate.StructureBlockInfo(above.pos(), cycleConnection(neighbor, false), above.nbt()));
                }
            }
        }

        StructureTemplate.StructureBlockInfo below = contraption.getBlocks().get(pos.below());
        if (below != null) {
            BlockState neighbor = below.state();

            if (neighbor.is(state.getBlock())) {
                boolean connectionChanged = canConnectTo(state, neighbor)
                                                    != canConnectTo(state.cycle(ShutterBlock.OPEN), neighbor);

                if (connectionChanged) {
                    state = cycleConnection(state, false);
                    this.setContraptionBlockData(contraption.entity, pos.below(),
                            new StructureTemplate.StructureBlockInfo(below.pos(), cycleConnection(neighbor, true), below.nbt()));
                }
            }
        }

        return state;
    }

    public BlockState cycleConnection(BlockState state, boolean top) {
        return state.setValue(ShutterBlock.VERTICAL, cycleConnection(state.getValue(ShutterBlock.VERTICAL), top));
    }

    public VerticalConnectionType cycleConnection(VerticalConnectionType type, boolean top) {
        return VerticalConnectionType.values()[type.ordinal() ^ (top ? 1 : 3)];
    }

    public boolean canConnectTo(BlockState state, BlockState other) {
        return other.is(state.getBlock())
                       //&& other.getValue(VERTICAL) == state.getValue(VERTICAL)
                       && other.getValue(ShutterBlock.FACING) == state.getValue(ShutterBlock.FACING)
                       && other.getValue(ShutterBlock.OPEN) == state.getValue(ShutterBlock.OPEN)
                       && other.getValue(ShutterBlock.HINGE) == state.getValue(ShutterBlock.HINGE)
                       && other.getValue(ShutterBlock.VARIANT).equals(state.getValue(ShutterBlock.VARIANT));
    }

    @Override
    protected boolean updateColliders() {
        return true;
    }
}
