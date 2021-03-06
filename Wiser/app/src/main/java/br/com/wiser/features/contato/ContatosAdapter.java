package br.com.wiser.features.contato;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import br.com.wiser.R;
import br.com.wiser.features.usuario.Usuario;
import br.com.wiser.interfaces.IClickListener;
import br.com.wiser.utils.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Jefferson on 23/09/2016.
 */
public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.ViewHolder> {

    private List<Usuario> listaContatos;
    private IClickListener clickListener;
    private IClickListener onPerfilClickListener;

    public ContatosAdapter(List<Usuario> listaContatos) {
        this.listaContatos = listaContatos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_contact, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Usuario contato = listaContatos.get(position);

        holder.setPosicao(position);
        holder.viewSeparator.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        Utils.loadImageInBackground(contato.getUrlFotoPerfil(), holder.imgPerfil, holder.prgBarra);
        holder.lblNome.setText(contato.getNome());
    }

    @Override
    public int getItemCount() {
        return listaContatos.size();
    }

    public void setOnPerfilClickListener(IClickListener onPerfilClickListener) {
        this.onPerfilClickListener = onPerfilClickListener;
    }

    public void setItems(List<Usuario> listaContatos) {
        this.listaContatos = listaContatos;
        notifyDataSetChanged();
    }

    public void setClickListener(IClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.viewSeparator) View viewSeparator;
        @BindView(R.id.imgPerfil) ImageView imgPerfil;
        @BindView(R.id.prgBarra) ProgressBar prgBarra;
        @BindView(R.id.lblNome) TextView lblNome;

        private int posicao;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            ButterKnife.bind(this, itemView);
        }

        public void setPosicao(int posicao) {
            this.posicao = posicao;
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClicked(view, getAdapterPosition());
            }
        }

        @OnClick(R.id.imgPerfil)
        public void onImgPerfilClicked() {
            if (onPerfilClickListener != null) {
                onPerfilClickListener.itemClicked(imgPerfil, posicao);
            }
        }
    }
}
