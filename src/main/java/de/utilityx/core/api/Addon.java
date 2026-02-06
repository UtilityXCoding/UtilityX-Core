package de.utilityx.core.api;

import de.utilityx.core.UtilityXCore;

public interface Addon {
   void onLoad(UtilityXCore core);
   void onAddonEnable();
   void onAddonDisable();
}