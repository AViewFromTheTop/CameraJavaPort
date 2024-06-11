package net.lunade.camera.entity;

import net.lunade.camera.CameraPortMain;
import net.lunade.camera.networking.CameraPossessPacket;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class CameraEntity extends Mob {
    private static final EntityDataAccessor<Float> TRACKED_HEIGHT = SynchedEntityData.defineId(CameraEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> TIMER = SynchedEntityData.defineId(CameraEntity.class, EntityDataSerializers.INT);
    public ArrayList<UUID> queuedUUIDS = new ArrayList<>();
    public long lastHit;
    public float prevTimer;
    public float timer;
    private boolean goingUp = false;

    public CameraEntity(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
        this.setPersistenceRequired();
        this.getNavigation().setCanFloat(false);
    }

    @NotNull
    public static AttributeSupplier.Builder addAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.FOLLOW_RANGE, 80D)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.MOVEMENT_SPEED, 0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 100D)
                .add(Attributes.STEP_HEIGHT, 0D);
    }

    @Override
    protected @NotNull EntityDimensions getDefaultDimensions(Pose pose) {
        return EntityDimensions.scalable(this.getBoundingBoxRadius() * 2F, this.getTrackedHeight()).scale(this.getAgeScale());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TRACKED_HEIGHT, 1.75F);
        builder.define(TIMER, 0);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevTimer = this.getTimer();
        if (!this.level().isClientSide && this.level() instanceof ServerLevel) {
            if (this.getTimer() > 0) {
                this.setTimer(this.getTimer() - 1);
                if (!this.queuedUUIDS.isEmpty()) {
                    Player chosen = this.level().getPlayerByUUID(this.queuedUUIDS.get(0));
                    if (chosen != null) {
                        this.getLookControl().setLookAt(chosen);
                    }
                }
                if (this.getTimer() == 1) {
                    if (this.level() instanceof ServerLevel serverLevel) {
                        ArrayList<ServerPlayer> queuedPlayers = new ArrayList<>();
                        for (UUID uuid : this.queuedUUIDS) {
                            Player player = this.level().getPlayerByUUID(uuid);
                            if (player instanceof ServerPlayer serverPlayer) {
                                queuedPlayers.add(serverPlayer);
                            }
                        }
                        ArrayList<ServerPlayer> nonQueuedPlayers = new ArrayList<>(serverLevel.getServer().getPlayerList().getPlayers());
                        nonQueuedPlayers.remove(queuedPlayers);
                        nonQueuedPlayers.removeIf(serverPlayer -> serverPlayer.level().dimension() != serverLevel.dimension());

                        for (ServerPlayer player : nonQueuedPlayers) {
                            player.connection.send(
                                    new ClientboundSoundPacket(
                                            BuiltInRegistries.SOUND_EVENT.wrapAsHolder(CameraPortMain.CAMERA_SNAP),
                                            this.getSoundSource(),
                                            this.getX(),
                                            this.getY(),
                                            this.getZ(),
                                            0.5F,
                                            1F,
                                            this.random.nextLong()
                                    )
                            );
                        }

                        for (ServerPlayer serverPlayer : queuedPlayers) {
                            CameraPossessPacket.sendTo(serverPlayer, this);
                        }
                        this.queuedUUIDS.removeIf(uuid -> true);
                    }
                }
            }
        }
        this.timer = Math.max(0, this.prevTimer -= 1);
        this.refreshDimensions();
    }

    @Override
    protected InteractionResult mobInteract(@NotNull Player player, InteractionHand hand) {
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown()) {
            if (this.canBeAdjusted()) {
                float change = this.goingUp ? 0.095F : -0.095f;
                float newHeight = (this.getTrackedHeight() + change);
                if (newHeight >= getMaxHeight()) {
                    this.goingUp = false;
                    newHeight = this.getMaxHeight();
                } else if (newHeight <= this.getMinHeight()) {
                    this.goingUp = true;
                    newHeight = this.getMinHeight();
                }
                this.setTrackedHeight(newHeight);
                this.level().playSound(null, getX(), getEyeY(), getZ(), CameraPortMain.CAMERA_ADJUST, SoundSource.NEUTRAL, this.getSoundVolume(), this.getTrackedHeight());
                return InteractionResult.SUCCESS;
            }
        } else {
            if (this.getTimer() > 1) {
                if (this.addPlayerToQueue(player)) {
                    this.playSound(CameraPortMain.CAMERA_PRIME, this.getSoundVolume(), this.getVoicePitch());
                }
            } else {
                if (this.addPlayerToQueue(player)) {
                    this.setTimer(60);
                    this.playSound(CameraPortMain.CAMERA_PRIME, this.getSoundVolume(), this.getVoicePitch());
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    public float getMaxHeight() {
        return 1.75F;
    }

    public float getMinHeight() {
        return 0.8F;
    }

    public float getBoundingBoxRadius() {
        return 0.3F;
    }

    public boolean canBeAdjusted() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide || this.isRemoved()) {
            return false;
        } else if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.kill();
            return false;
        } else if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            this.brokenByAnything(source);
            this.kill();
            return false;
        } else if (source.is(DamageTypeTags.IGNITES_ARMOR_STANDS)) {
            if (this.isOnFire()) {
                this.causeDamage(source, 0.15F);
            } else {
                this.igniteForSeconds(5);
            }

            return false;
        } else if (source.is(DamageTypeTags.BURNS_ARMOR_STANDS) && this.getHealth() > 0.5F) {
            this.causeDamage(source, 4F);
            return false;
        } else {
            boolean bl = source.is(DamageTypeTags.CAN_BREAK_ARMOR_STAND);
            boolean bl2 = source.is(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS);
            if (!bl && !bl2) {
                return false;
            } else {
                if (source.getEntity() instanceof Player player && !player.getAbilities().mayBuild) {
                    return false;
                }

                if (source.isCreativePlayer()) {
                    this.playBrokenSound();
                    this.showBreakingParticles();
                    this.kill();
                } else {
                    long l = this.level().getGameTime();
                    if (l - this.lastHit > 5L && !bl2) {
                        this.level().broadcastEntityEvent(this, (byte) 32);
                        this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
                        this.lastHit = l;
                    } else {
                        this.brokenByPlayer(source);
                        this.showBreakingParticles();
                        this.kill();
                    }
                }
                return true;
            }
        }
    }

    private void showBreakingParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.STICK)),
                    this.getX(),
                    this.getY(0.6666666666666666D),
                    this.getZ(),
                    10,
                    this.getBbWidth() / 4.0F,
                    this.getBbHeight() / 4.0F,
                    this.getBbWidth() / 4.0F, 0.05D
            );
        }
    }

    private void causeDamage(DamageSource damageSource, float amount) {
        float f = this.getHealth();
        f -= amount;
        if (f <= 0.5F) {
            this.brokenByAnything(damageSource);
            this.kill();
        } else {
            this.setHealth(f);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());
        }
    }

    private void brokenByPlayer(DamageSource damageSource) {
        ItemStack itemStack = this.getPickResult();
        itemStack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
        Block.popResource(this.level(), this.blockPosition(), itemStack);
        this.brokenByAnything(damageSource);
    }

    private void brokenByAnything(DamageSource damageSource) {
        this.playBrokenSound();
		if (this.level() instanceof ServerLevel serverLevel) {
			this.dropAllDeathLoot(serverLevel, damageSource);
		}
    }

    private void playBrokenSound() {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), CameraPortMain.CAMERA_BREAK, this.getSoundSource(), 1.0F, 1.0F);
    }

    public boolean addPlayerToQueue(@NotNull Player player) {
        UUID playerUUID = player.getUUID();
        if (!this.queuedUUIDS.contains(playerUUID)) {
            this.queuedUUIDS.add(playerUUID);
            return true;
        }
        return false;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 32) {
            if (this.level().isClientSide) {
                this.level().playLocalSound(
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        CameraPortMain.CAMERA_HIT,
                        this.getSoundSource(),
                        0.3F,
                        1F,
                        false
                );
                this.lastHit = this.level().getGameTime();
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void kill() {
        this.remove(Entity.RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    public void doPush(Entity entity) {
    }

    @Override
    protected float getJumpPower() {
        return 0F;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance mobEffectInstance) {
        return false;
    }

    @NotNull
    @Override
    public Fallsounds getFallSounds() {
        return new Fallsounds(CameraPortMain.CAMERA_FALL, CameraPortMain.CAMERA_FALL);
    }

    @Override
    public SoundEvent getHurtSound(DamageSource damageSource) {
        return CameraPortMain.CAMERA_HIT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return CameraPortMain.CAMERA_BREAK;
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

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("ticksToPhoto", this.getTimer());
        if (!this.queuedUUIDS.isEmpty()) {
            for (UUID uuid : this.queuedUUIDS) {
                int index = this.queuedUUIDS.indexOf(uuid);
                String uuidTag = "queuedUUID" + index;
                compoundTag.putUUID(uuidTag, uuid);
            }
        }
        compoundTag.putFloat("currentHeight", this.getTrackedHeight());
        compoundTag.putBoolean("goingUp", this.goingUp);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setTimer(compoundTag.getInt("ticksToPhoto"));
        for (int i = 0; true; i++) {
            String uuidTag = "queuedUUID" + i;
            if (compoundTag.contains(uuidTag)) {
                this.queuedUUIDS.add(compoundTag.getUUID(uuidTag));
            } else {
                break;
            }
        }
        this.setTrackedHeight(compoundTag.getFloat("currentHeight"));
        this.goingUp = compoundTag.getBoolean("goingUp");
    }

    public float getLerpedTimer(float tickDelta) {
        return Mth.lerp(tickDelta, this.prevTimer, this.timer);
    }

    public float getTrackedHeight() {
        return this.entityData.get(TRACKED_HEIGHT);
    }

    public void setTrackedHeight(float value) {
        this.entityData.set(TRACKED_HEIGHT, value);
    }

    public int getTimer() {
        return this.entityData.get(TIMER);
    }

    public void setTimer(int value) {
        this.entityData.set(TIMER, value);
    }
}
