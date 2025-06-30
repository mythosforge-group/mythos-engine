package mythosengine.core.entity;

import java.util.UUID;
public record Relationship(UUID sourceId, UUID targetId, String type) {}
