package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Connection;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.LastSeenMessagesValidator;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSignatureCache;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.chat.SignableCommand;
import net.minecraft.network.chat.SignedMessageBody;
import net.minecraft.network.chat.SignedMessageChain;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundStartConfigurationPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundChunkBatchReceivedPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundConfigurationAcknowledgedPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundContainerSlotStateChangedPacket;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FutureChain;
import net.minecraft.util.Mth;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

public class ServerGamePacketListenerImpl
    extends ServerCommonPacketListenerImpl
    implements ServerGamePacketListener,
    ServerPlayerConnection,
    TickablePacketListener {
    static final Logger LOGGER = LogUtils.getLogger();
    /**
     * Forge: Deprecated in favor of range/reach attributes.
     * @see net.neoforged.neoforge.common.NeoForgeMod#BLOCK_REACH
     * @see net.neoforged.neoforge.common.NeoForgeMod#ENTITY_REACH
     */
    @Deprecated
    public static final double MAX_INTERACTION_DISTANCE = Mth.square(6.0);
    private static final int NO_BLOCK_UPDATES_TO_ACK = -1;
    private static final int TRACKED_MESSAGE_DISCONNECT_THRESHOLD = 4096;
    private static final Component CHAT_VALIDATION_FAILED = Component.translatable("multiplayer.disconnect.chat_validation_failed");
    public ServerPlayer player;
    public final PlayerChunkSender chunkSender;
    private int tickCount;
    private int ackBlockChangesUpTo = -1;
    private int chatSpamTickCount;
    private int dropSpamTickCount;
    private double firstGoodX;
    private double firstGoodY;
    private double firstGoodZ;
    private double lastGoodX;
    private double lastGoodY;
    private double lastGoodZ;
    @Nullable
    private Entity lastVehicle;
    private double vehicleFirstGoodX;
    private double vehicleFirstGoodY;
    private double vehicleFirstGoodZ;
    private double vehicleLastGoodX;
    private double vehicleLastGoodY;
    private double vehicleLastGoodZ;
    @Nullable
    private Vec3 awaitingPositionFromClient;
    private int awaitingTeleport;
    private int awaitingTeleportTime;
    private boolean clientIsFloating;
    private int aboveGroundTickCount;
    private boolean clientVehicleIsFloating;
    private int aboveGroundVehicleTickCount;
    private int receivedMovePacketCount;
    private int knownMovePacketCount;
    @Nullable
    private RemoteChatSession chatSession;
    private SignedMessageChain.Decoder signedMessageDecoder;
    private final LastSeenMessagesValidator lastSeenMessages = new LastSeenMessagesValidator(20);
    private final MessageSignatureCache messageSignatureCache = MessageSignatureCache.createDefault();
    private final FutureChain chatMessageChain;
    private boolean waitingForSwitchToConfig;
    private net.neoforged.neoforge.network.connection.ConnectionType connectionType;

    public ServerGamePacketListenerImpl(MinecraftServer p_9770_, Connection p_9771_, ServerPlayer p_9772_, CommonListenerCookie p_301978_) {
        super(p_9770_, p_9771_, p_301978_);
        this.chunkSender = new PlayerChunkSender(p_9771_.isMemoryConnection());
        p_9771_.setListener(this);
        this.player = p_9772_;
        p_9772_.connection = this;
        p_9772_.getTextFilter().join();
        this.signedMessageDecoder = SignedMessageChain.Decoder.unsigned(p_9772_.getUUID(), p_9770_::enforceSecureProfile);
        this.chatMessageChain = new FutureChain(p_9770_);
        this.connectionType = p_301978_.connectionType();
    }

    @Override
    public void tick() {
        if (this.ackBlockChangesUpTo > -1) {
            this.send(new ClientboundBlockChangedAckPacket(this.ackBlockChangesUpTo));
            this.ackBlockChangesUpTo = -1;
        }

        this.resetPosition();
        this.player.xo = this.player.getX();
        this.player.yo = this.player.getY();
        this.player.zo = this.player.getZ();
        this.player.doTick();
        this.player.absMoveTo(this.firstGoodX, this.firstGoodY, this.firstGoodZ, this.player.getYRot(), this.player.getXRot());
        ++this.tickCount;
        this.knownMovePacketCount = this.receivedMovePacketCount;
        if (this.clientIsFloating && !this.player.isSleeping() && !this.player.isPassenger() && !this.player.isDeadOrDying()) {
            if (++this.aboveGroundTickCount > 80) {
                LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
                this.disconnect(Component.translatable("multiplayer.disconnect.flying"));
                return;
            }
        } else {
            this.clientIsFloating = false;
            this.aboveGroundTickCount = 0;
        }

        this.lastVehicle = this.player.getRootVehicle();
        if (this.lastVehicle != this.player && this.lastVehicle.getControllingPassenger() == this.player) {
            this.vehicleFirstGoodX = this.lastVehicle.getX();
            this.vehicleFirstGoodY = this.lastVehicle.getY();
            this.vehicleFirstGoodZ = this.lastVehicle.getZ();
            this.vehicleLastGoodX = this.lastVehicle.getX();
            this.vehicleLastGoodY = this.lastVehicle.getY();
            this.vehicleLastGoodZ = this.lastVehicle.getZ();
            if (this.clientVehicleIsFloating && this.player.getRootVehicle().getControllingPassenger() == this.player) {
                if (++this.aboveGroundVehicleTickCount > 80) {
                    LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
                    this.disconnect(Component.translatable("multiplayer.disconnect.flying"));
                    return;
                }
            } else {
                this.clientVehicleIsFloating = false;
                this.aboveGroundVehicleTickCount = 0;
            }
        } else {
            this.lastVehicle = null;
            this.clientVehicleIsFloating = false;
            this.aboveGroundVehicleTickCount = 0;
        }

        this.keepConnectionAlive();
        if (this.chatSpamTickCount > 0) {
            --this.chatSpamTickCount;
        }

        if (this.dropSpamTickCount > 0) {
            --this.dropSpamTickCount;
        }

        if (this.player.getLastActionTime() > 0L
            && this.server.getPlayerIdleTimeout() > 0
            && Util.getMillis() - this.player.getLastActionTime() > (long)this.server.getPlayerIdleTimeout() * 1000L * 60L) {
            this.disconnect(Component.translatable("multiplayer.disconnect.idling"));
        }
    }

    public void resetPosition() {
        this.firstGoodX = this.player.getX();
        this.firstGoodY = this.player.getY();
        this.firstGoodZ = this.player.getZ();
        this.lastGoodX = this.player.getX();
        this.lastGoodY = this.player.getY();
        this.lastGoodZ = this.player.getZ();
    }

    @Override
    public boolean isAcceptingMessages() {
        return this.connection.isConnected() && !this.waitingForSwitchToConfig;
    }

    @Override
    public boolean shouldHandleMessage(Packet<?> p_295183_) {
        if (super.shouldHandleMessage(p_295183_)) {
            return true;
        } else {
            return this.waitingForSwitchToConfig && this.connection.isConnected() && p_295183_ instanceof ServerboundConfigurationAcknowledgedPacket;
        }
    }

    @Override
    protected GameProfile playerProfile() {
        return this.player.getGameProfile();
    }

    private <T, R> CompletableFuture<R> filterTextPacket(T p_243240_, BiFunction<TextFilter, T, CompletableFuture<R>> p_243271_) {
        return p_243271_.apply(this.player.getTextFilter(), p_243240_).thenApply(p_264862_ -> {
            if (!this.isAcceptingMessages()) {
                LOGGER.debug("Ignoring packet due to disconnection");
                throw new CancellationException("disconnected");
            } else {
                return p_264862_;
            }
        });
    }

    private CompletableFuture<FilteredText> filterTextPacket(String p_243213_) {
        return this.filterTextPacket(p_243213_, TextFilter::processStreamMessage);
    }

    private CompletableFuture<List<FilteredText>> filterTextPacket(List<String> p_243258_) {
        return this.filterTextPacket(p_243258_, TextFilter::processMessageBundle);
    }

    @Override
    public void handlePlayerInput(ServerboundPlayerInputPacket p_9893_) {
        PacketUtils.ensureRunningOnSameThread(p_9893_, this, this.player.serverLevel());
        this.player.setPlayerInput(p_9893_.getXxa(), p_9893_.getZza(), p_9893_.isJumping(), p_9893_.isShiftKeyDown());
    }

    private static boolean containsInvalidValues(double p_143664_, double p_143665_, double p_143666_, float p_143667_, float p_143668_) {
        return Double.isNaN(p_143664_) || Double.isNaN(p_143665_) || Double.isNaN(p_143666_) || !Floats.isFinite(p_143668_) || !Floats.isFinite(p_143667_);
    }

    private static double clampHorizontal(double p_143610_) {
        return Mth.clamp(p_143610_, -3.0E7, 3.0E7);
    }

    private static double clampVertical(double p_143654_) {
        return Mth.clamp(p_143654_, -2.0E7, 2.0E7);
    }

    @Override
    public void handleMoveVehicle(ServerboundMoveVehiclePacket p_9876_) {
        PacketUtils.ensureRunningOnSameThread(p_9876_, this, this.player.serverLevel());
        if (containsInvalidValues(p_9876_.getX(), p_9876_.getY(), p_9876_.getZ(), p_9876_.getYRot(), p_9876_.getXRot())) {
            this.disconnect(Component.translatable("multiplayer.disconnect.invalid_vehicle_movement"));
        } else {
            Entity entity = this.player.getRootVehicle();
            if (entity != this.player && entity.getControllingPassenger() == this.player && entity == this.lastVehicle) {
                ServerLevel serverlevel = this.player.serverLevel();
                double d0 = entity.getX();
                double d1 = entity.getY();
                double d2 = entity.getZ();
                double d3 = clampHorizontal(p_9876_.getX());
                double d4 = clampVertical(p_9876_.getY());
                double d5 = clampHorizontal(p_9876_.getZ());
                float f = Mth.wrapDegrees(p_9876_.getYRot());
                float f1 = Mth.wrapDegrees(p_9876_.getXRot());
                double d6 = d3 - this.vehicleFirstGoodX;
                double d7 = d4 - this.vehicleFirstGoodY;
                double d8 = d5 - this.vehicleFirstGoodZ;
                double d9 = entity.getDeltaMovement().lengthSqr();
                double d10 = d6 * d6 + d7 * d7 + d8 * d8;
                if (d10 - d9 > 100.0 && !this.isSingleplayerOwner()) {
                    LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName().getString(), this.player.getName().getString(), d6, d7, d8);
                    this.send(new ClientboundMoveVehiclePacket(entity));
                    return;
                }

                boolean flag = serverlevel.noCollision(entity, entity.getBoundingBox().deflate(0.0625));
                d6 = d3 - this.vehicleLastGoodX;
                d7 = d4 - this.vehicleLastGoodY - 1.0E-6;
                d8 = d5 - this.vehicleLastGoodZ;
                boolean flag1 = entity.verticalCollisionBelow;
                if (entity instanceof LivingEntity livingentity && livingentity.onClimbable()) {
                    livingentity.resetFallDistance();
                }

                entity.move(MoverType.PLAYER, new Vec3(d6, d7, d8));
                d6 = d3 - entity.getX();
                d7 = d4 - entity.getY();
                if (d7 > -0.5 || d7 < 0.5) {
                    d7 = 0.0;
                }

                d8 = d5 - entity.getZ();
                d10 = d6 * d6 + d7 * d7 + d8 * d8;
                boolean flag2 = false;
                if (d10 > 0.0625) {
                    flag2 = true;
                    LOGGER.warn("{} (vehicle of {}) moved wrongly! {}", entity.getName().getString(), this.player.getName().getString(), Math.sqrt(d10));
                }

                entity.absMoveTo(d3, d4, d5, f, f1);
                resyncPlayerWithVehicle(entity); // Neo - Resync player position on vehicle moving
                boolean flag3 = serverlevel.noCollision(entity, entity.getBoundingBox().deflate(0.0625));
                if (flag && (flag2 || !flag3)) {
                    entity.absMoveTo(d0, d1, d2, f, f1);
                    resyncPlayerWithVehicle(entity); // Neo - Resync player position on vehicle moving
                    this.send(new ClientboundMoveVehiclePacket(entity));
                    return;
                }

                this.player.serverLevel().getChunkSource().move(this.player);
                this.player.checkMovementStatistics(this.player.getX() - d0, this.player.getY() - d1, this.player.getZ() - d2);
                this.clientVehicleIsFloating = d7 >= -0.03125
                    && !flag1
                    && !this.server.isFlightAllowed()
                    && !entity.isNoGravity()
                    && this.noBlocksAround(entity);
                this.vehicleLastGoodX = entity.getX();
                this.vehicleLastGoodY = entity.getY();
                this.vehicleLastGoodZ = entity.getZ();
            }
        }
    }

    private void resyncPlayerWithVehicle(Entity vehicle) {
        Vec3 oldPos = this.player.position();
        float yRot = this.player.getYRot();
        float xRot = this.player.getXRot();
        float yHeadRot = this.player.getYHeadRot();

        vehicle.positionRider(this.player);

        // preserve old rotation and store old position in xo/yo/zo
        this.player.setYRot(yRot);
        this.player.setXRot(xRot);
        this.player.setYHeadRot(yHeadRot);
        this.player.xo = oldPos.x;
        this.player.yo = oldPos.y;
        this.player.zo = oldPos.z;
    }

    private boolean noBlocksAround(Entity p_9794_) {
        return p_9794_.level()
            .getBlockStates(p_9794_.getBoundingBox().inflate(0.0625).expandTowards(0.0, -0.55, 0.0))
            .allMatch(BlockBehaviour.BlockStateBase::isAir);
    }

    @Override
    public void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket p_9835_) {
        PacketUtils.ensureRunningOnSameThread(p_9835_, this, this.player.serverLevel());
        if (p_9835_.getId() == this.awaitingTeleport) {
            if (this.awaitingPositionFromClient == null) {
                this.disconnect(Component.translatable("multiplayer.disconnect.invalid_player_movement"));
                return;
            }

            this.player
                .absMoveTo(
                    this.awaitingPositionFromClient.x,
                    this.awaitingPositionFromClient.y,
                    this.awaitingPositionFromClient.z,
                    this.player.getYRot(),
                    this.player.getXRot()
                );
            this.lastGoodX = this.awaitingPositionFromClient.x;
            this.lastGoodY = this.awaitingPositionFromClient.y;
            this.lastGoodZ = this.awaitingPositionFromClient.z;
            if (this.player.isChangingDimension()) {
                this.player.hasChangedDimension();
            }

            this.awaitingPositionFromClient = null;
        }
    }

    @Override
    public void handleRecipeBookSeenRecipePacket(ServerboundRecipeBookSeenRecipePacket p_9897_) {
        PacketUtils.ensureRunningOnSameThread(p_9897_, this, this.player.serverLevel());
        this.server.getRecipeManager().byKey(p_9897_.getRecipe()).ifPresent(this.player.getRecipeBook()::removeHighlight);
    }

    @Override
    public void handleRecipeBookChangeSettingsPacket(ServerboundRecipeBookChangeSettingsPacket p_9895_) {
        PacketUtils.ensureRunningOnSameThread(p_9895_, this, this.player.serverLevel());
        this.player.getRecipeBook().setBookSetting(p_9895_.getBookType(), p_9895_.isOpen(), p_9895_.isFiltering());
    }

    @Override
    public void handleSeenAdvancements(ServerboundSeenAdvancementsPacket p_9903_) {
        PacketUtils.ensureRunningOnSameThread(p_9903_, this, this.player.serverLevel());
        if (p_9903_.getAction() == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) {
            ResourceLocation resourcelocation = Objects.requireNonNull(p_9903_.getTab());
            AdvancementHolder advancementholder = this.server.getAdvancements().get(resourcelocation);
            if (advancementholder != null) {
                this.player.getAdvancements().setSelectedTab(advancementholder);
            }
        }
    }

    @Override
    public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket p_9847_) {
        PacketUtils.ensureRunningOnSameThread(p_9847_, this, this.player.serverLevel());
        StringReader stringreader = new StringReader(p_9847_.getCommand());
        if (stringreader.canRead() && stringreader.peek() == '/') {
            stringreader.skip();
        }

        ParseResults<CommandSourceStack> parseresults = this.server.getCommands().getDispatcher().parse(stringreader, this.player.createCommandSourceStack());
        this.server
            .getCommands()
            .getDispatcher()
            .getCompletionSuggestions(parseresults)
            .thenAccept(p_297953_ -> this.send(new ClientboundCommandSuggestionsPacket(p_9847_.getId(), p_297953_)));
    }

    @Override
    public void handleSetCommandBlock(ServerboundSetCommandBlockPacket p_9911_) {
        PacketUtils.ensureRunningOnSameThread(p_9911_, this, this.player.serverLevel());
        if (!this.server.isCommandBlockEnabled()) {
            this.player.sendSystemMessage(Component.translatable("advMode.notEnabled"));
        } else if (!this.player.canUseGameMasterBlocks()) {
            this.player.sendSystemMessage(Component.translatable("advMode.notAllowed"));
        } else {
            BaseCommandBlock basecommandblock = null;
            CommandBlockEntity commandblockentity = null;
            BlockPos blockpos = p_9911_.getPos();
            BlockEntity blockentity = this.player.level().getBlockEntity(blockpos);
            if (blockentity instanceof CommandBlockEntity) {
                commandblockentity = (CommandBlockEntity)blockentity;
                basecommandblock = commandblockentity.getCommandBlock();
            }

            String s = p_9911_.getCommand();
            boolean flag = p_9911_.isTrackOutput();
            if (basecommandblock != null) {
                CommandBlockEntity.Mode commandblockentity$mode = commandblockentity.getMode();
                BlockState blockstate = this.player.level().getBlockState(blockpos);
                Direction direction = blockstate.getValue(CommandBlock.FACING);

                BlockState blockstate1 = (switch(p_9911_.getMode()) {
                    case SEQUENCE -> Blocks.CHAIN_COMMAND_BLOCK.defaultBlockState();
                    case AUTO -> Blocks.REPEATING_COMMAND_BLOCK.defaultBlockState();
                    default -> Blocks.COMMAND_BLOCK.defaultBlockState();
                }).setValue(CommandBlock.FACING, direction).setValue(CommandBlock.CONDITIONAL, Boolean.valueOf(p_9911_.isConditional()));
                if (blockstate1 != blockstate) {
                    this.player.level().setBlock(blockpos, blockstate1, 2);
                    blockentity.setBlockState(blockstate1);
                    this.player.level().getChunkAt(blockpos).setBlockEntity(blockentity);
                }

                basecommandblock.setCommand(s);
                basecommandblock.setTrackOutput(flag);
                if (!flag) {
                    basecommandblock.setLastOutput(null);
                }

                commandblockentity.setAutomatic(p_9911_.isAutomatic());
                if (commandblockentity$mode != p_9911_.getMode()) {
                    commandblockentity.onModeSwitch();
                }

                basecommandblock.onUpdated();
                if (!StringUtil.isNullOrEmpty(s)) {
                    this.player.sendSystemMessage(Component.translatable("advMode.setCommand.success", s));
                }
            }
        }
    }

    @Override
    public void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket p_9913_) {
        PacketUtils.ensureRunningOnSameThread(p_9913_, this, this.player.serverLevel());
        if (!this.server.isCommandBlockEnabled()) {
            this.player.sendSystemMessage(Component.translatable("advMode.notEnabled"));
        } else if (!this.player.canUseGameMasterBlocks()) {
            this.player.sendSystemMessage(Component.translatable("advMode.notAllowed"));
        } else {
            BaseCommandBlock basecommandblock = p_9913_.getCommandBlock(this.player.level());
            if (basecommandblock != null) {
                basecommandblock.setCommand(p_9913_.getCommand());
                basecommandblock.setTrackOutput(p_9913_.isTrackOutput());
                if (!p_9913_.isTrackOutput()) {
                    basecommandblock.setLastOutput(null);
                }

                basecommandblock.onUpdated();
                this.player.sendSystemMessage(Component.translatable("advMode.setCommand.success", p_9913_.getCommand()));
            }
        }
    }

    @Override
    public void handlePickItem(ServerboundPickItemPacket p_9880_) {
        PacketUtils.ensureRunningOnSameThread(p_9880_, this, this.player.serverLevel());
        this.player.getInventory().pickSlot(p_9880_.getSlot());
        this.player
            .connection
            .send(
                new ClientboundContainerSetSlotPacket(
                    -2, 0, this.player.getInventory().selected, this.player.getInventory().getItem(this.player.getInventory().selected)
                )
            );
        this.player.connection.send(new ClientboundContainerSetSlotPacket(-2, 0, p_9880_.getSlot(), this.player.getInventory().getItem(p_9880_.getSlot())));
        this.player.connection.send(new ClientboundSetCarriedItemPacket(this.player.getInventory().selected));
    }

    @Override
    public void handleRenameItem(ServerboundRenameItemPacket p_9899_) {
        PacketUtils.ensureRunningOnSameThread(p_9899_, this, this.player.serverLevel());
        AbstractContainerMenu abstractcontainermenu = this.player.containerMenu;
        if (abstractcontainermenu instanceof AnvilMenu anvilmenu) {
            if (!anvilmenu.stillValid(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", this.player, anvilmenu);
                return;
            }

            anvilmenu.setItemName(p_9899_.getName());
        }
    }

    @Override
    public void handleSetBeaconPacket(ServerboundSetBeaconPacket p_9907_) {
        PacketUtils.ensureRunningOnSameThread(p_9907_, this, this.player.serverLevel());
        AbstractContainerMenu abstractcontainermenu = this.player.containerMenu;
        if (abstractcontainermenu instanceof BeaconMenu beaconmenu) {
            if (!this.player.containerMenu.stillValid(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.containerMenu);
                return;
            }

            beaconmenu.updateEffects(p_9907_.getPrimary(), p_9907_.getSecondary());
        }
    }

    @Override
    public void handleSetStructureBlock(ServerboundSetStructureBlockPacket p_9919_) {
        PacketUtils.ensureRunningOnSameThread(p_9919_, this, this.player.serverLevel());
        if (this.player.canUseGameMasterBlocks()) {
            BlockPos blockpos = p_9919_.getPos();
            BlockState blockstate = this.player.level().getBlockState(blockpos);
            BlockEntity blockentity = this.player.level().getBlockEntity(blockpos);
            if (blockentity instanceof StructureBlockEntity structureblockentity) {
                structureblockentity.setMode(p_9919_.getMode());
                structureblockentity.setStructureName(p_9919_.getName());
                structureblockentity.setStructurePos(p_9919_.getOffset());
                structureblockentity.setStructureSize(p_9919_.getSize());
                structureblockentity.setMirror(p_9919_.getMirror());
                structureblockentity.setRotation(p_9919_.getRotation());
                structureblockentity.setMetaData(p_9919_.getData());
                structureblockentity.setIgnoreEntities(p_9919_.isIgnoreEntities());
                structureblockentity.setShowAir(p_9919_.isShowAir());
                structureblockentity.setShowBoundingBox(p_9919_.isShowBoundingBox());
                structureblockentity.setIntegrity(p_9919_.getIntegrity());
                structureblockentity.setSeed(p_9919_.getSeed());
                if (structureblockentity.hasStructureName()) {
                    String s = structureblockentity.getStructureName();
                    if (p_9919_.getUpdateType() == StructureBlockEntity.UpdateType.SAVE_AREA) {
                        if (structureblockentity.saveStructure()) {
                            this.player.displayClientMessage(Component.translatable("structure_block.save_success", s), false);
                        } else {
                            this.player.displayClientMessage(Component.translatable("structure_block.save_failure", s), false);
                        }
                    } else if (p_9919_.getUpdateType() == StructureBlockEntity.UpdateType.LOAD_AREA) {
                        if (!structureblockentity.isStructureLoadable()) {
                            this.player.displayClientMessage(Component.translatable("structure_block.load_not_found", s), false);
                        } else if (structureblockentity.placeStructureIfSameSize(this.player.serverLevel())) {
                            this.player.displayClientMessage(Component.translatable("structure_block.load_success", s), false);
                        } else {
                            this.player.displayClientMessage(Component.translatable("structure_block.load_prepare", s), false);
                        }
                    } else if (p_9919_.getUpdateType() == StructureBlockEntity.UpdateType.SCAN_AREA) {
                        if (structureblockentity.detectSize()) {
                            this.player.displayClientMessage(Component.translatable("structure_block.size_success", s), false);
                        } else {
                            this.player.displayClientMessage(Component.translatable("structure_block.size_failure"), false);
                        }
                    }
                } else {
                    this.player.displayClientMessage(Component.translatable("structure_block.invalid_structure_name", p_9919_.getName()), false);
                }

                structureblockentity.setChanged();
                this.player.level().sendBlockUpdated(blockpos, blockstate, blockstate, 3);
            }
        }
    }

    @Override
    public void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket p_9917_) {
        PacketUtils.ensureRunningOnSameThread(p_9917_, this, this.player.serverLevel());
        if (this.player.canUseGameMasterBlocks()) {
            BlockPos blockpos = p_9917_.getPos();
            BlockState blockstate = this.player.level().getBlockState(blockpos);
            BlockEntity blockentity = this.player.level().getBlockEntity(blockpos);
            if (blockentity instanceof JigsawBlockEntity jigsawblockentity) {
                jigsawblockentity.setName(p_9917_.getName());
                jigsawblockentity.setTarget(p_9917_.getTarget());
                jigsawblockentity.setPool(ResourceKey.create(Registries.TEMPLATE_POOL, p_9917_.getPool()));
                jigsawblockentity.setFinalState(p_9917_.getFinalState());
                jigsawblockentity.setJoint(p_9917_.getJoint());
                jigsawblockentity.setPlacementPriority(p_9917_.getPlacementPriority());
                jigsawblockentity.setSelectionPriority(p_9917_.getSelectionPriority());
                jigsawblockentity.setChanged();
                this.player.level().sendBlockUpdated(blockpos, blockstate, blockstate, 3);
            }
        }
    }

    @Override
    public void handleJigsawGenerate(ServerboundJigsawGeneratePacket p_9868_) {
        PacketUtils.ensureRunningOnSameThread(p_9868_, this, this.player.serverLevel());
        if (this.player.canUseGameMasterBlocks()) {
            BlockPos blockpos = p_9868_.getPos();
            BlockEntity blockentity = this.player.level().getBlockEntity(blockpos);
            if (blockentity instanceof JigsawBlockEntity jigsawblockentity) {
                jigsawblockentity.generate(this.player.serverLevel(), p_9868_.levels(), p_9868_.keepJigsaws());
            }
        }
    }

    @Override
    public void handleSelectTrade(ServerboundSelectTradePacket p_9905_) {
        PacketUtils.ensureRunningOnSameThread(p_9905_, this, this.player.serverLevel());
        int i = p_9905_.getItem();
        AbstractContainerMenu abstractcontainermenu = this.player.containerMenu;
        if (abstractcontainermenu instanceof MerchantMenu merchantmenu) {
            if (!merchantmenu.stillValid(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", this.player, merchantmenu);
                return;
            }

            merchantmenu.setSelectionHint(i);
            merchantmenu.tryMoveItems(i);
        }
    }

    @Override
    public void handleEditBook(ServerboundEditBookPacket p_9862_) {
        int i = p_9862_.getSlot();
        if (Inventory.isHotbarSlot(i) || i == 40) {
            List<String> list = Lists.newArrayList();
            Optional<String> optional = p_9862_.getTitle();
            optional.ifPresent(list::add);
            p_9862_.getPages().stream().limit(100L).forEach(list::add);
            Consumer<List<FilteredText>> consumer = optional.isPresent()
                ? p_238198_ -> this.signBook(p_238198_.get(0), p_238198_.subList(1, p_238198_.size()), i)
                : p_143627_ -> this.updateBookContents(p_143627_, i);
            this.filterTextPacket(list).thenAcceptAsync(consumer, this.server);
        }
    }

    private void updateBookContents(List<FilteredText> p_9813_, int p_9814_) {
        ItemStack itemstack = this.player.getInventory().getItem(p_9814_);
        if (itemstack.is(Items.WRITABLE_BOOK)) {
            this.updateBookPages(p_9813_, UnaryOperator.identity(), itemstack);
        }
    }

    private void signBook(FilteredText p_215209_, List<FilteredText> p_215210_, int p_215211_) {
        ItemStack itemstack = this.player.getInventory().getItem(p_215211_);
        if (itemstack.is(Items.WRITABLE_BOOK)) {
            ItemStack itemstack1 = new ItemStack(Items.WRITTEN_BOOK);
            CompoundTag compoundtag = itemstack.getTag();
            if (compoundtag != null) {
                itemstack1.setTag(compoundtag.copy());
            }
            net.neoforged.neoforge.attachment.AttachmentUtils.copyStackAttachments(itemstack, itemstack1);

            itemstack1.addTagElement("author", StringTag.valueOf(this.player.getName().getString()));
            if (this.player.isTextFilteringEnabled()) {
                itemstack1.addTagElement("title", StringTag.valueOf(p_215209_.filteredOrEmpty()));
            } else {
                itemstack1.addTagElement("filtered_title", StringTag.valueOf(p_215209_.filteredOrEmpty()));
                itemstack1.addTagElement("title", StringTag.valueOf(p_215209_.raw()));
            }

            this.updateBookPages(p_215210_, p_238206_ -> Component.Serializer.toJson(Component.literal(p_238206_)), itemstack1);
            this.player.getInventory().setItem(p_215211_, itemstack1);
        }
    }

    private void updateBookPages(List<FilteredText> p_143635_, UnaryOperator<String> p_143636_, ItemStack p_143637_) {
        ListTag listtag = new ListTag();
        if (this.player.isTextFilteringEnabled()) {
            p_143635_.stream().map(p_238209_ -> StringTag.valueOf(p_143636_.apply(p_238209_.filteredOrEmpty()))).forEach(listtag::add);
        } else {
            CompoundTag compoundtag = new CompoundTag();
            int i = 0;

            for(int j = p_143635_.size(); i < j; ++i) {
                FilteredText filteredtext = p_143635_.get(i);
                String s = filteredtext.raw();
                listtag.add(StringTag.valueOf(p_143636_.apply(s)));
                if (filteredtext.isFiltered()) {
                    compoundtag.putString(String.valueOf(i), p_143636_.apply(filteredtext.filteredOrEmpty()));
                }
            }

            if (!compoundtag.isEmpty()) {
                p_143637_.addTagElement("filtered_pages", compoundtag);
            }
        }

        p_143637_.addTagElement("pages", listtag);
    }

    @Override
    public void handleEntityTagQuery(ServerboundEntityTagQuery p_9864_) {
        PacketUtils.ensureRunningOnSameThread(p_9864_, this, this.player.serverLevel());
        if (this.player.hasPermissions(2)) {
            Entity entity = this.player.level().getEntity(p_9864_.getEntityId());
            if (entity != null) {
                CompoundTag compoundtag = entity.saveWithoutId(new CompoundTag());
                this.player.connection.send(new ClientboundTagQueryPacket(p_9864_.getTransactionId(), compoundtag));
            }
        }
    }

    @Override
    public void handleContainerSlotStateChanged(ServerboundContainerSlotStateChangedPacket p_307480_) {
        PacketUtils.ensureRunningOnSameThread(p_307480_, this, this.player.serverLevel());
        if (!this.player.isSpectator() && p_307480_.containerId() == this.player.containerMenu.containerId) {
            AbstractContainerMenu abstractcontainermenu = this.player.containerMenu;
            if (abstractcontainermenu instanceof CrafterMenu craftermenu) {
                Container container = craftermenu.getContainer();
                if (container instanceof CrafterBlockEntity crafterblockentity) {
                    crafterblockentity.setSlotState(p_307480_.slotId(), p_307480_.newState());
                }
            }
        }
    }

    @Override
    public void handleBlockEntityTagQuery(ServerboundBlockEntityTagQuery p_9837_) {
        PacketUtils.ensureRunningOnSameThread(p_9837_, this, this.player.serverLevel());
        if (this.player.hasPermissions(2)) {
            BlockEntity blockentity = this.player.level().getBlockEntity(p_9837_.getPos());
            CompoundTag compoundtag = blockentity != null ? blockentity.saveWithoutMetadata() : null;
            this.player.connection.send(new ClientboundTagQueryPacket(p_9837_.getTransactionId(), compoundtag));
        }
    }

    @Override
    public void handleMovePlayer(ServerboundMovePlayerPacket p_9874_) {
        PacketUtils.ensureRunningOnSameThread(p_9874_, this, this.player.serverLevel());
        if (containsInvalidValues(p_9874_.getX(0.0), p_9874_.getY(0.0), p_9874_.getZ(0.0), p_9874_.getYRot(0.0F), p_9874_.getXRot(0.0F))) {
            this.disconnect(Component.translatable("multiplayer.disconnect.invalid_player_movement"));
        } else {
            ServerLevel serverlevel = this.player.serverLevel();
            if (!this.player.wonGame) {
                if (this.tickCount == 0) {
                    this.resetPosition();
                }

                if (this.awaitingPositionFromClient != null) {
                    if (this.tickCount - this.awaitingTeleportTime > 20) {
                        this.awaitingTeleportTime = this.tickCount;
                        this.teleport(
                            this.awaitingPositionFromClient.x,
                            this.awaitingPositionFromClient.y,
                            this.awaitingPositionFromClient.z,
                            this.player.getYRot(),
                            this.player.getXRot()
                        );
                    }
                } else {
                    this.awaitingTeleportTime = this.tickCount;
                    double d0 = clampHorizontal(p_9874_.getX(this.player.getX()));
                    double d1 = clampVertical(p_9874_.getY(this.player.getY()));
                    double d2 = clampHorizontal(p_9874_.getZ(this.player.getZ()));
                    float f = Mth.wrapDegrees(p_9874_.getYRot(this.player.getYRot()));
                    float f1 = Mth.wrapDegrees(p_9874_.getXRot(this.player.getXRot()));
                    if (this.player.isPassenger()) {
                        this.player.absMoveTo(this.player.getX(), this.player.getY(), this.player.getZ(), f, f1);
                        this.player.serverLevel().getChunkSource().move(this.player);
                    } else {
                        double d3 = this.player.getX();
                        double d4 = this.player.getY();
                        double d5 = this.player.getZ();
                        double d6 = d0 - this.firstGoodX;
                        double d7 = d1 - this.firstGoodY;
                        double d8 = d2 - this.firstGoodZ;
                        double d9 = this.player.getDeltaMovement().lengthSqr();
                        double d10 = d6 * d6 + d7 * d7 + d8 * d8;
                        if (this.player.isSleeping()) {
                            if (d10 > 1.0) {
                                this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), f, f1);
                            }
                        } else {
                            if (serverlevel.tickRateManager().runsNormally()) {
                                ++this.receivedMovePacketCount;
                                int i = this.receivedMovePacketCount - this.knownMovePacketCount;
                                if (i > 5) {
                                    LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), i);
                                    i = 1;
                                }

                                if (!this.player.isChangingDimension()
                                    && (
                                        !this.player.level().getGameRules().getBoolean(GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK)
                                            || !this.player.isFallFlying()
                                    )) {
                                    float f2 = this.player.isFallFlying() ? 300.0F : 100.0F;
                                    if (d10 - d9 > (double)(f2 * (float)i) && !this.isSingleplayerOwner()) {
                                        LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), d6, d7, d8);
                                        this.teleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYRot(), this.player.getXRot());
                                        return;
                                    }
                                }
                            }

                            AABB aabb = this.player.getBoundingBox();
                            d6 = d0 - this.lastGoodX;
                            d7 = d1 - this.lastGoodY;
                            d8 = d2 - this.lastGoodZ;
                            boolean flag2 = d7 > 0.0;
                            if (this.player.onGround() && !p_9874_.isOnGround() && flag2) {
                                this.player.jumpFromGround();
                            }

                            boolean flag = this.player.verticalCollisionBelow;
                            this.player.move(MoverType.PLAYER, new Vec3(d6, d7, d8));
                            d6 = d0 - this.player.getX();
                            d7 = d1 - this.player.getY();
                            if (d7 > -0.5 || d7 < 0.5) {
                                d7 = 0.0;
                            }

                            d8 = d2 - this.player.getZ();
                            d10 = d6 * d6 + d7 * d7 + d8 * d8;
                            boolean flag1 = false;
                            if (!this.player.isChangingDimension()
                                && d10 > 0.0625
                                && !this.player.isSleeping()
                                && !this.player.gameMode.isCreative()
                                && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
                                flag1 = true;
                                LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
                            }

                            if (this.player.noPhysics
                                || this.player.isSleeping()
                                || (!flag1 || !serverlevel.noCollision(this.player, aabb))
                                    && !this.isPlayerCollidingWithAnythingNew(serverlevel, aabb, d0, d1, d2)) {
                                this.player.absMoveTo(d0, d1, d2, f, f1);
                                this.clientIsFloating = d7 >= -0.03125
                                    && !flag
                                    && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR
                                    && !this.server.isFlightAllowed()
                                    && !this.player.mayFly()
                                    && !this.player.hasEffect(MobEffects.LEVITATION)
                                    && !this.player.isFallFlying()
                                    && !this.player.isAutoSpinAttack()
                                    && this.noBlocksAround(this.player);
                                this.player.serverLevel().getChunkSource().move(this.player);
                                this.player.doCheckFallDamage(this.player.getX() - d3, this.player.getY() - d4, this.player.getZ() - d5, p_9874_.isOnGround());
                                this.player
                                    .setOnGroundWithKnownMovement(
                                        p_9874_.isOnGround(), new Vec3(this.player.getX() - d3, this.player.getY() - d4, this.player.getZ() - d5)
                                    );
                                if (flag2) {
                                    this.player.resetFallDistance();
                                }

                                this.player.checkMovementStatistics(this.player.getX() - d3, this.player.getY() - d4, this.player.getZ() - d5);
                                this.lastGoodX = this.player.getX();
                                this.lastGoodY = this.player.getY();
                                this.lastGoodZ = this.player.getZ();
                            } else {
                                this.teleport(d3, d4, d5, f, f1);
                                this.player.doCheckFallDamage(this.player.getX() - d3, this.player.getY() - d4, this.player.getZ() - d5, p_9874_.isOnGround());
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isPlayerCollidingWithAnythingNew(LevelReader p_289008_, AABB p_288986_, double p_288990_, double p_288991_, double p_288967_) {
        AABB aabb = this.player.getBoundingBox().move(p_288990_ - this.player.getX(), p_288991_ - this.player.getY(), p_288967_ - this.player.getZ());
        Iterable<VoxelShape> iterable = p_289008_.getCollisions(this.player, aabb.deflate(1.0E-5F));
        VoxelShape voxelshape = Shapes.create(p_288986_.deflate(1.0E-5F));

        for(VoxelShape voxelshape1 : iterable) {
            if (!Shapes.joinIsNotEmpty(voxelshape1, voxelshape, BooleanOp.AND)) {
                return true;
            }
        }

        return false;
    }

    public void teleport(double p_9775_, double p_9776_, double p_9777_, float p_9778_, float p_9779_) {
        this.teleport(p_9775_, p_9776_, p_9777_, p_9778_, p_9779_, Collections.emptySet());
    }

    public void teleport(double p_9781_, double p_9782_, double p_9783_, float p_9784_, float p_9785_, Set<RelativeMovement> p_9786_) {
        double d0 = p_9786_.contains(RelativeMovement.X) ? this.player.getX() : 0.0;
        double d1 = p_9786_.contains(RelativeMovement.Y) ? this.player.getY() : 0.0;
        double d2 = p_9786_.contains(RelativeMovement.Z) ? this.player.getZ() : 0.0;
        float f = p_9786_.contains(RelativeMovement.Y_ROT) ? this.player.getYRot() : 0.0F;
        float f1 = p_9786_.contains(RelativeMovement.X_ROT) ? this.player.getXRot() : 0.0F;
        this.awaitingPositionFromClient = new Vec3(p_9781_, p_9782_, p_9783_);
        if (++this.awaitingTeleport == Integer.MAX_VALUE) {
            this.awaitingTeleport = 0;
        }

        this.awaitingTeleportTime = this.tickCount;
        this.player.absMoveTo(p_9781_, p_9782_, p_9783_, p_9784_, p_9785_);
        this.player
            .connection
            .send(new ClientboundPlayerPositionPacket(p_9781_ - d0, p_9782_ - d1, p_9783_ - d2, p_9784_ - f, p_9785_ - f1, p_9786_, this.awaitingTeleport));
    }

    @Override
    public void handlePlayerAction(ServerboundPlayerActionPacket p_9889_) {
        PacketUtils.ensureRunningOnSameThread(p_9889_, this, this.player.serverLevel());
        BlockPos blockpos = p_9889_.getPos();
        this.player.resetLastActionTime();
        ServerboundPlayerActionPacket.Action serverboundplayeractionpacket$action = p_9889_.getAction();
        switch(serverboundplayeractionpacket$action) {
            case SWAP_ITEM_WITH_OFFHAND:
                if (!this.player.isSpectator()) {
                    ItemStack itemstack = this.player.getItemInHand(InteractionHand.OFF_HAND);
                    var event = net.neoforged.neoforge.common.CommonHooks.onLivingSwapHandItems(this.player);
                    if (event.isCanceled()) return;
                    this.player.setItemInHand(InteractionHand.OFF_HAND, event.getItemSwappedToOffHand());
                    this.player.setItemInHand(InteractionHand.MAIN_HAND, event.getItemSwappedToMainHand());
                    this.player.stopUsingItem();
                }

                return;
            case DROP_ITEM:
                if (!this.player.isSpectator()) {
                    this.player.drop(false);
                }

                return;
            case DROP_ALL_ITEMS:
                if (!this.player.isSpectator()) {
                    this.player.drop(true);
                }

                return;
            case RELEASE_USE_ITEM:
                this.player.releaseUsingItem();
                return;
            case START_DESTROY_BLOCK:
            case ABORT_DESTROY_BLOCK:
            case STOP_DESTROY_BLOCK:
                this.player
                    .gameMode
                    .handleBlockBreakAction(
                        blockpos, serverboundplayeractionpacket$action, p_9889_.getDirection(), this.player.level().getMaxBuildHeight(), p_9889_.getSequence()
                    );
                this.player.connection.ackBlockChangesUpTo =p_9889_.getSequence();
                return;
            default:
                throw new IllegalArgumentException("Invalid player action");
        }
    }

    private static boolean wasBlockPlacementAttempt(ServerPlayer p_9791_, ItemStack p_9792_) {
        if (p_9792_.isEmpty()) {
            return false;
        } else {
            Item item = p_9792_.getItem();
            return (item instanceof BlockItem || item instanceof BucketItem) && !p_9791_.getCooldowns().isOnCooldown(item);
        }
    }

    @Override
    public void handleUseItemOn(ServerboundUseItemOnPacket p_9930_) {
        PacketUtils.ensureRunningOnSameThread(p_9930_, this, this.player.serverLevel());
        this.player.connection.ackBlockChangesUpTo = p_9930_.getSequence();
        ServerLevel serverlevel = this.player.serverLevel();
        InteractionHand interactionhand = p_9930_.getHand();
        ItemStack itemstack = this.player.getItemInHand(interactionhand);
        if (itemstack.isItemEnabled(serverlevel.enabledFeatures())) {
            BlockHitResult blockhitresult = p_9930_.getHitResult();
            Vec3 vec3 = blockhitresult.getLocation();
            BlockPos blockpos = blockhitresult.getBlockPos();
            Vec3 vec31 = Vec3.atCenterOf(blockpos);
            if (this.player.canReach(blockpos, 1.5)) { // Vanilla uses eye-to-center distance < 6, which implies a padding of 1.5
                Vec3 vec32 = vec3.subtract(vec31);
                double d0 = 1.0000001;
                if (Math.abs(vec32.x()) < 1.0000001 && Math.abs(vec32.y()) < 1.0000001 && Math.abs(vec32.z()) < 1.0000001) {
                    Direction direction = blockhitresult.getDirection();
                    this.player.resetLastActionTime();
                    int i = this.player.level().getMaxBuildHeight();
                    if (blockpos.getY() < i) {
                        if (this.awaitingPositionFromClient == null
                            && this.player.distanceToSqr((double)blockpos.getX() + 0.5, (double)blockpos.getY() + 0.5, (double)blockpos.getZ() + 0.5) < 64.0
                            && serverlevel.mayInteract(this.player, blockpos)) {
                            InteractionResult interactionresult = this.player
                                .gameMode
                                .useItemOn(this.player, serverlevel, itemstack, interactionhand, blockhitresult);
                            if (direction == Direction.UP
                                && !interactionresult.consumesAction()
                                && blockpos.getY() >= i - 1
                                && wasBlockPlacementAttempt(this.player, itemstack)) {
                                Component component = Component.translatable("build.tooHigh", i - 1).withStyle(ChatFormatting.RED);
                                this.player.sendSystemMessage(component, true);
                            } else if (interactionresult.shouldSwing()) {
                                this.player.swing(interactionhand, true);
                            }
                        }
                    } else {
                        Component component1 = Component.translatable("build.tooHigh", i - 1).withStyle(ChatFormatting.RED);
                        this.player.sendSystemMessage(component1, true);
                    }

                    this.player.connection.send(new ClientboundBlockUpdatePacket(serverlevel, blockpos));
                    this.player.connection.send(new ClientboundBlockUpdatePacket(serverlevel, blockpos.relative(direction)));
                } else {
                    LOGGER.warn(
                        "Rejecting UseItemOnPacket from {}: Location {} too far away from hit block {}.",
                        this.player.getGameProfile().getName(),
                        vec3,
                        blockpos
                    );
                }
            }
        }
    }

    @Override
    public void handleUseItem(ServerboundUseItemPacket p_9932_) {
        PacketUtils.ensureRunningOnSameThread(p_9932_, this, this.player.serverLevel());
        this.ackBlockChangesUpTo(p_9932_.getSequence());
        ServerLevel serverlevel = this.player.serverLevel();
        InteractionHand interactionhand = p_9932_.getHand();
        ItemStack itemstack = this.player.getItemInHand(interactionhand);
        this.player.resetLastActionTime();
        if (!itemstack.isEmpty() && itemstack.isItemEnabled(serverlevel.enabledFeatures())) {
            InteractionResult interactionresult = this.player.gameMode.useItem(this.player, serverlevel, itemstack, interactionhand);
            if (interactionresult.shouldSwing()) {
                this.player.swing(interactionhand, true);
            }
        }
    }

    @Override
    public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket p_9928_) {
        PacketUtils.ensureRunningOnSameThread(p_9928_, this, this.player.serverLevel());
        if (this.player.isSpectator()) {
            for(ServerLevel serverlevel : this.server.getAllLevels()) {
                Entity entity = p_9928_.getEntity(serverlevel);
                if (entity != null) {
                    this.player.teleportTo(serverlevel, entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                    return;
                }
            }
        }
    }

    @Override
    public void handlePaddleBoat(ServerboundPaddleBoatPacket p_9878_) {
        PacketUtils.ensureRunningOnSameThread(p_9878_, this, this.player.serverLevel());
        Entity entity = this.player.getControlledVehicle();
        if (entity instanceof Boat boat) {
            boat.setPaddleState(p_9878_.getLeft(), p_9878_.getRight());
        }
    }

    @Override
    public void onDisconnect(Component p_9825_) {
        LOGGER.info("{} lost connection: {}", this.player.getName().getString(), p_9825_.getString());
        this.removePlayerFromWorld();
        super.onDisconnect(p_9825_);
    }

    private void removePlayerFromWorld() {
        this.chatMessageChain.close();
        this.server.invalidateStatus();
        this.server
            .getPlayerList()
            .broadcastSystemMessage(Component.translatable("multiplayer.player.left", this.player.getDisplayName()).withStyle(ChatFormatting.YELLOW), false);
        this.player.disconnect();
        this.server.getPlayerList().remove(this.player);
        this.player.getTextFilter().leave();
    }

    public void ackBlockChangesUpTo(int p_215202_) {
        if (p_215202_ < 0) {
            throw new IllegalArgumentException("Expected packet sequence nr >= 0");
        } else {
            this.ackBlockChangesUpTo = Math.max(p_215202_, this.ackBlockChangesUpTo);
        }
    }

    @Override
    public void handleSetCarriedItem(ServerboundSetCarriedItemPacket p_9909_) {
        PacketUtils.ensureRunningOnSameThread(p_9909_, this, this.player.serverLevel());
        if (p_9909_.getSlot() >= 0 && p_9909_.getSlot() < Inventory.getSelectionSize()) {
            if (this.player.getInventory().selected != p_9909_.getSlot() && this.player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                this.player.stopUsingItem();
            }

            this.player.getInventory().selected = p_9909_.getSlot();
            this.player.resetLastActionTime();
        } else {
            LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
        }
    }

    @Override
    public void handleChat(ServerboundChatPacket p_9841_) {
        if (isChatMessageIllegal(p_9841_.message())) {
            this.disconnect(Component.translatable("multiplayer.disconnect.illegal_characters"));
        } else {
            Optional<LastSeenMessages> optional = this.tryHandleChat(p_9841_.lastSeenMessages());
            if (optional.isPresent()) {
                this.server.submit(() -> {
                    PlayerChatMessage playerchatmessage;
                    try {
                        playerchatmessage = this.getSignedMessage(p_9841_, optional.get());
                    } catch (SignedMessageChain.DecodeException signedmessagechain$decodeexception) {
                        this.handleMessageDecodeFailure(signedmessagechain$decodeexception);
                        return;
                    }

                    CompletableFuture<FilteredText> completablefuture = this.filterTextPacket(playerchatmessage.signedContent());
                    Component component = net.neoforged.neoforge.common.CommonHooks.getServerChatSubmittedDecorator().decorate(this.player, playerchatmessage.decoratedContent());
                    this.chatMessageChain.append(completablefuture, p_300785_ -> {
                        if (component == null) return; // Forge: ServerChatEvent was canceled if this is null.
                        PlayerChatMessage playerchatmessage1 = playerchatmessage.withUnsignedContent(component).filter(p_300785_.mask());
                        this.broadcastChatMessage(playerchatmessage1);
                    });
                });
            }
        }
    }

    @Override
    public void handleChatCommand(ServerboundChatCommandPacket p_215225_) {
        if (isChatMessageIllegal(p_215225_.command())) {
            this.disconnect(Component.translatable("multiplayer.disconnect.illegal_characters"));
        } else {
            Optional<LastSeenMessages> optional = this.tryHandleChat(p_215225_.lastSeenMessages());
            if (optional.isPresent()) {
                this.server.submit(() -> {
                    this.performChatCommand(p_215225_, optional.get());
                    this.detectRateSpam();
                });
            }
        }
    }

    private void performChatCommand(ServerboundChatCommandPacket p_251139_, LastSeenMessages p_250484_) {
        ParseResults<CommandSourceStack> parseresults = this.parseCommand(p_251139_.command());

        Map<String, PlayerChatMessage> map;
        try {
            map = this.collectSignedArguments(p_251139_, SignableCommand.of(parseresults), p_250484_);
        } catch (SignedMessageChain.DecodeException signedmessagechain$decodeexception) {
            this.handleMessageDecodeFailure(signedmessagechain$decodeexception);
            return;
        }

        CommandSigningContext commandsigningcontext = new CommandSigningContext.SignedArguments(map);
        parseresults = Commands.mapSource(parseresults, p_301740_ -> p_301740_.withSigningContext(commandsigningcontext, this.chatMessageChain));
        this.server.getCommands().performCommand(parseresults, p_251139_.command());
    }

    private void handleMessageDecodeFailure(SignedMessageChain.DecodeException p_252068_) {
        LOGGER.warn("Failed to update secure chat state for {}: '{}'", this.player.getGameProfile().getName(), p_252068_.getComponent().getString());
        if (p_252068_.shouldDisconnect()) {
            this.disconnect(p_252068_.getComponent());
        } else {
            this.player.sendSystemMessage(p_252068_.getComponent().copy().withStyle(ChatFormatting.RED));
        }
    }

    private Map<String, PlayerChatMessage> collectSignedArguments(
        ServerboundChatCommandPacket p_249441_, SignableCommand<?> p_250039_, LastSeenMessages p_249207_
    ) throws SignedMessageChain.DecodeException {
        Map<String, PlayerChatMessage> map = new Object2ObjectOpenHashMap<>();

        for(SignableCommand.Argument<?> argument : p_250039_.arguments()) {
            MessageSignature messagesignature = p_249441_.argumentSignatures().get(argument.name());
            SignedMessageBody signedmessagebody = new SignedMessageBody(argument.value(), p_249441_.timeStamp(), p_249441_.salt(), p_249207_);
            map.put(argument.name(), this.signedMessageDecoder.unpack(messagesignature, signedmessagebody));
        }

        return map;
    }

    private ParseResults<CommandSourceStack> parseCommand(String p_242938_) {
        CommandDispatcher<CommandSourceStack> commanddispatcher = this.server.getCommands().getDispatcher();
        return commanddispatcher.parse(p_242938_, this.player.createCommandSourceStack());
    }

    private Optional<LastSeenMessages> tryHandleChat(LastSeenMessages.Update p_249613_) {
        Optional<LastSeenMessages> optional = this.unpackAndApplyLastSeen(p_249613_);
        if (this.player.getChatVisibility() == ChatVisiblity.HIDDEN) {
            this.send(new ClientboundSystemChatPacket(Component.translatable("chat.disabled.options").withStyle(ChatFormatting.RED), false));
            return Optional.empty();
        } else {
            this.player.resetLastActionTime();
            return optional;
        }
    }

    private Optional<LastSeenMessages> unpackAndApplyLastSeen(LastSeenMessages.Update p_249673_) {
        synchronized(this.lastSeenMessages) {
            Optional<LastSeenMessages> optional = this.lastSeenMessages.applyUpdate(p_249673_);
            if (optional.isEmpty()) {
                LOGGER.warn("Failed to validate message acknowledgements from {}", this.player.getName().getString());
                this.disconnect(CHAT_VALIDATION_FAILED);
            }

            return optional;
        }
    }

    private static boolean isChatMessageIllegal(String p_215215_) {
        for(int i = 0; i < p_215215_.length(); ++i) {
            if (!SharedConstants.isAllowedChatCharacter(p_215215_.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    private PlayerChatMessage getSignedMessage(ServerboundChatPacket p_251061_, LastSeenMessages p_250566_) throws SignedMessageChain.DecodeException {
        SignedMessageBody signedmessagebody = new SignedMessageBody(p_251061_.message(), p_251061_.timeStamp(), p_251061_.salt(), p_250566_);
        return this.signedMessageDecoder.unpack(p_251061_.signature(), signedmessagebody);
    }

    private void broadcastChatMessage(PlayerChatMessage p_243277_) {
        this.server.getPlayerList().broadcastChatMessage(p_243277_, this.player, ChatType.bind(ChatType.CHAT, this.player));
        this.detectRateSpam();
    }

    private void detectRateSpam() {
        this.chatSpamTickCount += 20;
        if (this.chatSpamTickCount > 200 && !this.server.getPlayerList().isOp(this.player.getGameProfile())) {
            this.disconnect(Component.translatable("disconnect.spam"));
        }
    }

    @Override
    public void handleChatAck(ServerboundChatAckPacket p_242387_) {
        synchronized(this.lastSeenMessages) {
            if (!this.lastSeenMessages.applyOffset(p_242387_.offset())) {
                LOGGER.warn("Failed to validate message acknowledgements from {}", this.player.getName().getString());
                this.disconnect(CHAT_VALIDATION_FAILED);
            }
        }
    }

    @Override
    public void handleAnimate(ServerboundSwingPacket p_9926_) {
        PacketUtils.ensureRunningOnSameThread(p_9926_, this, this.player.serverLevel());
        this.player.resetLastActionTime();
        this.player.swing(p_9926_.getHand());
    }

    @Override
    public void handlePlayerCommand(ServerboundPlayerCommandPacket p_9891_) {
        PacketUtils.ensureRunningOnSameThread(p_9891_, this, this.player.serverLevel());
        this.player.resetLastActionTime();
        switch(p_9891_.getAction()) {
            case PRESS_SHIFT_KEY:
                this.player.setShiftKeyDown(true);
                break;
            case RELEASE_SHIFT_KEY:
                this.player.setShiftKeyDown(false);
                break;
            case START_SPRINTING:
                this.player.setSprinting(true);
                break;
            case STOP_SPRINTING:
                this.player.setSprinting(false);
                break;
            case STOP_SLEEPING:
                if (this.player.isSleeping()) {
                    this.player.stopSleepInBed(false, true);
                    this.awaitingPositionFromClient = this.player.position();
                }
                break;
            case START_RIDING_JUMP:
                Entity entity2 = this.player.getControlledVehicle();
                if (entity2 instanceof PlayerRideableJumping playerrideablejumping1) {
                    int i = p_9891_.getData();
                    if (playerrideablejumping1.canJump() && i > 0) {
                        playerrideablejumping1.handleStartJump(i);
                    }
                }
                break;
            case STOP_RIDING_JUMP:
                Entity entity1 = this.player.getControlledVehicle();
                if (entity1 instanceof PlayerRideableJumping playerrideablejumping) {
                    playerrideablejumping.handleStopJump();
                }
                break;
            case OPEN_INVENTORY:
                Entity $$2 = this.player.getVehicle();
                if ($$2 instanceof HasCustomInventoryScreen hascustominventoryscreen) {
                    hascustominventoryscreen.openCustomInventoryScreen(this.player);
                }
                break;
            case START_FALL_FLYING:
                if (!this.player.tryToStartFallFlying()) {
                    this.player.stopFallFlying();
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid client command!");
        }
    }

    public void addPendingMessage(PlayerChatMessage p_242439_) {
        MessageSignature messagesignature = p_242439_.signature();
        if (messagesignature != null) {
            this.messageSignatureCache.push(p_242439_.signedBody(), p_242439_.signature());
            int i;
            synchronized(this.lastSeenMessages) {
                this.lastSeenMessages.addPending(messagesignature);
                i = this.lastSeenMessages.trackedMessagesCount();
            }

            if (i > 4096) {
                this.disconnect(Component.translatable("multiplayer.disconnect.too_many_pending_chats"));
            }
        }
    }

    public void sendPlayerChatMessage(PlayerChatMessage p_250321_, ChatType.Bound p_250910_) {
        this.send(
            new ClientboundPlayerChatPacket(
                p_250321_.link().sender(),
                p_250321_.link().index(),
                p_250321_.signature(),
                p_250321_.signedBody().pack(this.messageSignatureCache),
                p_250321_.unsignedContent(),
                p_250321_.filterMask(),
                p_250910_.toNetwork(this.player.level().registryAccess())
            )
        );
        this.addPendingMessage(p_250321_);
    }

    public void sendDisguisedChatMessage(Component p_251804_, ChatType.Bound p_250040_) {
        this.send(new ClientboundDisguisedChatPacket(p_251804_, p_250040_.toNetwork(this.player.level().registryAccess())));
    }

    public SocketAddress getRemoteAddress() {
        return this.connection.getRemoteAddress();
    }

    public void switchToConfig() {
        this.waitingForSwitchToConfig = true;
        this.removePlayerFromWorld();
        this.send(new ClientboundStartConfigurationPacket());
    }

    @Override
    public void handlePingRequest(ServerboundPingRequestPacket p_298422_) {
        this.connection.send(new ClientboundPongResponsePacket(p_298422_.getTime()));
    }

    @Override
    public void handleInteract(ServerboundInteractPacket p_9866_) {
        PacketUtils.ensureRunningOnSameThread(p_9866_, this, this.player.serverLevel());
        final ServerLevel serverlevel = this.player.serverLevel();
        final Entity entity = p_9866_.getTarget(serverlevel);
        this.player.resetLastActionTime();
        this.player.setShiftKeyDown(p_9866_.isUsingSecondaryAction());
        if (entity != null) {
            if (!serverlevel.getWorldBorder().isWithinBounds(entity.blockPosition())) {
                return;
            }

            AABB aabb = entity.getBoundingBox();
            if (this.player.canReach(entity, 3)) { // Vanilla padding is 3.0 (distSq < 6.0 * 6.0)
                p_9866_.dispatch(
                    new ServerboundInteractPacket.Handler() {
                        private void performInteraction(InteractionHand p_143679_, ServerGamePacketListenerImpl.EntityInteraction p_143680_) {
                            ItemStack itemstack = ServerGamePacketListenerImpl.this.player.getItemInHand(p_143679_);
                            if (itemstack.isItemEnabled(serverlevel.enabledFeatures())) {
                                ItemStack itemstack1 = itemstack.copy();
                                InteractionResult interactionresult = p_143680_.run(ServerGamePacketListenerImpl.this.player, entity, p_143679_);
                                if (interactionresult.consumesAction()) {
                                    CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.trigger(ServerGamePacketListenerImpl.this.player, itemstack1, entity);
                                    if (interactionresult.shouldSwing()) {
                                        ServerGamePacketListenerImpl.this.player.swing(p_143679_, true);
                                    }
                                }
                            }
                        }
    
                        @Override
                        public void onInteraction(InteractionHand p_143677_) {
                            this.performInteraction(p_143677_, Player::interactOn);
                        }
    
                        @Override
                        public void onInteraction(InteractionHand p_143682_, Vec3 p_143683_) {
                            this.performInteraction(p_143682_, (p_143686_, p_143687_, p_143688_) -> {
                                InteractionResult onInteractEntityAtResult = net.neoforged.neoforge.common.CommonHooks.onInteractEntityAt(player, entity, p_143683_, p_143682_);
                                if (onInteractEntityAtResult != null) return onInteractEntityAtResult;
                                return p_143687_.interactAt(p_143686_, p_143683_, p_143688_);
                            });
                        }
    
                        @Override
                        public void onAttack() {
                            if (!(entity instanceof ItemEntity)
                                && !(entity instanceof ExperienceOrb)
                                && !(entity instanceof AbstractArrow)
                                && entity != ServerGamePacketListenerImpl.this.player) {
                                ItemStack itemstack = ServerGamePacketListenerImpl.this.player.getItemInHand(InteractionHand.MAIN_HAND);
                                if (itemstack.isItemEnabled(serverlevel.enabledFeatures())) {
                                    ServerGamePacketListenerImpl.this.player.attack(entity);
                                }
                            } else {
                                ServerGamePacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.invalid_entity_attacked"));
                                ServerGamePacketListenerImpl.LOGGER
                                    .warn("Player {} tried to attack an invalid entity", ServerGamePacketListenerImpl.this.player.getName().getString());
                            }
                        }
                    }
                );
            }
        }
    }

    @Override
    public void handleClientCommand(ServerboundClientCommandPacket p_9843_) {
        PacketUtils.ensureRunningOnSameThread(p_9843_, this, this.player.serverLevel());
        this.player.resetLastActionTime();
        ServerboundClientCommandPacket.Action serverboundclientcommandpacket$action = p_9843_.getAction();
        switch(serverboundclientcommandpacket$action) {
            case PERFORM_RESPAWN:
                if (this.player.wonGame) {
                    this.player.wonGame = false;
                    this.player = this.server.getPlayerList().respawn(this.player, true);
                    CriteriaTriggers.CHANGED_DIMENSION.trigger(this.player, Level.END, Level.OVERWORLD);
                } else {
                    if (this.player.getHealth() > 0.0F) {
                        return;
                    }

                    this.player = this.server.getPlayerList().respawn(this.player, false);
                    if (this.server.isHardcore()) {
                        this.player.setGameMode(GameType.SPECTATOR);
                        this.player.level().getGameRules().getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS).set(false, this.server);
                    }
                }
                break;
            case REQUEST_STATS:
                this.player.getStats().sendStats(this.player);
        }
    }

    @Override
    public void handleContainerClose(ServerboundContainerClosePacket p_9858_) {
        PacketUtils.ensureRunningOnSameThread(p_9858_, this, this.player.serverLevel());
        this.player.doCloseContainer();
    }

    @Override
    public void handleContainerClick(ServerboundContainerClickPacket p_9856_) {
        PacketUtils.ensureRunningOnSameThread(p_9856_, this, this.player.serverLevel());
        this.player.resetLastActionTime();
        if (this.player.containerMenu.containerId == p_9856_.getContainerId()) {
            if (this.player.isSpectator()) {
                this.player.containerMenu.sendAllDataToRemote();
            } else if (!this.player.containerMenu.stillValid(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.containerMenu);
            } else {
                int i = p_9856_.getSlotNum();
                if (!this.player.containerMenu.isValidSlotIndex(i)) {
                    LOGGER.debug(
                        "Player {} clicked invalid slot index: {}, available slots: {}", this.player.getName(), i, this.player.containerMenu.slots.size()
                    );
                } else {
                    boolean flag = p_9856_.getStateId() != this.player.containerMenu.getStateId();
                    this.player.containerMenu.suppressRemoteUpdates();
                    this.player.containerMenu.clicked(i, p_9856_.getButtonNum(), p_9856_.getClickType(), this.player);

                    for(Entry<ItemStack> entry : Int2ObjectMaps.fastIterable(p_9856_.getChangedSlots())) {
                        this.player.containerMenu.setRemoteSlotNoCopy(entry.getIntKey(), entry.getValue());
                    }

                    this.player.containerMenu.setRemoteCarried(p_9856_.getCarriedItem());
                    this.player.containerMenu.resumeRemoteUpdates();
                    if (flag) {
                        this.player.containerMenu.broadcastFullState();
                    } else {
                        this.player.containerMenu.broadcastChanges();
                    }
                }
            }
        }
    }

    @Override
    public void handlePlaceRecipe(ServerboundPlaceRecipePacket p_9882_) {
        PacketUtils.ensureRunningOnSameThread(p_9882_, this, this.player.serverLevel());
        this.player.resetLastActionTime();
        if (!this.player.isSpectator()
            && this.player.containerMenu.containerId == p_9882_.getContainerId()
            && this.player.containerMenu instanceof RecipeBookMenu) {
            if (!this.player.containerMenu.stillValid(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.containerMenu);
            } else {
                this.server
                    .getRecipeManager()
                    .byKey(p_9882_.getRecipe())
                    .ifPresent(p_300787_ -> ((RecipeBookMenu)this.player.containerMenu).handlePlacement(p_9882_.isShiftDown(), p_300787_, this.player));
            }
        }
    }

    @Override
    public void handleContainerButtonClick(ServerboundContainerButtonClickPacket p_9854_) {
        PacketUtils.ensureRunningOnSameThread(p_9854_, this, this.player.serverLevel());
        this.player.resetLastActionTime();
        if (this.player.containerMenu.containerId == p_9854_.getContainerId() && !this.player.isSpectator()) {
            if (!this.player.containerMenu.stillValid(this.player)) {
                LOGGER.debug("Player {} interacted with invalid menu {}", this.player, this.player.containerMenu);
            } else {
                boolean flag = this.player.containerMenu.clickMenuButton(this.player, p_9854_.getButtonId());
                if (flag) {
                    this.player.containerMenu.broadcastChanges();
                }
            }
        }
    }

    @Override
    public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket p_9915_) {
        PacketUtils.ensureRunningOnSameThread(p_9915_, this, this.player.serverLevel());
        if (this.player.gameMode.isCreative()) {
            boolean flag = p_9915_.getSlotNum() < 0;
            ItemStack itemstack = p_9915_.getItem();
            if (!itemstack.isItemEnabled(this.player.level().enabledFeatures())) {
                return;
            }

            CompoundTag compoundtag = BlockItem.getBlockEntityData(itemstack);
            if (!itemstack.isEmpty() && compoundtag != null && compoundtag.contains("x") && compoundtag.contains("y") && compoundtag.contains("z")) {
                BlockPos blockpos = BlockEntity.getPosFromTag(compoundtag);
                if (this.player.level().isLoaded(blockpos)) {
                    BlockEntity blockentity = this.player.level().getBlockEntity(blockpos);
                    if (blockentity != null) {
                        blockentity.saveToItem(itemstack);
                    }
                }
            }

            boolean flag1 = p_9915_.getSlotNum() >= 1 && p_9915_.getSlotNum() <= 45;
            boolean flag2 = itemstack.isEmpty() || itemstack.getDamageValue() >= 0 && itemstack.getCount() <= 64 && !itemstack.isEmpty();
            if (flag1 && flag2) {
                this.player.inventoryMenu.getSlot(p_9915_.getSlotNum()).setByPlayer(itemstack);
                this.player.inventoryMenu.broadcastChanges();
            } else if (flag && flag2 && this.dropSpamTickCount < 200) {
                this.dropSpamTickCount += 20;
                this.player.drop(itemstack, true);
            }
        }
    }

    @Override
    public void handleSignUpdate(ServerboundSignUpdatePacket p_9921_) {
        List<String> list = Stream.of(p_9921_.getLines()).map(ChatFormatting::stripFormatting).collect(Collectors.toList());
        this.filterTextPacket(list).thenAcceptAsync(p_215245_ -> this.updateSignText(p_9921_, p_215245_), this.server);
    }

    private void updateSignText(ServerboundSignUpdatePacket p_9923_, List<FilteredText> p_9924_) {
        this.player.resetLastActionTime();
        ServerLevel serverlevel = this.player.serverLevel();
        BlockPos blockpos = p_9923_.getPos();
        if (serverlevel.hasChunkAt(blockpos)) {
            BlockEntity blockentity = serverlevel.getBlockEntity(blockpos);
            if (!(blockentity instanceof SignBlockEntity)) {
                return;
            }

            SignBlockEntity signblockentity = (SignBlockEntity)blockentity;
            signblockentity.updateSignText(this.player, p_9923_.isFrontText(), p_9924_);
        }
    }

    @Override
    public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket p_9887_) {
        PacketUtils.ensureRunningOnSameThread(p_9887_, this, this.player.serverLevel());
        this.player.getAbilities().flying = p_9887_.isFlying() && this.player.mayFly();
    }

    @Override
    public void handleClientInformation(ServerboundClientInformationPacket p_301979_) {
        PacketUtils.ensureRunningOnSameThread(p_301979_, this, this.player.serverLevel());
        this.player.updateOptions(p_301979_.information());
    }

    @Override
    public void handleChangeDifficulty(ServerboundChangeDifficultyPacket p_9839_) {
        PacketUtils.ensureRunningOnSameThread(p_9839_, this, this.player.serverLevel());
        if (this.player.hasPermissions(2) || this.isSingleplayerOwner()) {
            this.server.setDifficulty(p_9839_.getDifficulty(), false);
        }
    }

    @Override
    public void handleLockDifficulty(ServerboundLockDifficultyPacket p_9872_) {
        PacketUtils.ensureRunningOnSameThread(p_9872_, this, this.player.serverLevel());
        if (this.player.hasPermissions(2) || this.isSingleplayerOwner()) {
            this.server.setDifficultyLocked(p_9872_.isLocked());
        }
    }

    @Override
    public void handleChatSessionUpdate(ServerboundChatSessionUpdatePacket p_253950_) {
        PacketUtils.ensureRunningOnSameThread(p_253950_, this, this.player.serverLevel());
        RemoteChatSession.Data remotechatsession$data = p_253950_.chatSession();
        ProfilePublicKey.Data profilepublickey$data = this.chatSession != null ? this.chatSession.profilePublicKey().data() : null;
        ProfilePublicKey.Data profilepublickey$data1 = remotechatsession$data.profilePublicKey();
        if (!Objects.equals(profilepublickey$data, profilepublickey$data1)) {
            if (profilepublickey$data != null && profilepublickey$data1.expiresAt().isBefore(profilepublickey$data.expiresAt())) {
                this.disconnect(ProfilePublicKey.EXPIRED_PROFILE_PUBLIC_KEY);
            } else {
                try {
                    SignatureValidator signaturevalidator = this.server.getProfileKeySignatureValidator();
                    if (signaturevalidator == null) {
                        LOGGER.warn("Ignoring chat session from {} due to missing Services public key", this.player.getGameProfile().getName());
                        return;
                    }

                    this.resetPlayerChatState(remotechatsession$data.validate(this.player.getGameProfile(), signaturevalidator));
                } catch (ProfilePublicKey.ValidationException profilepublickey$validationexception) {
                    LOGGER.error("Failed to validate profile key: {}", profilepublickey$validationexception.getMessage());
                    this.disconnect(profilepublickey$validationexception.getComponent());
                }
            }
        }
    }

    @Override
    public void handleConfigurationAcknowledged(ServerboundConfigurationAcknowledgedPacket p_294416_) {
        if (!this.waitingForSwitchToConfig) {
            throw new IllegalStateException("Client acknowledged config, but none was requested");
        } else {
            this.connection
                .setListener(new ServerConfigurationPacketListenerImpl(this.server, this.connection, this.createCookie(this.player.clientInformation(), this.connectionType)));
        }
    }

    @Override
    public void handleChunkBatchReceived(ServerboundChunkBatchReceivedPacket p_295247_) {
        PacketUtils.ensureRunningOnSameThread(p_295247_, this, this.player.serverLevel());
        this.chunkSender.onChunkBatchReceivedByClient(p_295247_.desiredChunksPerTick());
    }

    private void resetPlayerChatState(RemoteChatSession p_253823_) {
        this.chatSession = p_253823_;
        this.signedMessageDecoder = p_253823_.createMessageDecoder(this.player.getUUID());
        this.chatMessageChain
            .append(
                () -> {
                    this.player.setChatSession(p_253823_);
                    this.server
                        .getPlayerList()
                        .broadcastAll(
                            new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT), List.of(this.player))
                        );
                }
            );
    }

    @Override
    public ServerPlayer getPlayer() {
        return this.player;
    }

    @FunctionalInterface
    interface EntityInteraction {
        InteractionResult run(ServerPlayer p_143695_, Entity p_143696_, InteractionHand p_143697_);
    }

    @Override
    public void handleCustomPayload(net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket p_294276_) {
        if (p_294276_.payload() instanceof net.neoforged.neoforge.network.payload.MinecraftRegisterPayload minecraftRegisterPayload) {
            this.connectionType = this.connectionType.withMinecraftRegisterPayload();
            net.neoforged.neoforge.network.registration.NetworkRegistry.getInstance().onMinecraftRegister(this, minecraftRegisterPayload.newChannels());
            return;
        }

        if (p_294276_.payload() instanceof net.neoforged.neoforge.network.payload.MinecraftUnregisterPayload minecraftUnregisterPayload) {
            this.connectionType = this.connectionType.withMinecraftRegisterPayload();
            net.neoforged.neoforge.network.registration.NetworkRegistry.getInstance().onMinecraftUnregister(this, minecraftUnregisterPayload.forgottenChannels());
            return;
        }

        if (this.connectionType.isVanilla()) {
            super.handleCustomPayload(p_294276_);
            return;
        }

        net.neoforged.neoforge.network.registration.NetworkRegistry.getInstance().onModdedPacketAtServer(
                this, p_294276_
        );
    }

    public net.neoforged.neoforge.network.connection.ConnectionType getConnectionType() {
        return connectionType;
    }
}
