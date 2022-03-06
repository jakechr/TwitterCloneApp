package edu.byu.cs.tweeter.client.presenter;


import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.View.AuthenticationView;

public class RegisterPresenter extends AuthenticationPresenter {

    private UserService userService;

    public RegisterPresenter(AuthenticationView view) {
        super(view);
        this.userService = new UserService();
    }

    @Override
    String getDescription() {
        return "Failed to register user";
    }

    public void register(Bitmap image, String firstName, String lastName, String alias, String password) {
        userService.register(image, firstName, lastName, alias, password, new AuthenticationObserver());
    }

    public void validateRegistration(String firstName, String lastName, String alias, String password, Drawable imageToUpload) {
        validateLoginInfo(alias, password);
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }

        if (imageToUpload == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }
}
