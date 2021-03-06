package eu.peppol.persistence.api.account;

import eu.peppol.identifier.ParticipantId;
import eu.peppol.persistence.AccountId;
import eu.peppol.persistence.api.MessageNumber;
import eu.peppol.persistence.api.SrAccountNotFoundException;
import eu.peppol.persistence.api.UserName;

/**
 * @author Steinar Overbeck Cook
 *         <p/>
 *         Created by
 *         User: steinar
 *         Date: 31.12.11
 *         Time: 16:54
 */
public interface AccountRepository {

    public Account findAccountById(final AccountId id) throws SrAccountNotFoundException;

    Account findAccountByParticipantId(final ParticipantId participantId);

    /**
     * Locates an account, which you expect to exist by the username.
     *
     * @param username
     * @return a reference to the account if found, null otherwise
     *
     * @see #accountExists(UserName) to figure out whether an account exists or not.
     */
    Account findAccountByUsername(final UserName username) throws SrAccountNotFoundException;

    /**
     * Creates an account with the provided details. if the account already
     * exists it will be retrieved from the database based on the participant id
     *
     * Default client role will be added
     *
     * If participantId is not null, account_receiver entry will also be created
     * @param account
     * @param participantId if not null will be used in account_receiver
     * @return
     */
    Account createAccount(final Account account, final ParticipantId participantId);

    /**
     * Persists new customer
     * @return
     */
    Customer createCustomer(final String name, final String email, final String phone, final String country, final String contactPerson, final String address1, final String address2, final String zip, final String city, final String orgNo);

    /**
     * Deletes the account with the given id
     * @param accountId
     */
    void deleteAccount(AccountId accountId);

    /**
     * Inspects the repository to see if an account identified by username exists or not.
     *
     * @param username
     * @return true if account exists, false otherwise.
     *
     * @see #findAccountByUsername(UserName)
     */
    boolean accountExists(final UserName username);

    Customer findCustomerById(final Integer id);

    void updatePasswordOnAccount(final AccountId id, final String hash);

    Account findAccountAsOwnerOfMessage(MessageNumber messageNumber);
}
