package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import java.io.*;
import java.util.Base64;

public class S3DAO implements ImageDAO {
    String bucketName = "lances-tweeter-images";
    final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_1).build();

    @Override
    public String uploadImage(String image, String alias) {
        InputStream is = new ByteArrayInputStream(getByteArray(image));
        String keyName = alias + ".png";
         ObjectMetadata metadata = new ObjectMetadata(); metadata.setContentType("image/png");
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, is, metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(putObjectRequest);
        return s3.getUrl(bucketName, keyName).toString();
    }

    private byte[] getByteArray(String image) {
        return Base64.getDecoder().decode(image);
    }
}
