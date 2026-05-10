package team.bytephoria.byteclansmenu.hook.zmenu.action.loader;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.bytephoria.byteclansmenu.chat.ChatInput;
import team.bytephoria.byteclansmenu.hook.zmenu.action.ClanCreateAction;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

public final class ClanCreateActionLoader extends ActionLoader {

    private final FileConfiguration fileConfiguration;
    private final Executor mainThreadExecutor;
    private final ChatInput chatInput;

    public ClanCreateActionLoader(
            final @NotNull FileConfiguration fileConfiguration,
            final @NotNull Executor mainThreadExecutor,
            final @NotNull ChatInput chatInput
    ) {
        this.fileConfiguration = fileConfiguration;
        this.mainThreadExecutor = mainThreadExecutor;
        this.chatInput = chatInput;
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull @Unmodifiable List<String> getKeys() {
        return Collections.singletonList("clan-create");
    }

    @Override
    public @NotNull Action load(
            final @NotNull String path,
            final @NotNull TypedMapAccessor accessor,
            final @NotNull File file
    ) {
        return new ClanCreateAction(this.fileConfiguration, this.mainThreadExecutor, this.chatInput);
    }
}
