package me.jakev.extraeffects.particleblock;

import org.schema.game.client.controller.PlayerInput;
import org.schema.game.client.data.GameClientState;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.gui.GUIElement;

/**
 * Created by Jake on 12/14/2020.
 * <insert description here>
 */
public class GUIParticleEditorInput extends PlayerInput {
    private GUIParticleEmitterPanel panel;
    public GUIParticleEditorInput(GameClientState client, ParticleSpawnerMCModule module) {
        super(client);
        this.panel = new GUIParticleEmitterPanel(client,700,500, this, module );
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
