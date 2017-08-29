package br.com.wiser.features.contato;

import android.content.Context;
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

/**
 * Created by Jefferson on 23/09/2016.
 */
public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.ViewHolder> {

    private Context context;
    private List<Usuario> listaContatos;

    private IClickListener clickListener;

    public ContatosAdapter(Context context, List<Usuario> listaContatos) {
        this.context = context;
        this.listaContatos = listaContatos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.contatos_contato, parent, false);

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Usuario contato = listaContatos.get(position);

        holder.viewSeparator.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
        Utils.loadImageInBackground(context, contato.getUrlFotoPerfil(), holder.imgPerfil, holder.prgBarra);
        holder.lblNome.setText(contato.getNome());
    }

    @Override
    public int getItemCount() {
        return listaContatos.size();
    }

    public void setItems(List<Usuario> listaContatos) {
        this.listaContatos = listaContatos;
        notifyDataSetChanged();
    }

    public void setClickListener(IClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View viewSeparator;
        public ImageView imgPerfil;
        public ProgressBar prgBarra;
        public TextView lblNome;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            imgPerfil = (ImageView) itemView.findViewById(R.id.imgPerfil);
            prgBarra = (ProgressBar) itemView.findViewById(R.id.prgBarra);
            lblNome = (TextView) itemView.findViewById(R.id.lblNome);
            viewSeparator = itemView.findViewById(R.id.viewSeparator);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.itemClicked(view, getAdapterPosition());
            }
        }
    }
}
