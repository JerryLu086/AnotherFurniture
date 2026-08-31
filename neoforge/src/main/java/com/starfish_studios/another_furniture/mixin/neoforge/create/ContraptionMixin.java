package com.starfish_studios.another_furniture.mixin.neoforge.create;

import com.simibubi.create.content.contraptions.Contraption;
import com.starfish_studios.another_furniture.block.SeatBlock;
import com.starfish_studios.another_furniture.entity.SeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

@Mixin(value = Contraption.class, remap = false)
public abstract class ContraptionMixin {
    @Shadow
    private Map<BlockPos, Entity> initialPassengers;
    @Shadow
    public abstract List<BlockPos> getSeats();
    @Shadow
    protected abstract BlockPos toLocalPos(BlockPos globalPos);

    @Inject(method = "moveBlock", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 7), locals = LocalCapture.CAPTURE_FAILHARD)
    private void moveBlock(Level world, Direction forcedDirection, Queue<BlockPos> frontier,
                           Set<BlockPos> visited, CallbackInfoReturnable<Boolean> cir,
                           BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof SeatBlock)
            moveAFSeat(world, pos);
    }

    @Unique
    private void moveAFSeat(Level world, BlockPos pos) {
        BlockPos local = toLocalPos(pos);
        getSeats().add(local);
        List<SeatEntity> seatsEntities = world.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
        if (!seatsEntities.isEmpty()) {
            SeatEntity seat = seatsEntities.get(0);
            List<Entity> passengers = seat.getPassengers();
            if (!passengers.isEmpty())
                initialPassengers.put(local, passengers.get(0));
        }
    }
}
