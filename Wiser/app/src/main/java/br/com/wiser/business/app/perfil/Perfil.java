package br.com.wiser.business.app.perfil;

import java.io.Serializable;

/**
 * Created by Jefferson on 30/10/2016.
 */
public class Perfil implements Serializable {

    private String fullName;
    private String firstName;
    private String urlProfilePicture;
    private int idade = 18;

    private boolean carregado;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUrlProfilePicture() {
        return urlProfilePicture;
    }

    public void setUrlProfilePicture(String urlProfilePicture) {
        this.urlProfilePicture = urlProfilePicture;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }
}
