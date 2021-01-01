package me.jakev.particleemitter.gui;

import org.schema.game.client.controller.PlayerGameTextInput;
import org.schema.game.client.data.GameClientState;
import org.schema.schine.common.InputChecker;
import org.schema.schine.common.TextCallback;
import org.schema.schine.graphicsengine.core.settings.PrefixNotFoundException;
import org.schema.schine.graphicsengine.forms.gui.newgui.GUIDialogWindow;

public class PlayerFloatInput extends PlayerGameTextInput {
    private final FloatConsumer onInput;

    public PlayerFloatInput(GameClientState var1, Object var2, Object var3, String var4, FloatConsumer onInput) {
        super("FLOAT_INPUT", var1, 430, 100, 100, var2, var3, var4);
        this.onInput = onInput;
        this.getInputPanel().onInit();
        ((GUIDialogWindow)this.getInputPanel().background).getMainContentPane().setTextBoxHeightLast(80);
        this.setInputChecker(new InputChecker() {
            public boolean check(String var1, TextCallback var2) {
                try {
                    Float.parseFloat(var1);
                    return true;
                } catch (NumberFormatException var3) {
                    var2.onFailedTextCheck("Whatever you just entered is NOT a decimal! Try harder next time.");
                    return false;
                }
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

    public boolean onInput(String var1) {
        try {
            onInput.onInput(Float.parseFloat(var1));
            return true;
        } catch (NumberFormatException var2) {
            var2.printStackTrace();
            return false;
        }
    }
}
