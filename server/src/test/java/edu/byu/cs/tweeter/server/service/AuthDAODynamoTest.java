package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.AuthDAO;
import edu.byu.cs.tweeter.server.dao.dynamo.AuthDAODynamo;
import org.junit.Assert;
import org.junit.Test;

public class AuthDAODynamoTest {
    private final AuthDAO authDAO = new AuthDAODynamo();

    @Test
    public void createAuthToken() {
        AuthToken authToken = authDAO.createAuthToken("@lmeurs");
        Assert.assertNotNull(authToken);
    }

    @Test
    public void verifyAuthToken() {
        AuthToken authToken = authDAO.createAuthToken("@lmeurs");
        Assert.assertNotNull(authToken);
        Assert.assertTrue(authDAO.verifyAuthToken(authToken));
    }

    @Test
    public void deleteAuthToken() {
        AuthToken authToken = authDAO.createAuthToken("@lmeurs");
        Assert.assertNotNull(authToken);
        authDAO.deleteAuthToken(authToken);
        Assert.assertFalse(authDAO.verifyAuthToken(authToken));
    }
}
