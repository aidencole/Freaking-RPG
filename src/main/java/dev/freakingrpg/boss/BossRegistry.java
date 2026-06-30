package dev.freakingrpg.boss;

import dev.freakingrpg.boss.builtin.ColossusDrillBoss;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class BossRegistry {

    private final Map<String, BossDefinition> definitions = new LinkedHashMap<>();

    public BossRegistry() {
        register(ColossusDrillBoss.create());
    }

    public void register(BossDefinition definition) {
        definitions.put(definition.id(), definition);
    }

    public Optional<BossDefinition> find(String id) {
        return Optional.ofNullable(definitions.get(id));
    }

    public Collection<BossDefinition> all() {
        return definitions.values();
    }
}
