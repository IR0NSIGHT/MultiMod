package me.jakev.extraeffects.particleblock;

import javax.vecmath.Vector4f;

/**
 * Created by Jake on 12/19/2020.
 * <insert description here>
 */
public class ParticleBlockConfig {
    public int particleSprite = 1;
    public int particleCount = 1;
    public int lifetimeMs = 4000;

    public float startSize = 1;
    public float endSize = 1;

    public float launchSpeed = 1;
    public float randomVelocity = 0;
    public float speedDampener = 1;
    public float rotationSpeed = 0.1F;

    public float randomOffsetX = 0F;
    public float randomOffsetY = 0F;
    public float randomOffsetZ = 0F;

    public Vector4f startColor = new Vector4f(1,1,1,1);
    public Vector4f endColor = new Vector4f(1,1,1,0);
}
