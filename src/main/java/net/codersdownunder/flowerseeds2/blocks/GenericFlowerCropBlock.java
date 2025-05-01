package net.codersdownunder.flowerseeds2.blocks;

import net.codersdownunder.flowerseeds2.init.CropRegistries;
import net.codersdownunder.flowerseeds2.util.SeedColour;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.SpecialPlantable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GenericFlowerCropBlock extends CropBlock implements SpecialPlantable {

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    };

    private SeedColour colour;

    public GenericFlowerCropBlock(Properties pProperties, SeedColour colour) {

        super(pProperties);
        this.colour = colour;
    }

    public SeedColour getColour() {

        return colour;
    }


    @Override
    public void appendHoverText(ItemStack p_49816_, Item.TooltipContext p_339606_, List<Component> list, TooltipFlag p_49819_) {

        if (ModList.get().isLoaded("flowerseedsaether") && this.getDescriptionId().contains("flowerseedsaether")) {
            list.add(Component.translatable("tooltip.aetherseed").setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
            return;
        }


        if (ModList.get().isLoaded("flowerseedsbop") && this.getDescriptionId().contains("flowerseedsbop")) {
            list.add(Component.translatable("tooltip.bopseed").setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
            return;
        }


        list.add(Component.translatable("tooltip.vanillaseed").setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));

    }

    @Override
    public ItemStack getCloneItemStack(LevelReader p_304482_, BlockPos p_52255_, BlockState p_52256_) {

        return this.asItem().getDefaultInstance();
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {

        Block ground = pLevel.getBlockState(pPos.below()).getBlock();

        if (this == CropRegistries.WITHER_ROSE_CROP.get()) {
            return ground == Blocks.SOUL_SAND || ground == Blocks.NETHERRACK;
        } else {
            return super.canSurvive(pState, pLevel, pPos);
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter block, BlockPos pos) {

        if (this == CropRegistries.WITHER_ROSE_CROP.get()) {
            return block.getBlockState(pos).getBlock() == Blocks.SOUL_SAND || block.getBlockState(pos).getBlock() == Blocks.NETHERRACK;
        }

        return super.mayPlaceOn(state, block, pos);
    }

    @Override
    public boolean canPlacePlantAtPosition(ItemStack itemStack, LevelReader level, BlockPos pos, @Nullable Direction direction) {

        return this.canSurvive(this.asBlock().defaultBlockState(), level, pos);
    }

    @Override
    public void spawnPlantAtPosition(ItemStack itemStack, LevelAccessor level, BlockPos pos, @Nullable Direction direction) {
        level.setBlock(pos, this.asBlock().defaultBlockState(), 2);

    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(this.getAgeProperty())];
    }

}
