package com.starfish_studios.another_furniture.integration.neoforge.create;

import com.simibubi.create.api.contraption.BlockMovementChecks.AttachedCheck;
import com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult;
import com.starfish_studios.another_furniture.block.ShutterBlock;
import com.starfish_studios.another_furniture.block.properties.VerticalConnectionType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ShutterAttachedCheck implements AttachedCheck {
    @Override
    public CheckResult isBlockAttachedTowards(BlockState state, Level world, BlockPos pos, Direction direction) {
        if (state.getBlock() instanceof ShutterBlock) {
            VerticalConnectionType type = state.getValue(ShutterBlock.VERTICAL);

            if (direction == Direction.DOWN)
                return type == VerticalConnectionType.MIDDLE || type == VerticalConnectionType.BOTTOM ? CheckResult.SUCCESS : CheckResult.PASS;
            if (direction == Direction.UP)
                return type == VerticalConnectionType.TOP || type == VerticalConnectionType.MIDDLE ? CheckResult.SUCCESS : CheckResult.PASS;
        }
        return CheckResult.PASS;
    }
}
