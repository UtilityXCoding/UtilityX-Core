package xyz.blackdev.utilityxcore.api;

import java.util.List;
import java.util.Set;

public interface ConfigFacade {
    String getString(String path, String def);
    int getInt(String path, int def);
    boolean getBoolean(String path, boolean def);
    double getDouble(String path, double def);
    List<String> getStringList(String path);
    Set<String> getKeys(String path);
    void set(String path, Object value);
    void reload();
    void save();
}
