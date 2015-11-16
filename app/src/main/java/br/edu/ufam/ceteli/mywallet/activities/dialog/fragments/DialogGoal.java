package br.edu.ufam.ceteli.mywallet.activities.dialog.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.activities.GoalActivity;
import br.edu.ufam.ceteli.mywallet.classes.Entry;
import br.edu.ufam.ceteli.mywallet.classes.RecyclerViewBudgetAdapter;

/**
 * Created by ceteli on 10/11/2015.
 */
public class DialogGoal extends AppCompatDialogFragment {

    private View view = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        view = View.inflate(getContext(), R.layout.fragment_dialog_goal, null);

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

    private void savePreferences(String key, String value) {
        SharedPreferences sp = getActivity().getSharedPreferences("SELECTED_FILTER", 0);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        edit.commit();
    }

    private DialogInterface.OnClickListener onClickListenerConfirm(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                EditText meta = (EditText) view.findViewById(R.id.etGoal);
                TextView tvMeta = (TextView) getActivity().findViewById(R.id.tvMetaValue);
                tvMeta.setText(numberFormat.format(Float.valueOf(meta.getText().toString())));
                savePreferences("meta", meta.getText().toString());
            }
        };
    }


}
