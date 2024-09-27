package com.techelevator.tenmo.model;

import javax.validation.constraints.Size;

public class UserCredentials {

    @Size(max = 50)
    private String username;
    @Size(max = 50)
    private String password;

    public UserCredentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

