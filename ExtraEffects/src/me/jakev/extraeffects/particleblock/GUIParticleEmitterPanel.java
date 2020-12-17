package me.jakev.extraeffects.particleblock;

import me.jakev.extraeffects.SpriteList;
import org.schema.game.client.controller.PlayerGameDropDownInput;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.gui.GUIInputPanel;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalButtonTablePane;
import org.schema.schine.input.InputState;

/**
 * Created by Jake on 12/14/2020.
 * <insert description here>
 */
public class GUIParticleEmitterPanel extends GUIInputPanel {
    GUIParticleEditorInput panel;
    private final ParticleSpawnerMCModule module;
    private SegmentPiece openedOn;

    public GUIParticleEmitterPanel(InputState state, int width, int height, final GUIParticleEditorInput panel, ParticleSpawnerMCModule module) {
        super("GUITPPanelNew",
                state,
                width,
                height,
                new GUICallback() {
                    @Override
                    public void callback(GUIElement guiElement, MouseEvent mouseEvent) {

                    }

                    @Override
                    public boolean isOccluded() {
                        return false;
                    }
                },
                "Particle Editor",
                "");


        this.panel = panel;
        this.module = module;
        setOkButton(false);

    }

    @Override
    public void onInit() {
        super.onInit();
        //===================================== [Text Panel A: Particle Sprite/Count] ========================================
        GUIHorizontalButtonTablePane spriteNamePane = new GUIHorizontalButtonTablePane(getState(),2, 2,
                ((GUIDialogWindow) background).getMainContentPane().getContent(0));
        spriteNamePane.onInit();

        spriteNamePane.addText(0,0, new Object(){
            @Override
            public String toString() {
                return "Particle Name: " + module.particleSprite;
            }
        });
        spriteNamePane.addText(0,1, new Object(){
            @Override
            public String toString() {
                return "Particle Count: " + module.particleCount;
            }
        });
        GUIAncor[] bb = new GUIAncor[SpriteList.values().length];
        for (int i = 0; i < bb.length; i++) {
            bb[i] = new GUIAncor(getState(), 300, 24);
            SpriteList sprite = SpriteList.values()[i];
            bb[i].setUserPointer(sprite);
            GUITextOverlay t = new GUITextOverlay(300, 24, FontLibrary.getBoldArial12White(), getState());
            t.setTextSimple(sprite.getName());
            t.setPos(4, 4, 0);
            bb[i].attach(t);
        }
        PlayerGameDropDownInput pp = new PlayerGameDropDownInput("ExtraEffects_SpriteList", (GameClientState) getState(), "Select Sprite", 24, "the sprite", bb) {

            @Override
            public void onDeactivate() {
//                getPlayerCatalogControlManager().suspend(false);
            }

            @Override
            public boolean isOccluded() {
                return false;
            }

            @Override
            public void pressedOK(GUIListElement current) {
                if (current != null) {
                    module.particleSprite = ((SpriteList) current.getContent().getUserPointer()).getName();

                } else {
                    System.err.println("[UPLOAD] dropdown null selected");
                }
                deactivate();
            }



        };
        pp.activate();

        ((GUIDialogWindow) background).getMainContentPane().getContent(0).attach(spriteNamePane);

        ///=============================================================================================================

//        ((GUIDialogWindow) background).getMainContentPane().getContent(1).attach(p);

        ((GUIDialogWindow) background).getMainContentPane().setTextBoxHeightLast(56);
        ((GUIDialogWindow) background).getMainContentPane().addNewTextBox(66);

        ((GUIDialogWindow) background).getMainContentPane().addNewTextBox(200);


    }

    private boolean active = false;

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean isActive() {
        return active;
//        return (getState().getController().getPlayerInputs().isEmpty() || getState().getController().getPlayerInputs().get(getState().getController().getPlayerInputs().size() - 1).getInputPanel() == this) && super.isActive();
    }

    public SegmentPiece getOpenedOn() {
        return openedOn;
    }

    public void setOpenedOn(SegmentPiece openedOn) {
        this.openedOn = openedOn;
    }

}
