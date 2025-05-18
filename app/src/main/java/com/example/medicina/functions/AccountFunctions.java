package com.example.medicina.functions;

public class AccountFunctions {
    public static boolean handleLogin(String username, String password) {
        // if database does not contain username, throw NoAccountException()

        // if username.password != password, throw WrongPasswordException()

        // return username.equals("testuser") && password.equals("password123");

        return true;
    }

    public static boolean handleSignUp(){
        return false;
    }

    /*
    public static int handleSignUp(){

        return 0;
    }
     */
}
