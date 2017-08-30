package br.com.wiser.features.usuario;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Jefferson on 06/04/2016.
 */
public class Usuario implements Serializable {

    private long id;
    private String nome;

    @SerializedName("primeiro_nome")
    private String primeiroNome;

    @SerializedName("data_nascimento")
    private Date dataNascimento;

    @SerializedName("facebook_id")
    private String facebookID;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("data_ultimo_acesso")
    private Date dataUltimoAcesso;

    @SerializedName("conta_ativa")
    private boolean contaAtiva;

    @SerializedName("setou_configuracoes")
    private boolean setouConfiguracoes;

    private double latitude;
    private double longitude;
    private int idioma;
    private int fluencia;
    private String status;

    public Usuario() { }

    public Usuario(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPrimeiroNome() {
        return primeiroNome;
    }

    public void setPrimeiroNome(String primeiroNome) {
        this.primeiroNome = primeiroNome;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
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

    public String getUrlFotoPerfil() {
        return "https://graph.facebook.com/" + facebookID + "/picture?type=large&w‌idth=720&height=720";
    }
}
