package de.utilityx.core.managers;

import de.utilityx.core.UtilityXCore;
import de.utilityx.core.api.Addon;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;


public final class AddonManager {

    private final UtilityXCore core;
    private final Map<File, URLClassLoader> fileLoaders = new LinkedHashMap<>();
    private final Map<File, List<Addon>> fileAddons = new LinkedHashMap<>();

    public AddonManager(UtilityXCore core) {
        this.core = core;
    }

    public synchronized void loadAll() {
        File dir = new File(core.getDataFolder(), "addons");
        if (!dir.exists()) dir.mkdirs();

        File[] jars = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".jar"));
        if (jars == null || jars.length == 0) return;

        for (File jar : jars) loadOne(jar);

        if (!fileLoaders.isEmpty()) {
            StringBuilder sb = new StringBuilder("Loaded addons: ");
            boolean first = true;
            for (File jar : fileLoaders.keySet()) {
                if (!first) sb.append(", ");
                sb.append(jar.getName());
                first = false;
            }
            core.getLogger().info(sb.toString());
        }
    }

    public synchronized void disableAll() {
        List<File> jars = new ArrayList<>(fileAddons.keySet());
        Collections.reverse(jars);
        for (File jar : jars) disableJar(jar);
    }

    public synchronized void loadOne(File jar) {
        if (fileLoaders.containsKey(jar)) return;
        try {
            URL url = jar.toURI().toURL();
            ClassLoader parent = core.getClass().getClassLoader();
            URLClassLoader cl = new URLClassLoader(new URL[]{url}, parent);

            ServiceLoader<Addon> sl = ServiceLoader.load(Addon.class, cl);
            Iterator<Addon> it = sl.iterator();
            if (!it.hasNext()) {
                cl.close();
                return;
            }

            List<Addon> addons = new ArrayList<>();
            while (it.hasNext()) {
                Addon addon = it.next();
                addon.onLoad(core);
                addon.onAddonEnable();
                addons.add(addon);
            }
            if (!addons.isEmpty()) {
                fileLoaders.put(jar, cl);
                fileAddons.put(jar, addons);
            } else {
                cl.close();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public synchronized void disableJar(File jar) {
        List<Addon> addons = fileAddons.remove(jar);
        if (addons != null) {
            Collections.reverse(addons);
            for (Addon addon : addons) {
                try {
                    addon.onAddonDisable();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        URLClassLoader cl = fileLoaders.remove(jar);
        if (cl != null) {
            try {
                cl.close();
            } catch (Throwable ignored) {
            }
        }
    }

    public synchronized boolean isLoaded(File jar) {
        return fileLoaders.containsKey(jar);
    }
}
