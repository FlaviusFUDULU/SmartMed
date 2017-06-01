package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ffudulu.licenta.Initialization;
import com.example.ffudulu.licenta.R;
import com.example.ffudulu.licenta.SubmitPersonalData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import transformations.CircleTransform;

public class MainDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mTxtViewEmail;
    private TextView mTxtViewName;
    private NavigationView mNavHeaderView;
    private View mHeaderView;
    private FirebaseUser firebaseUser;
    private ImageView mAccountImageView;

    ///???
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    //???

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        toolbar.setStatusBarTranslucentt(true);

        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //FROM HERE
        //Verify if data is complete
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (TextUtils.isEmpty(firebaseUser.getDisplayName())){
            Intent mPData = new Intent(MainDrawer.this, SubmitPersonalData.class);
            startActivity(mPData);
        }
        //end

        //Initialize data
        mNavHeaderView = (NavigationView) findViewById(R.id.nav_view);
        mHeaderView = navigationView.getHeaderView(0);

        mTxtViewEmail = (TextView) mHeaderView.findViewById(R.id.txtViewEmail);
        mTxtViewName = (TextView)  mHeaderView.findViewById(R.id.txtViewName);
        mAccountImageView = (ImageView) mHeaderView.findViewById(R.id.accountImageView);

        String fullname = firebaseUser.getDisplayName();
        mTxtViewEmail.setText(firebaseUser.getEmail());
        try{
            mTxtViewName.setText(fullname.split(" ")[0].substring(0, 1).toUpperCase() +
                    fullname.split(" ")[0].substring(1).toLowerCase() + " " +
                    fullname.split(" ")[1].toUpperCase());
        }
        finally {

        }
        if (!TextUtils.isEmpty(fullname.split(" ")[0]) &&
                !TextUtils.isEmpty(fullname.split(" ")[1])) {
            mTxtViewName.setText(fullname.split(" ")[0].substring(0, 1).toUpperCase() +
                    fullname.split(" ")[0].substring(1).toLowerCase() + " " +
                    fullname.split(" ")[1].toUpperCase());
        }
        Picasso.with(MainDrawer.this).load(firebaseUser.getPhotoUrl())
                .transform(new CircleTransform())
                .centerInside().resize(mAccountImageView.getMaxWidth(), mAccountImageView.getMaxHeight())
                .noFade()
                .into(mAccountImageView);

        //end
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_personal_data) {
            AccountFragment fragmentAccount = new AccountFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fl_content, fragmentAccount);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_test) {
            AccountFragment fragmentTest = new AccountFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_fl_content, fragmentTest);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_slideshow) {
            Intent mPData = new Intent(MainDrawer.this, SubmitPersonalData.class);
            startActivity(mPData);

        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Intent mInitialize = new Intent(MainDrawer.this, Initialization.class);
            startActivity(mInitialize);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
