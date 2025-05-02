package com.techbank.account.cmd.api.commands;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.core.handlers.EventSourceHandler;
import com.techbank.cqrs.core.infrastructure.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountCommandHandler implements CommandHandler{

    @Autowired
    EventSourceHandler<AccountAggregate> eventSourceHandler;

    @Override
    public void handle(OpenAccountCommand command) {
        var aggregate = new AccountAggregate(command);
        eventSourceHandler.save(aggregate);


    }

    @Override
    public void handle(DepositFundsCommand command) {

        var aggregate =  eventSourceHandler.getId(command.getId());
        aggregate.depositFunds(command.getAmount());
        eventSourceHandler.save(aggregate);
    }

    @Override
    public void handle(WithdrawFundsCommand command) {
        var aggregate =  eventSourceHandler.getId(command.getId());
        if(command.getAmount() > aggregate.getBalance())
            throw new IllegalStateException("Insufficient funds");
        aggregate.withdrawFunds(command.getAmount());
        eventSourceHandler.save(aggregate);

    }

    @Override
    public void handle(CloseAccountCommand command) {

        var aggregate =  eventSourceHandler.getId(command.getId());
        if(aggregate.getBalance() > 0){
            throw new IllegalStateException("Funds must be withdrawn before closing account");
        }

        aggregate.closeAccount();
        eventSourceHandler.save(aggregate);


    }
}
