package team.bytephoria.byteclansmenu.hook.zmenu.action;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.bytephoria.byteclans.api.ClanPlayer;
import team.bytephoria.byteclans.api.access.ByteClans;
import team.bytephoria.byteclans.bukkitapi.BukkitClanPlayer;
import team.bytephoria.byteclansmenu.chat.ChatInput;
import team.bytephoria.byteclansmenu.util.AsyncExecutor;

import java.util.Locale;
import java.util.concurrent.Executor;

public final class ClanCreateAction extends Action {

    private final FileConfiguration configuration;
    private final Executor mainThreadExecutor;
    private final ChatInput chatInput;

    public ClanCreateAction(
            final @NotNull FileConfiguration configuration,
            final @NotNull Executor mainThreadExecutor,
            final @NotNull ChatInput chatInput
    ) {
        this.configuration = configuration;
        this.mainThreadExecutor = mainThreadExecutor;
        this.chatInput = chatInput;
    }

    @Override
    protected void execute(
            final @NotNull Player player,
            final @NotNull Button button,
            final @NotNull InventoryEngine inventoryEngine,
            final @NotNull Placeholders placeholders
    ) {

        this.chatInput.register(
                player,
                clanName -> this.createClan(player, clanName),
                () -> this.sendMessage(player, "messages.create.timeout", null)
        );
    }

    private void createClan(
            final @NotNull Player player,
            final @NotNull String clanName
    ) {

        final ClanPlayer bukkitClanPlayer = BukkitClanPlayer.wrap(player);
        AsyncExecutor.supplyAsync(() ->
                        ByteClans.getAPI()
                                .clanManager()
                                .createClan(bukkitClanPlayer, clanName)
                )
                .thenAccept(response ->
                        this.mainThreadExecutor.execute(() -> {
                            final String resultPath = response.result()
                                    .name()
                                    .toLowerCase(Locale.ROOT)
                                    .replace('_', '-');

                            this.sendMessage(
                                    player,
                                    "messages.create." + resultPath,
                                    clanName
                            );
                        })
                );
    }

    private void sendMessage(
            final @NotNull Player player,
            final @NotNull String path,
            final String clanName
    ) {

        String message = this.configuration.getString(path);
        if (message == null || message.isBlank()) {
            return;
        }

        if (clanName != null) {
            message = message.replace("{clan}", clanName);
        }

        final Component component = MiniMessage.miniMessage().deserialize(message);
        player.sendMessage(component);
    }
}