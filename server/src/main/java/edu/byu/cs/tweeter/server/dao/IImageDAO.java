package edu.byu.cs.tweeter.server.dao;

import java.io.IOException;

public interface IImageDAO {
    String uploadImage(String image, String alias);
}
