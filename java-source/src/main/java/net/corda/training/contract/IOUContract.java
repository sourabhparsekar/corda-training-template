package net.corda.training.contract;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.TypeOnlyCommandData;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.training.state.IOUState;

import java.security.PublicKey;
import java.util.HashSet;
import java.util.Set;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * This is where you'll add the contract code which defines how the [IOUState] behaves. Looks at the unit tests in
 * [IOUContractTests] for instructions on how to complete the [IOUContract] class.
 */
public class IOUContract implements Contract {
    public static final String IOU_CONTRACT_ID = "net.corda.training.contract.IOUContract";

    /**
     * Add any commands required for this contract as classes within this interface.
     * It is useful to encapsulate your commands inside an interface, so you can use the [requireSingleCommand]
     * function to check for a number of commands which implement this interface.
     */
    public interface Commands extends CommandData {
        // Add commands here.
        // E.g
        // class DoSomething extends TypeOnlyCommandData implements Commands{}
        class Issue extends TypeOnlyCommandData implements Commands {
        }

        class Transfer extends TypeOnlyCommandData implements Commands {
        }

    }

    /**
     * The contract code for the [IOUContract].
     * The constraints are self documenting so don't require any additional explanation.
     */
    @Override
    public void verify(LedgerTransaction tx) {
        // Add contract code here.
        //get commands
        final CommandWithParties<Commands> commandWithParties = requireSingleCommand(tx.getCommands(), Commands.class);
        final Commands commands = commandWithParties.getValue();

        //for this command check
        if (commands.equals(new Commands.Issue())) {
            requireThat(req -> {

                //Task 2. As previously observed, issue transactions should not have any input state references. Therefore we must check to ensure that no input states are included in a transaction to issue an IOU.
                req.using("No inputs should be consumed when issuing an IOU", tx.getInputStates().size() == 0);

                //Task 3. Now we need to ensure that only one {@link IOUState} is issued per transaction.
                req.using("Only one output state should be created when issuing an IOU.", tx.getOutputStates().size() == 1);

                IOUState iouOutputState = tx.outputsOfType(IOUState.class).get(0);

                //Task 4. Now we need to consider the properties of the {@link IOUState}. We need to ensure that an IOU should always have a positive value.
                req.using("A newly issued IOU must have a positive amount.", iouOutputState.getAmount().getQuantity() > 0);

                //Task 5. For obvious reasons, the identity of the lender and borrower must be different.
                req.using("The lender and borrower cannot have the same identity.", iouOutputState.getLender().getOwningKey() != iouOutputState.getBorrower().getOwningKey());

                //Task 6. The list of public keys which the commands hold should contain all of the participants defined in the {@link IOUState}.
                Set<PublicKey> publicKeysSet = new HashSet<>();
                tx.getCommands().get(0).getSigners().forEach(publicKey ->
                        publicKeysSet.add(publicKey)
                );

                Set<PublicKey> participantKeysSet = new HashSet<>();
                tx.getOutputStates().get(0).getParticipants().forEach(abstractParty ->
                        participantKeysSet.add(abstractParty.getOwningKey())
                );

                //both input tx and output state should have same participants
                req.using("Both lender and borrower together only may sign IOU issue transaction.", publicKeysSet.size() == participantKeysSet.size() && publicKeysSet.containsAll(participantKeysSet));

                return null;
            });
        } else if (commands.equals(new Commands.Transfer())) {

            requireThat(req -> {

                //Task 2. The transfer transaction should only have one input state and one output state.
                req.using("An IOU transfer transaction should only consume one input state.", tx.getInputStates().size() == 1);
                req.using("An IOU transfer transaction should only create one output state.", tx.getOutputStates().size() == 1);

                //Task 3. Add a constraint to the contract code to ensure only the lender property can change when transferring IOUs.
                IOUState iouInputState = (IOUState) tx.getInputStates().get(0);
                IOUState iouOutputState = (IOUState) tx.getOutputStates().get(0);
                IOUState iouOutputStateToBeChecked = iouOutputState.withNewLender(iouInputState.getLender());

                //equals to be overridden in iou contract for this to work else have to check each field
                req.using("Only the lender property may change.", iouOutputStateToBeChecked.equals(iouInputState));

                return null;
            });

        }
    }
}