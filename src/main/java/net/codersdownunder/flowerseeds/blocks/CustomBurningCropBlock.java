package net.codersdownunder.flowerseeds.blocks;

import net.codersdownunder.flowerseeds.events.SeedColour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.IPlantable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomBurningCropBlock extends CropBlock implements IPlantable {

    private final SeedColour colour;

    public CustomBurningCropBlock(Properties pProperties, SeedColour colour) {

        super(pProperties);
        this.colour = colour;

    }

    @Override
    public void appendHoverText(ItemStack p_49816_, @Nullable BlockGetter p_49817_, List<Component> list, TooltipFlag p_49819_) {

        list.add(Component.translatable("tooltip.bopseed").setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
    }

    public SeedColour getColour() {

        return colour;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader p_304482_, BlockPos p_52255_, BlockState p_52256_) {

        return this.asItem().getDefaultInstance();
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {

        Block ground = pLevel.getBlockState(pPos.below()).getBlock();

        return ground == Blocks.NETHERRACK || ground == Blocks.SOUL_SAND || ground == Blocks.SOUL_SOIL || ground == Blocks.CRIMSON_NYLIUM || ground == Blocks.WARPED_NYLIUM || super.canSurvive(pState, pLevel, pPos);

    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {

        if (this.getAge(pState) == 7) {
            if (!pEntity.fireImmune()) {
                pEntity.setRemainingFireTicks(pEntity.getRemainingFireTicks() + 1);
                if (pEntity.getRemainingFireTicks() == 0) {
                    pEntity.setSecondsOnFire(1);
                }

                pEntity.hurt(pLevel.damageSources().inFire(), 1.0F);
            }
        }

        super.entityInside(pState, pLevel, pPos, pEntity);
    }

    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {

        super.animateTick(stateIn, worldIn, pos, rand);
        if (rand.nextInt(8) == 0) {
            worldIn.addParticle(ParticleTypes.FLAME, (double) pos.getX() + 0.5 + (rand.nextDouble() - rand.nextDouble()) / 4.0, (double) pos.getY() + 0.75, (double) pos.getZ() + 0.5 + (rand.nextDouble() - rand.nextDouble()) / 4.0, 0.0, 0.0, 0.0);
        }

        if (rand.nextInt(4) == 0) {
            worldIn.addParticle(ParticleTypes.SMOKE, (double) pos.getX() + 0.5 + (rand.nextDouble() - rand.nextDouble()) / 4.0, (double) pos.getY() + 0.75, (double) pos.getZ() + 0.5 + (rand.nextDouble() - rand.nextDouble()) / 4.0, 0.0, 0.0, 0.0);
        }
    }

}
