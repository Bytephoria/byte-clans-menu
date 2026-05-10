package team.bytephoria.byteclansmenu.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class AsyncExecutor {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newVirtualThreadPerTaskExecutor();

    private AsyncExecutor() {
        throw new UnsupportedOperationException();
    }

    @Contract("_ -> new")
    public static @NotNull CompletableFuture<Void> runAsync(final @NotNull Runnable runnable) {
        return CompletableFuture.runAsync(runnable, EXECUTOR_SERVICE);
    }

    public static @NotNull <T> CompletableFuture<T> supplyAsync(final @NotNull Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, EXECUTOR_SERVICE);
    }

    public static void shutdown() {
        EXECUTOR_SERVICE.shutdown();

        try {
            final boolean terminated = EXECUTOR_SERVICE.awaitTermination(3, TimeUnit.SECONDS);
            if (!terminated) {
                EXECUTOR_SERVICE.shutdownNow();
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
