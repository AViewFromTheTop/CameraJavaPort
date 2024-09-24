package net.lunade.camera.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.CameraEntityTypes;
import net.lunade.camera.image_transfer.ServerTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Photograph extends HangingEntity {
    private static final EntityDataAccessor<String> DATA_PHOTOGRAPH = SynchedEntityData.defineId(Photograph.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DATA_SIZE = SynchedEntityData.defineId(Photograph.class, EntityDataSerializers.INT);

    @Environment(EnvType.CLIENT)
    public ServerTexture serverTexture;

    public Photograph(EntityType<? extends Photograph> entityType, Level world) {
        super(entityType, world);
    }

    private Photograph(Level world, BlockPos pos) {
        super(CameraEntityTypes.PHOTOGRAPH, world, pos);
    }

    public Photograph(Level world, BlockPos pos, Direction direction) {
        this(world, pos);
        this.setDirection(direction);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        builder.define(DATA_PHOTOGRAPH, "test");
        builder.define(DATA_SIZE, 2);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        if (DATA_SIZE.equals(data)) {
            this.recalculateBoundingBox();
        }
    }

    public String getPhotographName() {
        return this.entityData.get(DATA_PHOTOGRAPH);
    }

    public void setPhotographName(String string) {
        this.entityData.set(DATA_PHOTOGRAPH, string);
    }

    public int getSize() {
        return this.entityData.get(DATA_SIZE);
    }

    public void setSize(int size) {
        this.entityData.set(DATA_SIZE, size);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        nbt.putByte("facing", (byte) this.direction.get2DDataValue());
        super.addAdditionalSaveData(nbt);
        nbt.putString("PhotographName", this.getPhotographName());
        nbt.putInt("PhotographSize", this.getSize());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        this.direction = Direction.from2DDataValue(nbt.getByte("facing"));
        super.readAdditionalSaveData(nbt);
        this.setDirection(this.direction);
        if (nbt.contains("PhotographName")) this.setPhotographName(nbt.getString("PhotographName"));
        if (nbt.contains("PhotographSize")) this.setSize(nbt.getInt("PhotographSize"));
    }

    @Override
    protected @NotNull AABB calculateBoundingBox(BlockPos pos, Direction direction) {
        int size = this.getSize();
        Vec3 vec3 = Vec3.atCenterOf(pos).relative(direction, -0.46875);
        double sizeOffset = this.offsetForSize(size);
        Vec3 vec32 = vec3.relative(direction.getCounterClockWise(), sizeOffset).relative(Direction.UP, sizeOffset);
        Direction.Axis axis = direction.getAxis();
        double g = axis == Direction.Axis.X ? 0.0625 : (double)size;
        double i = axis == Direction.Axis.Z ? 0.0625 : (double)size;
        return AABB.ofSize(vec32, g, size, i);
    }

    private double offsetForSize(int width) {
        return width % 2 == 0 ? 0.5 : 0.0;
    }

    @Override
    public void dropItem(@Nullable Entity breaker) {
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (breaker instanceof Player player && player.hasInfiniteMaterials()) {
                return;
            }

            this.spawnAtLocation(Items.PAINTING);
        }
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    public void moveTo(double x, double y, double z, float yaw, float pitch) {
        this.setPos(x, y, z);
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
        this.setPos(x, y, z);
    }

    @Override
    public @NotNull Vec3 trackingPosition() {
        return Vec3.atLowerCornerOf(this.pos);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity trackerEntry) {
        return new ClientboundAddEntityPacket(this, this.direction.get3DDataValue(), this.getPos());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        this.setDirection(Direction.from3DDataValue(packet.getData()));
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(Items.PAINTING);
    }
}