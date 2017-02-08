package br.com.wiser.models.usuario;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.Observable;

/**
 * Created by Jefferson on 06/04/2016.
 */
public class Usuario extends Observable implements Serializable {

    @SerializedName("id")
    private long userID;

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

    @SerializedName("idioma")
    private int idioma;

    @SerializedName("fluencia")
    private int fluencia;

    @SerializedName("status")
    private String status;

    @SerializedName("conta_ativa")
    private boolean contaAtiva = true;

    @SerializedName("setou_configuracoes")
    private boolean setouConfiguracoes = false;

    @SerializedName("isContato")
    private boolean isContato = false;

    private boolean isPerfilLoaded;

    private Perfil perfil = new Perfil();

    public Usuario(long userID) {
        super();
        this.userID = userID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

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

    public int getIdioma() {
        return idioma;
    }

    public void setIdioma(int idioma) {
        this.idioma = idioma;
    }

    public int getFluencia() {
        return fluencia;
    }

    public void setFluencia(int fluencia) {
        this.fluencia = fluencia;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isContaAtiva() {
        return contaAtiva;
    }

    public void setContaAtiva(boolean contaAtiva) {
        this.contaAtiva = contaAtiva;
    }

    public boolean isSetouConfiguracoes() {
        return setouConfiguracoes;
    }

    public void setSetouConfiguracoes(boolean setouConfiguracoes) {
        this.setouConfiguracoes = setouConfiguracoes;
    }

    public boolean isContato() {
        return isContato;
    }

    public void setContato(boolean contato) {
        isContato = contato;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;

        try {
            if (countObservers() > 0) {
                setChanged();
                notifyObservers();
            }
        }
        catch (Exception e) {
            Log.d("Usuario", "Nenhum observer disponivel");
        }
    }


    public boolean isPerfilLoaded() {
        return isPerfilLoaded;
    }

    public void setPerfilLoaded(boolean perfilLoaded) {
        isPerfilLoaded = perfilLoaded;
    }
}
