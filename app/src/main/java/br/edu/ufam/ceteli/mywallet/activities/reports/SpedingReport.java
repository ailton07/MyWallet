package br.edu.ufam.ceteli.mywallet.activities.reports;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.classes.DividerItemDecoration;
import br.edu.ufam.ceteli.mywallet.classes.Entrada;
import br.edu.ufam.ceteli.mywallet.classes.RecyclerScrollListener;
import br.edu.ufam.ceteli.mywallet.classes.RecyclerViewAdapter;

public class SpedingReport extends Fragment {
    private static Fragment instance = null;
    private RecyclerView recyclerView = null;

    public static Fragment getInstance() {
        return (instance == null)? instance = new SpedingReport() : instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);
        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setPadding(0, (int) (56 * getResources().getDisplayMetrics().density + 0.5f), 0, 0);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(new RecyclerViewAdapter(Entrada.getSaida()));
        recyclerView.addOnScrollListener(RecyclerScrollListener.getInstance(appBarLayout,null));
        registerForContextMenu(recyclerView);
        return recyclerView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.menu_recyclerview, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()) {
            switch (item.getItemId()) {
                case R.id.action_remove_item:
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Atencao");
                    builder.setMessage("Deseja excluir este item?");
                    builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((RecyclerViewAdapter) recyclerView.getAdapter()).remove(((RecyclerViewAdapter) recyclerView.getAdapter()).getClickedItem());
                            //TODO: Cadê o método de remoção do BD???
                        }
                    });
                    builder.setNegativeButton("Cancelar", null);
                    builder.create().show();
                    break;

                case R.id.action_edit_item:
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        try{
            RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
            recyclerView.scrollToPosition(0);
        } catch (Exception e){
            // Faz nada
        }
    }
}
