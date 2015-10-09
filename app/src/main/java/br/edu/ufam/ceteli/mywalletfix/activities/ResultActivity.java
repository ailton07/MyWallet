package br.edu.ufam.ceteli.mywalletfix.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import br.edu.ufam.ceteli.mywalletfix.R;
import br.edu.ufam.ceteli.mywalletfix.classes.login.FacebookAccountConnection;
import br.edu.ufam.ceteli.mywalletfix.classes.login.GoogleAccountConnection;
import br.edu.ufam.ceteli.mywalletfix.classes.login.ILoginConnection;

/**
 * Created by rodrigo on 08/10/15.
 */
public class ResultActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    // Conta
    private ILoginConnection loggedAccount = null;

    // Drawer
    private DrawerLayout drawerLayout = null;
    private NavigationView resultFrameDrawer = null;
    private Handler drawerHandler = new Handler();
    private ImageView toggleArrow = null;

    // Nova entrada (Botão Flutuante)
    private FloatingActionsMenu fabNewInput = null;

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
                cover.setBackgroundColor(Color.parseColor("#607D8B"));
            }
        }

        // Drawer e Toolbar
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        //drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        resultFrameDrawer = (NavigationView) findViewById(R.id.resultFrameDrawer);
        resultFrameDrawer.inflateMenu(R.menu.menu_drawer);
        resultFrameDrawer.setNavigationItemSelectedListener(this);
        toggleArrow = (ImageView) findViewById(R.id.toggleArrow);
        //toggleArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_24dp));
        NavigationView resultFrameDefaultOptionsDrawer = (NavigationView) findViewById(R.id.resultFrameDefaultOptionsDrawer);
        resultFrameDefaultOptionsDrawer.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);

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
        //fabPhotoInput.setIcon(R.drawable.ic_camera_enhance_white_24dp);
        fabPhotoInput.setSize(FloatingActionButton.SIZE_MINI);
        fabPhotoInput.setOnClickListener(fabPhotoOnClick());

        FloatingActionButton fabManualInput = (FloatingActionButton) findViewById(R.id.fabManualInput);
        //fabManualInput.setIcon(R.drawable.ic_mode_edit_white_24dp);
        fabManualInput.setSize(FloatingActionButton.SIZE_MINI);
        fabManualInput.setOnClickListener(fabManualOnClick());
    }

    private View.OnClickListener fabManualOnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chamar dialogo manual
                fabNewInput.collapse();
            }
        };
    }

    private View.OnClickListener fabPhotoOnClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chamar dialogo foto
                fabNewInput.collapse();
            }
        };
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (menuItem.getItemId()) {
            case R.id.drawer_item_budget:
                break;

            case R.id.drawer_item_report:
                startActivity(new Intent(ResultActivity.this, ReportsActivity.class));
                break;

            case R.id.drawer_item_planning:
                break;

            case R.id.drawer_item_settings:
                startActivity(new Intent(ResultActivity.this, SettingsActivity.class).putExtra("DBKey", loggedAccount.getAccountEmail()));
                break;

            case R.id.drawer_item_disconnect:
                loggedAccount.disconnect(this);
                break;

            case R.id.drawer_item_revoke:
                loggedAccount.revoke(this);
                break;
        }
        return true;
    }

    public void loginOptions(View view){
        if(resultFrameDrawer.getMenu().findItem(R.id.drawer_item_revoke) == null){
            resultFrameDrawer.getMenu().clear();
            resultFrameDrawer.inflateMenu(R.menu.menu_drawer_login);
            //toggleArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_white_24dp));
        } else {
            resultFrameDrawer.getMenu().clear();
            resultFrameDrawer.inflateMenu(R.menu.menu_drawer);
            //toggleArrow.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_24dp));
        }
    }
}
