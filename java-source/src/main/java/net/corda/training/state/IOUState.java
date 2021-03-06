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

import java.util.Currency;
import java.util.List;
import java.util.Objects;

/**
 * This is where you'll add the definition of your state object. Look at the unit tests in [IOUStateTests] for
 * instructions on how to complete the [IOUState] class.
 */
public class IOUState implements ContractState, LinearState {

    private Amount amount;

    private Party lender;

    private Party borrower;

    private Amount paid;

    private UniqueIdentifier linearId;

    public IOUState(Amount amount, Party lender, Party borrower) {
        this(amount, lender, borrower, new Amount(0, amount.getToken()), new UniqueIdentifier());
    }

    @ConstructorForDeserialization
    private IOUState(Amount amount, Party lender, Party borrower, Amount paid, UniqueIdentifier linearId) {
        this.amount = amount;
        this.lender = lender;
        this.borrower = borrower;
        this.paid = paid;
        this.linearId = linearId;
    }

    /**
     * This method will return a list of the nodes which can "use" this state in a valid transaction. In this case, the
     * lender or the borrower.
     */
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(lender, borrower);
    }

    public Amount getAmount() {
        return amount;
    }

    public Party getLender() {
        return lender;
    }

    public Party getBorrower() {
        return borrower;
    }

    public Amount getPaid() {
        return paid;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public IOUState pay(Amount amount) {
        Amount amountPaid = this.paid.plus(amount);
        return new IOUState(amount, lender, borrower, amountPaid, linearId);
    }

    public IOUState withNewLender(Party newLenderParty) {
        return new IOUState(amount, newLenderParty, borrower, paid, linearId);
    }


    /**
     * Copy/clone constructor
     *
     * @param amount
     * @param lender
     * @param borrower
     * @param paid
     * @return
     */
    public ContractState copy(Amount<Currency> amount, Party lender, Party borrower, Amount<Currency> paid) {
        return new IOUState(amount, lender, borrower, paid, this.linearId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IOUState iouState = (IOUState) o;
        return Objects.equals(amount, iouState.amount) &&
                Objects.equals(lender, iouState.lender) &&
                Objects.equals(borrower, iouState.borrower) &&
                Objects.equals(paid, iouState.paid) &&
                Objects.equals(linearId, iouState.linearId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, lender, borrower, paid, linearId);
    }
}