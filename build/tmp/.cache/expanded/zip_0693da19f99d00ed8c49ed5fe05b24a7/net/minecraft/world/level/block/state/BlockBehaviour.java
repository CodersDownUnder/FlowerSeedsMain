package net.minecraft.world.level.block.state;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BlockBehaviour implements FeatureElement {
    protected static final Direction[] UPDATE_SHAPE_ORDER = new Direction[]{
        Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP
    };
    protected final boolean hasCollision;
    protected final float explosionResistance;
    protected final boolean isRandomlyTicking;
    protected final SoundType soundType;
    protected final float friction;
    protected final float speedFactor;
    protected final float jumpFactor;
    protected final boolean dynamicShape;
    protected final FeatureFlagSet requiredFeatures;
    protected final BlockBehaviour.Properties properties;
    @Nullable
    protected ResourceLocation drops;

    public BlockBehaviour(BlockBehaviour.Properties p_60452_) {
        this.hasCollision = p_60452_.hasCollision;
        this.drops = p_60452_.drops;
        this.explosionResistance = p_60452_.explosionResistance;
        this.isRandomlyTicking = p_60452_.isRandomlyTicking;
        this.soundType = p_60452_.soundType;
        this.friction = p_60452_.friction;
        this.speedFactor = p_60452_.speedFactor;
        this.jumpFactor = p_60452_.jumpFactor;
        this.dynamicShape = p_60452_.dynamicShape;
        this.requiredFeatures = p_60452_.requiredFeatures;
        this.properties = p_60452_;
        final ResourceLocation lootTableCache = p_60452_.drops;
        if (lootTableCache != null) {
            this.lootTableSupplier = () -> lootTableCache;
        } else if (p_60452_.lootTableSupplier != null) {
            this.lootTableSupplier = p_60452_.lootTableSupplier;
        } else {
            this.lootTableSupplier = () -> {
                ResourceLocation registryName = BuiltInRegistries.BLOCK.getKey((Block) this);
                return new ResourceLocation(registryName.getNamespace(), "blocks/" + registryName.getPath());
            };
        }
    }

    public BlockBehaviour.Properties properties() {
        return this.properties;
    }

    protected abstract MapCodec<? extends Block> codec();

    public static <B extends Block> RecordCodecBuilder<B, BlockBehaviour.Properties> propertiesCodec() {
        return BlockBehaviour.Properties.CODEC.fieldOf("properties").forGetter(BlockBehaviour::properties);
    }

    public static <B extends Block> MapCodec<B> simpleCodec(Function<BlockBehaviour.Properties, B> p_304394_) {
        return RecordCodecBuilder.mapCodec(p_304392_ -> p_304392_.group(propertiesCodec()).apply(p_304392_, p_304394_));
    }

    @Deprecated
    public void updateIndirectNeighbourShapes(BlockState p_60520_, LevelAccessor p_60521_, BlockPos p_60522_, int p_60523_, int p_60524_) {
    }

    @Deprecated
    public boolean isPathfindable(BlockState p_60475_, BlockGetter p_60476_, BlockPos p_60477_, PathComputationType p_60478_) {
        switch(p_60478_) {
            case LAND:
                return !p_60475_.isCollisionShapeFullBlock(p_60476_, p_60477_);
            case WATER:
                return p_60476_.getFluidState(p_60477_).is(FluidTags.WATER);
            case AIR:
                return !p_60475_.isCollisionShapeFullBlock(p_60476_, p_60477_);
            default:
                return false;
        }
    }

    @Deprecated
    public BlockState updateShape(BlockState p_60541_, Direction p_60542_, BlockState p_60543_, LevelAccessor p_60544_, BlockPos p_60545_, BlockPos p_60546_) {
        return p_60541_;
    }

    @Deprecated
    public boolean skipRendering(BlockState p_60532_, BlockState p_60533_, Direction p_60534_) {
        return false;
    }

    @Deprecated
    public void neighborChanged(BlockState p_60509_, Level p_60510_, BlockPos p_60511_, Block p_60512_, BlockPos p_60513_, boolean p_60514_) {
        DebugPackets.sendNeighborsUpdatePacket(p_60510_, p_60511_);
    }

    @Deprecated
    public void onPlace(BlockState p_60566_, Level p_60567_, BlockPos p_60568_, BlockState p_60569_, boolean p_60570_) {
    }

    @Deprecated
    public void onRemove(BlockState p_60515_, Level p_60516_, BlockPos p_60517_, BlockState p_60518_, boolean p_60519_) {
        if (p_60515_.hasBlockEntity() && (!p_60515_.is(p_60518_.getBlock()) || !p_60518_.hasBlockEntity())) {
            p_60516_.removeBlockEntity(p_60517_);
        }
    }

    @Deprecated
    public void onExplosionHit(BlockState p_311951_, Level p_312820_, BlockPos p_312489_, Explosion p_312925_, BiConsumer<ItemStack, BlockPos> p_312073_) {
        if (!p_311951_.isAir() && p_312925_.getBlockInteraction() != Explosion.BlockInteraction.TRIGGER_BLOCK) {
            Block block = p_311951_.getBlock();
            boolean flag = p_312925_.getIndirectSourceEntity() instanceof Player;
            if (p_311951_.canDropFromExplosion(p_312820_, p_312489_, p_312925_) && p_312820_ instanceof ServerLevel serverlevel) {
                BlockEntity blockentity = p_311951_.hasBlockEntity() ? p_312820_.getBlockEntity(p_312489_) : null;
                LootParams.Builder lootparams$builder = new LootParams.Builder(serverlevel)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(p_312489_))
                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity)
                    .withOptionalParameter(LootContextParams.THIS_ENTITY, p_312925_.getDirectSourceEntity());
                if (p_312925_.getBlockInteraction() == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
                    lootparams$builder.withParameter(LootContextParams.EXPLOSION_RADIUS, p_312925_.radius());
                }

                p_311951_.spawnAfterBreak(serverlevel, p_312489_, ItemStack.EMPTY, flag);
                p_311951_.getDrops(lootparams$builder).forEach(p_311752_ -> p_312073_.accept(p_311752_, p_312489_));
            }

            p_311951_.onBlockExploded(p_312820_, p_312489_, p_312925_);
        }
    }

    @Deprecated
    public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
        return InteractionResult.PASS;
    }

    @Deprecated
    public boolean triggerEvent(BlockState p_60490_, Level p_60491_, BlockPos p_60492_, int p_60493_, int p_60494_) {
        return false;
    }

    @Deprecated
    public RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.MODEL;
    }

    @Deprecated
    public boolean useShapeForLightOcclusion(BlockState p_60576_) {
        return false;
    }

    @Deprecated
    public boolean isSignalSource(BlockState p_60571_) {
        return false;
    }

    @Deprecated
    public FluidState getFluidState(BlockState p_60577_) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Deprecated
    public boolean hasAnalogOutputSignal(BlockState p_60457_) {
        return false;
    }

    public float getMaxHorizontalOffset() {
        return 0.25F;
    }

    public float getMaxVerticalOffset() {
        return 0.2F;
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.requiredFeatures;
    }

    @Deprecated
    public BlockState rotate(BlockState p_60530_, Rotation p_60531_) {
        return p_60530_;
    }

    @Deprecated
    public BlockState mirror(BlockState p_60528_, Mirror p_60529_) {
        return p_60528_;
    }

    @Deprecated
    public boolean canBeReplaced(BlockState p_60470_, BlockPlaceContext p_60471_) {
        return p_60470_.canBeReplaced() && (p_60471_.getItemInHand().isEmpty() || !p_60471_.getItemInHand().is(this.asItem()));
    }

    @Deprecated
    public boolean canBeReplaced(BlockState p_60535_, Fluid p_60536_) {
        return p_60535_.canBeReplaced() || !p_60535_.isSolid();
    }

    @Deprecated
    public List<ItemStack> getDrops(BlockState p_287732_, LootParams.Builder p_287596_) {
        ResourceLocation resourcelocation = this.getLootTable();
        if (resourcelocation == BuiltInLootTables.EMPTY) {
            return Collections.emptyList();
        } else {
            LootParams lootparams = p_287596_.withParameter(LootContextParams.BLOCK_STATE, p_287732_).create(LootContextParamSets.BLOCK);
            ServerLevel serverlevel = lootparams.getLevel();
            LootTable loottable = serverlevel.getServer().getLootData().getLootTable(resourcelocation);
            return loottable.getRandomItems(lootparams);
        }
    }

    @Deprecated
    public long getSeed(BlockState p_60539_, BlockPos p_60540_) {
        return Mth.getSeed(p_60540_);
    }

    @Deprecated
    public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
        return p_60578_.getShape(p_60579_, p_60580_);
    }

    @Deprecated
    public VoxelShape getBlockSupportShape(BlockState p_60581_, BlockGetter p_60582_, BlockPos p_60583_) {
        return this.getCollisionShape(p_60581_, p_60582_, p_60583_, CollisionContext.empty());
    }

    @Deprecated
    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return Shapes.empty();
    }

    @Deprecated
    public int getLightBlock(BlockState p_60585_, BlockGetter p_60586_, BlockPos p_60587_) {
        if (p_60585_.isSolidRender(p_60586_, p_60587_)) {
            return p_60586_.getMaxLightLevel();
        } else {
            return p_60585_.propagatesSkylightDown(p_60586_, p_60587_) ? 0 : 1;
        }
    }

    @Nullable
    @Deprecated
    public MenuProvider getMenuProvider(BlockState p_60563_, Level p_60564_, BlockPos p_60565_) {
        return null;
    }

    @Deprecated
    public boolean canSurvive(BlockState p_60525_, LevelReader p_60526_, BlockPos p_60527_) {
        return true;
    }

    @Deprecated
    public float getShadeBrightness(BlockState p_60472_, BlockGetter p_60473_, BlockPos p_60474_) {
        return p_60472_.isCollisionShapeFullBlock(p_60473_, p_60474_) ? 0.2F : 1.0F;
    }

    @Deprecated
    public int getAnalogOutputSignal(BlockState p_60487_, Level p_60488_, BlockPos p_60489_) {
        return 0;
    }

    @Deprecated
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return Shapes.block();
    }

    @Deprecated
    public VoxelShape getCollisionShape(BlockState p_60572_, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_) {
        return this.hasCollision ? p_60572_.getShape(p_60573_, p_60574_) : Shapes.empty();
    }

    @Deprecated
    public boolean isCollisionShapeFullBlock(BlockState p_181242_, BlockGetter p_181243_, BlockPos p_181244_) {
        return Block.isShapeFullBlock(p_181242_.getCollisionShape(p_181243_, p_181244_));
    }

    @Deprecated
    public boolean isOcclusionShapeFullBlock(BlockState p_222959_, BlockGetter p_222960_, BlockPos p_222961_) {
        return Block.isShapeFullBlock(p_222959_.getOcclusionShape(p_222960_, p_222961_));
    }

    @Deprecated
    public VoxelShape getVisualShape(BlockState p_60479_, BlockGetter p_60480_, BlockPos p_60481_, CollisionContext p_60482_) {
        return this.getCollisionShape(p_60479_, p_60480_, p_60481_, p_60482_);
    }

    @Deprecated
    public void randomTick(BlockState p_222954_, ServerLevel p_222955_, BlockPos p_222956_, RandomSource p_222957_) {
    }

    @Deprecated
    public void tick(BlockState p_222945_, ServerLevel p_222946_, BlockPos p_222947_, RandomSource p_222948_) {
    }

    @Deprecated
    public float getDestroyProgress(BlockState p_60466_, Player p_60467_, BlockGetter p_60468_, BlockPos p_60469_) {
        float f = p_60466_.getDestroySpeed(p_60468_, p_60469_);
        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = net.neoforged.neoforge.common.CommonHooks.isCorrectToolForDrops(p_60466_, p_60467_) ? 30 : 100;
            return p_60467_.getDigSpeed(p_60466_, p_60469_) / f / (float)i;
        }
    }

    @Deprecated
    public void spawnAfterBreak(BlockState p_222949_, ServerLevel p_222950_, BlockPos p_222951_, ItemStack p_222952_, boolean p_222953_) {
        if (p_222953_) net.neoforged.neoforge.common.CommonHooks.dropXpForBlock(p_222949_, p_222950_, p_222951_, p_222952_);
    }

    @Deprecated
    public void attack(BlockState p_60499_, Level p_60500_, BlockPos p_60501_, Player p_60502_) {
    }

    @Deprecated
    public int getSignal(BlockState p_60483_, BlockGetter p_60484_, BlockPos p_60485_, Direction p_60486_) {
        return 0;
    }

    @Deprecated
    public void entityInside(BlockState p_60495_, Level p_60496_, BlockPos p_60497_, Entity p_60498_) {
    }

    @Deprecated
    public int getDirectSignal(BlockState p_60559_, BlockGetter p_60560_, BlockPos p_60561_, Direction p_60562_) {
        return 0;
    }

    public final ResourceLocation getLootTable() {
        if (this.drops == null) {
            this.drops = this.lootTableSupplier.get();
        }

        return this.drops;
    }

    @Deprecated
    public void onProjectileHit(Level p_60453_, BlockState p_60454_, BlockHitResult p_60455_, Projectile p_60456_) {
    }

    public abstract Item asItem();

    protected abstract Block asBlock();

    public MapColor defaultMapColor() {
        return this.properties.mapColor.apply(this.asBlock().defaultBlockState());
    }

    public float defaultDestroyTime() {
        return this.properties.destroyTime;
    }

    protected boolean isAir(BlockState state) {
        return ((BlockStateBase)state).isAir;
    }

    /* ======================================== FORGE START ===================================== */
    private final java.util.function.Supplier<ResourceLocation> lootTableSupplier;
    /* ========================================= FORGE END ====================================== */

    public abstract static class BlockStateBase extends StateHolder<Block, BlockState> {
        private final int lightEmission;
        private final boolean useShapeForLightOcclusion;
        private final boolean isAir;
        private final boolean ignitedByLava;
        @Deprecated
        private final boolean liquid;
        @Deprecated
        private boolean legacySolid;
        private final PushReaction pushReaction;
        private final MapColor mapColor;
        private final float destroySpeed;
        private final boolean requiresCorrectToolForDrops;
        private final boolean canOcclude;
        private final BlockBehaviour.StatePredicate isRedstoneConductor;
        private final BlockBehaviour.StatePredicate isSuffocating;
        private final BlockBehaviour.StatePredicate isViewBlocking;
        private final BlockBehaviour.StatePredicate hasPostProcess;
        private final BlockBehaviour.StatePredicate emissiveRendering;
        private final Optional<BlockBehaviour.OffsetFunction> offsetFunction;
        private final boolean spawnTerrainParticles;
        private final NoteBlockInstrument instrument;
        private final boolean replaceable;
        @Nullable
        protected BlockBehaviour.BlockStateBase.Cache cache;
        private FluidState fluidState = Fluids.EMPTY.defaultFluidState();
        private boolean isRandomlyTicking;

        protected BlockStateBase(Block p_60608_, ImmutableMap<Property<?>, Comparable<?>> p_60609_, MapCodec<BlockState> p_60610_) {
            super(p_60608_, p_60609_, p_60610_);
            BlockBehaviour.Properties blockbehaviour$properties = p_60608_.properties;
            this.lightEmission = blockbehaviour$properties.lightEmission.applyAsInt(this.asState());
            this.useShapeForLightOcclusion = p_60608_.useShapeForLightOcclusion(this.asState());
            this.isAir = blockbehaviour$properties.isAir;
            this.ignitedByLava = blockbehaviour$properties.ignitedByLava;
            this.liquid = blockbehaviour$properties.liquid;
            this.pushReaction = blockbehaviour$properties.pushReaction;
            this.mapColor = blockbehaviour$properties.mapColor.apply(this.asState());
            this.destroySpeed = blockbehaviour$properties.destroyTime;
            this.requiresCorrectToolForDrops = blockbehaviour$properties.requiresCorrectToolForDrops;
            this.canOcclude = blockbehaviour$properties.canOcclude;
            this.isRedstoneConductor = blockbehaviour$properties.isRedstoneConductor;
            this.isSuffocating = blockbehaviour$properties.isSuffocating;
            this.isViewBlocking = blockbehaviour$properties.isViewBlocking;
            this.hasPostProcess = blockbehaviour$properties.hasPostProcess;
            this.emissiveRendering = blockbehaviour$properties.emissiveRendering;
            this.offsetFunction = blockbehaviour$properties.offsetFunction;
            this.spawnTerrainParticles = blockbehaviour$properties.spawnTerrainParticles;
            this.instrument = blockbehaviour$properties.instrument;
            this.replaceable = blockbehaviour$properties.replaceable;
        }

        private boolean calculateSolid() {
            if (this.owner.properties.forceSolidOn) {
                return true;
            } else if (this.owner.properties.forceSolidOff) {
                return false;
            } else if (this.cache == null) {
                return false;
            } else {
                VoxelShape voxelshape = this.cache.collisionShape;
                if (voxelshape.isEmpty()) {
                    return false;
                } else {
                    AABB aabb = voxelshape.bounds();
                    if (aabb.getSize() >= 0.7291666666666666) {
                        return true;
                    } else {
                        return aabb.getYsize() >= 1.0;
                    }
                }
            }
        }

        public void initCache() {
            this.fluidState = this.owner.getFluidState(this.asState());
            this.isRandomlyTicking = this.owner.isRandomlyTicking(this.asState());
            if (!this.getBlock().hasDynamicShape()) {
                this.cache = new BlockBehaviour.BlockStateBase.Cache(this.asState());
            }

            this.legacySolid = this.calculateSolid();
        }

        public Block getBlock() {
            return this.owner;
        }

        public Holder<Block> getBlockHolder() {
            return this.owner.builtInRegistryHolder();
        }

        @Deprecated
        public boolean blocksMotion() {
            Block block = this.getBlock();
            return block != Blocks.COBWEB && block != Blocks.BAMBOO_SAPLING && this.isSolid();
        }

        @Deprecated
        public boolean isSolid() {
            return this.legacySolid;
        }

        public boolean isValidSpawn(BlockGetter p_60644_, BlockPos p_60645_, EntityType<?> p_60646_) {
            return this.getBlock().properties.isValidSpawn.test(this.asState(), p_60644_, p_60645_, p_60646_);
        }

        public boolean propagatesSkylightDown(BlockGetter p_60632_, BlockPos p_60633_) {
            return this.cache != null ? this.cache.propagatesSkylightDown : this.getBlock().propagatesSkylightDown(this.asState(), p_60632_, p_60633_);
        }

        public int getLightBlock(BlockGetter p_60740_, BlockPos p_60741_) {
            return this.cache != null ? this.cache.lightBlock : this.getBlock().getLightBlock(this.asState(), p_60740_, p_60741_);
        }

        public VoxelShape getFaceOcclusionShape(BlockGetter p_60656_, BlockPos p_60657_, Direction p_60658_) {
            return this.cache != null && this.cache.occlusionShapes != null
                ? this.cache.occlusionShapes[p_60658_.ordinal()]
                : Shapes.getFaceShape(this.getOcclusionShape(p_60656_, p_60657_), p_60658_);
        }

        public VoxelShape getOcclusionShape(BlockGetter p_60769_, BlockPos p_60770_) {
            return this.getBlock().getOcclusionShape(this.asState(), p_60769_, p_60770_);
        }

        public boolean hasLargeCollisionShape() {
            return this.cache == null || this.cache.largeCollisionShape;
        }

        public boolean useShapeForLightOcclusion() {
            return this.useShapeForLightOcclusion;
        }

        /** @deprecated Forge: Use {@link BlockState#getLightEmission(BlockGetter, BlockPos)} instead */
        @Deprecated
        public int getLightEmission() {
            return this.lightEmission;
        }

        public boolean isAir() {
            return this.getBlock().isAir((BlockState)this);
        }

        public boolean ignitedByLava() {
            return this.ignitedByLava;
        }

        @Deprecated
        public boolean liquid() {
            return this.liquid;
        }

        public MapColor getMapColor(BlockGetter p_285002_, BlockPos p_285293_) {
            return getBlock().getMapColor(this.asState(), p_285002_, p_285293_, this.mapColor);
        }

        /** @deprecated use {@link BlockState#rotate(LevelAccessor, BlockPos, Rotation)} */
        @Deprecated
        public BlockState rotate(Rotation p_60718_) {
            return this.getBlock().rotate(this.asState(), p_60718_);
        }

        public BlockState mirror(Mirror p_60716_) {
            return this.getBlock().mirror(this.asState(), p_60716_);
        }

        public RenderShape getRenderShape() {
            return this.getBlock().getRenderShape(this.asState());
        }

        public boolean emissiveRendering(BlockGetter p_60789_, BlockPos p_60790_) {
            return this.emissiveRendering.test(this.asState(), p_60789_, p_60790_);
        }

        public float getShadeBrightness(BlockGetter p_60793_, BlockPos p_60794_) {
            return this.getBlock().getShadeBrightness(this.asState(), p_60793_, p_60794_);
        }

        public boolean isRedstoneConductor(BlockGetter p_60797_, BlockPos p_60798_) {
            return this.isRedstoneConductor.test(this.asState(), p_60797_, p_60798_);
        }

        public boolean isSignalSource() {
            return this.getBlock().isSignalSource(this.asState());
        }

        public int getSignal(BlockGetter p_60747_, BlockPos p_60748_, Direction p_60749_) {
            return this.getBlock().getSignal(this.asState(), p_60747_, p_60748_, p_60749_);
        }

        public boolean hasAnalogOutputSignal() {
            return this.getBlock().hasAnalogOutputSignal(this.asState());
        }

        public int getAnalogOutputSignal(Level p_60675_, BlockPos p_60676_) {
            return this.getBlock().getAnalogOutputSignal(this.asState(), p_60675_, p_60676_);
        }

        public float getDestroySpeed(BlockGetter p_60801_, BlockPos p_60802_) {
            return this.destroySpeed;
        }

        public float getDestroyProgress(Player p_60626_, BlockGetter p_60627_, BlockPos p_60628_) {
            return this.getBlock().getDestroyProgress(this.asState(), p_60626_, p_60627_, p_60628_);
        }

        public int getDirectSignal(BlockGetter p_60776_, BlockPos p_60777_, Direction p_60778_) {
            return this.getBlock().getDirectSignal(this.asState(), p_60776_, p_60777_, p_60778_);
        }

        public PushReaction getPistonPushReaction() {
            PushReaction reaction = getBlock().getPistonPushReaction(asState());
            if (reaction != null) return reaction;
            return this.pushReaction;
        }

        public boolean isSolidRender(BlockGetter p_60805_, BlockPos p_60806_) {
            if (this.cache != null) {
                return this.cache.solidRender;
            } else {
                BlockState blockstate = this.asState();
                return blockstate.canOcclude() ? Block.isShapeFullBlock(blockstate.getOcclusionShape(p_60805_, p_60806_)) : false;
            }
        }

        public boolean canOcclude() {
            return this.canOcclude;
        }

        public boolean skipRendering(BlockState p_60720_, Direction p_60721_) {
            return this.getBlock().skipRendering(this.asState(), p_60720_, p_60721_);
        }

        public VoxelShape getShape(BlockGetter p_60809_, BlockPos p_60810_) {
            return this.getShape(p_60809_, p_60810_, CollisionContext.empty());
        }

        public VoxelShape getShape(BlockGetter p_60652_, BlockPos p_60653_, CollisionContext p_60654_) {
            return this.getBlock().getShape(this.asState(), p_60652_, p_60653_, p_60654_);
        }

        public VoxelShape getCollisionShape(BlockGetter p_60813_, BlockPos p_60814_) {
            return this.cache != null ? this.cache.collisionShape : this.getCollisionShape(p_60813_, p_60814_, CollisionContext.empty());
        }

        public VoxelShape getCollisionShape(BlockGetter p_60743_, BlockPos p_60744_, CollisionContext p_60745_) {
            return this.getBlock().getCollisionShape(this.asState(), p_60743_, p_60744_, p_60745_);
        }

        public VoxelShape getBlockSupportShape(BlockGetter p_60817_, BlockPos p_60818_) {
            return this.getBlock().getBlockSupportShape(this.asState(), p_60817_, p_60818_);
        }

        public VoxelShape getVisualShape(BlockGetter p_60772_, BlockPos p_60773_, CollisionContext p_60774_) {
            return this.getBlock().getVisualShape(this.asState(), p_60772_, p_60773_, p_60774_);
        }

        public VoxelShape getInteractionShape(BlockGetter p_60821_, BlockPos p_60822_) {
            return this.getBlock().getInteractionShape(this.asState(), p_60821_, p_60822_);
        }

        public final boolean entityCanStandOn(BlockGetter p_60635_, BlockPos p_60636_, Entity p_60637_) {
            return this.entityCanStandOnFace(p_60635_, p_60636_, p_60637_, Direction.UP);
        }

        public final boolean entityCanStandOnFace(BlockGetter p_60639_, BlockPos p_60640_, Entity p_60641_, Direction p_60642_) {
            return Block.isFaceFull(this.getCollisionShape(p_60639_, p_60640_, CollisionContext.of(p_60641_)), p_60642_);
        }

        public Vec3 getOffset(BlockGetter p_60825_, BlockPos p_60826_) {
            return this.offsetFunction.<Vec3>map(p_273089_ -> p_273089_.evaluate(this.asState(), p_60825_, p_60826_)).orElse(Vec3.ZERO);
        }

        public boolean hasOffsetFunction() {
            return this.offsetFunction.isPresent();
        }

        public boolean triggerEvent(Level p_60678_, BlockPos p_60679_, int p_60680_, int p_60681_) {
            return this.getBlock().triggerEvent(this.asState(), p_60678_, p_60679_, p_60680_, p_60681_);
        }

        @Deprecated
        public void neighborChanged(Level p_60691_, BlockPos p_60692_, Block p_60693_, BlockPos p_60694_, boolean p_60695_) {
            this.getBlock().neighborChanged(this.asState(), p_60691_, p_60692_, p_60693_, p_60694_, p_60695_);
        }

        public final void updateNeighbourShapes(LevelAccessor p_60702_, BlockPos p_60703_, int p_60704_) {
            this.updateNeighbourShapes(p_60702_, p_60703_, p_60704_, 512);
        }

        public final void updateNeighbourShapes(LevelAccessor p_60706_, BlockPos p_60707_, int p_60708_, int p_60709_) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(Direction direction : BlockBehaviour.UPDATE_SHAPE_ORDER) {
                blockpos$mutableblockpos.setWithOffset(p_60707_, direction);
                p_60706_.neighborShapeChanged(direction.getOpposite(), this.asState(), blockpos$mutableblockpos, p_60707_, p_60708_, p_60709_);
            }
        }

        public final void updateIndirectNeighbourShapes(LevelAccessor p_60759_, BlockPos p_60760_, int p_60761_) {
            this.updateIndirectNeighbourShapes(p_60759_, p_60760_, p_60761_, 512);
        }

        public void updateIndirectNeighbourShapes(LevelAccessor p_60763_, BlockPos p_60764_, int p_60765_, int p_60766_) {
            this.getBlock().updateIndirectNeighbourShapes(this.asState(), p_60763_, p_60764_, p_60765_, p_60766_);
        }

        public void onPlace(Level p_60697_, BlockPos p_60698_, BlockState p_60699_, boolean p_60700_) {
            this.getBlock().onPlace(this.asState(), p_60697_, p_60698_, p_60699_, p_60700_);
        }

        public void onRemove(Level p_60754_, BlockPos p_60755_, BlockState p_60756_, boolean p_60757_) {
            this.getBlock().onRemove(this.asState(), p_60754_, p_60755_, p_60756_, p_60757_);
        }

        public void onExplosionHit(Level p_312839_, BlockPos p_311872_, Explosion p_312863_, BiConsumer<ItemStack, BlockPos> p_312559_) {
            this.getBlock().onExplosionHit(this.asState(), p_312839_, p_311872_, p_312863_, p_312559_);
        }

        public void tick(ServerLevel p_222964_, BlockPos p_222965_, RandomSource p_222966_) {
            this.getBlock().tick(this.asState(), p_222964_, p_222965_, p_222966_);
        }

        public void randomTick(ServerLevel p_222973_, BlockPos p_222974_, RandomSource p_222975_) {
            this.getBlock().randomTick(this.asState(), p_222973_, p_222974_, p_222975_);
        }

        public void entityInside(Level p_60683_, BlockPos p_60684_, Entity p_60685_) {
            this.getBlock().entityInside(this.asState(), p_60683_, p_60684_, p_60685_);
        }

        public void spawnAfterBreak(ServerLevel p_222968_, BlockPos p_222969_, ItemStack p_222970_, boolean p_222971_) {
            this.getBlock().spawnAfterBreak(this.asState(), p_222968_, p_222969_, p_222970_, p_222971_);
        }

        public List<ItemStack> getDrops(LootParams.Builder p_287688_) {
            return this.getBlock().getDrops(this.asState(), p_287688_);
        }

        public InteractionResult use(Level p_60665_, Player p_60666_, InteractionHand p_60667_, BlockHitResult p_60668_) {
            var useOnContext = new net.minecraft.world.item.context.UseOnContext(p_60665_, p_60666_, p_60667_, p_60666_.getItemInHand(p_60667_).copy(), p_60668_);
            var e = net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent(useOnContext, net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent.UsePhase.BLOCK));
            if (e.isCanceled()) return e.getCancellationResult();
            return this.getBlock().use(this.asState(), p_60665_, p_60668_.getBlockPos(), p_60666_, p_60667_, p_60668_);
        }

        public void attack(Level p_60687_, BlockPos p_60688_, Player p_60689_) {
            this.getBlock().attack(this.asState(), p_60687_, p_60688_, p_60689_);
        }

        public boolean isSuffocating(BlockGetter p_60829_, BlockPos p_60830_) {
            return this.isSuffocating.test(this.asState(), p_60829_, p_60830_);
        }

        public boolean isViewBlocking(BlockGetter p_60832_, BlockPos p_60833_) {
            return this.isViewBlocking.test(this.asState(), p_60832_, p_60833_);
        }

        public BlockState updateShape(Direction p_60729_, BlockState p_60730_, LevelAccessor p_60731_, BlockPos p_60732_, BlockPos p_60733_) {
            return this.getBlock().updateShape(this.asState(), p_60729_, p_60730_, p_60731_, p_60732_, p_60733_);
        }

        public boolean isPathfindable(BlockGetter p_60648_, BlockPos p_60649_, PathComputationType p_60650_) {
            return this.getBlock().isPathfindable(this.asState(), p_60648_, p_60649_, p_60650_);
        }

        public boolean canBeReplaced(BlockPlaceContext p_60630_) {
            return this.getBlock().canBeReplaced(this.asState(), p_60630_);
        }

        public boolean canBeReplaced(Fluid p_60723_) {
            return this.getBlock().canBeReplaced(this.asState(), p_60723_);
        }

        public boolean canBeReplaced() {
            return this.replaceable;
        }

        public boolean canSurvive(LevelReader p_60711_, BlockPos p_60712_) {
            return this.getBlock().canSurvive(this.asState(), p_60711_, p_60712_);
        }

        public boolean hasPostProcess(BlockGetter p_60836_, BlockPos p_60837_) {
            return this.hasPostProcess.test(this.asState(), p_60836_, p_60837_);
        }

        @Nullable
        public MenuProvider getMenuProvider(Level p_60751_, BlockPos p_60752_) {
            return this.getBlock().getMenuProvider(this.asState(), p_60751_, p_60752_);
        }

        public boolean is(TagKey<Block> p_204337_) {
            return this.getBlock().builtInRegistryHolder().is(p_204337_);
        }

        public boolean is(TagKey<Block> p_204339_, Predicate<BlockBehaviour.BlockStateBase> p_204340_) {
            return this.is(p_204339_) && p_204340_.test(this);
        }

        public boolean is(HolderSet<Block> p_204342_) {
            return p_204342_.contains(this.getBlock().builtInRegistryHolder());
        }

        public boolean is(Holder<Block> p_298890_) {
            return this.is(p_298890_.value());
        }

        public Stream<TagKey<Block>> getTags() {
            return this.getBlock().builtInRegistryHolder().tags();
        }

        public boolean hasBlockEntity() {
            return this.getBlock() instanceof EntityBlock;
        }

        @Nullable
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_155945_, BlockEntityType<T> p_155946_) {
            return this.getBlock() instanceof EntityBlock ? ((EntityBlock)this.getBlock()).getTicker(p_155945_, this.asState(), p_155946_) : null;
        }

        public boolean is(Block p_60714_) {
            return this.getBlock() == p_60714_;
        }

        public boolean is(ResourceKey<Block> p_304668_) {
            return this.getBlock().builtInRegistryHolder().is(p_304668_);
        }

        public FluidState getFluidState() {
            return this.fluidState;
        }

        public boolean isRandomlyTicking() {
            return this.isRandomlyTicking;
        }

        public long getSeed(BlockPos p_60727_) {
            return this.getBlock().getSeed(this.asState(), p_60727_);
        }

        public SoundType getSoundType() {
            return this.getBlock().getSoundType(this.asState());
        }

        public void onProjectileHit(Level p_60670_, BlockState p_60671_, BlockHitResult p_60672_, Projectile p_60673_) {
            this.getBlock().onProjectileHit(p_60670_, p_60671_, p_60672_, p_60673_);
        }

        public boolean isFaceSturdy(BlockGetter p_60784_, BlockPos p_60785_, Direction p_60786_) {
            return this.isFaceSturdy(p_60784_, p_60785_, p_60786_, SupportType.FULL);
        }

        public boolean isFaceSturdy(BlockGetter p_60660_, BlockPos p_60661_, Direction p_60662_, SupportType p_60663_) {
            return this.cache != null ? this.cache.isFaceSturdy(p_60662_, p_60663_) : p_60663_.isSupporting(this.asState(), p_60660_, p_60661_, p_60662_);
        }

        public boolean isCollisionShapeFullBlock(BlockGetter p_60839_, BlockPos p_60840_) {
            return this.cache != null ? this.cache.isCollisionShapeFullBlock : this.getBlock().isCollisionShapeFullBlock(this.asState(), p_60839_, p_60840_);
        }

        protected abstract BlockState asState();

        public boolean requiresCorrectToolForDrops() {
            return this.requiresCorrectToolForDrops;
        }

        public boolean shouldSpawnTerrainParticles() {
            return this.spawnTerrainParticles;
        }

        public NoteBlockInstrument instrument() {
            return this.instrument;
        }

        static final class Cache {
            private static final Direction[] DIRECTIONS = Direction.values();
            private static final int SUPPORT_TYPE_COUNT = SupportType.values().length;
            protected final boolean solidRender;
            final boolean propagatesSkylightDown;
            final int lightBlock;
            @Nullable
            final VoxelShape[] occlusionShapes;
            protected final VoxelShape collisionShape;
            protected final boolean largeCollisionShape;
            private final boolean[] faceSturdy;
            protected final boolean isCollisionShapeFullBlock;

            Cache(BlockState p_60853_) {
                Block block = p_60853_.getBlock();
                this.solidRender = p_60853_.isSolidRender(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
                this.propagatesSkylightDown = block.propagatesSkylightDown(p_60853_, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
                this.lightBlock = block.getLightBlock(p_60853_, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
                if (!p_60853_.canOcclude()) {
                    this.occlusionShapes = null;
                } else {
                    this.occlusionShapes = new VoxelShape[DIRECTIONS.length];
                    VoxelShape voxelshape = block.getOcclusionShape(p_60853_, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);

                    for(Direction direction : DIRECTIONS) {
                        this.occlusionShapes[direction.ordinal()] = Shapes.getFaceShape(voxelshape, direction);
                    }
                }

                this.collisionShape = block.getCollisionShape(p_60853_, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty());
                if (!this.collisionShape.isEmpty() && p_60853_.hasOffsetFunction()) {
                    throw new IllegalStateException(
                        String.format(
                            Locale.ROOT,
                            "%s has a collision shape and an offset type, but is not marked as dynamicShape in its properties.",
                            BuiltInRegistries.BLOCK.getKey(block)
                        )
                    );
                } else {
                    this.largeCollisionShape = Arrays.stream(Direction.Axis.values())
                        .anyMatch(p_60860_ -> this.collisionShape.min(p_60860_) < 0.0 || this.collisionShape.max(p_60860_) > 1.0);
                    this.faceSturdy = new boolean[DIRECTIONS.length * SUPPORT_TYPE_COUNT];

                    for(Direction direction1 : DIRECTIONS) {
                        for(SupportType supporttype : SupportType.values()) {
                            this.faceSturdy[getFaceSupportIndex(direction1, supporttype)] = supporttype.isSupporting(
                                p_60853_, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, direction1
                            );
                        }
                    }

                    this.isCollisionShapeFullBlock = Block.isShapeFullBlock(p_60853_.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
                }
            }

            public boolean isFaceSturdy(Direction p_60862_, SupportType p_60863_) {
                return this.faceSturdy[getFaceSupportIndex(p_60862_, p_60863_)];
            }

            private static int getFaceSupportIndex(Direction p_60867_, SupportType p_60868_) {
                return p_60867_.ordinal() * SUPPORT_TYPE_COUNT + p_60868_.ordinal();
            }
        }
    }

    public interface OffsetFunction {
        Vec3 evaluate(BlockState p_273639_, BlockGetter p_273732_, BlockPos p_273779_);
    }

    public static enum OffsetType {
        NONE,
        XZ,
        XYZ;
    }

    public static class Properties {
        public static final Codec<BlockBehaviour.Properties> CODEC = Codec.unit(() -> of());
        Function<BlockState, MapColor> mapColor = p_284884_ -> MapColor.NONE;
        boolean hasCollision = true;
        SoundType soundType = SoundType.STONE;
        ToIntFunction<BlockState> lightEmission = p_60929_ -> 0;
        float explosionResistance;
        float destroyTime;
        boolean requiresCorrectToolForDrops;
        boolean isRandomlyTicking;
        float friction = 0.6F;
        float speedFactor = 1.0F;
        float jumpFactor = 1.0F;
        ResourceLocation drops;
        boolean canOcclude = true;
        boolean isAir;
        boolean ignitedByLava;
        @Deprecated
        boolean liquid;
        @Deprecated
        boolean forceSolidOff;
        boolean forceSolidOn;
        PushReaction pushReaction = PushReaction.NORMAL;
        boolean spawnTerrainParticles = true;
        NoteBlockInstrument instrument = NoteBlockInstrument.HARP;
        private java.util.function.Supplier<ResourceLocation> lootTableSupplier;
        boolean replaceable;
        BlockBehaviour.StateArgumentPredicate<EntityType<?>> isValidSpawn = (p_284893_, p_284894_, p_284895_, p_284896_) -> p_284893_.isFaceSturdy(
                    p_284894_, p_284895_, Direction.UP
                )
                && p_284893_.getLightEmission(p_284894_, p_284895_) < 14;
        BlockBehaviour.StatePredicate isRedstoneConductor = (p_284888_, p_284889_, p_284890_) -> p_284888_.isCollisionShapeFullBlock(p_284889_, p_284890_);
        BlockBehaviour.StatePredicate isSuffocating = (p_284885_, p_284886_, p_284887_) -> p_284885_.blocksMotion()
                && p_284885_.isCollisionShapeFullBlock(p_284886_, p_284887_);
        BlockBehaviour.StatePredicate isViewBlocking = this.isSuffocating;
        BlockBehaviour.StatePredicate hasPostProcess = (p_60963_, p_60964_, p_60965_) -> false;
        BlockBehaviour.StatePredicate emissiveRendering = (p_60931_, p_60932_, p_60933_) -> false;
        boolean dynamicShape;
        FeatureFlagSet requiredFeatures = FeatureFlags.VANILLA_SET;
        Optional<BlockBehaviour.OffsetFunction> offsetFunction = Optional.empty();

        private Properties() {
        }

        public static BlockBehaviour.Properties of() {
            return new BlockBehaviour.Properties();
        }

        public static BlockBehaviour.Properties ofFullCopy(BlockBehaviour p_312473_) {
            BlockBehaviour.Properties blockbehaviour$properties = ofLegacyCopy(p_312473_);
            BlockBehaviour.Properties blockbehaviour$properties1 = p_312473_.properties;
            blockbehaviour$properties.jumpFactor = blockbehaviour$properties1.jumpFactor;
            blockbehaviour$properties.isRedstoneConductor = blockbehaviour$properties1.isRedstoneConductor;
            blockbehaviour$properties.isValidSpawn = blockbehaviour$properties1.isValidSpawn;
            blockbehaviour$properties.hasPostProcess = blockbehaviour$properties1.hasPostProcess;
            blockbehaviour$properties.isSuffocating = blockbehaviour$properties1.isSuffocating;
            blockbehaviour$properties.isViewBlocking = blockbehaviour$properties1.isViewBlocking;
            blockbehaviour$properties.drops = blockbehaviour$properties1.drops;
            return blockbehaviour$properties;
        }

        @Deprecated
        public static BlockBehaviour.Properties ofLegacyCopy(BlockBehaviour p_312385_) {
            BlockBehaviour.Properties blockbehaviour$properties = new BlockBehaviour.Properties();
            BlockBehaviour.Properties blockbehaviour$properties1 = p_312385_.properties;
            blockbehaviour$properties.destroyTime = blockbehaviour$properties1.destroyTime;
            blockbehaviour$properties.explosionResistance = blockbehaviour$properties1.explosionResistance;
            blockbehaviour$properties.hasCollision = blockbehaviour$properties1.hasCollision;
            blockbehaviour$properties.isRandomlyTicking = blockbehaviour$properties1.isRandomlyTicking;
            blockbehaviour$properties.lightEmission = blockbehaviour$properties1.lightEmission;
            blockbehaviour$properties.mapColor = blockbehaviour$properties1.mapColor;
            blockbehaviour$properties.soundType = blockbehaviour$properties1.soundType;
            blockbehaviour$properties.friction = blockbehaviour$properties1.friction;
            blockbehaviour$properties.speedFactor = blockbehaviour$properties1.speedFactor;
            blockbehaviour$properties.dynamicShape = blockbehaviour$properties1.dynamicShape;
            blockbehaviour$properties.canOcclude = blockbehaviour$properties1.canOcclude;
            blockbehaviour$properties.isAir = blockbehaviour$properties1.isAir;
            blockbehaviour$properties.ignitedByLava = blockbehaviour$properties1.ignitedByLava;
            blockbehaviour$properties.liquid = blockbehaviour$properties1.liquid;
            blockbehaviour$properties.forceSolidOff = blockbehaviour$properties1.forceSolidOff;
            blockbehaviour$properties.forceSolidOn = blockbehaviour$properties1.forceSolidOn;
            blockbehaviour$properties.pushReaction = blockbehaviour$properties1.pushReaction;
            blockbehaviour$properties.requiresCorrectToolForDrops = blockbehaviour$properties1.requiresCorrectToolForDrops;
            blockbehaviour$properties.offsetFunction = blockbehaviour$properties1.offsetFunction;
            blockbehaviour$properties.spawnTerrainParticles = blockbehaviour$properties1.spawnTerrainParticles;
            blockbehaviour$properties.requiredFeatures = blockbehaviour$properties1.requiredFeatures;
            blockbehaviour$properties.emissiveRendering = blockbehaviour$properties1.emissiveRendering;
            blockbehaviour$properties.instrument = blockbehaviour$properties1.instrument;
            blockbehaviour$properties.replaceable = blockbehaviour$properties1.replaceable;
            return blockbehaviour$properties;
        }

        public BlockBehaviour.Properties mapColor(DyeColor p_285331_) {
            this.mapColor = p_284892_ -> p_285331_.getMapColor();
            return this;
        }

        public BlockBehaviour.Properties mapColor(MapColor p_285137_) {
            this.mapColor = p_222988_ -> p_285137_;
            return this;
        }

        public BlockBehaviour.Properties mapColor(Function<BlockState, MapColor> p_285406_) {
            this.mapColor = p_285406_;
            return this;
        }

        public BlockBehaviour.Properties noCollission() {
            this.hasCollision = false;
            this.canOcclude = false;
            return this;
        }

        public BlockBehaviour.Properties noOcclusion() {
            this.canOcclude = false;
            return this;
        }

        public BlockBehaviour.Properties friction(float p_60912_) {
            this.friction = p_60912_;
            return this;
        }

        public BlockBehaviour.Properties speedFactor(float p_60957_) {
            this.speedFactor = p_60957_;
            return this;
        }

        public BlockBehaviour.Properties jumpFactor(float p_60968_) {
            this.jumpFactor = p_60968_;
            return this;
        }

        public BlockBehaviour.Properties sound(SoundType p_60919_) {
            this.soundType = p_60919_;
            return this;
        }

        public BlockBehaviour.Properties lightLevel(ToIntFunction<BlockState> p_60954_) {
            this.lightEmission = p_60954_;
            return this;
        }

        public BlockBehaviour.Properties strength(float p_60914_, float p_60915_) {
            return this.destroyTime(p_60914_).explosionResistance(p_60915_);
        }

        public BlockBehaviour.Properties instabreak() {
            return this.strength(0.0F);
        }

        public BlockBehaviour.Properties strength(float p_60979_) {
            this.strength(p_60979_, p_60979_);
            return this;
        }

        public BlockBehaviour.Properties randomTicks() {
            this.isRandomlyTicking = true;
            return this;
        }

        public BlockBehaviour.Properties dynamicShape() {
            this.dynamicShape = true;
            return this;
        }

        public BlockBehaviour.Properties noLootTable() {
            this.drops = BuiltInLootTables.EMPTY;
            return this;
        }

        @Deprecated // FORGE: Use the variant that takes a Supplier below
        public BlockBehaviour.Properties dropsLike(Block p_60917_) {
            this.lootTableSupplier = () -> p_60917_.getLootTable();
            return this;
        }

        public BlockBehaviour.Properties lootFrom(java.util.function.Supplier<? extends Block> blockIn) {
             this.lootTableSupplier = () -> blockIn.get().getLootTable();
             return this;
        }

        public BlockBehaviour.Properties ignitedByLava() {
            this.ignitedByLava = true;
            return this;
        }

        public BlockBehaviour.Properties liquid() {
            this.liquid = true;
            return this;
        }

        public BlockBehaviour.Properties forceSolidOn() {
            this.forceSolidOn = true;
            return this;
        }

        @Deprecated
        public BlockBehaviour.Properties forceSolidOff() {
            this.forceSolidOff = true;
            return this;
        }

        public BlockBehaviour.Properties pushReaction(PushReaction p_278265_) {
            this.pushReaction = p_278265_;
            return this;
        }

        public BlockBehaviour.Properties air() {
            this.isAir = true;
            return this;
        }

        public BlockBehaviour.Properties isValidSpawn(BlockBehaviour.StateArgumentPredicate<EntityType<?>> p_60923_) {
            this.isValidSpawn = p_60923_;
            return this;
        }

        public BlockBehaviour.Properties isRedstoneConductor(BlockBehaviour.StatePredicate p_60925_) {
            this.isRedstoneConductor = p_60925_;
            return this;
        }

        public BlockBehaviour.Properties isSuffocating(BlockBehaviour.StatePredicate p_60961_) {
            this.isSuffocating = p_60961_;
            return this;
        }

        public BlockBehaviour.Properties isViewBlocking(BlockBehaviour.StatePredicate p_60972_) {
            this.isViewBlocking = p_60972_;
            return this;
        }

        public BlockBehaviour.Properties hasPostProcess(BlockBehaviour.StatePredicate p_60983_) {
            this.hasPostProcess = p_60983_;
            return this;
        }

        public BlockBehaviour.Properties emissiveRendering(BlockBehaviour.StatePredicate p_60992_) {
            this.emissiveRendering = p_60992_;
            return this;
        }

        public BlockBehaviour.Properties requiresCorrectToolForDrops() {
            this.requiresCorrectToolForDrops = true;
            return this;
        }

        public BlockBehaviour.Properties destroyTime(float p_155955_) {
            this.destroyTime = p_155955_;
            return this;
        }

        public BlockBehaviour.Properties explosionResistance(float p_155957_) {
            this.explosionResistance = Math.max(0.0F, p_155957_);
            return this;
        }

        public BlockBehaviour.Properties offsetType(BlockBehaviour.OffsetType p_222980_) {
            switch(p_222980_) {
                case XYZ:
                    this.offsetFunction = Optional.of((p_272562_, p_272563_, p_272564_) -> {
                        Block block = p_272562_.getBlock();
                        long i = Mth.getSeed(p_272564_.getX(), 0, p_272564_.getZ());
                        double d0 = ((double)((float)(i >> 4 & 15L) / 15.0F) - 1.0) * (double)block.getMaxVerticalOffset();
                        float f = block.getMaxHorizontalOffset();
                        double d1 = Mth.clamp(((double)((float)(i & 15L) / 15.0F) - 0.5) * 0.5, (double)(-f), (double)f);
                        double d2 = Mth.clamp(((double)((float)(i >> 8 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-f), (double)f);
                        return new Vec3(d1, d0, d2);
                    });
                    break;
                case XZ:
                    this.offsetFunction = Optional.of((p_272565_, p_272566_, p_272567_) -> {
                        Block block = p_272565_.getBlock();
                        long i = Mth.getSeed(p_272567_.getX(), 0, p_272567_.getZ());
                        float f = block.getMaxHorizontalOffset();
                        double d0 = Mth.clamp(((double)((float)(i & 15L) / 15.0F) - 0.5) * 0.5, (double)(-f), (double)f);
                        double d1 = Mth.clamp(((double)((float)(i >> 8 & 15L) / 15.0F) - 0.5) * 0.5, (double)(-f), (double)f);
                        return new Vec3(d0, 0.0, d1);
                    });
                    break;
                default:
                    this.offsetFunction = Optional.empty();
            }

            return this;
        }

        public BlockBehaviour.Properties noTerrainParticles() {
            this.spawnTerrainParticles = false;
            return this;
        }

        public BlockBehaviour.Properties requiredFeatures(FeatureFlag... p_248792_) {
            this.requiredFeatures = FeatureFlags.REGISTRY.subset(p_248792_);
            return this;
        }

        public BlockBehaviour.Properties instrument(NoteBlockInstrument p_282170_) {
            this.instrument = p_282170_;
            return this;
        }

        public BlockBehaviour.Properties replaceable() {
            this.replaceable = true;
            return this;
        }
    }

    public interface StateArgumentPredicate<A> {
        boolean test(BlockState p_61031_, BlockGetter p_61032_, BlockPos p_61033_, A p_61034_);
    }

    public interface StatePredicate {
        boolean test(BlockState p_61036_, BlockGetter p_61037_, BlockPos p_61038_);
    }
}
