/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.aws;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.StorageClass;
import com.heatbud.entity.HeatbudS3Object;
import com.heatbud.entity.S3File;
import com.heatbud.util.Configuration;

/**
 * This is a class for storing and retrieving data on S3.
 */
public class HeatbudS3Util {

	// Logger object
	private static final Logger logger = Logger.getLogger(HeatbudS3Util.class.getName());
	// Heatbud properties
	private static Configuration config = Configuration.getInstance();
	// AWS credentials
	private static AWSCredentials creds = new BasicAWSCredentials(config.getProperty("accessKey"), config.getProperty("secretKey"));

	/*
	 * The s3 client class is thread safe so we only ever need one static instance.
	 * While you can have multiple instances it is better to only have one because it's
	 * a relatively heavy weight class.
	 */
    private static AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
       	.withEndpointConfiguration(new EndpointConfiguration("https://s3.amazonaws.com", "us-west-2"))
       	.withCredentials(new AWSStaticCredentialsProvider(creds))
       	.build();

	/**
	 * Stores a given object in S3
	 * @param obj the data to be stored
	 * @param reducedRedundancy whether or not to use reduced redundancy storage
	 * @param acl a canned access control list indicating what permissions to store this object with (can be null to leave it set to default)
	 * @return SUCCESS or OBJECT_EXISTS
	 */
	public String store(HeatbudS3Object obj, boolean reducedRedundancy, CannedAccessControlList acl, boolean overwrite) {

		if ( !overwrite ) {
			try {
				// check if the object already exists in S3
				s3Client.getObjectMetadata(obj.getBucketName(), obj.getKey());
				// if exists, return error
				return "OBJECT_EXISTS";
			} catch (Exception e) {
				// ignore errors
			}
		}

		// define object metadata
		ObjectMetadata omd = new ObjectMetadata();
		omd.setContentLength(obj.getData().length);
		omd.setContentType(obj.getContentType());
		omd.setCacheControl(obj.getCacheControl());

		// define request and set reduced redundancy
		ByteArrayInputStream is = new ByteArrayInputStream(obj.getData());
		PutObjectRequest request = new PutObjectRequest(obj.getBucketName(), obj.getKey(), is, omd);
		if (reducedRedundancy) request.setStorageClass(StorageClass.ReducedRedundancy);

		// put object into s3
		s3Client.putObject(request);
		if ( acl != null ) s3Client.setObjectAcl(obj.getBucketName(), obj.getKey(), acl);

		IOUtils.closeQuietly(is);
		return "SUCCESS";

	}

	/**
	 * Reads Object Data from S3 storage
	 * @param s3Store the s3 object to be loaded from the store
	 * @return input stream for reading in the raw object
	 * @throws IOException
	 */
	public InputStream get(HeatbudS3Object s3Store) throws IOException {
		S3Object s3 = s3Client.getObject(s3Store.getBucketName(),s3Store.getKey());
		return s3.getObjectContent();
	}

	/**
	 * Deletes the specified S3 key from the S3 bucket.
	 * @param bucketName Name of the s3 bucket
	 * @param key Key of the object
	 */
	public void delete(String bucketName, String key) {

	    s3Client.deleteObject(bucketName,key);

	}

	/**
	 * Deletes the specified S3 key from the S3 bucket.  If the key
	 * has child S3 objects, it will recursively delete the underlying objects.
	 * @param bucketName Name of the s3 bucket
	 * @param key Key of the object or the starting path of the objects
	 */
	public void deleteRecursive(String bucketName, String key) {

		// Exit if the key is null. We don't want to delete everything in the bucket.
		if (key == null || key.equals("")) {
			logger.log(Level.WARNING,"Empty storage path passed to delete method");
			return;
		}

		// Go through the store structure and delete child objects
		ObjectListing listing = s3Client.listObjects(bucketName, key);
		while (true) {
			List<S3ObjectSummary> objectList = listing.getObjectSummaries();
			for (S3ObjectSummary summary:objectList) {
			    s3Client.deleteObject(bucketName,summary.getKey());
			}
			if (listing.isTruncated()) {
				listing = s3Client.listNextBatchOfObjects(listing);
			}
			else {
				break;
			}
		}

	}

	/**
	 * Obtain a presigned URL that will expire on the given date.
	 * @param s3Store the S3 object for which to obtain a presigned url
	 * @param expirationDate date when the presigned url should expire
	 * @return the signed URL
	 */
	public URL getSignedUrl(HeatbudS3Object s3Store, Date expirationDate) {
		return s3Client.generatePresignedUrl(s3Store.getBucketName(), s3Store.getKey(), expirationDate);
	}

	/**
	 * Obtain resource URL of the object stored in S3
	 * @param bucket name of the S3 bucket
	 * @param key key of the S3 object
	 * @return the resource URL
	 */
	public String getResourceUrl(String bucket, String key) {
	    return s3Client.getUrl(bucket, key).toString();
	}

	/**
	 * Lists folders in a given bucket at a given path
	 * @param bucket name of the S3 bucket
	 * @param prefix starting path for the listing
	 * @return list of folders
	 */
	public List<String> listFolders(String bucket, String prefix) {

		ListObjectsRequest lor = new ListObjectsRequest()
			.withBucketName(bucket)
			.withPrefix(prefix)
			.withDelimiter("/");
		ObjectListing ol = s3Client.listObjects(lor);
		return ol.getCommonPrefixes();

	}

	/**
	 * Lists files in a given bucket at a given path
	 * @param bucket name of the S3 bucket
	 * @param prefix starting path for the listing
	 * @return list of files
	 */
	public List<S3File> listFiles(String bucket, String prefix) {

		Calendar c = Calendar.getInstance();
		List<S3File> fileList = new ArrayList<S3File>();

		ListObjectsRequest lor = new ListObjectsRequest()
			.withBucketName(bucket)
			.withPrefix(prefix);
		ObjectListing ol;

		do {
			ol = s3Client.listObjects(lor);
			for (S3ObjectSummary os : ol.getObjectSummaries() ) {
				if ( !StringUtils.equals(os.getKey(), prefix) ) { // don't add the directory name
					S3File file = new S3File();
					file.setName(os.getKey());
					c.setTime(os.getLastModified());
					file.setDate(c.getTimeInMillis());
					fileList.add(file);
				}
			}
			lor.setMarker(ol.getNextMarker());
		} while (ol.isTruncated());

		return fileList;

	}

}
