package br.com.wiser.activity.forum;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.wiser.Sistema;
import br.com.wiser.R;
import br.com.wiser.activity.forum.discussao.ForumDiscussaoActivity;
import br.com.wiser.business.forum.discussao.Discussao;
import br.com.wiser.business.forum.discussao.DiscussaoDAO;
import br.com.wiser.dialogs.DialogPerfilUsuario;
import br.com.wiser.utils.UtilsDate;
import br.com.wiser.utils.Utils;
import android.widget.ProgressBar;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class DiscussaoCardViewAdapter extends RecyclerView.Adapter<DiscussaoCardViewAdapter.ViewHolder> {

    private IDiscussaoCardViewAdapterCallback mCallback;
    private Context context;
    private List<DiscussaoDAO> listaDiscussoes;

    public DiscussaoCardViewAdapter(Activity activity, List<DiscussaoDAO> listaDiscussoes) {
        this.mCallback = (IDiscussaoCardViewAdapterCallback) activity;
        this.context = (Context) activity;
        this.listaDiscussoes = listaDiscussoes;
    }

    public DiscussaoCardViewAdapter(Activity activity, Object fragment, List<DiscussaoDAO> listaDiscussoes) {
        this.mCallback = (IDiscussaoCardViewAdapterCallback) fragment;
        this.context = (Context) activity;
        this.listaDiscussoes = listaDiscussoes;
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

        Utils.loadImageInBackground(context, objDiscussao.getUsuario().getUrlProfilePicture(), viewHolder.imgPerfil, viewHolder.prgBarra);
        viewHolder.lblIDDiscussao.setText("#" + objDiscussao.getId());
        viewHolder.lblAutorDiscussao.setText(objDiscussao.getUsuario().getFirstName());
        viewHolder.lblTituloDiscussao.setText(objDiscussao.getTitulo());
        viewHolder.lblDescricaoDiscussao.setText(objDiscussao.getDescricao());
        viewHolder.lblContRespostas.setText(
                context.getString(objDiscussao.getListaRespostas().size() == 1 ? R.string.resposta : R.string.respostas,
                        objDiscussao.getListaRespostas().size()));
        viewHolder.lblDataHora.setText(UtilsDate.formatDate(objDiscussao.getDataHora(), UtilsDate.DDMMYYYY_HHMMSS));
        viewHolder.btnDesativar.setText(objDiscussao.isAtiva() ? context.getString(R.string.desativar) : context.getString(R.string.ativar));

        if(objDiscussao.getUsuario().getUserID() != Sistema.getUsuario(context).getUserID()){
            viewHolder.btnDesativar.setVisibility(View.INVISIBLE);
        }

        viewHolder.setPosicao(position);
    }

    @Override
    public int getItemCount() {
        return listaDiscussoes.size();
    }

    public Discussao getDiscussao(int posicao) {
        return listaDiscussoes.get(posicao);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemLayoutView.setOnClickListener(this);

            lblIDDiscussao = (TextView) itemLayoutView.findViewById(R.id.lblIDDiscussao);
            imgPerfil = (ImageView) itemLayoutView.findViewById(R.id.imgPerfil);
            prgBarra = (ProgressBar) itemLayoutView.findViewById(R.id.prgBarra);
            lblAutorDiscussao = (TextView) itemLayoutView.findViewById(R.id.lblAutorDiscussao);
            lblTituloDiscussao = (TextView) itemLayoutView.findViewById(R.id.lblTituloDiscussao);
            lblDescricaoDiscussao = (TextView) itemLayoutView.findViewById(R.id.lblDescricaoDiscussao);
            lblContRespostas = (TextView) itemLayoutView.findViewById(R.id.lblContRespostas);
            lblDataHora = (TextView) itemLayoutView.findViewById(R.id.lblDataHora);
            btnDesativar = (Button) itemLayoutView.findViewById(R.id.btnDesativar);
            btnCompartilhar = (Button) itemLayoutView.findViewById(R.id.btnCompartilhar);

            btnDesativar.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    mCallback.desativarDiscussao(listaDiscussoes.get(posicao));
                }
            });

            imgPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogPerfilUsuario dialog = new DialogPerfilUsuario();
                    dialog.mostrarDetalhes(v.getContext(), listaDiscussoes.get(posicao).getUsuario());
                }
            });

            btnCompartilhar.setTag(itemLayoutView);

            View.OnClickListener btnCompartilharListener = new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Utils.compartilharComoImagem((CardView) v.getTag());
                }
            };

            btnCompartilhar.setOnClickListener(btnCompartilharListener);
        }

        @Override
        public void onClick(View view) {

            Bundle bundle = new Bundle();
            bundle.putSerializable("discussao", listaDiscussoes.get(posicao));

            Intent i = new Intent(view.getContext(), ForumDiscussaoActivity.class);
            i.putExtra("discussoes", bundle);
            view.getContext().startActivity(i);
        }

        public void setPosicao(int posicao) {
            this.posicao = posicao;
        }
    }
}