package br.edu.ufam.ceteli.mywallet.activities.dialog.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observer;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.classes.Entry;
import br.edu.ufam.ceteli.mywallet.classes.RecyclerViewBudgetAdapter;

/**
 * Created by rodrigo on 08/11/15.
 */
public class DialogBudget extends AppCompatDialogFragment {
    private View view = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = View.inflate(getContext(), R.layout.fragment_dialog_budget, null);

        // Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.dialogBudgetToolbar);
        toolbar.setTitle("Nova Entrada");

        // O dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(view);
        builder.setPositiveButton("Adicionar", onClickListenerConfirm());
        builder.setNegativeButton("Cancelar", onClickListenerCancel());
        return builder.create();
    }

    private DialogInterface.OnClickListener onClickListenerCancel(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        };
    }

    private DialogInterface.OnClickListener onClickListenerConfirm(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Entry entry = new Entry();
                if(getFragmentManager().findFragmentById(R.id.frameFragment) instanceof Observer)
                    entry.addObserverClass((Observer) getFragmentManager().findFragmentById(R.id.frameFragment));
                EditText etBudget = (EditText) view.findViewById(R.id.etGoal);
                EditText etBonus = (EditText) view.findViewById(R.id.etBonus);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                Date date = new Date();
                float budget = 0, bonus = 0;

                if(!etBudget.getText().toString().equals("")){
                    budget = Float.parseFloat(etBudget.getText().toString());
                }

                if(!etBonus.getText().toString().equals("")){
                    bonus = Float.parseFloat(etBonus.getText().toString());
                }

                entry.setOrcamento(budget);
                entry.setBonus(bonus);
                entry.setTotal(budget + bonus);
                entry.setDataOrcamento(dateFormat.format(date));
                entry.salvar();

                RecyclerViewBudgetAdapter.getInstance(null).add(entry);
            }
        };
    }
}
