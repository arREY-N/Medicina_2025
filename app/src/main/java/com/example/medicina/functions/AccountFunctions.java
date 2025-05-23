package com.example.medicina.functions;

import static java.util.regex.Pattern.matches;

import com.example.medicina.model.Account;
import com.example.medicina.model.Repository;
import java.util.List;
import kotlinx.coroutines.flow.StateFlow;

public class AccountFunctions {
    private static final Repository repository = Repository.INSTANCE;

    private static final StateFlow<List<Account>> accountsFlow = repository.getAllAccounts();

    private static List<Account> getCurrentAccounts() {
        return accountsFlow.getValue();
    }

    public static Account handleLogin(String username, String password) throws AccountException {
        List<Account> accounts = getCurrentAccounts();

        for (Account account : accounts) {
            if (account.getUsername().equals(username) && account.getPassword().equals(password)) {
                return account;
            }
        }
        throw new AccountException("Incorrect username or password");
    }


    public static boolean handleSignUp(
            String p_firstname, String p_lastname,
            String p_middlename, String username,
            String password, String confirmPassword) throws AccountException {

        List<Account> accounts = getCurrentAccounts();

        String firstname = capitalizeWords(p_firstname);
        String lastname = capitalizeWords(p_lastname);
        String middlename = capitalizeWords(p_middlename);

        if(firstname.isEmpty() || lastname.isEmpty() || middlename.isEmpty() || username.isEmpty() || password.isEmpty()){
            throw new AccountException("Please fill up all information fields!");
        }

        if(!username.matches("^[a-zA-Z0-9]+$")){
            throw new AccountException("Username can only contain letters and numbers!");
        }

        if(username.length() < 8){
            throw new AccountException("Username must be at least 8 characters long!");
        }

        if(password.length() < 8){
            throw new AccountException("Password must be at least 8 characters long!");
        }

        if(!password.equals(confirmPassword)){
            throw new AccountException("Please retype password!");
        }

        for (Account account : accounts) {
            if (account.getUsername().equals(username.trim())) {
                throw new AccountException("Username already in use!");
            }
            if(account.getFirstname().equals(firstname.trim()) && account.getLastname().equals(lastname.trim()) && account.getMiddlename().equals(middlename.trim())){
                throw new AccountException("User found! Log-in instead.");
            }
        }

        return true;
    }

    public static String capitalizeWords(String str) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : str.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
