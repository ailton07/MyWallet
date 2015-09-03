package br.edu.ufam.ceteli.mywallet.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.ufam.ceteli.mywallet.Classes.OCR.CommsEngine;
import br.edu.ufam.ceteli.mywallet.LoginClasses.FacebookAccountConnection;
import br.edu.ufam.ceteli.mywallet.LoginClasses.GoogleAccountConnection;
import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.Classes.AdapterListView;
import br.edu.ufam.ceteli.mywallet.Classes.Entrada;
import br.edu.ufam.ceteli.mywallet.Classes.NavigationDrawerFragment;
import br.edu.ufam.ceteli.mywallet.Classes.OCR.OCRResposta;
import br.edu.ufam.ceteli.mywallet.Classes.OCR.*;


public class ResultActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, AdapterView.OnItemSelectedListener{

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;
    // 0 -> Entrada e 1 -> Saída
    private int radioClicado = 0;
    private String categoriaSpinnerSelecionado="";

   // private ArrayAdapter<Entrada> adapter;
   private AdapterListView adapter;
    // ListView listView;

    // Dialog
    TextView estabelecimento;
    TextView valor;

    // OCR
    private final int RESPONSE_OK = 200;
    private final int IMAGE_PICKER_REQUEST = 1;
    private TextView picNameText;
    private String apiKey;
    private String langCode;
    private String fileName = "";

    CommsEngine commsEngine;
    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private String mImageFullPathAndName = "";
    private String localImagePath = "";
    private static final int OPTIMIZED_LENGTH = 1024;

    private  final  String idol_ocr_service = "https://api.idolondemand.com/1/api/async/ocrdocument/v1?";
    private  final  String idol_ocr_job_result = "https://api.idolondemand.com/1/job/result/";
    private String jobID = "";

    ProgressBar pbOCRReconizing;
    ImageView ivSelectedImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        /* Obviamente vai ser retirado */
        //Google
        TextView tv = (TextView) findViewById(R.id.textView);
        //ID
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        //Nome
        TextView tv3 = (TextView) findViewById(R.id.textView3);
        //Email
        TextView tv4 = (TextView) findViewById(R.id.textView4);
        TextView tv5 = (TextView) findViewById(R.id.tvEmail);

        tv5.setText(getIntent().getExtras().getString("TYPE"));

        if(getIntent().getExtras().getString("TYPE").contains("GOOGLE")){
            /* Já foi instanciado na primeira atividade, tão tu faz passar null ou this */
            tv.setText(GoogleAccountConnection.getInstance(null).getAccountID());
            tv2.setText(GoogleAccountConnection.getInstance(null).getAccountName());
            tv3.setText(GoogleAccountConnection.getInstance(this).getAccountEmail());
            /* Posteriormente poderemos usar isso para a UI */
            //tv4.setText(GoogleAccountConnection.getInstance(this).getAccountPicURL());

            /*
             * Oferecer uma dessas opções no Drawer
             * Revogar acesso ou Desconectar (Desconectar é am elhor opção)
             * Não precisa destruir a atividade, pois a classe já faz isso ao passar a atividade
             * como parametro
             */
            //GoogleAccountConnection.getInstance(this).revoke(this);
            //GoogleAccountConnection.getInstance(this).disconnect(this);
        }

        if(getIntent().getExtras().getString("TYPE").contains("FACEBOOK")){
            /* Já foi instanciado na primeira atividade, tão tu faz passar null ou this */
            tv.setText(FacebookAccountConnection.getInstance(null).getAccountID());
            tv2.setText(FacebookAccountConnection.getInstance(null).getAccountName());
            tv3.setText(FacebookAccountConnection.getInstance(null).getAccountEmail());
            /* Posteriormente poderemos usar isso para a UI */
            //tv4.setText(FacebookAccountConnection.getInstance(this).getAccountPicURL());

            /*
             * Oferecer uma dessas opções no Drawer
             * Revogar acesso ou Desconectar (Desconectar é am elhor opção)
             * Não precisa destruir a atividade, pois a classe já faz isso ao passar a atividade
             * como parametro
             */
            //FacebookAccountConnection.getInstance(this).revoke(this);
            //FacebookAccountConnection.getInstance(this).disconnect(this);
        }


        List<Entrada> values = Entrada.getComments();


    //  Adapter Original
    //    adapter = new ArrayAdapter<Entrada>(this,
    //           android.R.layout.simple_list_item_1, values);

        adapter = new AdapterListView(this, values);

        ListView lv = (ListView) findViewById(android.R.id.list);
        //setListAdapter(adapter);
        //lv.setAdapter(adapter);
        lv.setAdapter(adapter);



        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        //***************************************************
        // OCR


        CreateLocalImageFolder();

    }

    /*
     * Botão Logout, só pra demonstrar
     */
    public void logOut(View view){
        if(getIntent().getExtras().getString("TYPE").contains("GOOGLE")){
            /*
             * Ao desconectar também destruimos a atividade atual e iniciamos automaticamente a atividade de login (tela inicial)
             */
            //GoogleAccountConnection.getInstance(this).revoke(this);
            GoogleAccountConnection.getInstance(null).disconnect(this);
        }
        if(getIntent().getExtras().getString("TYPE").contains("FACEBOOK")){
            //FacebookAccountConnection.getInstance(null).revoke(this);
            FacebookAccountConnection.getInstance(null).disconnect(this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_MENU ) {
            // do nothing
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        //if (id == R.id.action_settings) {
        if (id == R.id.action_example) {
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Chain together various setter methods to set the dialog characteristics
            //builder.setMessage(R.string.dialog_message)
            //        .setTitle(R.string.dialog_title);

            // 3. Get the AlertDialog from create()
            //AlertDialog dialog = builder.create();
            AlertDialog dialog = (AlertDialog) onCreateDialog();
            dialog.show();
            return true;
        }
        else if(id == R.id.action_foto){
            // 1. Instantiate an AlertDialog.Builder with its constructor


            // 2. Chain together various setter methods to set the dialog characteristics
            //builder.setMessage(R.string.dialog_message)
            //        .setTitle(R.string.dialog_title);

            // 3. Get the AlertDialog from create()
            //AlertDialog dialog = builder.create();
            AlertDialog dialog = (AlertDialog) onCreateDialogFoto();
            dialog.show();
            return true;


        }

        return super.onOptionsItemSelected(item);
    }

    // Dialog de Entrada customizado
    // View é a dialog_layout.xml
    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setTitle(R.string.dialog_title);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        // Referencias:  http://stackoverflow.com/questions/30032005/access-buttons-in-custom-alert-dialog
        final View v = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(v)
                // Add action buttons
                .setPositiveButton("Adicionar ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Entrada entrada = new Entrada();

                        //Tipo
                        entrada.setTipo(radioClicado);

                        //Descrição
                        //TextView descricao = (TextView) findViewById(R.id.descricao);
                        TextView descricao = (TextView) v.findViewById(R.id.descricao);
                        entrada.setDescricao(descricao.getText().toString());

                        //Valor
                        TextView valor = (TextView) v.findViewById(R.id.valor);
                        entrada.setValor(Float.parseFloat(valor.getText().toString()));

                        //Estabelecimento
                        TextView estabelecimento = (TextView) v.findViewById(R.id.estabelecimento);
                        entrada.setEstabelecimento(estabelecimento.getText().toString());

                        //Categoria
                        //TextView categoria = (TextView) v.findViewById(R.id.categoria);
                        //entrada.setCategoria(categoria.getText().toString());
                        Spinner categoria = (Spinner) v.findViewById(R.id.categoria);
                        entrada.setCategoria(categoriaSpinnerSelecionado);

                        //Data de Inserção
                        //SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                        Date date = new Date();

                        //entrada.setDataInsercao(dateFormat.format(date).toString());

                        //Data de Compra

                        DatePicker dataCompra = (DatePicker) v.findViewById(R.id.datePicker);
                        dataCompra.setCalendarViewShown(false);

                        Date dateDataCompra = new Date();
                        dateDataCompra.setDate(dataCompra.getDayOfMonth());
                        dateDataCompra.setMonth(dataCompra.getMonth());
                        dateDataCompra.setYear(dataCompra.getYear()-1900); // http://stackoverflow.com/questions/9751050/simpledateformat-subclass-adds-1900-to-the-year


                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        entrada.setDataCompra(dateFormat.format(dateDataCompra).toString());

                        entrada.save();




                        adapter.add(entrada);
                        adapter.notifyDataSetChanged();

                        //Log
                        Log.v("App123", descricao.getText().toString());
                        Log.v("App123", String.valueOf(entrada.getTipo()));
                        Log.v("App123", entrada.getDescricao());
                        Log.v("App123", String.valueOf(entrada.getValor()));
                        Log.v("App123", entrada.getEstabelecimento());
                        Log.v("App123", entrada.getCategoria());
                        //Log.v("App123", entrada.getDataInsercaoFormatada().toString());
                        Log.v("App123", entrada.getDataCompra());

                    }
                })
                .setNegativeButton("Cancelar ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  LoginDialogFragment.this.getDialog().cancel();
                        dialog.cancel();
                    }
                });


        // Spinner configuration
        Spinner spinner = (Spinner) v.findViewById(R.id.categoria);
        ArrayAdapter<CharSequence> adapterS = ArrayAdapter.createFromResource(this,
                R.array.categoria_array, android.R.layout.simple_spinner_item);
        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterS);
        spinner.setOnItemSelectedListener(this);

        DatePicker dataCompra = (DatePicker) v.findViewById(R.id.datePicker);
        dataCompra.setCalendarViewShown(false);

        return builder.create();
    }

    public Dialog onCreateDialogFoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setTitle(R.string.dialog_title);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        // Referencias:  http://stackoverflow.com/questions/30032005/access-buttons-in-custom-alert-dialog
        final View v = inflater.inflate(R.layout.dialog_foto_layout, null);

        Button pickButton = (Button) v.findViewById((R.id.picImagebutton));
        picNameText = (TextView) v.findViewById(R.id.imageName);
        estabelecimento = (TextView) v.findViewById(R.id.estabelecimento);
        valor = (TextView) v.findViewById(R.id.valor);

        pbOCRReconizing = (ProgressBar) v.findViewById(R.id.pbocrrecognizing);
        ivSelectedImg = (ImageView) v.findViewById(R.id.imageView);

        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // Starting image picker activity
//                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), IMAGE_PICKER_REQUEST);
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECT_PICTURE);
            }
        });

        builder.setView(v).setPositiveButton("Adicionar ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Entrada entrada = new Entrada();

                //Tipo
                entrada.setTipo(1);

                //Descrição
                //TextView descricao = (TextView) findViewById(R.id.descricao);
                TextView descricao = (TextView) v.findViewById(R.id.descricao);
                entrada.setDescricao(estabelecimento.getText().toString());

                //Valor
                valor = (TextView) v.findViewById(R.id.valor);
                entrada.setValor(Float.parseFloat(valor.getText().toString()));

                //Estabelecimento
                estabelecimento = (TextView) v.findViewById(R.id.estabelecimento);
                entrada.setEstabelecimento(estabelecimento.getText().toString());

                //Categoria
                //TextView categoria = (TextView) v.findViewById(R.id.categoria);
                //entrada.setCategoria(categoria.getText().toString());
                Spinner categoria = (Spinner) v.findViewById(R.id.categoria);
                entrada.setCategoria(categoriaSpinnerSelecionado);

                //Data de Inserção
//                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                Date date = new Date();

                //entrada.setDataInsercao(dateFormat.format(date).toString());
                entrada.setDataInsercao(date);

                //Data de Compra

                DatePicker dataCompra = (DatePicker) v.findViewById(R.id.datePicker);
                dataCompra.setCalendarViewShown(false);
                Date dateDataCompra = new Date();
                dateDataCompra.setDate(dataCompra.getDayOfMonth());
                dateDataCompra.setMonth(dataCompra.getMonth());
                dateDataCompra.setYear(dataCompra.getYear());

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                entrada.setDataCompra(dateFormat.format(dateDataCompra).toString());

                entrada.save();

                adapter.add(entrada);
                adapter.notifyDataSetChanged();

                //Log
                Log.v("App123", estabelecimento.getText().toString());
                Log.v("App123", String.valueOf(entrada.getTipo()));
                Log.v("App123", entrada.getDescricao());
                Log.v("App123", String.valueOf(entrada.getValor()));
                Log.v("App123", entrada.getEstabelecimento());
                Log.v("App123", entrada.getCategoria());

                Log.v("App123", entrada.getDataCompra());


            }
        }).setNegativeButton("Cancelar ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //  LoginDialogFragment.this.getDialog().cancel();
                dialog.cancel();
            }
        });
            // Spinner configuration
            Spinner spinner = (Spinner) v.findViewById(R.id.categoria);
            ArrayAdapter<CharSequence> adapterS = ArrayAdapter.createFromResource(this,
                    R.array.categoria_array, android.R.layout.simple_spinner_item);
            adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterS);
            spinner.setOnItemSelectedListener(this);

            DatePicker dataCompra = (DatePicker) v.findViewById(R.id.datePicker);
            dataCompra.setCalendarViewShown(false);

            return builder.create();
    }

    //***************************
    // OCR

    public void trabalhaImagem(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE || requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mImageFullPathAndName = cursor.getString(columnIndex);
                cursor.close();
                jobID = "";
                File file = new File(mImageFullPathAndName);
                Bitmap mCurrentSelectedBitmap = decodeFile(file);

                if (mCurrentSelectedBitmap != null) {
                    // display the full size image
//                    ivSelectedImg.setImageBitmap(mCurrentSelectedBitmap);
                    // scale the image
                    // let check the resolution of the image.
                    // If it's too large, we can optimize it
                    int w = mCurrentSelectedBitmap.getWidth();
                    int h = mCurrentSelectedBitmap.getHeight();

                    int length = (w > h) ? w : h;
                    if (length > OPTIMIZED_LENGTH) {
                        // let's resize the image
                        float ratio = (float) w / h;
                        int newW, newH = 0;

                        if (ratio > 1.0) {
                            newW = OPTIMIZED_LENGTH;
                            newH = (int) (OPTIMIZED_LENGTH / ratio);
                        } else {
                            newH = OPTIMIZED_LENGTH;
                            newW = (int) (OPTIMIZED_LENGTH * ratio);
                        }
                        mCurrentSelectedBitmap = rescaleBitmap(mCurrentSelectedBitmap, newW, newH);
                    }
                    // let save the new image to our local folder
                    mImageFullPathAndName = SaveImage(mCurrentSelectedBitmap);
                    picNameText.setText(mImageFullPathAndName);
                }
            }
        }
        //*******************************
        DoStartOCR();
    }

    private void ParseSyncResponse(String response) {
        pbOCRReconizing.setVisibility(View.GONE);
        if (response == null) {
            Toast.makeText(this, "Unknown error occurred. Try again", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            JSONObject mainObject = new JSONObject(response);
            JSONArray textBlockArray = mainObject.getJSONArray("text_block");
            int count = textBlockArray.length();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    JSONObject texts = textBlockArray.getJSONObject(i);
                    String text = texts.getString("text");
                   // ivSelectedImg.setVisibility(View.GONE);
                  //  llResultContainer.setVisibility(View.VISIBLE);
                    OCRResposta reposta = new OCRResposta(text);
                    Log.v("ETSS", text);
                    estabelecimento.setText(reposta.getEmpresa());
                    valor.setText(reposta.getTotal());
                }
            }
            else
                Toast.makeText(this, "Not available", Toast.LENGTH_LONG).show();
        } catch (Exception ex){}
    }
    private void ParseAsyncResponse(String response) {
        pbOCRReconizing.setVisibility(View.GONE);
        if (response == null) {
            Toast.makeText(this, "Unknown error occurred. Try again", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            JSONObject mainObject = new JSONObject(response);
            JSONArray textBlockArray = mainObject.getJSONArray("actions");
            int count = textBlockArray.length();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    JSONObject actions = textBlockArray.getJSONObject(i);
                    String action = actions.getString("action");
                    String status = actions.getString("status");
                    JSONObject result = actions.getJSONObject("result");
                    JSONArray textArray = result.getJSONArray("text_block");
                    count = textArray.length();
                    if (count > 0) {
                        for (int n = 0; n < count; n++) {
                            JSONObject texts = textArray.getJSONObject(n);
                            String text = texts.getString("text");
                        //    ivSelectedImg.setVisibility(View.GONE);
                            //llResultContainer.setVisibility(View.VISIBLE);
                            OCRResposta reposta = new OCRResposta(text);
                            //edTextResult.setText(text);
                            Log.v("ETSS", reposta.getEmpresa());
                            Log.v("ETSS", reposta.getTotal());
                            estabelecimento.setText(reposta.getEmpresa());
                            valor.setText(reposta.getTotal());

                        }
                    }
                }
            } else {
                Toast.makeText(this, "Not available", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
        }
    }

    //**************************
    // HP

    public void DoStartOCR() {
        pbOCRReconizing.setVisibility(View.VISIBLE);
        if (jobID.length() > 0)
            getResultByJobId();
        else if (!mImageFullPathAndName.isEmpty()){
            Map<String,String> map =  new HashMap<String,String>();
            map.put("file", mImageFullPathAndName);
            //map.put("file", (picNameText.getText().toString()));
            String fileType = "image/jpeg";
            map.put("mode", "document_photo");
            commsEngine.ServicePostRequest(idol_ocr_service, fileType, map, new OnServerRequestCompleteListener() {
                @Override
                public void onServerRequestComplete(String response) {
                    try {
                        JSONObject mainObject = new JSONObject(response);
                        if (!mainObject.isNull("jobID")) {
                            jobID = mainObject.getString("jobID");
                            getResultByJobId();
                        } else
                            ParseSyncResponse(response);
                    } catch (Exception ex) {}
                }
                @Override
                public void onErrorOccurred(String error) {
                    // handle error
                }
            });
        } else
            Toast.makeText(this, "Please select an image.", Toast.LENGTH_LONG).show();
    }

    public void DoStartOCR(View v) {
        pbOCRReconizing.setVisibility(View.VISIBLE);
        if (jobID.length() > 0)
            getResultByJobId();
        else if (!mImageFullPathAndName.isEmpty()){
            Map<String,String> map =  new HashMap<String,String>();
            map.put("file", mImageFullPathAndName);
            //map.put("file", (picNameText.getText().toString()));
            String fileType = "image/jpeg";
            map.put("mode", "document_photo");
            commsEngine.ServicePostRequest(idol_ocr_service, fileType, map, new OnServerRequestCompleteListener() {
                @Override
                public void onServerRequestComplete(String response) {
                    try {
                        JSONObject mainObject = new JSONObject(response);
                        if (!mainObject.isNull("jobID")) {
                            jobID = mainObject.getString("jobID");
                            getResultByJobId();
                        } else
                            ParseSyncResponse(response);
                    } catch (Exception ex) {}
                }
                @Override
                public void onErrorOccurred(String error) {
                    // handle error
                }
            });
        } else
            Toast.makeText(this, "Please select an image.", Toast.LENGTH_LONG).show();
    }

    private void getResultByJobId() {
        String param = idol_ocr_job_result + jobID + "?";
        commsEngine.ServiceGetRequest(param, "", new
                OnServerRequestCompleteListener() {
                    @Override
                    public void onServerRequestComplete(String response) {
                        ParseAsyncResponse(response);
                    }

                    @Override
                    public void onErrorOccurred(String error) {
                        // handle error
                    }
                });
    }

    public Bitmap decodeFile(File file) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 1;
        int mImageRealWidth = options.outWidth;
        int mImageRealHeight = options.outHeight;
        Bitmap pic = null;
        try {
            pic = BitmapFactory.decodeFile(file.getPath(), options);
        } catch (Exception ex) {
            Log.e("MainActivity", ex.getMessage());
        }
        return pic;
    }

    public Bitmap rescaleBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    private String SaveImage(Bitmap image)
    {
        String fileName = localImagePath + "imagetoocr.jpg";
        try {

            File file = new File(fileName);
            FileOutputStream fileStream = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.JPEG, 100, fileStream);
            try {
                fileStream.flush();
                fileStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileName;
    }

    public void CreateLocalImageFolder()
    {
        if (localImagePath.length() == 0)
        {
            localImagePath = getFilesDir().getAbsolutePath() + "/orc/";
            File folder = new File(localImagePath);
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if (!success)
                Toast.makeText(this, "Cannot create local folder", Toast.LENGTH_LONG).show();
        }
    }

    //******************************************************

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        //categoriaSpinnerSelecionado = getResources().getStringArray(R.array.categoria_array)[1];
        categoriaSpinnerSelecionado = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        categoriaSpinnerSelecionado = parent.getItemAtPosition(0).toString();
    }


    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        //if (id == R.id.action_settings) {
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_opcao1:
                if (checked)
                    radioClicado = 0;
                    break;
            case R.id.radio_opcao2:
                if (checked)
                    radioClicado = 1;
                    break;
        }



    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((ResultActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (commsEngine == null)
            commsEngine = new CommsEngine();
    }

}
