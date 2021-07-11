package me.jakev.extraeffects;

import api.config.BlockConfig;
import api.listener.events.controller.ClientInitializeEvent;
import api.listener.events.controller.ServerInitializeEvent;
import api.mod.StarMod;
import api.network.Packet;
import api.network.packets.PacketUtil;
import api.utils.particle.ModParticleUtil;
import me.jakev.extraeffects.listeners.*;
import org.schema.game.common.data.element.ElementInformation;

/**
 * Created by Jake on 12/3/2020.
 * <insert description here>
 */
public class ExtraEffects extends StarMod {

    @Override
    public void onLoad() {
        inst = this;
    }

    @Override
    public void onBlockConfigLoad(BlockConfig config) {
        ElementInformation myBlock = BlockConfig.newElement(this, "MyBlock", new short[]{55, 56, 57, 58, 59, 60});
        BlockConfig.setBasicInfo(myBlock, "test", 1, 1, true, false, 444);
        BlockConfig.add(myBlock);
    }

    public static ExtraEffects inst;

    @Override
    public void onClientCreated(ClientInitializeEvent event) {
        ExtraEffectsDrawUtil.init();
        ExtraEffectCannonListener.init(this);

    }

    @Override
    public void onServerCreated(ServerInitializeEvent event) {
        super.onServerCreated(event);
        ExtraEffectMissileListener.init(this);
        ExtraEffectsJumpListener.init(this);

    }

    @Override
    public void onLoadModParticles(ModParticleUtil.LoadEvent event) {
        SpriteList.init(this, event);
        ExtraEffectsParticles.init(event);
    }

    @Override
    public void onEnable() {
        Packet.dumpPacketLookup();
        PacketUtil.registerPacket(RemotePlay.class);
        ExtraEffectBeamListener.init(this);
        ExtraEffectExplodeListener.init(this);
    };

    /**
     * get percentage of point on range [min,max]. f.e. 0,10,3 -> 0.3 (30% between min and max)
     * @param min
     * @param max
     * @param point
     */
    public static float extrapolate (float min, float max, float point) {
        //technote: benchmarked this version vs branchless, no improvement.
        if (point > max) {
            return 1;
        };
        if (point < min) {
            return 0;
        }
        float range = max - min;
        float percentage = ((point-min)/range);
        return percentage;
    };

    /**
     * get absolute value of point between min and max, defined by % on scale. f.e. 0,10,0.3 -> 3 (30% between min and max = 3)
     * @param min
     * @param max
     * @param percentage
     * @return
     */
    public static float interpolate (float min, float max, float percentage) {
        //technote: benchmarked this version vs branchless, no improvement.
        if (percentage >= 1) {
            return max;
        };
        if (percentage <= 0) {
            return min;
        }
        return (percentage * (max - min)) + min;
    };
}