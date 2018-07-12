/*
 * Copyright 2013 Heatbud LLC. All Rights Reserved.
 * This software is the property of Heatbud LLC. No part of this source code may be
 * copied or distributed without the written permission from Heatbud LLC.
 */
package com.heatbud.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Holds user information.
 * Implements the UserDetails interface to support authentication via Spring.
 */
@DynamoDBTable(tableName = "Users")
public class User implements Serializable, UserDetails {

	private static final long serialVersionUID = 1L;

	private String username; // email address
	private String userId; // (name part of the email).(a number to make this attribute unique)
	private String firstName;
	private String lastName;
	private String password;
	private String salt; // this will be the verification code for forgotPassword and verifyEmail
	private String role; // ROLE_NONE, ROLE_USER, ROLE_ADMIN
	private String ipAddress;
	private long createDate; // Milliseconds since Epoch
	public String gender;
	public String birthday;
	public String source; // will not be stored in the database
	public String fbId; // will be stored in Bloggers table
	public String about;
	public String contact;
	public String timeZone;
	public String profilePhoto;
	public String profileBG;
	public String error;

	@DynamoDBHashKey(attributeName="Username")
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	@DynamoDBAttribute(attributeName="UserId")
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	@DynamoDBAttribute(attributeName="FirstName")
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@DynamoDBAttribute(attributeName="LastName")
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@DynamoDBAttribute(attributeName="Password")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@DynamoDBAttribute(attributeName="Salt")
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}

	@DynamoDBAttribute(attributeName="Role")
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

	@DynamoDBAttribute(attributeName="IPAddress")
	public String getIPAddress() {
		return ipAddress;
	}
	public void setIPAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@DynamoDBAttribute(attributeName="CreateDate")
	public long getCreateDate() {
		return createDate;
	}
	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	@DynamoDBAttribute(attributeName="Gender")
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}

	@DynamoDBAttribute(attributeName="Birthday")
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	@DynamoDBIgnore
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	@DynamoDBIgnore
	public String getFbId() {
		return fbId;
	}
	public void setFbId(String fbId) {
		this.fbId = fbId;
	}

	@DynamoDBIgnore
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}

	@DynamoDBIgnore
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}

	@DynamoDBIgnore
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	@DynamoDBIgnore
	public String getProfilePhoto() {
		return profilePhoto;
	}
	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

	@DynamoDBIgnore
	public String getProfileBG() {
		return profileBG;
	}
	public void setProfileBG(String profileBG) {
		this.profileBG = profileBG;
	}

	@DynamoDBIgnore
	public String getError() {
		return error;
	}
	public void setError(String error) {
		if (this.error != null) {
			this.error = this.error + "; " + error;
		} else {
			this.error = error;
		}
	}

	/**
	 * All methods below this point are to implement the UserDetails interface.  
	 */
	@Transient
	@DynamoDBIgnore
	public Collection<GrantedAuthority> getAuthorities() {
		final GrantedAuthority auth = new SimpleGrantedAuthority(role);
		final Collection<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
		authorities.add(auth);
		return authorities;
	}

	@Transient
	@DynamoDBIgnore
	public boolean isAccountNonExpired() {
		return true;
	}

	@Transient
	@DynamoDBIgnore
	public boolean isAccountNonLocked() {
		return true;
	}

	@Transient
	@DynamoDBIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Transient
	@DynamoDBIgnore
	public boolean isEnabled() {
		if ( StringUtils.equals(role, "ROLE_USER") || StringUtils.equals(role, "ROLE_ADMIN") ) {
			return true;
		} else {
			return false;
		}
	}

}
