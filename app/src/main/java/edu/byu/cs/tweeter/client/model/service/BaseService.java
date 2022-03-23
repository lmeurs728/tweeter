package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;

public class BaseService {

    protected static ServerFacade serverFacade;
    static ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }

}
