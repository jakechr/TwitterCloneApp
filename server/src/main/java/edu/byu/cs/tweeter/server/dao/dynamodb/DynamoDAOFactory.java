package edu.byu.cs.tweeter.server.dao.dynamodb;

import edu.byu.cs.tweeter.server.dao.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.IFollowDAO;
import edu.byu.cs.tweeter.server.dao.IStatusDAO;
import edu.byu.cs.tweeter.server.dao.IUserDAO;

public class DynamoDAOFactory implements IDAOFactory {
    @Override
    public IFollowDAO getFollowDAO() {
        return new FollowDAODynamo();
    }

    @Override
    public IStatusDAO getStatusDAO() {
        return new StatusDAODynamo();
    }

    @Override
    public IUserDAO getUserDAO() {
        return new UserDAODynamo();
    }
}
