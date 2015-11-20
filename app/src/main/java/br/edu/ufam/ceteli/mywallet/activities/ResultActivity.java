package br.edu.ufam.ceteli.mywallet.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import java.util.Calendar;
import java.util.Random;

import br.edu.ufam.ceteli.mywallet.R;
import br.edu.ufam.ceteli.mywallet.activities.drawer.fragments.BudgetActivity;
import br.edu.ufam.ceteli.mywallet.activities.drawer.fragments.GoalActivity;
import br.edu.ufam.ceteli.mywallet.activities.drawer.fragments.MainScreenActivity;
import br.edu.ufam.ceteli.mywallet.activities.drawer.fragments.ReportsActivity;
import br.edu.ufam.ceteli.mywallet.classes.DesignUtils;
import br.edu.ufam.ceteli.mywallet.classes.RecyclerScrollListener;
import br.edu.ufam.ceteli.mywallet.classes.login.FacebookAccountConnection;
import br.edu.ufam.ceteli.mywallet.classes.login.GoogleAccountConnection;
import br.edu.ufam.ceteli.mywallet.classes.login.ILoginConnection;
import br.edu.ufam.ceteli.mywallet.classes.ocr.Utils;


public class ResultActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Conta
    private ILoginConnection loggedAccount = null;

    // Toolbar
    private Toolbar toolbar = null;

    // Drawer
    private DrawerLayout drawerLayout = null;
    private NavigationView resultFrameDrawer = null;
    private ImageView toggleArrow = null;

    // Show popup
    Point p;


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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        inflateDefaultFragment(MainScreenActivity.getInstance(), "Main");
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        drawerLayout.closeDrawers();

        // Um exemplo de redundancia :D
        switch (menuItem.getItemId()) {
            case R.id.drawer_item_home:
                inflateFragment(MainScreenActivity.getInstance(), "Main");
                break;

            case R.id.drawer_item_budget:
                inflateFragment(BudgetActivity.getInstance(), "Budget");
                break;

            case R.id.drawer_item_report:
                inflateFragment(ReportsActivity.getInstance(),"Report");
                break;

            case R.id.drawer_item_planning:
                inflateFragment(GoalActivity.getInstance(), "Planning");
                break;

            case R.id.drawer_item_disconnect:
                loggedAccount.disconnect(ResultActivity.this);
                break;

            case R.id.drawer_item_revoke:
                loggedAccount.revoke(ResultActivity.this);
                break;
        }

        removeTabBar();

        resultFrameDrawer.setCheckedItem(menuItem.getItemId());

        return true;
    }

    private void removeTabBar(){
        View view = findViewById(R.id.tabReports) != null? findViewById(R.id.tabReports) : findViewById(R.id.appBarBudget);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        if(view != null) {
            view.animate().translationY(-DesignUtils.getToolbarHeight(this)).setInterpolator(new DecelerateInterpolator(2)).start();
            ((ViewGroup) view.getParent()).removeView(view);
        }
        appBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    private void inflateFragment(Fragment fragment, String name){
        if(!fragment.isVisible()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.replace(R.id.frameFragment, fragment, name);
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                transaction.addToBackStack("Main");
            }
            transaction.commit();
        }
    }

    private void inflateDefaultFragment(Fragment fragment, String name){
        if(!fragment.isVisible()) {
            FragmentTransaction transactionHome = getSupportFragmentManager().beginTransaction();
            transactionHome.replace(R.id.frameFragment, fragment, name);
            transactionHome.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        }
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            resultFrameDrawer.setCheckedItem(resultFrameDrawer.getMenu().getItem(0).getItemId());
            removeTabBar();
            RecyclerScrollListener.removeObjectReference();
        }
        super.onBackPressed();
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

    public void settingOptions(View view){
        drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(ResultActivity.this, SettingsActivity.class).putExtra("DBKey", loggedAccount.getAccountEmail()));
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

    public void popup(String texto) {

        int[] location = new int[2];
        Toolbar button = (Toolbar) findViewById(R.id.toolbar);

        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        button.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        Point p = new Point();
        p.x = location[0];
        p.y = location[1];

        Utils.showPopup(ResultActivity.this, p, texto);
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
                    Calendar c = Calendar.getInstance();
                    int seconds = c.get(Calendar.SECOND);

                    int mes = c.get(Calendar.MONTH)+1; // String.valueOf(c.get(Calendar.MONTH)
                    int ano = c.get(Calendar.YEAR);
                    float proporcao = Utils.getSaidaMes(mes, ano)/Utils.getEntradaMes(mes, ano);

                    Log.d("Saldos Popup", mes + " " + ano);
                    Log.d("Saldos Popup", Utils.getSaidaMes(mes, ano) + " " + Utils.getEntradaMes(mes, ano));

                    if((seconds%4) == 0) {
                        if((proporcao>=0.25) && (proporcao<0.5)) {
                            Log.d("Saldos Popup", Utils.getEntradaMes(mes, ano) + " " + Utils.getSaidaMes(mes, ano));
                            popup("Voce já gastou mais de 25% da sua renda");
                        }
                        else if((proporcao>=0.5) && (proporcao<0.75)) {
                            Log.d("Saldos Popup", Utils.getEntradaMes(mes, ano) + " " + Utils.getSaidaMes(mes, ano));
                            popup("Voce já gastou mais de 50% da sua renda");
                        }
                        else if((proporcao>=0.75) && (proporcao<0.9)) {
                            Log.d("Saldos Popup", Utils.getEntradaMes(mes, ano) + " " + Utils.getSaidaMes(mes, ano));
                            popup("Voce já gastou mais de 75% da sua renda");
                        }
                        else if(proporcao >= 0.9) {
                            Log.d("Saldos Popup", Utils.getEntradaMes(mes, ano) + " " + Utils.getSaidaMes(mes, ano));
                            popup("Voce já gastou mais de 90% da sua renda");
                        }
                    }
                    else{
                        popup(); // popup("TEsteee");
                    }
                }
            }, 100);

        }
        Log.d("Popup", Integer.toString(seconds));
    }
}