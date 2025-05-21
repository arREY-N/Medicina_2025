package com.example.medicina.functions;

import com.example.medicina.model.Account;
import com.example.medicina.model.Repository;
import java.util.List;
import kotlinx.coroutines.flow.StateFlow;

public class AccountFunctions {
    public static boolean handleLogin(String username, String password) throws AccountException {
        Repository repository = Repository.INSTANCE;
        StateFlow<List<Account>> accountsFlow = repository.getAllAccounts();
        List<Account> accounts = accountsFlow.getValue();

        for (Account account : accounts) {
            if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
                return true;  // Found a matching account
            }
        }
        throw new AccountException("Incorrect username or password");

        // No matching account found after checking all
    }


    public static boolean handleSignUp(String firstname, String lastname, String middlename, String username, String password) throws AccountException {
        Repository repository = Repository.INSTANCE;
        StateFlow<List<Account>> accountsFlow = repository.getAllAccounts();
        List<Account> accounts = accountsFlow.getValue();

        for (Account account : accounts) {
            if (account.getUsername().equals(username.trim())) {
                throw new AccountException("Username already in use");
            }
            if(account.getFirstname().equals(firstname.trim()) && account.getLastname().equals(lastname.trim()) && account.getMiddlename().equals(middlename.trim())){
                throw new AccountException("User already in the system");
            }
            if(password.length() < 8){
                throw new AccountException("Password must be at least 8 characters long");
            }
        }
        return true;
    }
}
