package edu.byu.cs.tweeter.server.dao.dynamodb;

import static java.util.Base64.getDecoder;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

import javax.imageio.ImageIO;

import edu.byu.cs.tweeter.server.dao.IImageDAO;

public class ImageDAOS3 implements IImageDAO {

    @Override
    public String uploadImage(String image, String alias) {
        // Create AmazonS3 object for doing S3 operations
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion("us-east-1")
                .build();

        String fileName = alias + ".jpg";


        byte[] imageArray = Base64.getDecoder().decode(image);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageArray);

        String bucketName = "tweeterapp340";

        ObjectMetadata metadata = new ObjectMetadata();

        metadata.addUserMetadata("Content-Type", "image/jpeg");

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, alias, bis, metadata);
            s3.putObject(request);

            return s3.getUrl(bucketName, alias).toString();
        } catch (SdkClientException e) {
            e.printStackTrace();
            return "Failed";
        }
    }
}
