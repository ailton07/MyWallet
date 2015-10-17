package br.edu.ufam.ceteli.mywallet.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.activities.dialogs.DialogNoPhoto;
import br.edu.ufam.ceteli.mywallet.activities.dialogs.DialogPhoto;
import br.edu.ufam.ceteli.mywallet.classes.AdapterListView;
import br.edu.ufam.ceteli.mywallet.classes.Entrada;
import br.edu.ufam.ceteli.mywallet.classes.IUpdateListView;
import br.edu.ufam.ceteli.mywallet.classes.login.FacebookAccountConnection;
import br.edu.ufam.ceteli.mywallet.classes.login.GoogleAccountConnection;
import br.edu.ufam.ceteli.mywallet.classes.login.ILoginConnection;
import br.edu.ufam.ceteli.mywallet.classes.ocr.Utils;

import static br.edu.ufam.ceteli.mywallet.classes.ocr.Utils.getSaldoMes;



public class ResultActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnChartValueSelectedListener, IUpdateListView {
    // Conta
    private ILoginConnection loggedAccount = null;

    // Drawer
    private DrawerLayout drawerLayout = null;
    private NavigationView resultFrameDrawer = null;
    private ImageView toggleArrow = null;

    // Nova entrada (Botão Flutuante)
    private FloatingActionsMenu fabNewInput = null;

    // private ArrayAdapter<Entrada> adapter;
    private AdapterListView adapter;
    TextView orcamento, gastos, saldo, renda;
    Calendar c = Calendar.getInstance();

    // Show popup
    Point p;

    private List<String> mesano=new ArrayList<>();


    private LineChart mChart;

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
            } else if (type.contains("FACEBOOK")) {
                loggedAccount = FacebookAccountConnection.getInstance(null);
            }
        }

        // Drawer e Toolbar
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        //drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        resultFrameDrawer = (NavigationView) findViewById(R.id.resultFrameDrawer);
        resultFrameDrawer.inflateMenu(R.menu.menu_drawer);
        resultFrameDrawer.setNavigationItemSelectedListener(this);
        toggleArrow = (ImageView) findViewById(R.id.toggleArrow);
        toggleArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
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
        fabPhotoInput.setIcon(R.drawable.ic_photo_fab);
        fabPhotoInput.setSize(FloatingActionButton.SIZE_MINI);
        fabPhotoInput.setOnClickListener(fabPhotoOnClick());

        FloatingActionButton fabManualInput = (FloatingActionButton) findViewById(R.id.fabManualInput);
        fabManualInput.setIcon(R.drawable.ic_manual_fab);
        fabManualInput.setSize(FloatingActionButton.SIZE_MINI);
        fabManualInput.setOnClickListener(fabManualOnClick());

        initializeDB();

        List<Entrada> values = Entrada.getComments();

        adapter = new AdapterListView(this, values);

        //ListView lv = (ListView) findViewById(android.R.id.list);
        //setListAdapter(adapter);
        //lv.setAdapter(adapter);
        //lv.setAdapter(adapter);

        //List<Entrada> valuesDoMes = Entrada.getEntradasMesAno(10,2015);
        Log.d("Saldo", String.valueOf(getSaldoMes(10, 2015)));

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable value highlighting
        mChart.setHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        mChart.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
//        l.setYOffset(11f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setSpaceBetweenLabels(1);

        mesano.add("Jan/15");
        mesano.add("Fev/15");
        mesano.add("Mar/15");
        mesano.add("Abr/15");
        mesano.add("Mai/15");
        mesano.add("Jun/15");
        mesano.add("Jul/15");
        mesano.add("Ago/15");
        mesano.add("Set/15");
        mesano.add("Out/15");
        mesano.add("Nov/15");
        mesano.add("Dez/15");

        setData(12, 20.0f);


        orcamento = (TextView) findViewById(R.id.textView23);
        renda = (TextView) findViewById(R.id.textView24);
        gastos = (TextView) findViewById(R.id.textView25);
        saldo = (TextView) findViewById(R.id.textView27);

        orcamento.setText(String.valueOf(Utils.getOrcamentoTotalMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        renda.setText(String.valueOf(Utils.getSaldoOrcamentoTotal(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        gastos.setText(String.valueOf(Utils.getgastosMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));
        saldo.setText(String.valueOf(Utils.getSaldoMes(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR))));


    }

    protected void initializeDB() {
        com.activeandroid.Configuration.Builder configurationBuilder = new com.activeandroid.Configuration.Builder(this);
        configurationBuilder.addModelClasses(Entrada.class);
        configurationBuilder.addModelClasses(br.edu.ufam.ceteli.mywallet.classes.Entry.class);
        ActiveAndroid.initialize(configurationBuilder.create());
    }

        private void setData(int count,float range) {
        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            xVals.add(mesano.get(i));
        }

        ArrayList<Entry> yVals1 = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float mult = range / 2f;
            float val = (float) (Math.random() * mult) + 50;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals1.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals1, "Gastos mensais");
        set1.setAxisDependency(AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setCircleColor(Color.WHITE);
        set1.setLineWidth(2f);
        set1.setCircleSize(3f);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircles(false);
        set1.setDrawFilled(true);
        //set1.setFillFormatter(new MyFillFormatter(0f));
//        set1.setDrawHorizontalHighlightIndicator(false);
//        set1.setVisible(false);
//        set1.setCircleHoleColor(Color.WHITE);

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    public void restartArryaListAdapter(){
        List<Entrada> values = Entrada.getComments();
        adapter = new AdapterListView(this, values);
        ListView lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(adapter);
    }

    // Faz um update quando é adicionado um novo elemento a partir do dialogFragment
    @Override
    public void onListUpdated(Entrada newValueToAdd) {
        adapter.notifyDataSetInvalidated();
        adapter.add(newValueToAdd);
        adapter.notifyDataSetChanged();
    }

    private View.OnClickListener fabManualOnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNoPhoto dialogNoPhoto = new DialogNoPhoto();
                dialogNoPhoto.show(getSupportFragmentManager(), null);
                fabNewInput.collapse();
            }
        };
    }

    private View.OnClickListener fabPhotoOnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPhoto dialogPhoto = new DialogPhoto();
                dialogPhoto.show(getSupportFragmentManager(), null);
                fabNewInput.collapse();
            }
        };
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (menuItem.getItemId()) {
            case R.id.drawer_item_budget:
                startActivity(new Intent(ResultActivity.this, BudgetActivity.class));
                break;
            case R.id.drawer_item_report:
                startActivity(new Intent(ResultActivity.this, ReportsActivity.class));
                break;

            case R.id.drawer_item_planning:
                startActivity(new Intent(ResultActivity.this, GoalActivity.class));
                break;

            case R.id.drawer_item_settings:
                startActivity(new Intent(ResultActivity.this, SettingsActivity.class).putExtra("DBKey", loggedAccount.getAccountEmail()));
                break;

            case R.id.drawer_item_disconnect:
                loggedAccount.disconnect(ResultActivity.this);
                break;

            case R.id.drawer_item_revoke:
                loggedAccount.revoke(ResultActivity.this);
                break;
        }
        return true;
    }

    public void loginOptions(View view){
        if(resultFrameDrawer.getMenu().findItem(R.id.drawer_item_revoke) == null){
            resultFrameDrawer.getMenu().clear();
            resultFrameDrawer.inflateMenu(R.menu.menu_drawer_login);
            toggleArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_up));
        } else {
            resultFrameDrawer.getMenu().clear();
            resultFrameDrawer.inflateMenu(R.menu.menu_drawer);
            toggleArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_arrow_down));
        }
    }

    //******************************************************

    //***********************************
    // POPUP

    // Get the x and y position after the button is draw on screen
    // (It's important to note that we can't get the position in the onCreate(),
    // because at that stage most probably the view isn't drawn yet, so it will return (0, 0))

    public void popup() {

        int[] location = new int[2];
        Toolbar button = (Toolbar) findViewById(R.id.toolbar);

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        button.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = location[0];
        p.y = location[1];

        showPopup(ResultActivity.this, p);

    }


    // The method that displays the popup.
    private void showPopup(final Activity context, Point p) {
        int popupWidth = 250;
        int popupHeight = 225;

        // Inflate the popup_layout_up.xmlxml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_layout_up, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);


        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        // Getting a reference to Close button, and close the popup when clicked.
        Button close = (Button) layout.findViewById(R.id.close);

        Resources res = getResources();
        String[] dicasArray = res.getStringArray(R.array.dicas);
        TextView textoPopup = (TextView) layout.findViewById(R.id.textViewDicas);
        Random gerador = new Random();
        textoPopup.setText(dicasArray[gerador.nextInt(31)]);



        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //restartArryaListAdapter();

        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);

        //FIX crash se a Activity nao estiver ativa
        if(((seconds%2) == 0) & ((getWindow().getDecorView().getWindowVisibility() == View.GONE) )) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    popup();
                }
            }, 100);

        }
        Log.d("Popup", Integer.toString(seconds));
    }
}