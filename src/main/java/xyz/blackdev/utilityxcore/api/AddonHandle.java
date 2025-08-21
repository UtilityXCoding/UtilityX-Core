package xyz.blackdev.utilityxcore.api;

import java.util.concurrent.CompletableFuture;

public interface AddonHandle {
    String id();
    String name();
    String version();
    AddonState state();
    CompletableFuture<OperationResult> enable();
    CompletableFuture<OperationResult> disable();
    CompletableFuture<OperationResult> reload();
}
