package com.techbank.account.cmd.infra;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.core.domain.AggregateRoot;
import com.techbank.cqrs.core.handlers.EventSourceHandler;
import com.techbank.cqrs.core.infrastructure.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class AccountEventSourceHandler implements EventSourceHandler<AccountAggregate> {

    @Autowired
    EventStore eventStore;
    @Override
    public void save(AggregateRoot root) {
        eventStore.saveEvents(root.getId(),root.getUnCommittedChanges(),root.getVersion());
        root.markChangesAsCommitted();
    }

    @Override
    public AccountAggregate getId(String id) {
        var accountAggregate = new AccountAggregate();
        var events = eventStore.getEvents(id);
        if( events != null && events.isEmpty()){
            accountAggregate.replayEvents(events);
            var latestVersion = events.stream().map(x->x.getVersion()).max(Comparator.naturalOrder());
            accountAggregate.setVersion(latestVersion.get());
        }
        return accountAggregate;
    }
}
