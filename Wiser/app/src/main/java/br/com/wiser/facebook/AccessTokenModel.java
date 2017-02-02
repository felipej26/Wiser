package br.com.wiser.facebook;

import com.google.gson.annotations.SerializedName;

import java.util.Collection;

/**
 * Created by Jefferson on 26/01/2017.
 */
public class AccessTokenModel {
    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("appID")
    private String applicationID;

    @SerializedName("userID")
    private String userID;

    @SerializedName("permissions")
    private String permissions;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
