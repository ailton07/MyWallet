package br.edu.ufam.ceteli.mywallet.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import java.util.List;

import br.edu.ufam.ceteli.mywallet.Activities.Dialogs.DialogIn;
import br.edu.ufam.ceteli.mywallet.Classes.AdapterListView;
import br.edu.ufam.ceteli.mywallet.Classes.Entrada;
import br.edu.ufam.ceteli.mywallet.Classes.Login.FacebookAccountConnection;
import br.edu.ufam.ceteli.mywallet.Classes.Login.GoogleAccountConnection;
import br.edu.ufam.ceteli.mywallet.Classes.Login.ILoginConnection;
import br.edu.ufam.ceteli.mywallet.Classes.OCR.CommsEngine;
import br.edu.ufam.ceteli.mywallet.Classes.OCR.OCRImp;
import br.edu.ufam.ceteli.mywallet.Classes.OCR.OCRResposta;
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

    // 0 -> Entrada e 1 -> Saída
    private int categoriaSpinnerSelecionado;

    // private ArrayAdapter<Entrada> adapter;
    private AdapterListView adapter;

    // DialogIn
    DialogIn dialogIn;
    TextView estabelecimento;
    TextView valor;

    // OCR

    private TextView picNameText;

    CommsEngine commsEngine;
    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private String mImageFullPathAndName = "";
    private String localImagePath = "";
    private static final int OPTIMIZED_LENGTH = 1024;

    ProgressBar pbOCRReconizing;
    ImageView ivSelectedImg;

    OCRImp ocrImp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        setTitle("MyWallet");

        // ImageView do Cabeçalho do Drawer
        ImageView cover = (ImageView) findViewById(R.id.coverPhoto);
        ImageView profile = (ImageView) findViewById(R.id.profileImage);

        String type = getIntent().getExtras().getString("TYPE");
        if(type != null) {
            if (type.contains("GOOGLE")) {
                loggedAccount = GoogleAccountConnection.getInstance(null);
                cover.setBackgroundColor(Color.parseColor("#607D8B"));
            } else if (type.contains("FACEBOOK")) {
                loggedAccount = FacebookAccountConnection.getInstance(null);
                cover.setBackgroundColor(Color.parseColor("#3F51B5"));
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
        //lv.setAdapter(adapter);
        lv.setAdapter(adapter);


        //***************************************************
        // OCR


        ocrImp = new OCRImp(commsEngine, estabelecimento, valor, mImageFullPathAndName, pbOCRReconizing);
        CreateLocalImageFolder();

    }

    public void restartArryaListAdapter(){

        List<Entrada> values = Entrada.getComments();

        adapter = new AdapterListView(this, values);

        ListView lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(adapter);

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
                        //Toast.makeText(getApplicationContext(), "Inflar layout Relatório / Chamar activity Orçamento", Toast.LENGTH_LONG).show();
                        Intent it0 = new Intent(ResultActivity.this, RelatoriosActivity.class);
                        //TextView loggedEmail = (TextView) findViewById(R.id.tvHeaderEmail);
                        //it.putExtra("email", loggedEmail.getText().toString());
                        startActivity(it0);
                        break;

                    case R.id.drawer_item_tips:
                        Toast.makeText(getApplicationContext(), "Inflar layout Dicas / Chamar activity Dicas", Toast.LENGTH_LONG).show();
                        break;

                    case R.id.drawer_item_settings:
                        //Toast.makeText(getApplicationContext(), "Inflar layout Configurações / Chamar activity Configurações", Toast.LENGTH_LONG).show();
                        Intent it = new Intent(ResultActivity.this, SettingsActivity.class);
                        TextView loggedEmail = (TextView) findViewById(R.id.tvHeaderEmail);
                        it.putExtra("email", loggedEmail.getText().toString());
                        startActivity(it);
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

    //Verificar se pode ser excluído
    public void onSectionAttached(int number) {
        // Era utilizado pra mudar o nome do tittle quando uma opção do NavigationDrawer era escolhida
    /*    switch (number) {
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
        */
    }

    // DialogIn de Entrada customizado
    // View é a dialog_layout.xml
    private Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setTitle(R.string.dialog_title);

        dialogIn = new DialogIn(this, categoriaSpinnerSelecionado, builder, inflater, adapter);
        return dialogIn.gera();
    }

    // Não foi possível limpar
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
        ocrImp.setEstabelecimento(estabelecimento);
        valor = (TextView) v.findViewById(R.id.valor);
        ocrImp.setValor(valor);

        pbOCRReconizing = (ProgressBar) v.findViewById(R.id.pbocrrecognizing);
        ocrImp.setPbOCRReconizing(pbOCRReconizing);
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
                entrada.setDescricao(descricao.getText().toString());

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
                //SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
                Date date = new Date();

                entrada.setDataInsercao(dateFormat.format(date).toString());
                //entrada.setDataInsercao(date);

                //Data de Compra

                DatePicker dataCompra = (DatePicker) v.findViewById(R.id.datePicker);
                dataCompra.setCalendarViewShown(false);
                Date dateDataCompra = new Date();
                dateDataCompra.setDate(dataCompra.getDayOfMonth());
                dateDataCompra.setMonth(dataCompra.getMonth());
                dateDataCompra.setYear(dataCompra.getYear() - 1900);

                SimpleDateFormat dateFormat0 = new SimpleDateFormat("yyyyMMdd");
                //Log.d("Data", dateFormat0.format(dateDataCompra).toString());
                entrada.setDataCompra(dateFormat0.format(dateDataCompra).toString());

                //Nova Implementação da Entrada de data
                //entrada.setDataCompra(String.valueOf(dataCompra.getYear()-1900)+String.valueOf(dataCompra.getMonth())+String.valueOf(dataCompra.getDayOfMonth()));

                entrada.save();

                adapter.add(entrada);
                adapter.notifyDataSetChanged();

                //Log
                Log.v("App123", estabelecimento.getText().toString());
                Log.v("App123", String.valueOf(entrada.getTipo()));
                Log.v("App123", entrada.getDescricao());
                Log.v("App123", String.valueOf(entrada.getValor()));
                Log.v("App123", entrada.getEstabelecimento());
                //Log.v("App123", entrada.getCategoria());
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
                ocrImp.setmImageFullPathAndName(mImageFullPathAndName);
                cursor.close();
                //jobID = "";
                File file = new File(mImageFullPathAndName);
                Bitmap mCurrentSelectedBitmap = ocrImp.decodeFile(file);

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
                        mCurrentSelectedBitmap = ocrImp.rescaleBitmap(mCurrentSelectedBitmap, newW, newH);
                    }
                    // let save the new image to our local folder
                    mImageFullPathAndName = SaveImage(mCurrentSelectedBitmap);
                    ocrImp.setmImageFullPathAndName(mImageFullPathAndName);
                    picNameText.setText(mImageFullPathAndName);
                }
            }
        }
        //*******************************
        //DoStartOCR();
        ocrImp.DoStartOCR();
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
        categoriaSpinnerSelecionado = parent.getSelectedItemPosition();
        if(dialogIn!=null) {
            dialogIn.setCategoriaSpinnerSelecionado(categoriaSpinnerSelecionado);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
        categoriaSpinnerSelecionado = parent.getSelectedItemPosition();
        if(dialogIn!=null) {
            dialogIn.setCategoriaSpinnerSelecionado(categoriaSpinnerSelecionado);
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
        restartArryaListAdapter();
        if (commsEngine == null){
            commsEngine = new CommsEngine();
            ocrImp.setCommsEngine(commsEngine);
        }
    }

}