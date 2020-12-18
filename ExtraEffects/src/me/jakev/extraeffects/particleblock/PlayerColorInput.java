package me.jakev.extraeffects.particleblock;

import org.schema.game.client.controller.PlayerGameTextInput;
import org.schema.game.client.data.GameClientState;
import org.schema.schine.common.InputChecker;
import org.schema.schine.common.TextCallback;
import org.schema.schine.graphicsengine.core.settings.PrefixNotFoundException;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;

import javax.vecmath.Vector4f;

/**
 * Created by Jake on 12/17/2020.
 * <insert description here>
 */
public class PlayerColorInput extends PlayerGameTextInput {
    private final ColorConsumer onInput;

    public PlayerColorInput(GameClientState var1, Object var2, Object var3, String var4, ColorConsumer onInput) {
        super("FLOAT_INPUT", var1, 430, 100, 100, var2, var3, var4);
        this.onInput = onInput;
        this.getInputPanel().onInit();
        ((GUIDialogWindow) this.getInputPanel().background).getMainContentPane().setTextBoxHeightLast(80);
        this.setInputChecker(new InputChecker() {
            public boolean check(String var1, TextCallback var2) {
                Vector4f color = decodeColor(var1);
                if (color == null) {
                    var2.onFailedTextCheck("Whatever you just entered is NOT a number! Try harder next time.");
                    return false;
                }
                return true;
            }
        });
    }

    public String[] getCommandPrefixes() {
        return null;
    }

    public String handleAutoComplete(String var1, TextCallback var2, String var3) throws PrefixNotFoundException {
        return null;
    }

    public void onDeactivate() {
    }

    public void onFailedTextCheck(String var1) {
        this.setErrorMessage(var1);
    }

    private static Vector4f decodeColor(String s) {
        String c = s.replace(" ", "");
        try {
            String[] arr = c.split(",");
            float r = Integer.parseInt(arr[0]) / 255F;
            float g = Integer.parseInt(arr[1]) / 255F;
            float b = Integer.parseInt(arr[2]) / 255F;
            float a = Integer.parseInt(arr[3]) / 255F;
            return new Vector4f(r, g, b, a);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean onInput(String var1) {
        try {
            onInput.consume(decodeColor(var1));
            return true;
        } catch (NumberFormatException var2) {
            var2.printStackTrace();
            return false;
        }
    }
}
