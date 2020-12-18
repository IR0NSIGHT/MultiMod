package me.jakev.extraeffects.particleblock;

import api.common.GameClient;
import me.jakev.extraeffects.SpriteList;
import org.schema.game.client.controller.PlayerGameDropDownInput;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.gui.GUIInputPanel;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalArea;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalButtonTablePane;
import org.schema.schine.input.InputState;

import javax.vecmath.Vector4f;

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

    public static final GUIActivationCallback defaultAct = new GUIActivationCallback() {
        @Override
        public boolean isVisible(InputState inputState) {
            return true;
        }

        @Override
        public boolean isActive(InputState inputState) {
            return true;
        }
    };

    @Override
    public void onInit() {
        super.onInit();


        //===================================== [Text Panel A: Particle Sprite/Count] ========================================
        ((GUIDialogWindow) background).getMainContentPane().setTextBoxHeightLast(100);
        GUIHorizontalButtonTablePane spriteNamePane = new GUIHorizontalButtonTablePane(getState(), 2, 3,
                ((GUIDialogWindow) background).getMainContentPane().getContent(0));
        spriteNamePane.onInit();

        spriteNamePane.addText(0, 0, new Object() {
            @Override
            public String toString() {
                return "Particle Name: " + module.particleSprite;
            }
        });
        spriteNamePane.addText(0, 1, new Object() {
            @Override
            public String toString() {
                return "Particle Count: " + module.particleCount;
            }
        });
        spriteNamePane.addText(0, 2, new Object() {
            @Override
            public String toString() {
                return "Particle Lifetime (MS): " + module.lifetimeMs;
            }
        });
        spriteNamePane.addButton(1, 2, "Particle Lifetime", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                    PlayerFloatInput playerFloatInput = new PlayerFloatInput(GameClient.getClientState(),
                            "Particle Lifetime", "Input particle lifetime in milliseconds", String.valueOf(module.lifetimeMs),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    module.lifetimeMs = (int) f;
                                }
                            });
                    playerFloatInput.activate();
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, defaultAct);
        spriteNamePane.addButton(1, 1, "Particle Count", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                    PlayerFloatInput playerFloatInput = new PlayerFloatInput(GameClient.getClientState(),
                            "Input particle count", "bbb", String.valueOf(module.particleCount),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    module.particleCount = (int) f;
                                }
                            });
                    playerFloatInput.activate();
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, defaultAct);

        spriteNamePane.addButton(1, 0, "Select Sprite", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (!mouseEvent.pressedLeftMouse()) {
                    return;
                }
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
                    }

                    @Override
                    public boolean isOccluded() {
                        return false;
                    }

                    @Override
                    public void pressedOK(GUIListElement current) {
                        if (current != null) {
                            module.particleSprite = ((SpriteList) current.getContent().getUserPointer()).ordinal();
                        } else {
                            System.err.println("[UPLOAD] dropdown null selected");
                        }
                        deactivate();
                    }
                };
                pp.activate();
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, defaultAct);
        ((GUIDialogWindow) background).getMainContentPane().getContent(0).attach(spriteNamePane);

        ///=============================================================================================================
        ///========================================== [ Size Values ] ====================================================
        ((GUIDialogWindow) background).getMainContentPane().addNewTextBox(50);
        GUIHorizontalButtonTablePane sizePane = new GUIHorizontalButtonTablePane(getState(), 2, 2,
                ((GUIDialogWindow) background).getMainContentPane().getContent(1));
        sizePane.onInit();

        sizePane.addText(0, 0, new Object() {
            @Override
            public String toString() {
                return "Start Size: " + module.startSize;
            }
        });
        sizePane.addText(0, 1, new Object() {
            @Override
            public String toString() {
                return "End Size: " + module.endSize;
            }
        });
        sizePane.addButton(1, 0, "Start Size", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                    PlayerFloatInput playerFloatInput = new PlayerFloatInput(GameClient.getClientState(),
                            "Input start size", "Should be something like '5.25', a decimal", String.valueOf(module.startSize),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    module.startSize = f;
                                }
                            });
                    playerFloatInput.activate();
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, defaultAct);
        sizePane.addButton(1, 1, "End Size", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                    PlayerFloatInput playerFloatInput = new PlayerFloatInput(GameClient.getClientState(),
                            "Input end size", "Should be something like '5.25', a decimal", String.valueOf(module.endSize),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    module.endSize = f;
                                }
                            });
                    playerFloatInput.activate();
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, defaultAct);
        ((GUIDialogWindow) background).getMainContentPane().getContent(1).attach(sizePane);
        ///=============================================================================================================


        ///========================================== [ Misc Info ] ====================================================
        ((GUIDialogWindow) background).getMainContentPane().addNewTextBox(150);
        GUIHorizontalButtonTablePane miscPane = new GUIHorizontalButtonTablePane(getState(), 2, 4,
                ((GUIDialogWindow) background).getMainContentPane().getContent(2));
        miscPane.onInit();
        miscPane.addText(0, 0, new Object() {
            @Override
            public String toString() {
                return "Launch speed: " + module.launchSpeed;
            }
        });
        miscPane.addText(0, 1, new Object() {
            @Override
            public String toString() {
                return "Random Velocity: " + module.randomVelocity;
            }
        });
        miscPane.addText(0, 2, new Object() {
            @Override
            public String toString() {
                return "Speed Dampener: " + module.speedDampener;
            }
        });
        miscPane.addText(0, 3, new Object() {
            @Override
            public String toString() {
                return "Rotation speed: " + module.rotationSpeed;
            }
        });
        miscPane.addButton(1, 0, "Launch speed", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                    PlayerFloatInput playerFloatInput = new PlayerFloatInput(GameClient.getClientState(),
                            "Input Launch speed", "Should be something like '5.25', a decimal", String.valueOf(module.launchSpeed),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    module.launchSpeed = f;
                                }
                            });
                    playerFloatInput.activate();
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, defaultAct);
        miscPane.addButton(1, 1, "Random Velocity", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                    PlayerFloatInput playerFloatInput = new PlayerFloatInput(GameClient.getClientState(),
                            "Input Random velocity", "Should be something like '5.25', a decimal, \nThis controls how much random speed is added to a particle", String.valueOf(module.randomVelocity),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    module.randomVelocity = f;
                                }
                            });
                    playerFloatInput.activate();
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, defaultAct);
        miscPane.addButton(1, 2, "Speed Dampener", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                    PlayerFloatInput playerFloatInput = new PlayerFloatInput(GameClient.getClientState(),
                            "Input Random velocity", "Should be something like '5.25', a decimal, \nVelocity is multiplied by this every tick", String.valueOf(module.speedDampener),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    module.speedDampener = f;
                                }
                            });
                    playerFloatInput.activate();
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, defaultAct);
        miscPane.addButton(1, 3, "Rotation Speed", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                    PlayerFloatInput playerFloatInput = new PlayerFloatInput(GameClient.getClientState(),
                            "Input Random velocity", "Should be something like '5.25', a decimal, \nHow much the particle rotates every tick", String.valueOf(module.rotationSpeed),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    module.rotationSpeed = f;
                                }
                            });
                    playerFloatInput.activate();
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, defaultAct);
        ((GUIDialogWindow) background).getMainContentPane().getContent(2).attach(miscPane);
        ///=============================================================================================================

        ///========================================== [ Color Info ] ====================================================
        ((GUIDialogWindow) background).getMainContentPane().addNewTextBox(52);
        GUIHorizontalButtonTablePane colorPane = new GUIHorizontalButtonTablePane(getState(), 2, 2,
                ((GUIDialogWindow) background).getMainContentPane().getContent(3));
        colorPane.onInit();
        colorPane.addText(0, 0, new Object() {
            @Override
            public String toString() {
                return "Start Color: " + module.startColor;
            }
        });
        colorPane.addText(0, 1, new Object() {
            @Override
            public String toString() {
                return "End Color: " + module.endColor;
            }
        });
        colorPane.addButton(1, 0, "Start Color", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                    PlayerColorInput playerFloatInput = new PlayerColorInput(GameClient.getClientState(),
                            "Input starting colour", "Input a hexadecimal color value, such as 'ff0033ff', \nThe format is RRGGBBAA. \nI will replace with a color wheel later", String.valueOf(module.rotationSpeed),
                            new ColorConsumer() {
                                @Override
                                public void consume(Vector4f color) {
                                    module.startColor = color;
                                }
                            });
                    playerFloatInput.activate();
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, defaultAct);
        colorPane.addButton(1, 1, "End Color", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                    PlayerColorInput playerFloatInput = new PlayerColorInput(GameClient.getClientState(),
                            "Input starting colour", "Input a color value in format: '255, 255, 0, 255'\bThe format is R,G,B,A. 255 is the max value, 0 is the min", String.valueOf(module.rotationSpeed),
                            new ColorConsumer() {
                                @Override
                                public void consume(Vector4f color) {
                                    module.endColor = color;
                                }
                            });
                    playerFloatInput.activate();
                }
            }

            @Override
            public boolean isOccluded() {
                return false;
            }
        }, defaultAct);
        ((GUIDialogWindow) background).getMainContentPane().getContent(3).attach(colorPane);
        ///=============================================================================================================


        ///=============================================================================================================

        ///========================================== [ Misc Info ] ====================================================
        ((GUIDialogWindow) background).getMainContentPane().addNewTextBox(52);

//        ((GUIDialogWindow) background).getMainContentPane().getContent(1).attach(p);

//        ((GUIDialogWindow) background).getMainContentPane().setTextBoxHeightLast(56);
//        ((GUIDialogWindow) background).getMainContentPane().addNewTextBox(66);
//
//        ((GUIDialogWindow) background).getMainContentPane().addNewTextBox(200);


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
