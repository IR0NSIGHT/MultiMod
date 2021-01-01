package me.jakev.particleemitter;

import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;

import javax.vecmath.Vector4f;
import java.io.IOException;

/**
 * Created by Jake on 12/19/2020.
 * <insert description here>
 */
public class ParticleBlockConfig {
    public int particleSprite = 2;
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
    public void onTagSerialize(PacketWriteBuffer buffer) throws IOException {
        buffer.writeInt(particleSprite);
        buffer.writeInt(particleCount);
        buffer.writeInt(lifetimeMs);

        buffer.writeFloat(startSize);
        buffer.writeFloat(endSize);

        buffer.writeFloat(launchSpeed);
        buffer.writeFloat(randomVelocity);
        buffer.writeFloat(speedDampener);
        buffer.writeFloat(rotationSpeed);

        buffer.writeFloat(randomOffsetX);
        buffer.writeFloat(randomOffsetY);
        buffer.writeFloat(randomOffsetZ);

        buffer.writeVector4f(startColor);
        buffer.writeVector4f(endColor);
    }



    public void onTagDeserialize(PacketReadBuffer buffer) throws IOException{
        particleSprite = buffer.readInt();
        particleCount = buffer.readInt();
        lifetimeMs = buffer.readInt();

        startSize = buffer.readFloat();
        endSize = buffer.readFloat();

        launchSpeed = buffer.readFloat();
        randomVelocity = buffer.readFloat();
        speedDampener = buffer.readFloat();
        rotationSpeed = buffer.readFloat();

        randomOffsetX = buffer.readFloat();
        randomOffsetY = buffer.readFloat();
        randomOffsetZ = buffer.readFloat();

        startColor = buffer.readVector4f();
        endColor = buffer.readVector4f();

    }
}
