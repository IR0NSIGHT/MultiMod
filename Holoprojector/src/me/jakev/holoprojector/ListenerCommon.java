package me.jakev.holoprojector;

import api.listener.Listener;
import api.listener.events.block.SegmentPieceActivateByPlayer;
import api.listener.events.block.SegmentPieceAddByMetadataEvent;
import api.listener.events.block.SegmentPieceAddEvent;
import api.listener.events.block.SegmentPieceRemoveEvent;
import api.mod.StarLoader;
import org.schema.game.client.controller.PlayerTextAreaInput;
import org.schema.game.client.controller.element.world.ClientSegmentProvider;
import org.schema.game.client.controller.manager.ingame.PlayerInteractionControlManager;
import org.schema.game.common.controller.SendableSegmentProvider;
import org.schema.game.common.data.SegmentPiece;
import org.schema.game.common.data.SendableGameState;
import org.schema.game.common.data.element.ElementCollection;
import org.schema.game.common.data.world.Segment;
import org.schema.game.network.objects.remote.RemoteTextBlockPair;
import org.schema.game.network.objects.remote.TextBlockPair;
import org.schema.schine.common.TextCallback;
import org.schema.schine.graphicsengine.core.settings.PrefixNotFoundException;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;

/**
 * Created by Jake on 11/20/2020.
 * <insert description here>
 */
public class ListenerCommon {
    public static void init(HoloProjectorMod mod) {
        StarLoader.registerListener(SegmentPieceActivateByPlayer.class, new Listener<SegmentPieceActivateByPlayer>() {
            @Override
            public void onEvent(SegmentPieceActivateByPlayer event) {
                final SegmentPiece piece = event.getSegmentPiece();
                if (piece.getType() == HoloProjectorMod.holoProjector.id) {
                    final PlayerInteractionControlManager cm = event.getControlManager();

                    String text = piece.getSegment().getSegmentController().getTextMap().get(ElementCollection.getIndex4(piece.getAbsoluteIndex(), piece.getOrientation()));

                    if (text == null) {
                        text = "";
                    }

                    final PlayerTextAreaInput t = new PlayerTextAreaInput("EDIT_DISPLAY_BLOCK_POPUP", cm.getState(), 400, 300, SendableGameState.TEXT_BLOCK_LIMIT, SendableGameState.TEXT_BLOCK_LINE_LIMIT + 1, "Edit Holoprojector",
                            "",
                            text, FontLibrary.FontSize.SMALL) {
                        @Override
                        public void onDeactivate() {
                            cm.suspend(false);
                        }

                        @Override
                        public String[] getCommandPrefixes() {
                            return null;
                        }

                        @Override
                        public boolean onInput(String entry) {
                            SendableSegmentProvider ss = ((ClientSegmentProvider) piece.getSegment().getSegmentController().getSegmentProvider()).getSendableSegmentProvider();

                            TextBlockPair f = new TextBlockPair();

                            f.block = ElementCollection.getIndex4(piece.getAbsoluteIndex(), piece.getOrientation());
                            f.text = entry;
                            System.err.println("[CLIENT]Text entry:\n\"" + f.text + "\"");
                            ss.getNetworkObject().textBlockResponsesAndChangeRequests.add(new RemoteTextBlockPair(f, false));

                            return true;
                        }

                        @Override
                        public String handleAutoComplete(String s, TextCallback callback, String prefix) throws PrefixNotFoundException {
                            return null;
                        }

                        @Override
                        public boolean isOccluded() {
                            return false;
                        }


                        @Override
                        public void onFailedTextCheck(String msg) {
                        }


                    };

                    t.getTextInput().setAllowEmptyEntry(true);
                    t.getInputPanel().onInit();
                    t.activate();
                }
            }
        }, mod);

        //Register text blocks on block place
        StarLoader.registerListener(SegmentPieceAddEvent.class, new Listener<SegmentPieceAddEvent>() {
            @Override
            public void onEvent(SegmentPieceAddEvent event) {
                if (event.getNewType() == HoloProjectorMod.holoProjector.id) {
                    long indexAndOrientation = ElementCollection.getIndex4(event.getAbsIndex(), event.getOrientation());
                    event.getSegmentController().getTextBlocks().add(indexAndOrientation);
                }
            }
        }, mod);

        //Remove text block on block remove
        StarLoader.registerListener(SegmentPieceRemoveEvent.class, new Listener<SegmentPieceRemoveEvent>() {
            @Override
            public void onEvent(SegmentPieceRemoveEvent event) {
                if(event.getType() == HoloProjectorMod.holoProjector.id) {
                    Segment segment = event.getSegment();
                    long absoluteIndex = segment.getAbsoluteIndex(event.getX(), event.getY(), event.getZ());
                    long indexAndOrientation = ElementCollection.getIndex4(absoluteIndex, event.getOrientation());
                    event.getSegment().getSegmentController().getTextBlocks().remove(indexAndOrientation);
                    event.getSegment().getSegmentController().getTextMap().remove(indexAndOrientation);
                }
            }
        }, mod);

        StarLoader.registerListener(SegmentPieceAddByMetadataEvent.class, new Listener<SegmentPieceAddByMetadataEvent>() {
            @Override
            public void onEvent(SegmentPieceAddByMetadataEvent event) {
                if(event.getType() == HoloProjectorMod.holoProjector.id) {
                    event.getSegment().getSegmentController().getTextBlocks().add(event.getIndexAndOrientation());
                }
            }
        }, mod);
    }
}
