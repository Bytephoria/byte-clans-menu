package team.bytephoria.byteclansmenu.command;

import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclansmenu.PaperPlugin;

public final class ClanMenuCommand {

    private final PaperPlugin paperPlugin;
    public ClanMenuCommand(final @NotNull PaperPlugin paperPlugin) {
        this.paperPlugin = paperPlugin;
    }

    @Command("clan menu")
    public void clanMenu(final @NotNull Player player) {
        assert this.paperPlugin.zMenuHook().menuPlugin() != null;
        this.paperPlugin.zMenuHook().menuPlugin().getInventoryManager().openInventory(player, "clan");
    }

}
