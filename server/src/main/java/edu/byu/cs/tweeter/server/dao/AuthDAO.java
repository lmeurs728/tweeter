package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthDAO {
    AuthToken createAuthToken(String alias);
    void deleteAuthToken(AuthToken token);
    boolean verifyAuthToken(AuthToken token);
}
