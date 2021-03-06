
package org.n52.shared.session;

/**
 * Builds session information for a user and a given session id.<br>
 * <br>
 * It it recommended to create a random session id which can not be guessed easily, e.g. via
 * 
 * <pre>
 * {@code 
 * SessionInfo sessionInfo = SessionInfoBuilder.aLoginSession(domain, sessionId)
 *                                  .forUser(username)
 *                                  .withUserId(userId)
 *                                  .withRole(role)
 *                                  .build();
 * </pre>
 */
public class SessionInfoBuilder {
    private SessionInfo sessionInfo;

    public SessionInfoBuilder(String sessionId) {
        this.sessionInfo = new SessionInfo(sessionId);
    }

    /**
     * @param sessionId
     *        the session id generated by the server.
     * @return a {@link SessionInfoBuilder} instance to build a {@link SessionInfo}.
     */
    public static SessionInfoBuilder aSessionInfo(String sessionId) {
        return new SessionInfoBuilder(sessionId);
    }

    public SessionInfoBuilder forUser(String username) {
        sessionInfo.setUsername(username);
        return this;
    }

    public SessionInfoBuilder withRole(String role) {
        sessionInfo.setRole(role);
        return this;
    }

    public SessionInfoBuilder withUserId(String userId) {
        sessionInfo.setUserId(userId);
        return this;
    }
    
    /**
     * Builds the session info object.
     * 
     * @return a {@link SessionInfo}
     */
    public SessionInfo build() {
        return sessionInfo;
    }
}