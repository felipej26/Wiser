package br.com.wiser.models.procurarusuarios;

import java.util.LinkedList;

import br.com.wiser.features.usuario.Usuario;

/**
 * Created by Wesley on 03/04/2016.
 */
public class Pesquisa {

    private int idioma;
    private int fluencia;
    private int distancia;
    private LinkedList<Usuario> listaResultados = new LinkedList<Usuario>();

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

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public LinkedList<Usuario> getListaResultados() {
        return listaResultados;
    }

    public void setListaResultados(LinkedList<Usuario> listaResultados) {
        this.listaResultados = listaResultados;
    }

}
