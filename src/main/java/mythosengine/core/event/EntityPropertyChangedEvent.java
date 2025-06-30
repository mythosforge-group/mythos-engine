package mythosengine.core.event;

import org.springframework.context.ApplicationEvent;
import lombok.Getter;

@Getter
public class EntityPropertyChangedEvent extends ApplicationEvent {
    private final Object entityId;
    private final String propertyName;
    private final Object oldValue;
    private final Object newValue;

    public EntityPropertyChangedEvent(Object source, Object entityId, String propertyName, Object oldValue, Object newValue) {
        super(source);
        this.entityId = entityId;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
}