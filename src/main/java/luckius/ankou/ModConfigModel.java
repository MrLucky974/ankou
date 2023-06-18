package luckius.ankou;

import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = Mod.MOD_ID)
@Config(name = "ankou_config", wrapperName = "ModConfig")
public class ModConfigModel {
    @SectionHeader("totem_of_undying")
    public boolean isLinkable = true;

    @Nest
    public TotemEffects totemEffects = new TotemEffects();

    public static class TotemEffects {
        public boolean fireProtection = false;
    }

    @SectionHeader("enchantments")
    @PredicateConstraint("minDespawnDelayPredicate")
    public int defaultDespawnDelay = 6000;

    public static boolean minDespawnDelayPredicate(int despawnDelay) {
        return despawnDelay >= 20;
    }
}
