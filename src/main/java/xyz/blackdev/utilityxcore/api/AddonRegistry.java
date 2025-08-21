package xyz.blackdev.utilityxcore.api;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AddonRegistry {
    List<AddonRef> list();
    Optional<AddonHandle> findById(String id);
    Optional<AddonHandle> findByName(String name);
    CompletableFuture<OperationResult> install(URI uri);
    CompletableFuture<OperationResult> install(Path localJar);
    CompletableFuture<OperationResult> load(String id);
    CompletableFuture<OperationResult> unload(String id);
    CompletableFuture<OperationResult> reload(String id);
    void addListener(AddonLifecycleListener listener);
    void removeListener(AddonLifecycleListener listener);
}
