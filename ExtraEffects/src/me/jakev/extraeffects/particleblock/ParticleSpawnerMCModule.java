package me.jakev.extraeffects.particleblock;

import api.mod.StarMod;
import api.network.PacketReadBuffer;
import api.network.PacketWriteBuffer;
import api.network.packets.PacketUtil;
import api.utils.game.module.ModManagerContainerModule;
import api.utils.particle.ModParticle;
import api.utils.particle.ModParticleFactory;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.SpriteList;
import me.jakev.extraeffects.particleblock.network.PacketCSRequestParticleData;
import me.jakev.extraeffects.particles.ModuleParticle;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.SegmentController;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.schine.graphicsengine.core.Timer;

import javax.vecmath.Vector3f;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jake on 12/10/2020.
 * <insert description here>
 */
public class ParticleSpawnerMCModule extends ModManagerContainerModule {
    public ParticleSpawnerMCModule(SegmentController ship, ManagerContainer<?> managerContainer, StarMod mod, short blockId) {
        super(ship, managerContainer, mod, blockId);
        modules.add(this);
    }

    public HashMap<Long, ParticleBlockConfig> blockData = new HashMap<>();

    public static ArrayList<ParticleSpawnerMCModule> modules = new ArrayList<>();
    @Override
    public void handle(Timer timer) {
        //Want to spawn particle if:
        // Is singleplayer and is a server module
        // Is NOT singleplayer and is not server module
        boolean onServer = getManagerContainer().isOnServer();
        boolean singlePlayer = isOnSinglePlayer();
        if((singlePlayer && onServer) || (!singlePlayer && !onServer)) {
//                            if(timer.counter%4==0) {
            for (Long l : blocks.keySet()) {
                ParticleBlockConfig config = blockData.get(l);
                Vector3f pos = new Vector3f();
                ElementCollection.getPosFromIndex(l, pos);
                this.segmentController.getSegmentBuffer().getPointUnsave(l);
                pos.add(coreOffset);
                this.segmentController.getWorldTransform().transform(pos);
                byte orientation = blocks.get(l);
                Vector3f dir = new Vector3f(getDirFromOrientation(orientation));
                dir.scale(config.launchSpeed);
                this.segmentController.getWorldTransform().basis.transform(dir);
                ModParticleUtil.playClient(pos, SpriteList.values()[config.particleSprite].getSprite(), config.particleCount, config.lifetimeMs, dir, new ModParticleFactory() {
                    @Override
                    public ModParticle newParticle() {
                        return new ModuleParticle(config);
                    }
                });
//            ModParticleUtil.playClient(pos, SpriteList.FIRE.getSprite(), 1, 2000, dir, new ModParticleFactory() {
//                @Override
//                public ModParticle newParticle() {
//                    return new SimpleFireParticle(2,30);
//                }
//            });
            }
        }
    }

    @Override
    public double getPowerConsumedPerSecondResting() {
        return 1;
    }

    @Override
    public double getPowerConsumedPerSecondCharging() {
        return 1;
    }

    @Override
    public String getName() {
        return "ParticleSpawnerMCModule";
    }
    private static final Vector3f coreOffset = new Vector3f(-16F,-16F,-16F);
    private static Vector3f forward = new Vector3f(0,0,1);
    private static Vector3f backward = new Vector3f(0,0,-1);
    private static Vector3f left = new Vector3f(-1,0,0);
    private static Vector3f right = new Vector3f(1,0,0);
    private static Vector3f down = new Vector3f(0,-1,0);
    private static Vector3f up = new Vector3f(0,1,0);
    private static Vector3f unknown = new Vector3f(0,0,0);
    public static Vector3f getDirFromOrientation(byte b){
        switch (b){
            case 0: return forward;
            case 1: return backward;
            case 2: return up;
            case 3: return down;
            case 4: return left;
            case 5: return right;
            default:return unknown;
        }
    }

    @Override
    public void handlePlace(long abs, byte orientation) {
        super.handlePlace(abs, orientation);
        if(blockData.get(abs) == null){
            blockData.put(abs, new ParticleBlockConfig());
        }
        if(!isOnSinglePlayer() && !isOnServer()){
            PacketUtil.sendPacketToServer(new PacketCSRequestParticleData((ManagedUsableSegmentController<?>) this.getManagerContainer().getSegmentController(), abs));
        }
    }


    @Override
    public void onTagSerialize(PacketWriteBuffer buf) throws IOException {
        buf.writeInt(blockData.size());
        for (Map.Entry<Long, ParticleBlockConfig> entry : blockData.entrySet()) {
            long index = entry.getKey();
            ParticleBlockConfig config = entry.getValue();
            buf.writeLong(index);
            config.onTagSerialize(buf);
        }
    }

    @Override
    public void onTagDeserialize(PacketReadBuffer buf) throws IOException {
        int len = buf.readInt();
        for (int i = 0; i < len; i++) {
            long index = buf.readLong();
            ParticleBlockConfig config = blockData.get(index);
            if(config != null) {
                config.onTagDeserialize(buf);
            }else{
                ParticleBlockConfig value = new ParticleBlockConfig();
                blockData.put(index, value);
                value.onTagDeserialize(buf);
            }
        }

    }
}
