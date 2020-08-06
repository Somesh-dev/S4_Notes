package com.teams4.blog.Databases;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    //variables
    SharedPreferences userSession;
    SharedPreferences.Editor editor;
    Context context;

    //Session names
    public static final String SESSION_USERSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "rememberMe";

    //user login session
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "name";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PHONENO = "phoneNo";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";

    //remember Me session
    public static final String IS_REMEMBERME = "IsRememberMe";
    public static final String KEY_SESSIONPHONENO = "phoneNo";
    public static final String KEY_SESSIONPASSWORD = "password";


    public SessionManager(Context context, String sessionName) {
        this.context = context;
        userSession = this.context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = userSession.edit();
    }

    public void createLoginSession(String name, String username, String phoneNo, String email, String password) {

        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_NAME, name);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PHONENO, phoneNo);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_EMAIL, email);

        editor.commit();
    }

    public HashMap<String, String> getUserDetailFromSession() {

        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_NAME, userSession.getString(KEY_NAME, null));
        userData.put(KEY_USERNAME, userSession.getString(KEY_USERNAME, null));
        userData.put(KEY_EMAIL, userSession.getString(KEY_EMAIL, null));
        userData.put(KEY_PASSWORD, userSession.getString(KEY_PASSWORD, null));
        userData.put(KEY_PHONENO, userSession.getString(KEY_PHONENO, null));

        return userData;
    }

    public boolean checkLogin() {
        if (userSession.getBoolean(IS_LOGIN, true)) {
            return true;
        } else {
            return false;
        }
    }

    public void logoutUserFromSession() {
        editor.clear();
        editor.commit();
    }

    //RememberMe session
    public void createRememberMeSession(String phoneNo, String password) {

        editor.putBoolean(IS_REMEMBERME, true);

        editor.putString(KEY_SESSIONPASSWORD, password);
        editor.putString(KEY_SESSIONPHONENO, phoneNo);

        editor.commit();
    }

    public HashMap<String, String> RememberMeDetailFromSession() {

        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_SESSIONPASSWORD, userSession.getString(KEY_SESSIONPASSWORD, null));
        userData.put(KEY_SESSIONPHONENO, userSession.getString(KEY_SESSIONPHONENO, null));

        return userData;
    }

    public boolean checkRememberMe() {
        if (userSession.getBoolean(IS_REMEMBERME, false)) {
            return true;
        } else {
            return false;
        }
    }

}
