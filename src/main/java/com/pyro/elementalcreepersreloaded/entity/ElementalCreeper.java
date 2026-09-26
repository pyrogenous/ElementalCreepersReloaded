package com.pyro.elementalcreepersreloaded.entity;

import com.pyro.elementalcreepersreloaded.ECConfig;
import com.pyro.elementalcreepersreloaded.ElementalCreepers;
import com.pyro.elementalcreepersreloaded.entity.ai.ElementalSwellGoal;
import com.pyro.lomlibreloaded.nitea.NiteaSupport;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * A creeper with its own explosion. Swells and ignites like a vanilla creeper (the fuse, flint and steel, charging
 * by lightning), but when the fuse runs out it calls {@link #explosion} instead of the vanilla explosion.
 */
public abstract class ElementalCreeper extends Monster implements SwellingCreeper {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(ElementalCreeper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_IS_POWERED = SynchedEntityData.defineId(ElementalCreeper.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IS_IGNITED = SynchedEntityData.defineId(ElementalCreeper.class, EntityDataSerializers.BOOLEAN);

    protected int oldSwell;
    protected int swell;
    protected int maxSwell = 30;
    protected int explosionRadius = 3;
    /** Whether the explosion makes the vanilla boom (the knockback creepers are silent). */
    protected boolean explosionSound = true;

    protected ElementalCreeper(EntityType<? extends ElementalCreeper> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ElementalSwellGoal(this, 9.0, 49.0));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Ocelot.class, 6.0F, 1.0, 1.2));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Cat.class, 6.0F, 1.0, 1.2));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(DATA_SWELL_DIR, -1);
        entityData.define(DATA_IS_POWERED, false);
        entityData.define(DATA_IS_IGNITED, false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("powered", this.isPowered());
        output.putShort("Fuse", (short) this.maxSwell);
        output.putByte("ExplosionRadius", (byte) this.explosionRadius);
        output.putBoolean("ignited", this.isIgnited());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(DATA_IS_POWERED, input.getBooleanOr("powered", false));
        this.maxSwell = input.getShortOr("Fuse", (short) this.maxSwell);
        this.explosionRadius = input.getByteOr("ExplosionRadius", (byte) this.explosionRadius);
        if (input.getBooleanOr("ignited", false))
            this.ignite();
    }

    @Override
    public int getMaxFallDistance() {
        return this.getTarget() == null ? this.getComfortableFallDistance(0.0F) : this.getComfortableFallDistance(this.getHealth() - 1.0F);
    }

    @Override
    public boolean causeFallDamage(double fallDistance, float damageModifier, DamageSource damageSource) {
        boolean damaged = super.causeFallDamage(fallDistance, damageModifier, damageSource);
        this.swell += (int) (fallDistance * 1.5);
        if (this.swell > this.maxSwell - 5)
            this.swell = this.maxSwell - 5;
        return damaged;
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            this.oldSwell = this.swell;
            if (this.isIgnited())
                this.setSwellDir(1);
            int swellDir = this.getSwellDir();
            if (swellDir > 0 && this.swell == 0) {
                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
                this.gameEvent(GameEvent.PRIME_FUSE);
            }
            this.swell += swellDir * this.swellSpeed();
            if (this.swell < 0)
                this.swell = 0;
            if (this.swell >= this.maxSwell) {
                this.swell = this.maxSwell;
                this.explode();
            }
        }
        super.tick();
    }

    /** Fuse ticks per tick while swelling. */
    protected int swellSpeed() {
        return 1;
    }

    /** Runs the creeper's explosion. Called on both sides; the effect itself only happens on the server. */
    public void explode() {
        if (!(this.level() instanceof ServerLevel level)) return;
        int power = this.isPowered() ? 2 : 1;
        boolean griefing = ForgeEventFactory.getMobGriefingEvent(level, this);
        String name = BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).getPath();
        NiteaSupport.breadcrumb(ElementalCreepers.nitea(), "explosion", name + (power > 1 ? " (charged)" : "") + " exploded", this);
        // One broken explosion shouldn't take the world down with it: it's reported, and the creeper still goes away
        NiteaSupport.guard(ElementalCreepers.nitea(), name + " explosion", () -> this.explosion(level, power, griefing));
        if (this.explosionSound)
            level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE,
                    4.0F, (1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F) * 0.7F);
        level.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(0.5), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        if (this.diesAfterExplosion()) {
            this.dead = true;
            level.broadcastEntityEvent(this, EntityEvent.POOF);
            this.discard();
        } else {
            // Survivors start over instead of exploding again every tick
            this.swell = 0;
            this.oldSwell = 0;
        }
    }

    /**
     * The creeper's effect.
     *
     * @param power    2 when charged by lightning, else 1
     * @param griefing whether mobs may change blocks here (the mobGriefing game rule and Forge's event)
     */
    protected abstract void explosion(ServerLevel level, int power, boolean griefing);

    public boolean diesAfterExplosion() {
        return true;
    }

    /** A configured radius, multiplied by the explosion power when charged. */
    protected int radius(ForgeConfigSpec.IntValue radius, int power) {
        return radius.get() * power;
    }

    protected <T extends LivingEntity> List<T> nearby(Class<T> type, double radius) {
        return this.level().getEntitiesOfClass(type, this.getBoundingBox().inflate(radius), e -> e != this && e.isAlive());
    }

    /** Fills a ball of {@code radius} around the creeper with {@code state}, 3 in 4 air blocks. */
    protected void domeExplosion(ServerLevel level, int radius, BlockState state) {
        BlockPos center = this.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            if (pos.distSqr(center) <= radius * radius && level.getBlockState(pos).canBeReplaced() && state.canSurvive(level, pos)
                    && this.random.nextInt(4) < 3)
                level.setBlock(pos, state, 2);
        }
    }

    /** Scatters {@code state} over the ground around the creeper, on half the free spots. */
    protected void wildExplosion(ServerLevel level, int radius, BlockState state) {
        BlockPos center = this.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius), center.offset(radius, radius, radius))) {
            if (isGroundSpot(level, pos) && state.canSurvive(level, pos) && this.random.nextBoolean())
                level.setBlock(pos, state, 2);
        }
    }

    /** Dome or scatter, as the config says. */
    protected void fillExplosion(ServerLevel level, int radius, BlockState state) {
        if (ECConfig.DOME_EXPLOSIONS.get())
            this.domeExplosion(level, radius, state);
        else
            this.wildExplosion(level, radius, state);
    }

    /** A free block with solid ground under it. */
    protected static boolean isGroundSpot(Level level, BlockPos pos) {
        return level.getBlockState(pos).canBeReplaced() && !level.getBlockState(pos.below()).canBeReplaced();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.CREEPER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CREEPER_DEATH;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        return true;
    }

    public boolean isPowered() {
        return this.entityData.get(DATA_IS_POWERED);
    }

    public void setPowered(boolean powered) {
        this.entityData.set(DATA_IS_POWERED, powered);
    }

    public float getSwelling(float partialTicks) {
        return Mth.lerp(partialTicks, this.oldSwell, this.swell) / (this.maxSwell - 2);
    }

    public int getSwellDir() {
        return this.entityData.get(DATA_SWELL_DIR);
    }

    public void setSwellDir(int dir) {
        this.entityData.set(DATA_SWELL_DIR, dir);
    }

    public boolean isIgnited() {
        return this.entityData.get(DATA_IS_IGNITED);
    }

    public void ignite() {
        this.entityData.set(DATA_IS_IGNITED, true);
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt lightningBolt) {
        super.thunderHit(level, lightningBolt);
        this.entityData.set(DATA_IS_POWERED, true);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ItemTags.CREEPER_IGNITERS)) {
            SoundEvent sound = stack.is(Items.FIRE_CHARGE) ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE;
            this.level().playSound(player, this.getX(), this.getY(), this.getZ(), sound, this.getSoundSource(), 1.0F,
                    this.random.nextFloat() * 0.4F + 0.8F);
            if (!this.level().isClientSide()) {
                this.ignite();
                if (!stack.isDamageableItem())
                    stack.shrink(1);
                else
                    stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    protected AABB area(double radius) {
        return this.getBoundingBox().inflate(radius);
    }
}
