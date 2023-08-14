package org.dimas4ek.session;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ScheduledFuture;

@Getter
@Setter
public class MessageSession {
    private String state;
    private ScheduledFuture<?> scheduledTask;
    
    public MessageSession(String state) {
        this.state = state;
    }
    
    public void setScheduledTask(ScheduledFuture<?> scheduledTask) {
        this.scheduledTask = scheduledTask;
    }
}
