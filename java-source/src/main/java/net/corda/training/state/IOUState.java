package net.corda.training.state;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.Amount;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This is where you'll add the definition of your state object. Look at the unit tests in [IOUStateTests] for
 * instructions on how to complete the [IOUState] class.
 *
 */
public class IOUState implements ContractState, LinearState {

    private Amount amount;

    private Amount paid;

    private Party lender;

    private Party borrower;

    private UniqueIdentifier linearId;

    public IOUState(Amount amount, Party lender, Party borrower) {
        this.amount = amount;
        this.lender = lender;
        this.borrower = borrower;
    }

    @ConstructorForDeserialization
    public IOUState() {}

    /**
     *  This method will return a list of the nodes which can "use" this state in a valid transaction. In this case, the
     *  lender or the borrower.
     */
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(lender, borrower);
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public Party getLender() {
        return lender;
    }

    public void setLender(Party lender) {
        this.lender = lender;
    }

    public Amount getPaid() {
        return paid;
    }

    public void setPaid(Amount paid) {
        this.paid = paid;
    }

    public Party getBorrower() {
        return borrower;
    }

    public void setBorrower(Party borrower) {
        this.borrower = borrower;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }
}