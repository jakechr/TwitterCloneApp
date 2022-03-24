package edu.byu.cs.tweeter.server.service;

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
        if (!daoFactory.getAuthTokenDAO().authenticateCurrUserSession(request.getAuthToken())) {
            throw new RuntimeException("[BadRequest] The current user session is no longer valid. PLease logout and login again.");
        }

        User user = getUserDao().getUser(request.getUserAlias());

        return new GetUserResponse(user);
    }

    public LogoutResponse logout(LogoutRequest request) {
        // Don't authenticate so that the user can logout and refresh their session if their authToken
        // is expired.
        return daoFactory.getAuthTokenDAO().logout(request, true);
    }

    private IUserDAO getUserDao() {
        return daoFactory.getUserDAO();
    }
}
