package org.group3.csc325project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private static String loggedInUsername;
    private static String loggedInUserRole;
    private static Boolean twoFAEnabled;

    public static String getLoggedInUsername() {
        return loggedInUsername;
    }

    public static void setLoggedInUsername(String username) {
        SessionManager.loggedInUsername = username;
    }

    public static String getLoggedInUserRole() {
        logger.info("Retrieving user role: " + loggedInUserRole);
        return loggedInUserRole;
    }

    public static void setLoggedInUserRole(String role) {
        SessionManager.loggedInUserRole = role;
    }
    public static Boolean getTwoFAEnabled() {
        return twoFAEnabled;
    }
    public static void setTwoFAEnabled(Boolean enabled) {
        SessionManager.twoFAEnabled = enabled;
    }
    public static void clearUserSession() {
        loggedInUsername = null;
        loggedInUserRole = null;
        twoFAEnabled = null;
    }
}
