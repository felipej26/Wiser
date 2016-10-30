package br.com.wiser.activity.forum;

import android.app.Activity;
import android.content.Context;
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
import br.com.wiser.business.forum.discussao.Discussao;
import br.com.wiser.business.forum.discussao.DiscussaoDAO;
import br.com.wiser.utils.UtilsDate;
import br.com.wiser.utils.Utils;
import android.widget.ProgressBar;

/**
 * Created by Jefferson on 16/05/2016.
 */
public class DiscussaoCardViewAdapter extends RecyclerView.Adapter<DiscussaoCardViewAdapter.ViewHolder> {

    private IDiscussao mCallback;
    private Context context;
    private List<DiscussaoDAO> listaDiscussoes;

    public DiscussaoCardViewAdapter(Activity activity, List<DiscussaoDAO> listaDiscussoes) {
        this.mCallback = (IDiscussao) activity;
        this.context = activity;
        this.listaDiscussoes = listaDiscussoes;
    }

    public DiscussaoCardViewAdapter(Activity activity, Object fragment, List<DiscussaoDAO> listaDiscussoes) {
        this.mCallback = (IDiscussao) fragment;
        this.context = activity;
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

        Utils.loadImageInBackground(context, objDiscussao.getUsuario().getPerfil().getUrlProfilePicture(), viewHolder.imgPerfil, viewHolder.prgBarra);
        viewHolder.lblIDDiscussao.setText("#" + objDiscussao.getId());
        viewHolder.lblAutorDiscussao.setText(objDiscussao.getUsuario().getPerfil().getFirstName());
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
                    mCallback.onClick(posicao);
                }
            });

            imgPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onClickPerfil(posicao);
                }
            });

            btnDesativar.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    mCallback.desativarDiscussao(posicao);
                }
            });

            btnCompartilhar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.compartilharDiscussao(v);
                }
            });
        }

        public void setPosicao(int posicao) {
            this.posicao = posicao;
        }
    }
}