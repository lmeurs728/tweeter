package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoDAOFactory;

public class BaseService {
    // This tells the services which DAO to use
    protected final DAOFactory factory = new DynamoDAOFactory();
}
