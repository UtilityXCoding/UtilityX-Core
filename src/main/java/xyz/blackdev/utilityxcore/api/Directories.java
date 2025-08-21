package xyz.blackdev.utilityxcore.api;

import java.nio.file.Path;

public interface Directories {
    Path root();
    Path data();
    Path addons();
    Path cache();
}
