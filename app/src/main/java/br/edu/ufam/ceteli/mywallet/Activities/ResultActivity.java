package br.edu.ufam.ceteli.mywallet.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

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

import br.edu.ufam.ceteli.mywallet.Classes.AdapterListView;
import br.edu.ufam.ceteli.mywallet.Classes.Entrada;
import br.edu.ufam.ceteli.mywallet.Classes.Login.FacebookAccountConnection;
import br.edu.ufam.ceteli.mywallet.Classes.Login.GoogleAccountConnection;
import br.edu.ufam.ceteli.mywallet.Classes.Login.ILoginConnection;
import br.edu.ufam.ceteli.mywallet.Classes.OCR.CommsEngine;
import br.edu.ufam.ceteli.mywallet.Classes.OCR.OCRResposta;
import br.edu.ufam.ceteli.mywallet.Classes.OCR.OnServerRequestCompleteListener;
import br.edu.ufam.ceteli.mywallet.R;


public class ResultActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener{
    // Conta
    private ILoginConnection loggedAccount = null;

    // Drawer
    private DrawerLayout drawerLayout = null;
    private NavigationView resultFrameDrawer = null;
    private Handler drawerHandler = new Handler();

    // Nova entrada (Botão Flutuante)
    private FloatingActionsMenu fabNewInput = null;

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

        String type = getIntent().getExtras().getString("TYPE");
        if(type != null) {
            if (type.contains("GOOGLE")) {
                loggedAccount = GoogleAccountConnection.getInstance(null);
            } else if (type.contains("FACEBOOK")) {
                loggedAccount = FacebookAccountConnection.getInstance(null);
            }
        }

        // Drawer e Toolbar
        // TODO: Personalizar toolbar
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        resultFrameDrawer = (NavigationView) findViewById(R.id.resultFrameDrawer);
        resultFrameDrawer.inflateMenu(R.menu.menu_drawer);
        resultFrameDrawer.setNavigationItemSelectedListener(this);
        NavigationView resultFrameDefaultOptionsDrawer = (NavigationView) findViewById(R.id.resultFrameDefaultOptionsDrawer);
        resultFrameDefaultOptionsDrawer.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // TextView do Cabeçalho do Drawer
        TextView loggedName = (TextView) findViewById(R.id.tvHeaderName);
        TextView loggedEmail = (TextView) findViewById(R.id.tvHeaderEmail);
        loggedName.setText(loggedAccount.getAccountName());
        loggedEmail.setText(loggedAccount.getAccountEmail());

        // ImageView do Cabeçalho do Drawer
        ImageView cover = (ImageView) findViewById(R.id.coverPhoto);
        ImageView profile = (ImageView) findViewById(R.id.profileImage);

        loggedAccount.getAccountPicCover(cover);
        loggedAccount.getAccountPicProfile(profile);

        // Quando for mexer na lista, fechar esse FAB
        fabNewInput = (FloatingActionsMenu) findViewById(R.id.fabNewInput);

        FloatingActionButton fabPhotoInput = (FloatingActionButton) findViewById(R.id.fabPhotoInput);
        fabPhotoInput.setIcon(R.drawable.ic_camera_enhance_white_24dp);
        fabPhotoInput.setSize(FloatingActionButton.SIZE_MINI);
        fabPhotoInput.setOnClickListener(fabPhotoOnClick());

        FloatingActionButton fabManualInput = (FloatingActionButton) findViewById(R.id.fabManualInput);
        fabManualInput.setIcon(R.drawable.ic_mode_edit_white_24dp);
        fabManualInput.setSize(FloatingActionButton.SIZE_MINI);
        fabManualInput.setOnClickListener(fabManualOnClick());





























        List<Entrada> values = Entrada.getComments();


        //  Adapter Original
        //    adapter = new ArrayAdapter<Entrada>(this,
        //           android.R.layout.simple_list_item_1, values);

        adapter = new AdapterListView(this, values);

        ListView lv = (ListView) findViewById(android.R.id.list);
        //setListAdapter(adapter);
        lv.setAdapter(adapter);
        lv.setAdapter(adapter);



        mTitle = getTitle();




        //***************************************************
        // OCR


        CreateLocalImageFolder();

    }

    private View.OnClickListener fabManualOnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = (AlertDialog) onCreateDialog();
                dialog.show();
                fabNewInput.collapse();
            }
        };
    }

    private View.OnClickListener fabPhotoOnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chamar a camera aqui, não seria?
                AlertDialog dialog = (AlertDialog) onCreateDialogFoto();
                dialog.show();
                fabNewInput.collapse();
            }
        };
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);

        // Experiencia de usuário, por isso o delay (e a thread)
        drawerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (menuItem.getItemId()) {
                    case R.id.drawer_item_budget:
                        Toast.makeText(getApplicationContext(), "Inflar layout Orçamento / Chamar activity Orçamento", Toast.LENGTH_LONG).show();
                        break;

                    case R.id.drawer_item_report:
                        Toast.makeText(getApplicationContext(), "Inflar layout Relatório / Chamar activity Orçamento", Toast.LENGTH_LONG).show();
                        break;

                    case R.id.drawer_item_tips:
                        Toast.makeText(getApplicationContext(), "Inflar layout Dicas / Chamar activity Dicas", Toast.LENGTH_LONG).show();
                        break;

                    case R.id.drawer_item_settings:
                        Toast.makeText(getApplicationContext(), "Inflar layout Configurações / Chamar activity Configurações", Toast.LENGTH_LONG).show();
                        break;

                    case R.id.drawer_item_help:
                        Toast.makeText(getApplicationContext(), "Inflar layout Ajuda / Chamar activity Ajuda", Toast.LENGTH_LONG).show();
                        break;

                    case R.id.drawer_item_disconnect:
                        loggedAccount.disconnect(ResultActivity.this);
                        break;

                    case R.id.drawer_item_revoke:
                        loggedAccount.revoke(ResultActivity.this);
                        break;
                }
            }
        }, 200);
        return true;
    }

    public void loginOptions(View view){
        // Se a opçao desconectar não aparece, infle o menu login, senão volte
        if(resultFrameDrawer.getMenu().findItem(R.id.drawer_item_disconnect) == null){
            resultFrameDrawer.getMenu().clear();
            resultFrameDrawer.inflateMenu(R.menu.menu_drawer_login);
        } else {
            resultFrameDrawer.getMenu().clear();
            resultFrameDrawer.inflateMenu(R.menu.menu_drawer);
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

    // Dialog de Entrada customizado
    // View é a dialog_layout.xml
    private Dialog onCreateDialog() {
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

    private Dialog onCreateDialogFoto() {
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