package br.edu.ufam.ceteli.mywallet.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.edu.ufam.ceteli.mywallet.LoginClasses.FacebookAccountConnection;
import br.edu.ufam.ceteli.mywallet.LoginClasses.GoogleAccountConnection;
import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.Classes.Entrada;
import br.edu.ufam.ceteli.mywallet.Classes.NavigationDrawerFragment;


public class ResultActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, AdapterView.OnItemSelectedListener{
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private int radioClicado = 0;
    private String categoriaSpinnerSelecionado="";
    private ArrayAdapter<Entrada> adapter;

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

       adapter = new ArrayAdapter<Entrada>(this,
                android.R.layout.simple_list_item_1, values);

        ListView lv = (ListView) findViewById(android.R.id.list);
        //setListAdapter(adapter);
        lv.setAdapter(adapter);

        /*

        Teste

        Entrada entrada = new Entrada();
        entrada.setTipo(0);
        entrada.setEstabelecimento("Casa");
        entrada.setValor(12);
        entrada.setCategoria("Oias");

        entrada.setDescricao(tv.getText().toString());

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Date date = new Date();
        entrada.setDataInsercao(dateFormat.format(date).toString());

        entrada.setDataCompra(dateFormat.format(date).toString());
        //entrada.setIconeRid(R.drawable.com_facebook_button_icon);

        entrada.save();

        adapter.add(entrada);
        */

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


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
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
                        Date date = new Date();

                        entrada.setDataInsercao(dateFormat.format(date).toString());

                        //Data de Compra

                        DatePicker dataCompra = (DatePicker) v.findViewById(R.id.datePicker);
                        dataCompra.setCalendarViewShown(false);
                        Date dateDataCompra = new Date();
                        dateDataCompra.setDate(dataCompra.getDayOfMonth());
                        dateDataCompra.setMonth(dataCompra.getMonth());
                        dateDataCompra.setYear(dataCompra.getYear());

                        entrada.setDataCompra(dateFormat.format(dateDataCompra).toString());

                        entrada.save();

                        adapter.add(entrada);

                        //Log
                        Log.v("App123", descricao.getText().toString());
                        Log.v("App123", String.valueOf(entrada.getTipo()));
                        Log.v("App123", entrada.getDescricao());
                        Log.v("App123", String.valueOf(entrada.getValor()));
                        Log.v("App123", entrada.getEstabelecimento());
                        Log.v("App123", entrada.getCategoria());
                        Log.v("App123", entrada.getDataInsercao());
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

}
