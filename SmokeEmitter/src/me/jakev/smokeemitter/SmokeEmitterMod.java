package me.jakev.smokeemitter;

import api.config.BlockConfig;
import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateEvent;
import api.listener.events.register.ManagerContainerRegisterEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.game.module.ModManagerContainerModule;
import api.utils.particle.ModParticleUtil;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.data.element.ElementInformation;
import org.schema.schine.resource.ResourceLoader;

/**
 * Created by Jake on 4/19/2021.
 * <insert description here>
 */
public class SmokeEmitterMod extends StarMod {
    public static SmokeEmitterMod inst;
    public static ElementInformation emitterBlock;
    public static void log(String printf, Object... objs){
        System.err.printf("[SmokeEmitterMod] " + printf, objs);
    }

    @Override
    public void onLoadModParticles(ModParticleUtil.LoadEvent event) {
        SmokeEmitterParticles.init(event);
    }

    @Override
    public void onResourceLoad(ResourceLoader loader) {

    }

    @Override
    public void onBlockConfigLoad(BlockConfig config) {
        emitterBlock = BlockConfig.newElement(this, "SmokeEmitter", new short[]{55});
        BlockConfig.setBasicInfo(emitterBlock, "desc", 100, 0.1F, true, true, 333);
        log("Block config loaded");
        BlockConfig.add(emitterBlock);

    }

    @Override
    public void onEnable() {
        inst = this;
        StarLoader.registerListener(ManagerContainerRegisterEvent.class, this, new Listener<ManagerContainerRegisterEvent>() {
            @Override
            public void onEvent(ManagerContainerRegisterEvent event) {
                log("Manager container event fired for %s, onServer=%s", event.getContainer().getSegmentController().dbId, String.valueOf(event.getContainer().isOnServer()));
                event.addModMCModule(new SmokeEmitterModule(event.getSegmentController(), event.getContainer(), SmokeEmitterMod.this, emitterBlock.id));
            }
        });
        StarLoader.registerListener(SegmentPieceActivateEvent.class, this, new Listener<SegmentPieceActivateEvent>() {
            @Override
            public void onEvent(SegmentPieceActivateEvent event) {
                if(event.getSegmentPiece().getType() == emitterBlock.id){
                    ManagedUsableSegmentController<?> msuc = (ManagedUsableSegmentController<?>) event.getSegmentPiece().getSegmentController();
                    ModManagerContainerModule modMCModule = msuc.getManagerContainer().getModMCModule(emitterBlock.id);
                    ((SmokeEmitterModule) modMCModule).red = !((SmokeEmitterModule) modMCModule).red;
                    modMCModule.syncToServer();
                }
            }
        });

        log("Enabled");
    }
}
