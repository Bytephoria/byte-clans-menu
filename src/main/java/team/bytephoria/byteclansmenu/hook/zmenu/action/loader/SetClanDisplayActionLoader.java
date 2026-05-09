package team.bytephoria.byteclansmenu.hook.zmenu.action.loader;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import team.bytephoria.byteclansmenu.hook.zmenu.action.SetClanDisplayAction;

import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

public final class SetClanDisplayActionLoader extends ActionLoader {

    private final Duration defaultDuration;
    public SetClanDisplayActionLoader(final @NotNull Duration defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull @Unmodifiable List<String> getKeys() {
        return Collections.singletonList("set-clan-display");
    }

    @Override
    public @NotNull Action load(
            final @NotNull String path,
            final @NotNull TypedMapAccessor accessor,
            final @NotNull File file
    ) {
        final String display = accessor.getString("display");
        return new SetClanDisplayAction(display, this.defaultDuration);
    }

}
