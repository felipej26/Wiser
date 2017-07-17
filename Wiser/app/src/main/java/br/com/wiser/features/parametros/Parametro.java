package br.com.wiser.features.parametros;

/**
 * Created by Jefferson on 16/07/2017.
 */

public class Parametro {
    private CodigoParametro id;
    private String descricao;
    private String parametro;

    public Parametro() { }

    public Parametro(CodigoParametro id, String descricao, String parametro) {
        this.id = id;
        this.descricao = descricao;
        this.parametro = parametro;
    }

    public CodigoParametro getId() {
        return id;
    }

    public void setId(CodigoParametro id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }
}
