package br.com.wiser.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import br.com.wiser.R;
import br.com.wiser.interfaces.ICallbackFinish;

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
        public Button button;
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

    public List<Button> getFiltrosAsButton(Context context, final ViewGroup parent, final View.OnClickListener onClickListener) {
        List<Button> listaButtons = new ArrayList<>();

        for (final Filtro filtro : listaFiltros) {

            if (filtro.selecionado || filtro.bloqueado) {
                if (filtro.button == null) {
                    filtro.button = (Button) LayoutInflater.from(context).inflate(R.layout.frame_button_filter, parent, false);
                    filtro.button.setText(filtro.descricao);
                    filtro.button.setEnabled(!filtro.bloqueado);

                    filtro.button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            parent.removeView(filtro.button);
                            filtro.selecionado = false;
                            onClickListener.onClick(v);
                        }
                    });
                }

                listaButtons.add(filtro.button);
            }
        }

        return listaButtons;
    }

    public void selecionarItens(Context context, final ICallbackFinish callback) {
        final Set<Integer> itensSelecionados = new HashSet<>();

        final SelecionarFiltros selecionarFiltros = new SelecionarFiltros();
        for (Filtro filtro : listaFiltros) {
            if (!filtro.selecionado) {
                selecionarFiltros.add(filtro.codigo, filtro.descricao);
            }
        }

        if (selecionarFiltros.getFiltros().size() > 0) {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Selecione os idiomas")
                    .setMultiChoiceItems(selecionarFiltros.get(), null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if (itensSelecionados.contains(which)) {
                                itensSelecionados.remove(which);
                            }
                            else {
                                itensSelecionados.add(which);

                            }
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            for (SelecionarFiltros.Filtro filtro : selecionarFiltros.getFiltros()) {
                                if (itensSelecionados.contains(filtro.codigoDialog)) {
                                    for (Filtro f : listaFiltros) {
                                        if (f.codigo == filtro.codigo) {
                                            f.selecionado = true;
                                        }
                                    }
                                }
                            }

                            callback.onFinish();
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            dialog.show();
        }
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

    public long getCountNaoSelecionados() {
        int cont = 0;

        for (Filtro filtro : listaFiltros) {
            if (!filtro.selecionado) {
                cont++;
            }
        }

        return cont;
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

    public void limparSelecionados(ViewGroup parent) {
        for (Filtro filtro : listaFiltros) {
            if (filtro.selecionado && !filtro.bloqueado) {

                if (filtro.checkBox != null)
                    filtro.checkBox.setChecked(false);

                if (filtro.button != null)
                    parent.removeView(filtro.button);

                filtro.selecionado = false;
            }
        }
    }

    private class SelecionarFiltros {

        private class Filtro {
            public int codigo;
            public int codigoDialog;
            public String descricao;
        }

        private List<Filtro> filtros = new ArrayList<>();

        private List<Filtro> getFiltros() {
            return filtros;
        }

        public void add(int codigo, String descricao) {
            Filtro filtro = new Filtro();
            filtro.codigo = codigo;
            filtro.descricao = descricao;
            filtros.add(filtro);
        }

        public CharSequence[] get() {
            CharSequence[] charSequence = new CharSequence[filtros.size()];
            for (int i = 0; i < filtros.size(); i++) {
                charSequence[i] = String.valueOf(filtros.get(i).descricao);
            }

            int codigoDialog = 0;
            for (CharSequence c : charSequence) {
                filtros.get(codigoDialog).codigoDialog = codigoDialog;
                codigoDialog++;
            }

            return charSequence;
        }
    }
}
