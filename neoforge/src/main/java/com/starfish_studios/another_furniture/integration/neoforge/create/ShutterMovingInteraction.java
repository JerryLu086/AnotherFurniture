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

    protected BlockState handle(Player player, Contraption contraption, BlockPos pos, BlockState currentState) {
        currentState = toggleShutters(currentState, contraption, pos, player.isShiftKeyDown(), false);

        this.playSound(player, ShutterBlock.shutterSound(currentState.getValue(ShutterBlock.OPEN)), 1.0F);
        return currentState;
    }

    public BlockState toggleShutters(BlockState state, Contraption contraption, BlockPos pos, boolean crouching, boolean withHammer) {
        boolean open = state.getValue(ShutterBlock.OPEN);

        BlockPos currentPos = pos;
        BlockState currentState = state,
                   lastState = state,
                   result;

        // stops when either variants or open doesn't match
        int heightUp = (int) contraption.bounds.maxY - pos.getY();
        for (int i = 0; i < heightUp; i++) {
            StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(currentPos.above());
            if (info != null) {
                BlockState neighbor = info.state();
                if (neighbor.is(state.getBlock())
                            && neighbor.getValue(ShutterBlock.FACING) == currentState.getValue(ShutterBlock.FACING)
                            && neighbor.getValue(ShutterBlock.HINGE) == currentState.getValue(ShutterBlock.HINGE)) {

                    /*currentState = updateConnection(currentState, withHammer ? currentState :
                                                                      crouch ? currentState : currentState.setValue(ShutterBlock.OPEN, open),
                                                    contraption, pos, true);*/
                    Pair<BlockState, BlockState> updated = updateConnection(lastState,
                            withHammer ? currentState /*hammered*/ : crouching ? currentState.setValue(ShutterBlock.OPEN, open) : currentState, neighbor, false);

                    lastState = neighbor;

                    currentState = updated.getFirst();
                    neighbor = updated.getSecond();

                    if (currentPos == pos) {
                        result = currentState;
                    }

                    if (!neighbor.getValue(ShutterBlock.VARIANT).equals(lastState.getValue(ShutterBlock.VARIANT))
                                || neighbor.getValue(ShutterBlock.OPEN) != lastState.getValue(ShutterBlock.OPEN)) {
                        break;
                    }

                    currentPos = currentPos.above();
                }
            }
        }

        currentPos = pos;
        lastState = state;
        result = state;

        int heightDown = (int)contraption.bounds.minY - pos.getY();
        heightDown = (heightDown < 0) ? -heightDown : heightDown;
        for (int i = 0; i < heightDown; i++) {
            StructureTemplate.StructureBlockInfo info = contraption.getBlocks().get(currentPos.below());
            if (info != null) {
                BlockState neighbor = info.state();
                if (neighbor.is(state.getBlock())
                            && neighbor.getValue(ShutterBlock.FACING) == currentState.getValue(ShutterBlock.FACING)
                            && neighbor.getValue(ShutterBlock.HINGE) == currentState.getValue(ShutterBlock.HINGE)) {

                    /*currentState = updateConnection(currentState, withHammer ? currentState :
                                                                      crouch ? currentState : currentState.setValue(ShutterBlock.OPEN, open),
                                                    contraption, pos, true);*/
                    Pair<BlockState, BlockState> updated = updateConnection(lastState,
                            withHammer ? currentState /*hammered*/ : crouching ? currentState.setValue(ShutterBlock.OPEN, open) : currentState, neighbor, true);

                    lastState = neighbor;

                    currentState = updated.getFirst();
                    neighbor = updated.getSecond();

                    if (currentPos == pos) {
                        result = currentState;
                    }

                    if (!neighbor.getValue(ShutterBlock.VARIANT).equals(lastState.getValue(ShutterBlock.VARIANT))
                                || neighbor.getValue(ShutterBlock.OPEN) != lastState.getValue(ShutterBlock.OPEN)) {
                        break;
                    }

                    currentPos = currentPos.below();
                }
            }
        }

        /*BlockState updateState = state;
        BlockPos updatePos = pos;
        if (state.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.MIDDLE || state.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.BOTTOM) {
            int heightUp = (int)contraption.bounds.maxY - updatePos.getY();
            for (int i = 0; i < heightUp; i++) {
                StructureTemplate.StructureBlockInfo above = contraption.getBlocks().get(updatePos.above());
                if (above != null && above.state().is(state.getBlock())
                                  && above.state().getValue(ShutterBlock.FACING) == updateState.getValue(ShutterBlock.FACING)
                                  && above.state().getValue(ShutterBlock.HINGE) == updateState.getValue(ShutterBlock.HINGE)
                                  && above.state().getValue(ShutterBlock.OPEN) == open) {
                    updatePos = updatePos.above();
                    updateState = updateConnection(above.state(), contraption, updatePos, true);
                    this.setContraptionBlockData(contraption.entity, updatePos, new StructureTemplate.StructureBlockInfo(above.pos(), updateState.setValue(ShutterBlock.OPEN, open), above.nbt()));
                } else {
                    break;
                }
            }
        }
        if (state.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.MIDDLE || state.getValue(ShutterBlock.VERTICAL) == VerticalConnectionType.TOP) {
            open = state.getValue(ShutterBlock.OPEN);
            updateState = state;
            updatePos = pos;
            int heightDown = (int)contraption.bounds.minY - updatePos.getY();
            heightDown = (heightDown < 0) ? -heightDown : heightDown;
            for (int i = 0; i < heightDown; i++) {
                StructureTemplate.StructureBlockInfo below = contraption.getBlocks().get(updatePos.below());
                if (below != null && below.state().is(state.getBlock()) && below.state().getValue(ShutterBlock.FACING) == updateState.getValue(ShutterBlock.FACING) && below.state().getValue(ShutterBlock.HINGE) == updateState.getValue(ShutterBlock.HINGE) && below.state().getValue(ShutterBlock.OPEN) != open) {
                    updatePos = updatePos.below();
                    updateState = updateConnection(below.state(), contraption, updatePos, false);
                    this.setContraptionBlockData(contraption.entity, updatePos, new StructureTemplate.StructureBlockInfo(below.pos(), updateState.setValue(ShutterBlock.OPEN, open), below.nbt()));
                } else {
                    break;
                }
            }
        }*/
        return result;
    }

//    public Pair<BlockState, BlockState> updateConnection(BlockState state, BlockState newState, Contraption contraption, BlockPos pos, boolean above) {
//        BlockState neighbor = null;
//        BlockPos offset = above ? pos.above() : pos.below();
//        StructureTemplate.StructureBlockInfo target = contraption.getBlocks().get(offset);
//        if (target != null) {
//            neighbor = target.state();
//
//            if (neighbor.is(state.getBlock())) {
//                boolean connectionChanged = canConnectTo(state, neighbor)
//                                         != canConnectTo(newState, neighbor);
//
//                if (connectionChanged) {
//                    newState = cycleConnection(newState, above);
//                    neighbor = cycleConnection(neighbor, !above);
//                }
//            }
//        }
//        return Pair.of(newState, neighbor);
//    }

    public static Pair<BlockState, BlockState> updateConnection(BlockState state, BlockState newState, BlockState neighbor, boolean above) {
        if (neighbor.is(state.getBlock())) {
            boolean connectionChanged = canConnectTo(state, neighbor) != canConnectTo(newState, neighbor);

            if (connectionChanged) {
                newState = cycleConnection(newState, above);
                neighbor = cycleConnection(neighbor, !above);
            }
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

    @Override
    protected boolean updateColliders() {
        return true;
    }
}
