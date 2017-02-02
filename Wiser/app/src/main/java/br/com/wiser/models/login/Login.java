package br.com.wiser.models.login;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Jefferson on 22/01/2017.
 */
public class Login {
    @SerializedName("facebook_id")
    private String facebookID;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("data_ultimo_acesso")
    private Date dataUltimoAcesso;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    public String getFacebookID() {
        return facebookID;
    }

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getDataUltimoAcesso() {
        return dataUltimoAcesso;
    }

    public void setDataUltimoAcesso(Date dataUltimoAcesso) {
        this.dataUltimoAcesso = dataUltimoAcesso;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
