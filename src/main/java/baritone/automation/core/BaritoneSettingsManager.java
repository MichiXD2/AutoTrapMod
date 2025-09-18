package baritone.automation.core;

import baritone.api.IBaritone;
import baritone.api.Settings;

public class BaritoneSettingsManager {
    private final IBaritone baritone;
    private final Settings defaultSettings;

    public BaritoneSettingsManager(IBaritone baritone) {
        this.baritone = baritone;
        this.defaultSettings = new Settings();
        backupDefaultSettings();
    }

    private void backupDefaultSettings() {
        // Backup der Standard-Einstellungen
    }

    public void updateDynamic() {
        // Aktualisiere dynamische Einstellungen basierend auf dem Spielzustand
    }

    public void applyMiningSettings() {
        Settings settings = baritone.getSettings();
        settings.allowBreak.value = true;
        settings.allowSprint.value = true;
        settings.blockReachDistance.value = 4.5f;
    }

    public void applySafetySettings() {
        Settings settings = baritone.getSettings();
        settings.avoidance.value = true;
        settings.mobAvoidance.value = true;
        settings.allowParkour.value = false;
    }

    public void restoreDefaultSettings() {
        // Stelle die Standard-Einstellungen wieder her
        Settings settings = baritone.getSettings();
        settings.allowBreak.value = defaultSettings.allowBreak.value;
        settings.allowSprint.value = defaultSettings.allowSprint.value;
        settings.blockReachDistance.value = defaultSettings.blockReachDistance.value;
        settings.avoidance.value = defaultSettings.avoidance.value;
        settings.mobAvoidance.value = defaultSettings.mobAvoidance.value;
        settings.allowParkour.value = defaultSettings.allowParkour.value;
    }
}
