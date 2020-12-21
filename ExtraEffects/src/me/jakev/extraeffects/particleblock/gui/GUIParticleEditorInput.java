package me.jakev.extraeffects.particleblock.gui;

import api.common.GameCommon;
import me.jakev.extraeffects.ExtraEffects;
import me.jakev.extraeffects.particleblock.ParticleSpawnerMCModule;
import org.schema.game.client.controller.PlayerInput;
import org.schema.game.client.data.GameClientState;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;

/**
 * Created by Jake on 12/14/2020.
 * <insert description here>
 */
public class GUIParticleEditorInput extends PlayerInput {
    private GUIParticleEmitterPanel panel;
    public GUIParticleEditorInput(GameClientState client, ParticleSpawnerMCModule module, long index) {
        super(client);
        if(module.isOnSinglePlayer()){
            ManagedUsableSegmentController<?> ship = (ManagedUsableSegmentController<?>)
                    GameCommon.getGameObject(module.getManagerContainer().getSegmentController().getId());
            ParticleSpawnerMCModule realModule = (ParticleSpawnerMCModule) ship.getManagerContainer().getModMCModule(ExtraEffects.emitterId);
            assert realModule != module : "Uh oh";
            this.panel = new GUIParticleEmitterPanel(client,700,500, this, realModule, realModule.blockData.get(index), index);
        }else{
            this.panel = new GUIParticleEmitterPanel(client,700,500, this, module, module.blockData.get(index), index);
        }
        panel.setCallback(this);
    }

    @Override
    public void onDeactivate() {
        panel.setActive(false);
    }

    @Override
    public void activate() {
        super.activate();
        panel.setActive(true);
    }

    @Override
    public boolean isOccluded() {
        return false;
    }

    @Override
    public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
        getState().getController().queueUIAudio("0022_menu_ui - enter");
        super.callback(guiElement, mouseEvent);
    }

    @Override
    public void handleMouseEvent(MouseEvent mouseEvent) {

    }

    @Override
    public GUIElement getInputPanel() {
        return panel;
    }
    public void setErrorMessage(String msg) {
        System.err.println(msg);
    }
}
