package xyz.blackdev.utilityxcore.api;

public interface AddonLifecycleListener {
    default void onLoaded(AddonRef addon) {}
    default void onEnabled(AddonRef addon) {}
    default void onDisabled(AddonRef addon) {}
    default void onUnloaded(AddonRef addon) {}
    default void onFailed(AddonRef addon, String reason) {}
}
