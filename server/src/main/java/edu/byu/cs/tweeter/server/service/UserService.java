package edu.byu.cs.tweeter.server.service;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.server.dao.IDAOFactory;
import edu.byu.cs.tweeter.server.dao.IUserDAO;

public class UserService {
    IDAOFactory daoFactory;

    public UserService(IDAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public AuthenticationResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        }

        User user = getUserDao().login(request);

        AuthToken authToken = daoFactory.getAuthTokenDAO().generateAuthToken(user);

        return new AuthenticationResponse(user, authToken);
    }

    public AuthenticationResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[BadRequest] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[BadRequest] Missing a password");
        } else if(request.getFirstName() == null) {
            throw new RuntimeException("[BadRequest] Missing a first name");
        }  else if(request.getLastName() == null) {
            throw new RuntimeException("[BadRequest] Missing a last name");
        }  else if(request.getImage() == null) {
            throw new RuntimeException("[BadRequest] Missing a profile picture");
        }

        request.setImage(daoFactory.getImageDAO().uploadImage(request.getImage(), request.getUsername()));


        User user = getUserDao().register(request);

        AuthToken authToken = daoFactory.getAuthTokenDAO().generateAuthToken(user);

        return new AuthenticationResponse(user, authToken);
    }

    public GetUserResponse getUser(GetUserRequest request) {
        if(request.getUserAlias() == null){
            throw new RuntimeException("[BadRequest] Missing a user alias");
        }
        return getUserDao().getUser(request);
    }

    public LogoutResponse logout(LogoutRequest request) {
        return daoFactory.getAuthTokenDAO().logout(request);
    }

    private IUserDAO getUserDao() {
        return daoFactory.getUserDAO();
    }
}
