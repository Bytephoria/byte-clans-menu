package team.bytephoria.byteclansmenu.hook.zmenu;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclansmenu.PaperPlugin;
import team.bytephoria.byteclansmenu.hook.zmenu.action.loader.SetClanDisplayActionLoader;
import team.bytephoria.byteclansmenu.hook.zmenu.action.loader.SetClanInviteStateActionLoader;
import team.bytephoria.byteclansmenu.hook.zmenu.action.loader.SetClanPvPModeActionLoader;
import team.bytephoria.byteclansmenu.hook.zmenu.buttons.loader.ClanAllyButtonLoader;
import team.bytephoria.byteclansmenu.hook.zmenu.buttons.loader.ClanEnemyButtonLoader;
import team.bytephoria.byteclansmenu.hook.zmenu.buttons.loader.ClanListButtonLoader;
import team.bytephoria.byteclansmenu.hook.zmenu.buttons.loader.ClanMemberButtonLoader;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public final class ZMenuHook {

    private static final String[] DEFAULT_PATTERNS = {
            "zmenu/patterns/decoration.yml"
    };

    private static final String[] DEFAULT_INVENTORIES = {
            "zmenu/inventories/clan.yml",
            "zmenu/inventories/clan_allies.yml",
            "zmenu/inventories/clan_display.yml",
            "zmenu/inventories/clan_display_presets.yml",
            "zmenu/inventories/clan_enemies.yml",
            "zmenu/inventories/clan_member_list.yml",
            "zmenu/inventories/clan_settings.yml",
            "zmenu/inventories/clan_statistics.yml",
            "zmenu/inventories/clan_list.yml"
    };

    private final PaperPlugin paperPlugin;
    private final MenuPlugin menuPlugin;

    public ZMenuHook(final @NotNull PaperPlugin paperPlugin) {
        this.paperPlugin = paperPlugin;

        this.menuPlugin = (MenuPlugin) Bukkit.getPluginManager().getPlugin("zMenu");
        if (this.menuPlugin == null) {
            throw new UnsupportedOperationException("zMenu plugin is not loaded");
        }
    }

    public void loadAll() {
        this.registerDefaultActions();
        this.registerDefaultButtons();

        this.loadPatterns();
        this.loadInventories();
    }

    public void reload() {
        this.menuPlugin.getInventoryManager().deleteInventories(this.paperPlugin);

        this.loadPatterns();
        this.loadInventories();
    }

    public void unloadAll() {
        this.menuPlugin.getInventoryManager().deleteInventories(this.paperPlugin);
        this.menuPlugin.getButtonManager().unregisters(this.paperPlugin);
    }

    public void registerDefaultActions() {
        this.menuPlugin.getButtonManager().registerAction(
                new SetClanDisplayActionLoader(Duration.of(5, ChronoUnit.HOURS))
        );

        this.menuPlugin.getButtonManager().registerAction(new SetClanPvPModeActionLoader());
        this.menuPlugin.getButtonManager().registerAction(new SetClanInviteStateActionLoader());
    }

    public void registerDefaultButtons() {
        this.menuPlugin.getButtonManager().unregisters(this.paperPlugin);

        this.menuPlugin.getButtonManager().register(new ClanListButtonLoader(this.paperPlugin));
        this.menuPlugin.getButtonManager().register(new ClanMemberButtonLoader(this.paperPlugin));
        this.menuPlugin.getButtonManager().register(new ClanAllyButtonLoader(this.paperPlugin));
        this.menuPlugin.getButtonManager().register(new ClanEnemyButtonLoader(this.paperPlugin));
    }

    public void loadPatterns() {
        this.loadDirectory(
                "zmenu/patterns",
                DEFAULT_PATTERNS,
                "Patterns",
                file -> this.menuPlugin.getPatternManager().loadPattern(file)
        );
    }

    public void loadInventories() {
        this.loadDirectory(
                "zmenu/inventories",
                DEFAULT_INVENTORIES,
                "Inventories",
                file -> this.menuPlugin.getInventoryManager().loadInventory(this.paperPlugin, file)
        );
    }

    private void loadDirectory(
            final @NotNull String path,
            final @NotNull String[] defaults,
            final @NotNull String name,
            final @NotNull ThrowingConsumer<File> consumer
    ) {
        final File directory = new File(this.paperPlugin.getDataFolder(), path);
        if (directory.mkdirs()) {
            for (final String resource : defaults) {
                this.paperPlugin.saveResource(resource, false);
            }

            this.paperPlugin.getSLF4JLogger().info("{} directory was created.", name);
        }

        final File[] files = directory.listFiles((file, fileName) ->
                fileName.toLowerCase().endsWith(".yml")
        );

        if (files == null) {
            return;
        }

        for (final File file : files) {
            try {
                consumer.accept(file);
            } catch (InventoryException exception) {
                this.paperPlugin.getSLF4JLogger().error(exception.getMessage(), exception);
            }
        }
    }

    public MenuPlugin menuPlugin() {
        return this.menuPlugin;
    }

    @FunctionalInterface
    private interface ThrowingConsumer<T> {

        void accept(T value) throws InventoryException;
    }
}