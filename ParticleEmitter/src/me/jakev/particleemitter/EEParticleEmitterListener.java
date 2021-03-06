package me.jakev.particleemitter;

import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.register.ManagerContainerRegisterEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.game.module.ModManagerContainerModule;
import me.jakev.particleemitter.gui.GUIParticleEditorInput;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.elements.ManagerContainer;

/**
 * Created by Jake on 12/9/2020.
 * <insert description here>
 */
public class EEParticleEmitterListener {
    public static void init(StarMod mod){
        StarLoader.registerListener(ManagerContainerRegisterEvent.class, new Listener<ManagerContainerRegisterEvent>() {
            @Override
            public void onEvent(ManagerContainerRegisterEvent event) {
                if(event.getContainer().getSegmentController() instanceof Ship) {
                    event.addSimpleMCModule(ParticleEmitterMod.emitterId, new ParticleSpawnerMCModule(event.getSegmentController(), event.getContainer(), mod, ParticleEmitterMod.emitterId));
                }
            }
        }, mod);
        StarLoader.registerListener(SegmentPieceActivateByPlayer.class, new Listener<SegmentPieceActivateByPlayer>() {
            @Override
            public void onEvent(SegmentPieceActivateByPlayer event) {
                if(event.getSegmentPiece().getType() == ParticleEmitterMod.emitterId) {
                    ManagerContainer<?> container = ((ManagedUsableSegmentController<?>) event.getSegmentPiece().getSegmentController()).getManagerContainer();
                    ModManagerContainerModule modMCModule = container.getModMCModule(ParticleEmitterMod.emitterId);
                    long index = event.getSegmentPiece().getAbsoluteIndex();
                    GUIParticleEditorInput ri = new GUIParticleEditorInput(event.getControlManager().getState(), (ParticleSpawnerMCModule) modMCModule, index);
                    ri.activate();
                }
            }
        }, mod);
    }
}
