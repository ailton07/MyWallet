package br.edu.ufam.ceteli.mywallet.activities.dialog.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.classes.Entrada;
import br.edu.ufam.ceteli.mywallet.classes.RecyclerViewAdapter;
import br.edu.ufam.ceteli.mywallet.classes.ocr.CommsEngine;
import br.edu.ufam.ceteli.mywallet.classes.ocr.OCRImp;

/**
 * Created by rodrigo on 11/10/15.
 */
public class DialogPhoto extends AppCompatDialogFragment{
    private View view = null;
    private Calendar calendar = Calendar.getInstance();
    private List<Map<String, String>> data = new ArrayList<>();
    private Map<String, String> item = new HashMap<>();
    private SimpleAdapter simpleAdapter = null;
    private Spinner spinner = null;
    private OCRImp ocrImp = null;
    private CommsEngine commsEngine = null;
    private String localImagePath = "";
    private String mImageFullPathAndName = "";
    private EditText place = null;
    private EditText value = null;
    private ProgressBar pbOCRReconizing = null;
    private ImageView ivSelectedImg = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Pega View do dialogo
        view = View.inflate(getContext(), R.layout.fragment_dialog_photo, null);
        place = (EditText) view.findViewById(R.id.etPlace);
        value = (EditText) view.findViewById(R.id.etValue);
        pbOCRReconizing = (ProgressBar) view.findViewById(R.id.pbocrrecognizing);

        // OCR
        ocrImp = new OCRImp(commsEngine, place, value, mImageFullPathAndName, pbOCRReconizing);
        CreateLocalImageFolder();
        ocrImp.setEstabelecimento(place);
        ocrImp.setValor(value);
        ocrImp.setPbOCRReconizing(pbOCRReconizing);
        ivSelectedImg = (ImageView) view.findViewById(R.id.dialogPhotoSelectedPhoto);

        // Spinner
        spinner = (Spinner) view.findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getContext(), R.array.categoria_array, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);

        // Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.dialogPhotoToolbar);
        toolbar.setTitle("Nova Entrada");
        toolbar.inflateMenu(R.menu.menu_dialog_photo);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener());

        // Listview de UM ITEM para servir como botão para o datePicker
        ListView datePickerButton = (ListView) view.findViewById(R.id.datePicker);
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

    @Override
    public void onResume() {
        super.onResume();
        ocrImp = new OCRImp(commsEngine, place, value, mImageFullPathAndName, pbOCRReconizing);
        if (commsEngine == null){
            commsEngine = new CommsEngine();
            ocrImp.setCommsEngine(commsEngine);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 || requestCode == 2) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mImageFullPathAndName = cursor.getString(columnIndex);
                ocrImp.setmImageFullPathAndName(mImageFullPathAndName);
                cursor.close();

                File file = new File(mImageFullPathAndName);
                Bitmap mCurrentSelectedBitmap = ocrImp.decodeFile(file);

                if (mCurrentSelectedBitmap != null) {
                    int w = mCurrentSelectedBitmap.getWidth();
                    int h = mCurrentSelectedBitmap.getHeight();

                    int length = (w > h) ? w : h;
                    if (length > 1024) {
                        float ratio = (float) w / h;
                        int newW, newH;

                        if (ratio > 1.0) {
                            newW = 1024;
                            newH = (int) (1024/ ratio);
                        } else {
                            newH = 1024;
                            newW = (int) (1024 * ratio);
                        }
                        mCurrentSelectedBitmap = ocrImp.rescaleBitmap(mCurrentSelectedBitmap, newW, newH);
                    }
                    mImageFullPathAndName = SaveImage(mCurrentSelectedBitmap);
                    ocrImp.setmImageFullPathAndName(mImageFullPathAndName);
                    ivSelectedImg.setImageBitmap(mCurrentSelectedBitmap);
                }
            }
        }
        ocrImp.DoStartOCR();
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

                // OCR é sempre gastos
                entrada.setTipo(1);
                entrada.setDescricao(((TextView) view.findViewById(R.id.etDescription)).getText().toString());
                entrada.setEstabelecimento(((TextView) view.findViewById(R.id.etPlace)).getText().toString());
                entrada.setValor(Float.parseFloat(((TextView) view.findViewById(R.id.etValue)).getText().toString()));
                entrada.setCategoria(spinner.getSelectedItemPosition());
                entrada.setDataInsercao(dateFormatSave.format(new Date()));
                entrada.setDataCompra(dateFormatIn.format(calendar.getTime()));
                entrada.save();
                RecyclerViewAdapter.getInstance(null).add(entrada);
            }
        };
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClickListener(){
        return new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_dialog_album:
                        Intent albumIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(albumIntent, 1);
                        break;

                    case R.id.action_dialog_camera:
                        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(cameraIntent, 2);
                        break;
                }
                return true;
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

    private void CreateLocalImageFolder() {
        if (localImagePath.length() == 0) {
            localImagePath = getActivity().getFilesDir().getAbsolutePath() + "/ocr/";
            File folder = new File(localImagePath);

            boolean success = true;

            if (!folder.exists()) {
                success = folder.mkdir();
            }

            if (!success) {
                Toast.makeText(getActivity(), "Cannot create local folder", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String SaveImage(Bitmap image) {
        String fileName = localImagePath + "imageoocr.jpg";
        try {
            File file = new File(fileName);
            FileOutputStream fileStream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fileStream);
            try {
                fileStream.flush();
                fileStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
