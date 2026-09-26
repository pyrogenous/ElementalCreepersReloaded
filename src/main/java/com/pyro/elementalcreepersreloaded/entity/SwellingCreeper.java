package com.pyro.elementalcreepersreloaded.entity;

/** What the creeper renderers read: how far the fuse is, and whether lightning charged it. */
public interface SwellingCreeper {
    float getSwelling(float partialTicks);

    boolean isPowered();
}
