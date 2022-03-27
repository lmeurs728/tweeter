package edu.byu.cs.tweeter.server.dao.dynamo;

import edu.byu.cs.tweeter.server.dao.*;

public class DynamoDAOFactory implements DAOFactory {
    @Override
    public FollowDAO getFollowDAO() {
        return new FollowDAODynamo();
    }

    @Override
    public StatusDAO getStatusDAO() {
        return new StatusDAODynamo();
    }

    @Override
    public UserDAO getUserDAO() {
        return new UserDAODynamo();
    }

    @Override
    public AuthDAO getAuthDAO() {
        return new AuthDAODynamo();
    }

    @Override
    public ImageDAO getImageDAO() {
        return new S3DAO();
    }
}
