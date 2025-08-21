package xyz.blackdev.utilityxcore.api;

import java.util.Objects;
import java.util.function.Supplier;

public final class UtilityXAPI {
    private static Supplier<UtilityX> supplier;

    private UtilityXAPI() {}

    public static void bootstrap(Supplier<UtilityX> impl) { supplier = Objects.requireNonNull(impl); }
    public static UtilityX require() {
        UtilityX u = supplier == null ? UtilityX.get().orElse(null) : supplier.get();
        if (u == null) throw new IllegalStateException("UtilityX provider not available");
        return u;
    }
}
