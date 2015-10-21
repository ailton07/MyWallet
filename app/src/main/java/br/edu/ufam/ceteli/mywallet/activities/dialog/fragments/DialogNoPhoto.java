package br.edu.ufam.ceteli.mywallet.activities.dialog.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.activities.ResultActivity;
import br.edu.ufam.ceteli.mywallet.classes.Entrada;
import br.edu.ufam.ceteli.mywallet.classes.IUpdateListView;
import br.edu.ufam.ceteli.mywallet.classes.ocr.Utils;

/**
 * Created by rodrigo on 15/10/15.
 */
public class DialogNoPhoto extends AppCompatDialogFragment {
    private View view = null;
    private Calendar calendar = Calendar.getInstance();
    private List<Map<String, String>> data = new ArrayList<>();
    private Map<String, String> item = new HashMap<>();
    private SimpleAdapter simpleAdapter = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Pega View do dialogo
        view = View.inflate(getContext(), R.layout.fragment_dialog_nophoto, null);

        // Spinner
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getContext(), R.array.categoria_array, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) view.findViewById(R.id.spinnerCategoryNoPhoto)).setAdapter(adapterSpinner);

        // Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.dialogNoPhotoToolbar);
        toolbar.setTitle("Nova Entrada");

        // Listview de UM ITEM para servir como botão para o datePicker
        ListView datePickerButton = (ListView) view.findViewById(R.id.datePickerNoPhoto);
        item.put("Title", "Data do gasto:");
        item.put("Date", dateFormat.format(calendar.getTime()));
        data.add(item);
        simpleAdapter = new SimpleAdapter(getContext(), data, android.R.layout.simple_list_item_2, new String[] {"Title", "Date"}, new int[] {android.R.id.text1, android.R.id.text2});
        datePickerButton.setAdapter(simpleAdapter);
        datePickerButton.setOnItemClickListener(onItemClickListener());

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
                Entrada entrada = new Entrada();
                SimpleDateFormat dateFormatSave = new SimpleDateFormat("yyyyMMdd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

                entrada.setTipo(((RadioGroup) view.findViewById(R.id.typeRadioGroup)).indexOfChild(view.findViewById(R.id.typeRadioGroup).findViewById(((RadioGroup) view.findViewById(R.id.typeRadioGroup)).getCheckedRadioButtonId())));
                entrada.setDescricao(((TextView) view.findViewById(R.id.etDescriptionNoPhoto)).getText().toString());
                entrada.setEstabelecimento(((TextView) view.findViewById(R.id.etPlaceNoPhoto)).getText().toString());
                entrada.setValor(Float.parseFloat(((TextView) view.findViewById(R.id.etValueNoPhoto)).getText().toString()));
                entrada.setCategoria(((Spinner) view.findViewById(R.id.spinnerCategoryNoPhoto)).getSelectedItemPosition());
                entrada.setDataInsercao(dateFormatSave.format(new Date()));
                entrada.setDataCompra(dateFormatIn.format(calendar.getTime()));
                entrada.save();

                ((IUpdateListView) getFragmentManager().findFragmentByTag("Main")).onListUpdated(entrada);
            }
        };
    }



    private AdapterView.OnItemClickListener onItemClickListener(){
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new DatePickerDialog(getContext(), dateSetListener(), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        };
    }

    private DatePickerDialog.OnDateSetListener dateSetListener(){
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                calendar.set(year, monthOfYear, dayOfMonth);
                item.put("Date", dateFormat.format(calendar.getTime()));
                simpleAdapter.notifyDataSetChanged();
            }
        };
    }
}

