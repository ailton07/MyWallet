package br.edu.ufam.ceteli.mywallet.classes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.ufam.ceteli.mywallet.R;

/**
 * Created by rodrigo on 01/11/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{
    private List<Entrada> items = null;
    private String[] categories = null;
    private View view = null;

    public RecyclerViewAdapter(List<Entrada> list){
        items = list;
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_reports, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.tvDate.setText(items.get(position).getDataCompra());
        holder.tvDescription.setText(items.get(position).getDescricao());
        holder.tvPlace.setText(items.get(position).getEstabelecimento());
        holder.tvType.setText(view.getResources().getStringArray(R.array.categoria_array)[items.get(position).getCategoria()]);
        holder.tvValue.setText(Float.toString(items.get(position).getValor()));
        holder.ivType.setImageDrawable(items.get(position).getTipo() == 1? view.getResources().getDrawable(R.drawable.ic_spending) : view.getResources().getDrawable(R.drawable.ic_earning));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDescription = null;
        private TextView tvValue = null;
        private TextView tvType = null;
        private TextView tvDate = null;
        private TextView tvPlace = null;
        private ImageView ivType = null;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            tvValue = (TextView) itemView.findViewById(R.id.tvValue);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvPlace = (TextView) itemView.findViewById(R.id.tvPlace);
            ivType = (ImageView) itemView.findViewById(R.id.ivType);
        }
    }
}
