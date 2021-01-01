package me.jakev.particleemitter;

import api.DebugFile;
import api.config.BlockConfig;
import api.listener.events.controller.ClientInitializeEvent;
import api.mod.StarMod;
import api.network.Packet;
import api.network.packets.PacketUtil;
import api.utils.textures.StarLoaderTexture;
import org.schema.game.common.data.element.ElementInformation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by Jake on 12/3/2020.
 * <insert description here>
 */
public class ParticleEmitterMod extends StarMod {
    public static void main(String[] args) {

    }

    @Override
    public void onGameStart() {
        setModName("ParticleEmitter");
        setModVersion("0.1");
        setModAuthor("JakeV");
        setModDescription("A block that emits particles");
        inst = this;
        setSMDResourceId(334);
    }
    private static short[] emitterIds;
    public static short emitterId;
    public static short emitterIconId;
    @Override
    public void onBlockConfigLoad(BlockConfig config) {

        assert emitterIds != null;
        ElementInformation emitter = config.newElement(this, "Particle Emitter", emitterIds);
        BlockConfig.setBasicInfo(emitter, "Emits particles", 100, 0.1F, true, true, emitterIconId);
        emitter.setOrientatable(true);
        emitter.setHasActivationTexure(true);
        emitter.setCanActivate(true);
        emitter.signal = true;
        emitterId = emitter.getId();
        config.add(emitter);

    }

    public static ParticleEmitterMod inst;

    @Override
    public void onClientCreated(ClientInitializeEvent event) {
    }
    @Override
    public void onEnable() {
        PacketUtil.registerPacket(PacketCSUpdateParticleData.class);
        PacketUtil.registerPacket(PacketSCUpdateParticleData.class);
        PacketUtil.registerPacket(PacketCSRequestParticleData.class);

        Packet.dumpPacketLookup();

        SpriteList.init(this);
        EEParticleEmitterListener.init(this);

        try {
            BufferedImage blockTexture = ImageIO.read(ParticleEmitterMod.class.getResourceAsStream("res/particle_block.png"));
            BufferedImage iconTexture = ImageIO.read(ParticleEmitterMod.class.getResourceAsStream("res/particle_icon.png"));
            StarLoaderTexture slIcon = StarLoaderTexture.newIconTexture(iconTexture);
            emitterIconId = (short) slIcon.getTextureId();
            int res = 256;
            StarLoaderTexture front = StarLoaderTexture.newBlockTexture(blockTexture.getSubimage(0, 0, res, res));
            StarLoaderTexture sides = StarLoaderTexture.newBlockTexture(blockTexture.getSubimage(res, 0, res, res));
            StarLoaderTexture rear = StarLoaderTexture.newBlockTexture(blockTexture.getSubimage(res*2, 0, res, res));
            short sideId = (short) sides.getTextureId();
            short rearId = (short) rear.getTextureId();
            emitterIds = new short[]{(short) front.getTextureId(), sideId,rearId,rearId,sideId, sideId};
            DebugFile.log("Loaded block textures", this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}