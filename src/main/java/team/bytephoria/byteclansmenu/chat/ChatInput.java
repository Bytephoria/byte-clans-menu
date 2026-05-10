package team.bytephoria.byteclansmenu.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.bytephoria.byteclansmenu.PaperPlugin;
import team.bytephoria.byteclansmenu.chat.listener.AsyncChatListener;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class ChatInput {

    private final Map<UUID, InputSession> sessions = new ConcurrentHashMap<>();

    private final PaperPlugin plugin;
    private AsyncChatListener chatListener;

    public ChatInput(final @NotNull PaperPlugin plugin) {
        this.plugin = plugin;
    }

    public record InputSession(
            @NotNull Consumer<String> consumer,
            @NotNull Runnable onTimeout,
            int timeoutTaskId
    ) {

    }

    public void clear() {
        this.sessions.clear();
        this.unregisterListener();
    }

    public void register(
            final @NotNull Player player,
            final @NotNull Consumer<String> consumer,
            final @NotNull Runnable onTimeout
    ) {

        this.ensureListenerRegistered();

        final UUID uuid = player.getUniqueId();
        final int timeoutTaskId = this.plugin.runTaskLater(() -> {
            this.sessions.remove(uuid);
            onTimeout.run();
            this.unregisterListenerIfUnused();
        }, 20 * 60);

        this.sessions.put(uuid, new InputSession(consumer, onTimeout, timeoutTaskId));
    }

    public boolean handleMessage(
            final @NotNull Player player,
            final @Nullable String message
    ) {

        final InputSession session = this.sessions.remove(player.getUniqueId());
        if (session == null) {
            return false;
        }

        this.plugin.getServer()
                .getScheduler()
                .cancelTask(session.timeoutTaskId());

        this.unregisterListenerIfUnused();

        if (message != null) {
            session.consumer().accept(message);
        }

        return true;
    }

    public void remove(final @NotNull UUID uuid) {
        final InputSession session = this.sessions.remove(uuid);
        if (session == null) {
            return;
        }

        this.plugin.getServer()
                .getScheduler()
                .cancelTask(session.timeoutTaskId());

        this.unregisterListenerIfUnused();
    }

    public void remove(final @NotNull Player player) {
        this.remove(player.getUniqueId());
    }

    private void ensureListenerRegistered() {
        if (this.chatListener != null) {
            return;
        }

        this.chatListener = new AsyncChatListener(this);
        this.plugin.registerListener(this.chatListener);
    }

    private void unregisterListenerIfUnused() {
        if (!this.sessions.isEmpty()) {
            return;
        }

        this.unregisterListener();
    }

    private void unregisterListener() {
        if (this.chatListener == null) {
            return;
        }

        HandlerList.unregisterAll(this.chatListener);
        this.chatListener = null;
    }

}