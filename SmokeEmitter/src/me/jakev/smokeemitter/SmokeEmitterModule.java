package me.jakev.smokeemitter;

import api.mod.StarMod;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.utils.game.module.ModManagerContainerModule;
import api.utils.particle.ModParticleUtil;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.core.Timer;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.io.IOException;

/**
 * Created by Jake on 4/19/2021.
 * <insert description here>
 */
public class SmokeEmitterModule extends ModManagerContainerModule {
    public SmokeEmitterModule(SegmentController ship, ManagerContainer<?> managerContainer, StarMod mod, short blockId) {
        super(ship, managerContainer, mod, blockId);
    }
    boolean red = false;

    private static final Vector3f coreOffset = new Vector3f(-16F, -16F, -16F);
    @Override
    public void handle(Timer timer) {
        //For every block in our module
        for (Long l : blocks.keySet()) {
            //Get the position relative to the ships origin. (Not the local co-ord in the 32x32x32 chunk)
            Vector3f pos = new Vector3f();
            ElementCollection.getPosFromIndex(l, pos);
            //Add the core offset
            pos.add(coreOffset);

            //Transform the relative-to-ship position to a relative-to-world position
            this.segmentController.getWorldTransform().transform(pos);

//            this.segmentController.getWorldTransform().basis.transform(dir); //If you ever want to shoot out at a direction relative to the ship, this is how you do it.
            Vector3f linearVelocity = this.segmentController.getLinearVelocity(new Vector3f());
            linearVelocity.scale(16);

            //Play the particle
            Vector4f color = new Vector4f(1,1,1,1);
            if(red) color.set(1,0,0,1);
            ModParticleUtil.playClient(segmentController.getSectorId(), SmokeEmitterParticles.SMOKE_PARTICLE_FACTORY, pos, SmokeEmitterParticles.SMOKE_PARTICLE_SPRITE,
                    new ModParticleUtil.Builder().setLifetime(1000).setSize(new Vector2f(7,7)).setVelocity(linearVelocity).setColor(color));
        }

    }

    @Override
    public void onTagSerialize(PacketWriteBuffer packetWriteBuffer) throws IOException {
        packetWriteBuffer.writeBoolean(red);
    }

    @Override
    public void onTagDeserialize(PacketReadBuffer packetReadBuffer) throws IOException {
        packetReadBuffer.readBoolean();
    }

    @Override
    public double getPowerConsumedPerSecondResting() {
        return 0;
    }

    @Override
    public double getPowerConsumedPerSecondCharging() {
        return 0;
    }

    @Override
    public String getName() {
        return "Smoke Emitter";
    }
}
