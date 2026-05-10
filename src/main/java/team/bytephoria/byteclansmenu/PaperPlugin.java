package team.bytephoria.byteclansmenu;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.bukkitapi.access.BukkitByteClans;
import team.bytephoria.byteclansmenu.chat.ChatInput;
import team.bytephoria.byteclansmenu.chat.listener.AsyncChatListener;
import team.bytephoria.byteclansmenu.command.ClanMenuCommand;
import team.bytephoria.byteclansmenu.command.PluginCommand;
import team.bytephoria.byteclansmenu.hook.zmenu.ZMenuHook;
import team.bytephoria.byteclansmenu.util.AsyncExecutor;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;

public final class PaperPlugin extends JavaPlugin {

    public static final DateTimeFormatter COMPLETE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    private ZMenuHook zMenuHook;
    private ChatInput chatInput;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.chatInput = new ChatInput(this);

        this.zMenuHook = new ZMenuHook(this);
        this.zMenuHook.loadAll();

        BukkitByteClans.getAPI().annotationParser().parse(new ClanMenuCommand(this));
        this.getServer().getCommandMap().register("byteclansmenu", new PluginCommand(this));
        this.getServer().getPluginManager().registerEvents(new AsyncChatListener(this.chatInput), this);
    }

    @Override
    public void onDisable() {
        AsyncExecutor.shutdown();
        HandlerList.unregisterAll(this);
        this.getServer().getScheduler().cancelTasks(this);
        this.getServer().getCommandMap().getKnownCommands().remove("byteclansmenu");

        if (this.zMenuHook != null) {
            this.zMenuHook.unloadAll();
        }

        this.chatInput = null;
        this.zMenuHook = null;
    }

    public void registerListener(final @NotNull Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public void reload() {
        this.chatInput.clear();
        if (this.zMenuHook != null) {
            this.zMenuHook.reload();
        }
    }

    public int runTaskLater(final @NotNull Runnable task, final long delay) {
        return this.getServer().getScheduler().runTaskLater(this, task, delay).getTaskId();
    }

    public @NotNull Executor mainThreadExecutor() {
        return this.getServer().getScheduler().getMainThreadExecutor(this);
    }

    public ZMenuHook zMenuHook() {
        return this.zMenuHook;
    }

    public ChatInput chatInput() {
        return this.chatInput;
    }
}
