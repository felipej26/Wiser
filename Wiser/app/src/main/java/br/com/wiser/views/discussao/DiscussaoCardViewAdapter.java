package br.com.wiser.views.discussao;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import br.com.wiser.Sistema;
import br.com.wiser.R;
import br.com.wiser.models.forum.Discussao;
import br.com.wiser.models.usuario.Usuario;
import br.com.wiser.utils.UtilsDate;
import br.com.wiser.utils.Utils;
import android.widget.ProgressBar;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class DiscussaoCardViewAdapter extends RecyclerView.Adapter<DiscussaoCardViewAdapter.ViewHolder> {

    private IDiscussaoView view;
    private List<Discussao> listaDiscussoes;

    public DiscussaoCardViewAdapter(IDiscussaoView view) {
        this.view = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.forum_discussao_resultados, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Discussao objDiscussao = listaDiscussoes.get(position);
        Usuario usuario = null;
        String texto = objDiscussao.getDescricao();

        if (Sistema.getListaUsuarios().containsKey(objDiscussao.getUsuario())) {
            usuario = Sistema.getListaUsuarios().get(objDiscussao.getUsuario());
        }

        if(texto.length() > 80){
            texto = texto.substring(0, 80) + "...";
        }

        Utils.loadImageInBackground(view.getContext(), usuario.getPerfil().getUrlProfilePicture(), viewHolder.imgPerfil, viewHolder.prgBarra);
        viewHolder.lblIDDiscussao.setText("#" + objDiscussao.getId());
        viewHolder.lblAutorDiscussao.setText(usuario.getPerfil().getFirstName());
        viewHolder.lblTituloDiscussao.setText(Utils.decode(objDiscussao.getTitulo()));
        viewHolder.lblDescricaoDiscussao.setText(Utils.decode(texto));
        viewHolder.lblContRespostas.setText(
                view.getContext().getString(objDiscussao.getListaRespostas().size() == 1 ? R.string.resposta : R.string.respostas,
                        objDiscussao.getListaRespostas().size()));
        viewHolder.lblDataHora.setText(UtilsDate.formatDate(objDiscussao.getData(), UtilsDate.DDMMYYYY_HHMMSS));
        viewHolder.btnDesativar.setText(objDiscussao.isAtiva() ? view.getContext().getString(R.string.desativar) : view.getContext().getString(R.string.ativar));

        if (usuario.getUserID() != Sistema.getUsuario().getUserID()) {
            viewHolder.btnDesativar.setVisibility(View.INVISIBLE);
        }

        viewHolder.setPosicao(position);
    }

    @Override
    public int getItemCount() {
        if (listaDiscussoes == null) return 0;
        return listaDiscussoes.size();
    }

    public void setItems(LinkedList<Discussao> listaDiscussoes) {
        this.listaDiscussoes = listaDiscussoes;
        notifyDataSetChanged();
    }

    public Discussao getItem(int position) {
        return listaDiscussoes.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private int posicao;

        public TextView lblIDDiscussao;
        public ImageView imgPerfil;
        public ProgressBar prgBarra;
        public TextView lblAutorDiscussao;
        public TextView lblTituloDiscussao;
        public TextView lblDescricaoDiscussao;
        public TextView lblContRespostas;
        public TextView lblDataHora;
        public Button btnDesativar;
        public Button btnCompartilhar;

        public ViewHolder(View view) {
            super(view);

            lblIDDiscussao = (TextView) view.findViewById(R.id.lblIDDiscussao);
            imgPerfil = (ImageView) view.findViewById(R.id.imgPerfil);
            prgBarra = (ProgressBar) view.findViewById(R.id.prgBarra);
            lblAutorDiscussao = (TextView) view.findViewById(R.id.lblAutorDiscussao);
            lblTituloDiscussao = (TextView) view.findViewById(R.id.lblTituloDiscussao);
            lblDescricaoDiscussao = (TextView) view.findViewById(R.id.lblDescricaoDiscussao);
            lblContRespostas = (TextView) view.findViewById(R.id.lblContRespostas);
            lblDataHora = (TextView) view.findViewById(R.id.lblDataHora);
            btnDesativar = (Button) view.findViewById(R.id.btnDesativar);
            btnCompartilhar = (Button) view.findViewById(R.id.btnCompartilhar);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DiscussaoCardViewAdapter.this.view.onClick(posicao);
                }
            });

            imgPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DiscussaoCardViewAdapter.this.view.onClickPerfil(posicao);
                }
            });

            btnDesativar.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    DiscussaoCardViewAdapter.this.view.desativarDiscussao(posicao);
                }
            });

            btnCompartilhar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DiscussaoCardViewAdapter.this.view.compartilharDiscussao(v);
                }
            });
        }

        public void setPosicao(int posicao) {
            this.posicao = posicao;
        }
    }
}