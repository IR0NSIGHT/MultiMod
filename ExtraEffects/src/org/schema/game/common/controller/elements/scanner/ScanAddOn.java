//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.schema.game.common.controller.elements.scanner;

import api.listener.events.entity.EntityScanEvent;
import api.listener.events.register.RegisterAddonsEvent;
import api.mod.StarLoader;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.schema.common.util.StringTools;
import org.schema.game.client.data.GameStateInterface;
import org.schema.game.common.controller.ElementCountMap;
import org.schema.game.common.controller.elements.ManagerContainer;
import org.schema.game.common.controller.elements.RecharchableActivatableDurationSingleModule;
import org.schema.game.common.controller.elements.VoidElementManager;
import org.schema.game.common.data.ManagedSegmentController;
import org.schema.game.common.data.blockeffects.config.StatusEffectType;
import org.schema.game.common.data.element.ElementKeyMap;
import org.schema.game.common.data.player.AbstractOwnerState;
import org.schema.game.common.data.player.PlayerState;
import org.schema.game.common.data.player.inventory.Inventory;
import org.schema.game.common.data.player.playermessage.ServerPlayerMessager;
import org.schema.game.network.objects.remote.RemoteValueUpdate;
import org.schema.game.network.objects.valueUpdate.NTValueUpdateInterface;
import org.schema.game.network.objects.valueUpdate.ServerValueRequestUpdate.Type;
import org.schema.game.network.objects.valueUpdate.ValueUpdate.ValTypes;
import org.schema.game.server.data.GameServerState;
import org.schema.schine.common.language.Lng;
import org.schema.schine.graphicsengine.core.Timer;
import org.schema.schine.network.objects.Sendable;

public class ScanAddOn extends RecharchableActivatableDurationSingleModule {
    private static final long CARGO_SCAN_TIME = 15000L;
    private int currentCargoScanning;
    private long currentCargoScanningStart;
    private int lastScanned;

    public ScanAddOn(ManagerContainer<?> man) {
        super(man);
        RegisterAddonsEvent event = new RegisterAddonsEvent(man);
        StarLoader.fireEvent(RegisterAddonsEvent.class, event, this.isOnServer());
    }

    public int getDistance() {
        return (int)this.getConfigManager().apply(StatusEffectType.SCAN_LONG_RANGE_DISTANCE, ScannerElementManager.DEFAULT_SCAN_DISTANCE);
    }

    public void sendChargeUpdate() {
        if (this.isOnServer()) {
            ScanAddOnChargeValueUpdate var1;
            (var1 = new ScanAddOnChargeValueUpdate()).setServer(((ManagedSegmentController)this.getSegmentController()).getManagerContainer(), this.getUsableId());

            assert var1.getType() == ValTypes.SCAN_CHARGE_REACTOR;

            ((NTValueUpdateInterface)this.getSegmentController().getNetworkObject()).getValueUpdateBuffer().add(new RemoteValueUpdate(var1, this.getSegmentController().isOnServer()));
        }

    }

    public boolean isDischargedOnHit() {
        return true;
    }

    public void onChargedFullyNotAutocharged() {
        this.getSegmentController().popupOwnClientMessage(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_SCANNER_SCANADDON_0, 1);
    }

    public float getChargeRateFull() {
        float var1 = VoidElementManager.SCAN_CHARGE_NEEDED;
        return this.getConfigManager().apply(StatusEffectType.SCAN_CHARGE_TIME, var1);
    }

    public boolean canExecute() {
        return true;
    }

    public double getPowerConsumedPerSecondResting() {
        float var1 = VoidElementManager.SCAN_CONSUMPTION_RESTING + this.getMassWithDocks() * VoidElementManager.SCAN_CONSUMPTION_RESTING_ADDED_BY_MASS;
        double var2 = (double)this.getConfigManager().apply(StatusEffectType.SCAN_POWER_TOPOFF_RATE, var1);
        if (this.isActive()) {
            return this.getConfigManager().apply(StatusEffectType.SCAN_ACTIVE_RESTING_POWER_CONS, false) ? this.getConfigManager().apply(StatusEffectType.SCAN_ACTIVE_RESTING_POWER_CONS_MULT, this.getPowerConsumedPerSecondCharging()) : var2;
        } else {
            return this.getConfigManager().apply(StatusEffectType.SCAN_INACTIVE_RESTING_POWER_CONS, false) ? this.getConfigManager().apply(StatusEffectType.SCAN_INACTIVE_RESTING_POWER_CONS_MULT, this.getPowerConsumedPerSecondCharging()) : var2;
        }
    }

    public double getPowerConsumedPerSecondCharging() {
        float var1 = VoidElementManager.SCAN_CONSUMPTION_CHARGING + this.getMassWithDocks() * VoidElementManager.SCAN_CONSUMPTION_CHARGING_ADDED_BY_MASS;
        return (double)this.getConfigManager().apply(StatusEffectType.SCAN_POWER_CHARGE_RATE, var1);
    }

    public boolean isAutoCharging() {
        return true;
    }

    public boolean isAutoChargeToggable() {
        return true;
    }

    public long getUsableId() {
        return -9223372036854775801L;
    }

    public void chargingMessage() {
        this.getSegmentController().popupOwnClientMessage(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_SCANNER_SCANADDON_1, 1);
    }

    public void onCooldown(long var1) {
        this.getSegmentController().popupOwnClientMessage(StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_SCANNER_SCANADDON_2, new Object[]{var1}), 3);
    }

    public void onUnpowered() {
        this.getSegmentController().popupOwnClientMessage(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_SCANNER_SCANADDON_3, 3);
    }

    public String getTagId() {
        return "RSCN";
    }

    public int updatePrio() {
        return 1;
    }

    public PowerConsumerCategory getPowerConsumerCategory() {
        return PowerConsumerCategory.SCANNER;
    }

    public boolean isPlayerUsable() {
        return (((GameStateInterface)this.getSegmentController().getState()).getGameState().isModuleEnabledByDefault(this.getUsableId()) || this.getConfigManager().apply(StatusEffectType.SCAN_SHORT_RANGE_SCANNER_ENABLE, false)) && super.isPlayerUsable();
    }

    public String getWeaponRowName() {
        return Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_SCANNER_SCANADDON_4;
    }

    public short getWeaponRowIcon() {
        return 654;
    }

    public boolean isPowerConsumerActive() {
        return true;
    }

    public float getDuration() {
        return this.getConfigManager().apply(StatusEffectType.SCAN_USAGE_TIME, VoidElementManager.SCAN_DURATION_BASIC);
    }

    public float getActiveStrength() {
        return this.isActive() ? this.getConfigManager().apply(StatusEffectType.SCAN_STRENGTH, VoidElementManager.SCAN_STRENGTH_BASIC) : 0.0F;
    }

    public boolean executeModule() {
        boolean success = super.executeModule();
        AbstractOwnerState ownerState = this.getSegmentController().getOwnerState();
        EntityScanEvent event = new EntityScanEvent(this, success, ownerState, this.getSegmentController());
        StarLoader.fireEvent(EntityScanEvent.class, event, this.isOnServer());
        return success;
    }

    public void update(Timer var1) {
        super.update(var1);
        if (this.isActive()) {
            AbstractOwnerState var2;
            if (this.getConfigManager().apply(StatusEffectType.CARGO_SCANNER, false) && (var2 = this.getSegmentController().getOwnerState()) != null && var2 instanceof PlayerState) {
                PlayerState var11 = (PlayerState)var2;
                Sendable var3;
                if ((var3 = (Sendable)this.getState().getLocalAndRemoteObjectContainer().getLocalObjects().get(var11.getSelectedEntityId())) != null && var3 instanceof ManagedSegmentController) {
                    ManagerContainer var12 = ((ManagedSegmentController)var3).getManagerContainer();
                    long var6 = var1.currentTime - this.currentCargoScanningStart;
                    if (this.currentCargoScanning != var12.getSegmentController().getId()) {
                        this.currentCargoScanning = var12.getSegmentController().getId();
                        this.currentCargoScanningStart = var1.currentTime;
                        this.lastScanned = 0;
                    } else if (var6 > 15000L) {
                        if (this.lastScanned != var12.getSegmentController().getId()) {
                            this.getSegmentController().popupOwnClientMessage("SCNIDCRGO", StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_SCANNER_SCANADDON_5, new Object[]{var12.getSegmentController()}), 1);
                            if (this.isOnServer()) {
                                ServerPlayerMessager var15 = ((GameServerState)this.getState()).getServerPlayerMessager();
                                StringBuilder var9 = new StringBuilder();
                                ElementCountMap var10 = new ElementCountMap();
                                ObjectIterator var4 = var12.getInventories().values().iterator();

                                while(var4.hasNext()) {
                                    ((Inventory)var4.next()).addToCountMap(var10);
                                }

                                short[] var13;
                                int var5 = (var13 = ElementKeyMap.typeList()).length;

                                for(int var14 = 0; var14 < var5; ++var14) {
                                    short var7 = var13[var14];
                                    if (ElementKeyMap.isValidType(var10.get(var7))) {
                                        var9.append(String.format("%s: %d", ElementKeyMap.getInfo(var7).getName(), var10.get(var7)));
                                        var9.append("\n");
                                    }
                                }

                                var15.send(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_SCANNER_SCANADDON_9, var11.getName(), StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_SCANNER_SCANADDON_6, new Object[]{var12.getSegmentController()}), var9.toString());
                            }

                            this.lastScanned = var12.getSegmentController().getId();
                        }
                    } else {
                        long var8 = 15000L - var6;
                        this.getSegmentController().popupOwnClientMessage("SCNIDCRGO", StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_SCANNER_SCANADDON_7, new Object[]{var12.getSegmentController(), StringTools.formatTimeFromMS(var8)}), 1);
                    }
                }
            }

            this.getSegmentController().popupOwnClientMessage("SCNID", StringTools.format(Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_SCANNER_SCANADDON_8, new Object[]{this.getActiveStrength()}), 1);
        }

    }

    public String getName() {
        return "ScanAddOn";
    }

    protected Type getServerRequestType() {
        return Type.SCAN;
    }

    protected boolean isDeactivatableManually() {
        return true;
    }

    protected void onNoLongerConsumerActiveOrUsable(Timer var1) {
        this.currentCargoScanning = 0;
    }

    public String getExecuteVerb() {
        return Lng.ORG_SCHEMA_GAME_COMMON_CONTROLLER_ELEMENTS_SCANNER_SCANADDON_10;
    }
}
