package team.bytephoria.byteclansmenu;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import team.bytephoria.byteclans.bukkitapi.access.BukkitByteClans;
import team.bytephoria.byteclansmenu.command.ClanMenuCommand;
import team.bytephoria.byteclansmenu.command.PluginCommand;
import team.bytephoria.byteclansmenu.hook.zmenu.ZMenuHook;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class PaperPlugin extends JavaPlugin {

    public static final DateTimeFormatter COMPLETE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    .withZone(ZoneId.systemDefault());

    private ZMenuHook zMenuHook;

    @Override
    public void onEnable() {
        this.zMenuHook = new ZMenuHook(this);
        this.zMenuHook.loadAll();

        BukkitByteClans.getAPI().annotationParser().parse(new ClanMenuCommand(this));
        this.getServer().getCommandMap().register("byteclansmenu", new PluginCommand(this));
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.getServer().getScheduler().cancelTasks(this);
        this.getServer().getCommandMap().getKnownCommands().remove("byteclansmenu");

        if (this.zMenuHook != null) {
            this.zMenuHook.unloadAll();
        }

        this.zMenuHook = null;
    }

    public void reload() {
        if (this.zMenuHook != null) {
            this.zMenuHook.reload();
        }

    }

    public ZMenuHook zMenuHook() {
        return this.zMenuHook;
    }
}
