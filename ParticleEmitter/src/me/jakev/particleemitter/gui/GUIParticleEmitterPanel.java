package me.jakev.particleemitter.gui;

import api.common.GameClient;
import api.network.packets.PacketUtil;
import me.jakev.particleemitter.ParticleBlockConfig;
import me.jakev.particleemitter.ParticleSpawnerMCModule;
import me.jakev.particleemitter.SpriteList;
import org.schema.game.client.controller.PlayerGameDropDownInput;
import org.schema.game.client.data.GameClientState;
import org.schema.game.client.view.gui.GUIInputPanel;
import org.schema.game.common.controller.ManagedUsableSegmentController;
import org.schema.game.common.data.SegmentPiece;
import org.schema.schine.graphicsengine.core.MouseEvent;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;
import org.schema.schine.graphicsengine.forms.gui.*;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalArea;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIHorizontalButtonTablePane;
import org.schema.schine.input.InputState;
import me.jakev.particleemitter.network.PacketCSUpdateParticleData;

import javax.vecmath.Vector4f;

/**
 * Created by Jake on 12/14/2020.
 * <insert description here>
 */
public class GUIParticleEmitterPanel extends GUIInputPanel {
    GUIParticleEditorInput panel;
    private final ParticleSpawnerMCModule module;
    private final ParticleBlockConfig block;
    private final long blockIndex;
    private SegmentPiece openedOn;

    public GUIParticleEmitterPanel(InputState state, int width, int height, final GUIParticleEditorInput panel, ParticleSpawnerMCModule module, ParticleBlockConfig block, long blockIndex) {
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
        this.block = block;
        this.blockIndex = blockIndex;

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
    public void updateToServer(){
        if(!module.isOnSinglePlayer()) {
            PacketCSUpdateParticleData packet = new PacketCSUpdateParticleData((ManagedUsableSegmentController<?>) module.getManagerContainer().getSegmentController(), blockIndex);
            PacketUtil.sendPacketToServer(packet);
        }
    }

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
                return "Particle Name: " + block.particleSprite;
            }
        });
        spriteNamePane.addText(0, 1, new Object() {
            @Override
            public String toString() {
                return "Particle Count: " + block.particleCount;
            }
        });
        spriteNamePane.addText(0, 2, new Object() {
            @Override
            public String toString() {
                return "Particle Lifetime (MS): " + block.lifetimeMs;
            }
        });
        spriteNamePane.addButton(1, 2, "Particle Lifetime", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                    PlayerFloatInput playerFloatInput = new PlayerFloatInput(GameClient.getClientState(),
                            "Particle Lifetime", "Input particle lifetime in milliseconds", String.valueOf(block.lifetimeMs),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    block.lifetimeMs = (int) f;
                                    updateToServer();
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
                            "Input particle count", "Particle count", String.valueOf(block.particleCount),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    block.particleCount = (int) f;
                                    updateToServer();
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
                            block.particleSprite = ((SpriteList) current.getContent().getUserPointer()).ordinal();
                            updateToServer();
                        } else {
                            System.err.println("[] dropdown null selected");
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
                return "Start Size: " + block.startSize;
            }
        });
        sizePane.addText(0, 1, new Object() {
            @Override
            public String toString() {
                return "End Size: " + block.endSize;
            }
        });
        sizePane.addButton(1, 0, "Start Size", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                    PlayerFloatInput playerFloatInput = new PlayerFloatInput(GameClient.getClientState(),
                            "Input start size", "Should be something like '5.25', a decimal", String.valueOf(block.startSize),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    block.startSize = f;
                                    updateToServer();
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
                            "Input end size", "Should be something like '5.25', a decimal", String.valueOf(block.endSize),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    block.endSize = f;
                                    updateToServer();
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
                return "Launch speed: " + block.launchSpeed;
            }
        });
        miscPane.addText(0, 1, new Object() {
            @Override
            public String toString() {
                return "Random Velocity: " + block.randomVelocity;
            }
        });
        miscPane.addText(0, 2, new Object() {
            @Override
            public String toString() {
                return "Speed Dampener: " + block.speedDampener;
            }
        });
        miscPane.addText(0, 3, new Object() {
            @Override
            public String toString() {
                return "Rotation speed: " + block.rotationSpeed;
            }
        });
        miscPane.addButton(1, 0, "Launch speed", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                    PlayerFloatInput playerFloatInput = new PlayerFloatInput(GameClient.getClientState(),
                            "Input Launch speed", "Should be something like '5.25', a decimal", String.valueOf(block.launchSpeed),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    block.launchSpeed = f;
                                    updateToServer();
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
                            "Input Random velocity", "Should be something like '5.25', a decimal, \nThis controls how much random speed is added to a particle", String.valueOf(block.randomVelocity),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    block.randomVelocity = f;
                                    updateToServer();
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
                            "Input Random velocity", "Should be something like '5.25', a decimal, \nVelocity is multiplied by this every tick", String.valueOf(block.speedDampener),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    block.speedDampener = f;
                                    updateToServer();
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
                            "Input Random velocity", "Should be something like '5.25', a decimal, \nHow much the particle rotates every tick", String.valueOf(block.rotationSpeed),
                            new FloatConsumer() {
                                @Override
                                public void onInput(float f) {
                                    block.rotationSpeed = f;
                                    updateToServer();
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
                return "Start Color: " + block.startColor;
            }
        });
        colorPane.addText(0, 1, new Object() {
            @Override
            public String toString() {
                return "End Color: " + block.endColor;
            }
        });
        colorPane.addButton(1, 0, "Start Color", GUIHorizontalArea.HButtonColor.BLUE, new GUICallback() {
            @Override
            public void callback(GUIElement guiElement, MouseEvent mouseEvent) {
                if (mouseEvent.pressedLeftMouse()) {
                    PlayerColorInput playerFloatInput = new PlayerColorInput(GameClient.getClientState(),
                            "Input starting colour", "Input a hexadecimal color value, such as 'ff0033ff', \nThe format is RRGGBBAA. \nI will replace with a color wheel later",
                            ((int) (block.endColor.x * 255)) + ", " + ((int) (block.endColor.y * 255)) + ", " + ((int) (block.endColor.z * 255)) + ", " + ((int) (block.endColor.w * 255)),
                            new ColorConsumer() {
                                @Override
                                public void consume(Vector4f color) {
                                    block.startColor = color;
                                    updateToServer();
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
                            "Input starting colour", "Input a color value in format: '255, 255, 0, 255'\bThe format is R,G,B,A. 255 is the max value, 0 is the min",
                            ((int) (block.endColor.x * 255)) + ", " + ((int) (block.endColor.y * 255)) + ", " + ((int) (block.endColor.z * 255)) + ", " + ((int) (block.endColor.w * 255)),
                            new ColorConsumer() {
                                @Override
                                public void consume(Vector4f color) {
                                    block.endColor = color;
                                    updateToServer();
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
