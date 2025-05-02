package com.techbank.cqrs.core.domain;

import com.techbank.cqrs.core.events.BaseEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class AggregateRoot {
    public String id;
    private int version = -1;

    private final Logger logger = Logger.getLogger(AggregateRoot.class.getName());

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    private final List<BaseEvent> changes = new ArrayList<>();


    public String getId(){
        return id;
    }

    public List<BaseEvent> getUnCommittedChanges(){
        return this.changes;
    }

    public void markChangesAsCommitted(){
        this.changes.clear();
    }

    protected void applyChanges(BaseEvent event, Boolean isNewEvent){
        try {
            var method = getClass().getDeclaredMethod("apply",event.getClass());
            method.setAccessible(true);
            method.invoke(this,event);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }finally {
            if(isNewEvent){
                changes.add(event);
            }
        }

    }
    public void raiseEvent(BaseEvent event){
        applyChanges(event,true);

    }
    public void replayEvents(Iterable<BaseEvent> events){
        events.forEach(event -> applyChanges(event,false));
    }

}
