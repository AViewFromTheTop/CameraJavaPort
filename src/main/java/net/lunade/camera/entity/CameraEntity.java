package net.lunade.camera.entity;

import net.lunade.camera.ClientCameraManager;
import net.lunade.camera.Main;
import net.minecraft.client.MinecraftClient;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.EntityDataSerializers;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Objects;

public class CameraEntity extends Mob {
    private static final EntityDataAccessor<Boolean> HAS_SPYGLASS = SynchedEntityData.defineId(CameraEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> TRACKED_HEIGHT = SynchedEntityData.defineId(CameraEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> TIMER = SynchedEntityData.defineId(CameraEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PREV_TIMER = SynchedEntityData.defineId(CameraEntity.class, EntityDataSerializers.INT);

    public int ticksToDeath = -1;
    public String playerUUID = "temp";
    public int shotByShulker = 0;
    public boolean goingUp = false;
    public float maxHeight = 1.75f;
    public float minHeight = 0.8f;
    public double extendBy = 0.3;
    public int timeToWait;

    public Item cameraItem = Main.CAMERA_ITEM;
    public EntityType<? extends CameraEntity> oppositeCamera = Main.SMALL_CAMERA;

    public CameraEntity(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
        this.maxUpStep = 0.0F;
        this.setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(HAS_SPYGLASS, false);
        this.entityData.define(TRACKED_HEIGHT, 1.75F);
        this.entityData.define(TIMER, 0);
        this.entityData.define(PREV_TIMER, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this instanceof SmallCameraEntity)) {
            this.setBoundingBox(new AABB(this.position().add(this.extendBy, (this.maxHeight - this.getTrackedHeight()), this.extendBy), this.position().add(-this.extendBy, this.maxHeight, -this.extendBy)));
        }
    }

    @Override
    protected InteractionResult interactMob(Player playerEntity, InteractionHand hand) {
        if (playerEntity.getMainHandStack().isOf(Items.SPYGLASS) && !this.hasSpyglass() && !world.isClient) {
            if (!playerEntity.isCreative()) { playerEntity.getMainHandStack().decrement(1); }
            this.setSpyglass(true);
            return ActionResult.CONSUME;
        }
        if (playerEntity.isSneaking() && !world.isClient) {
            if (this.hasSpyglass()) {
                this.setSpyglass(false);
                ItemEntity item = EntityType.ITEM.create(world);
                assert item != null;
                item.refreshPositionAndAngles(this.getEyePos().x, this.getEyePos().y - 0.2, this.getEyePos().z, 0.0F, 0.0F);
                item.setStack(new ItemStack(Items.SPYGLASS));
                world.spawnEntity(item);
                return ActionResult.CONSUME;
            } else if (!(this instanceof SmallCameraEntity)) {
                float change = -0.095f;
                if (this.goingUp) {
                    change = 0.095f;
                }
                float newHeight = (this.getTrackedHeight() + change);
                if (newHeight > maxHeight) {
                    this.goingUp = false;
                    newHeight = this.maxHeight;
                }
                if (newHeight < minHeight) {
                    this.goingUp = true;
                    newHeight = this.minHeight;
                }
                this.setTrackedHeight(newHeight);
                this.world.playSound(null, getX(), getEyeY(), getZ(), Main.CAMERA_ADJUST, SoundCategory.NEUTRAL, 0.6F, this.getTrackedHeight());
                BlockHitResult hit = world.raycast(new RaycastContext(this.getEyePos(), this.getPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
                this.addVelocity(0, -this.getVelocity().y, 0);
                if (hit.getType() == HitResult.Type.BLOCK) {
                    this.updatePosition(this.getX(), hit.getPos().y - (this.maxHeight - this.getTrackedHeight()), this.getZ());
                }
                return ActionResult.SUCCESS;
                }
            } else {
            if (this.getTimer() < 0 || this.getPrevTimer() > 1) {
                if (this.getTimer() > 1) {
                    if (this.world.isClient && !this.ready) {
                        this.ready = true;
                        this.world.playSound(null, getX(), getEyeY(), getZ(), Main.CAMERA_PRIME, SoundCategory.NEUTRAL, 0.2F, (this.random.nextFloat() * 0.15F) + 0.7F);
                    }
                } else {
                    if (this.world.isClient) {
                        this.ready = true;
                    }
                    if (!world.isClient) {
                        this.playerUUID = playerEntity.getUuidAsString();
                        this.setTimer(60);
                        this.world.playSound(null, getX(), getEyeY(), getZ(), Main.CAMERA_PRIME, SoundCategory.NEUTRAL, 1F, (this.random.nextFloat() * 0.3F) + 0.8F);
                    }
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0D).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 0.0D).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0D).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 100.0D);
    }

    @Override
    public void onStruckByLightning(ServerWorld serverWorld, LightningEntity lightningEntity) {
        this.world.playSound(null, this.getBlockPos(), Main.CAMERA_CRACK, SoundCategory.NEUTRAL, 4.0F, 1.0F);
        this.world.playSound(null, this.getBlockPos(), Main.CAMERA_BREAK, SoundCategory.NEUTRAL, 4.0F, 1.0F);
        this.world.playSound(null, this.getBlockPos(), Main.CAMERA_PRIME, SoundCategory.NEUTRAL, 4.0F, 1.0F);
        CameraEntity small = (CameraEntity) this.oppositeCamera.create(world);
        assert small != null;
        small.refreshPositionAndAngles(this.getX(), this.getY() + (this.maxHeight - this.getTrackedHeight()), this.getZ(), this.getYaw(), this.getPitch());
        this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 2F, Explosion.DestructionType.NONE);
        small.setTrackedHeight(this.getTrackedHeight());
        small.goingUp = this.goingUp;
        small.setSpyglass(this.hasSpyglass());
        this.spawnBreakParticles();
        this.spawnBreakParticles();
        if (this.hasCustomName()) {
            small.setCustomName(this.getCustomName());
        }
        world.spawnEntity(small);
        this.discard();
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        this.spawnBreakParticles();
        if (this.getTimer() != 1 && this.getTimer() != 0) {
            this.spawnBreakParticles();
            this.spawnBreakParticles();
            this.spawnBreakParticles();
            if (!damageSource.isSourceCreativePlayer()) {
                if (this.hasSpyglass()) {
                    ItemEntity spy = EntityType.ITEM.create(world);
                    assert spy != null;
                    spy.refreshPositionAndAngles(this.getEyePos().x, this.getEyePos().y - 0.2, this.getEyePos().z, 0.0F, 0.0F);
                    spy.setStack(new ItemStack(Items.SPYGLASS));
                    world.spawnEntity(spy);
                }
                ItemEntity item = EntityType.ITEM.create(world);
                assert item != null;
                item.refreshPositionAndAngles(this.getEyePos().x, this.getEyePos().y - 0.2, this.getEyePos().z, 0.0F, 0.0F);
                item.setStack(new ItemStack(this.cameraItem));
                if (this.hasCustomName()) { item.setCustomName(this.getCustomName()); }
                if (damageSource.getAttacker() instanceof SkeletonEntity) {
                    item.setStack(new ItemStack(Items.MUSIC_DISC_CHIRP));
                    this.world.playSound(null, this.getBlockPos(), Main.CAMERA_CRACK, SoundCategory.NEUTRAL, 4.0F, 1.0F);
                }
                world.spawnEntity(item);
                this.remove(RemovalReason.KILLED);
            } else {
                this.remove(RemovalReason.KILLED);
            }
        } else {
            this.ticksToDeath = (int) (this.getTimer() + 2);
        }
    }

    @Override
    protected void applyDamage(DamageSource damageSource, float f) {
        this.spawnBreakParticles();
        super.applyDamage(damageSource, f);
    }

    public void spawnBreakParticles() {
        if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.STICK)), this.getX(), this.getBodyY(0.6666666666666666D), this.getZ(), 10, (double)(this.getWidth() / 4.0F), (double)(this.getHeight() / 4.0F), (double)(this.getWidth() / 4.0F), 0.05D);
        }
    }

    @Override
    public void doPush(Entity entity) { }

    @Override
    protected float getJumpPower() {
        return 0F;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSource) {
        return Main.CAMERA_HIT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return Main.CAMERA_BREAK;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.cameraItem);
    }

    @Override
    protected float getStandingEyeHeight(Pose entityPose, EntityDimensions entityDimensions) {
        return 1.62F;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canTakeItem(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    //CLIENT VARIABLES
    private float prevVignette=0.0f;
    private boolean wasHidden = false;
    private int prevFOV;
    public boolean ready;

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (this.ticksToDeath > 0 && this.ticksToDeath != 99) {
            --this.ticksToDeath;
        }
        if (this.ticksToDeath == 0) {
            this.onDeath(DamageSource.GENERIC);
        }
        if (this.getTimer() >= -1) {
            this.setTimer(this.getTimer() - 1);
            if (this.getTimer() > 6 && this.random.nextInt(0, 2) == 0) {
                this.level.addParticle(ParticleTypes.LARGE_SMOKE, getX(),getEyeY(),getZ(),0,0.15,0);
            }
            if (!this.level.isClientSide) {
                Entity chosen = null;
                List<? extends Entity> entities = this.level.getEntities(this, this.getBoundingBox().expandTowards(32, 32, 32));
                for (Entity entity : entities) {
                    if (Objects.equals(entity.getStringUUID(), this.playerUUID)) {
                        chosen = entity;
                        break;
                    }
                }
                if (chosen != null) {
                    this.getLookControl().setLookAt(chosen);
                }
            }
        }
        if (this.getTimer() == 1) {
            this.level.playSound(null, getX(),getEyeY(),getZ(), Main.CAMERA_SNAP, SoundSource.NEUTRAL, 1F, 1F);
        }
        //CLIENT ONLY

        if (this.timeToWait > 2) {
            super.tickMovement();
        } else {
            ++this.timeToWait;
        }
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("ticksToPhoto", this.getTimer());
        nbt.putInt("ticksToPrevPhoto", this.getPrevTimer());
        nbt.putInt("ticksToDeath", this.ticksToDeath);
        nbt.putString("playerUUID", this.playerUUID);
        nbt.putInt("shotByShulker", this.shotByShulker);
        nbt.putBoolean("goingUp", this.goingUp);
        nbt.putBoolean("hasSpyglass", this.hasSpyglass());
        nbt.putFloat("currentHeight", this.getTrackedHeight());
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setTimer(nbt.getInt("ticksToPhoto"));
        this.setPrevTimer(nbt.getInt("ticksToPrevPhoto"));
        this.ticksToDeath = nbt.getInt("ticksToDeath");
        this.playerUUID = nbt.getString("playerUUID");
        this.shotByShulker = nbt.getInt("shotByShulker");
        this.goingUp = nbt.getBoolean("goingUp");
        this.setSpyglass(nbt.getBoolean("hasSpyglass"));
        this.setTrackedHeight(nbt.getFloat("currentHeight"));
    }

    public float getLerpedTimer(float tickDelta) {
        return MathHelper.lerp(tickDelta, (float)this.getPrevTimer(), (float)this.getTimer());
    }

    public float getTrackedHeight() {
        return this.dataTracker.get(TRACKED_HEIGHT);
    }

    public void setTrackedHeight(float value) {
        this.dataTracker.set(TRACKED_HEIGHT, value);
    }

    public boolean hasSpyglass() {
        return this.dataTracker.get(HAS_SPYGLASS);
    }

    public void setSpyglass(boolean value) {
        this.dataTracker.set(HAS_SPYGLASS, value);
    }

    public int getTimer() {
        return this.dataTracker.get(TIMER);
    }

    public void setTimer(int value) {
        this.dataTracker.set(TIMER, value);
    }

    public int getPrevTimer() {
        return this.dataTracker.get(PREV_TIMER);
    }

    public void setPrevTimer(int value) {
        this.dataTracker.set(PREV_TIMER, value);
    }
}
