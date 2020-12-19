package me.jakev.extraeffects.listeners;

import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.register.ManagerContainerRegisterEvent;
import api.mod.StarLoader;
import api.mod.StarMod;
import api.utils.game.module.ModManagerContainerModule;
import me.jakev.extraeffects.particleblock.GUIParticleEditorInput;
import me.jakev.extraeffects.particleblock.ParticleSpawnerMCModule;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.controller.Ship;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.data.element.ElementKeyMap;

/**
 * Created by Jake on 12/9/2020.
 * <insert description here>
 */
public class EEParticleEmitterListener {
    public static short block = ElementKeyMap.CRYS_NOCX;

    public static void init(StarMod mod){
        StarLoader.registerListener(ManagerContainerRegisterEvent.class, new Listener<ManagerContainerRegisterEvent>() {
            @Override
            public void onEvent(ManagerContainerRegisterEvent event) {
                if(event.getContainer().getSegmentController() instanceof Ship) {
                    event.addSimpleMCModule(block, new ParticleSpawnerMCModule(event.getSegmentController(), event.getContainer(), mod, block));
                }
            }
        }, mod);
        StarLoader.registerListener(SegmentPieceActivateByPlayer.class, new Listener<SegmentPieceActivateByPlayer>() {
            @Override
            public void onEvent(SegmentPieceActivateByPlayer event) {
                if(event.getSegmentPiece().getType() == block) {
                    ManagerContainer<?> container = ((ManagedUsableSegmentController<?>) event.getSegmentPiece().getSegmentController()).getManagerContainer();
                    ModManagerContainerModule modMCModule = container.getModMCModule(block);
                    GUIParticleEditorInput ri = new GUIParticleEditorInput(event.getControlManager().getState(), (ParticleSpawnerMCModule) modMCModule);
                    ri.activate();
                }
            }
        }, mod);
    }
}
