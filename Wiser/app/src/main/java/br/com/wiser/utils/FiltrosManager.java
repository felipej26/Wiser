package br.com.wiser.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import br.com.wiser.R;

/**
 * Created by Jefferson on 03/09/2017.
 */

public class FiltrosManager {

    private class Filtro {
        public int codigo;
        public String descricao;
        public boolean selecionado;
        public boolean bloqueado;
        public CheckBox checkBox;
    }

    private Set<Filtro> listaFiltros;

    public FiltrosManager() {
        listaFiltros = new LinkedHashSet<>();
    }

    public void addFiltro(int codigo, String descricao) {
        addFiltro(codigo, descricao, false, false);
    }

    public void addFiltro(int codigo, String descricao, boolean selecionado, boolean bloqueado) {
        Filtro filtro = new Filtro();
        filtro.codigo = codigo;
        filtro.descricao = descricao;
        filtro.selecionado = selecionado;
        filtro.bloqueado = bloqueado;

        listaFiltros.add(filtro);
    }

    public List<CheckBox> getFiltrosAsCheckBox(Context context, ViewGroup parent) {
        List<CheckBox> listaChecks = new ArrayList<>();

        for (final Filtro filtro : listaFiltros) {
            filtro.checkBox = (CheckBox) LayoutInflater.from(context).inflate(R.layout.frame_checkbox_filter, parent, false);
            filtro.checkBox.setText(filtro.descricao);
            filtro.checkBox.setChecked(filtro.selecionado);
            filtro.checkBox.setEnabled(!filtro.bloqueado);

            filtro.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    filtro.selecionado = isChecked;
                }
            });

            listaChecks.add(filtro.checkBox);
        }

        return listaChecks;
    }

    public Set<Integer> getSelecionados() {
        Set<Integer> selecionados = new HashSet<>();

        for (Filtro filtro : listaFiltros) {
            if (filtro.selecionado) {
                selecionados.add(filtro.codigo);
            }
        }

        return selecionados;
    }

    public Set<String> getDescricoesSelecionadas() {
        Set<String> descricoes = new HashSet<>();

        for (Filtro filtro : listaFiltros) {
            if (filtro.selecionado) {
                descricoes.add(filtro.descricao);
            }
        }

        return descricoes;
    }

    public void limparSelecionados() {
        for (Filtro filtro : listaFiltros) {
            if (filtro.selecionado && !filtro.bloqueado) {
                filtro.checkBox.setChecked(false);
            }
        }
    }
}
