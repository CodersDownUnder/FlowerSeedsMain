package net.codersdownunder.flowerseeds.blocks;

import net.codersdownunder.flowerseeds.events.SeedColour;
import net.codersdownunder.flowerseeds.init.BlockInit;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.IPlantable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomCropBlock extends CropBlock implements IPlantable {

    private final SeedColour colour;

    public CustomCropBlock(Properties pProperties, SeedColour colour) {

        super(pProperties);
        this.colour = colour;

    }

    @Override
    public void appendHoverText(ItemStack p_49816_, @Nullable BlockGetter p_49817_, List<Component> list, TooltipFlag p_49819_) {

        if (ModList.get().isLoaded("flowerseedsaether") && this.getDescriptionId().contains("flowerseedsaether")) {
            list.add(Component.translatable("tooltip.aetherseed").setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
            return;
        }


        if (ModList.get().isLoaded("flowerseedsbop") && this.getDescriptionId().contains("flowerseedsbop")) {
            list.add(Component.translatable("tooltip.bopseed").setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));
            return;
        }


            list.add(Component.translatable("tooltip.vanillaseed").setStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)));


//        if (this.getDescriptionId().contains("flowerseedsaether")) {
//            list.add(Component.translatable("tooltip.aetherseed"));
//        }
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

        if (this == BlockInit.WITHER_ROSE_SEED.get()) {
            return ground == Blocks.SOUL_SAND || ground == Blocks.NETHERRACK || super.canSurvive(pState, pLevel, pPos);
        } else {
            return super.canSurvive(pState, pLevel, pPos);
        }

    }

}
