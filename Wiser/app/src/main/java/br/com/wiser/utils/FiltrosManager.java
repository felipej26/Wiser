package br.com.wiser.utils;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.wiser.R;
import br.com.wiser.WiserApplication;

/**
 * Created by Jefferson on 03/09/2017.
 */

public class FiltrosManager {

    private class Filtro {
        public int codigo;
        public String descricao;
        public boolean selecionado;
        public int idView;
    }

    private Set<Filtro> listaFiltros;

    public FiltrosManager() {
        listaFiltros = new HashSet<>();
    }

    public void addFiltro(int codigo, String descricao) {
        addFiltro(codigo, descricao, false);
    }

    public void addFiltro(int codigo, String descricao, boolean selecionado) {
        Filtro filtro = new Filtro();
        filtro.codigo = codigo;
        filtro.descricao = descricao;
        filtro.selecionado = selecionado;

        listaFiltros.add(filtro);
    }

    public List<CheckBox> getFiltrosAsCheckBox(Context context) {
        List<CheckBox> listaChecks = new ArrayList<>();

        int idView = 1000;
        int marginFiltersDp = context.getResources().getDimensionPixelSize(R.dimen.margin_filters);

        for (Filtro filtro : listaFiltros) {
            filtro.idView = idView;

            CheckBox check = new CheckBox(new ContextThemeWrapper(WiserApplication.getAppContext(), R.style.AppCompatTema_CheckBokFilter));
            check.setText(filtro.descricao);
            check.setId(filtro.idView);

            // A partir da segunda Check
            if (idView != 1000) {
                final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(marginFiltersDp, 0, 0, 0);
                params.addRule(RelativeLayout.END_OF, idView - 1);

                check.setLayoutParams(params);
            }

            listaChecks.add(check);
            idView++;
        }

        return listaChecks;
    }
}
