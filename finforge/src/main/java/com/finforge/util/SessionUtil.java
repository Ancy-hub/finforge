package com.finforge.util;

import com.finforge.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Utility class for managing the HTTP session attributes related to authentication.
 */
public final class SessionUtil {

    /** Session attribute key for the logged-in {@link User} object. */
    public static final String SESSION_USER    = "loggedInUser";
    /** Session attribute key for the logged-in user's integer ID. */
    public static final String SESSION_USER_ID = "userId";

    private SessionUtil() {
        // Utility class — no instances
    }

    /**
     * Stores the authenticated user in the session.
     * A new session is created if one does not already exist.
     */
    public static void setLoggedInUser(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_USER,    user);
        session.setAttribute(SESSION_USER_ID, user.getUserId());
    }

    /**
     * Returns the currently logged-in user, or {@code null} if no session exists.
     */
    public static User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(SESSION_USER);
    }

    /**
     * Returns the currently logged-in user's ID, or {@code -1} if not authenticated.
     */
    public static int getLoggedInUserId(HttpServletRequest request) {
        User user = getLoggedInUser(request);
        return (user != null) ? user.getUserId() : -1;
    }

    /**
     * Returns {@code true} when the request belongs to an authenticated session.
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        return getLoggedInUser(request) != null;
    }

    /**
     * Invalidates the current session, effectively logging the user out.
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
