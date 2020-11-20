package me.jakev.holoprojector;

import api.listener.fastevents.TextBoxDrawListener;
import org.schema.game.client.view.SegmentDrawer;
import org.schema.game.client.view.textbox.AbstractTextBox;
import org.schema.schine.graphicsengine.forms.font.FontLibrary;


/**
 * Created by Jake on 11/20/2020.
 * <insert description here>
 */
public class TextDrawListener implements TextBoxDrawListener {
    @Override
    public void draw(SegmentDrawer.TextBoxSeg.TextBoxElement textBoxElement, AbstractTextBox box) {

        textBoxElement.text.setFont(FontLibrary.getBlenderProHeavy31());
        if(textBoxElement.realText.contains("monke")) {
            box.getBg().setSprite(HoloProjectorMod.monke);
        }else{
            box.getBg().setSprite(HoloProjectorMod.nothing);
        }
    }

    @Override
    public void preDrawBackground(SegmentDrawer.TextBoxSeg textBoxSeg, AbstractTextBox abstractTextBox) {
    }
}
