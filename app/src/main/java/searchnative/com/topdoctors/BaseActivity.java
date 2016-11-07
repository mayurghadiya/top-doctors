package searchnative.com.topdoctors;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import org.w3c.dom.Text;

public class BaseActivity extends AppCompatActivity {

    //define the variables
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    TabFragment tabFragment = new TabFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_base);

        //set font
        final NavigationView navView = (NavigationView) findViewById(R.id.navigation_view);
        Menu m = navView.getMenu();
        for(int  i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if(subMenu != null && subMenu.size() > 0) {
                for(int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            applyFontToMenuItem(mi);
        }

        //setup the drawer layout and navigation view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        //inflate the first fragment
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, tabFragment).commit();

        //setup click event on navigation view
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mNavigationView.getMenu().findItem(menuItem.getItemId()).setChecked(true);
                mDrawerLayout.closeDrawers();
                if(menuItem.getItemId() == R.id.logout_btn) {
                    Preference.setValue(BaseActivity.this, "PREF_FNAME", "");
                    Preference.setValue(BaseActivity.this, "PREF_ADDRESS", "");
                    Preference.setValue(BaseActivity.this, "PREF_ISLOGIN", "");
                    Preference.setValue(BaseActivity.this, "LOGIN_ID", "");

                    //fb logout
                    FacebookSdk.sdkInitialize(getApplicationContext());
                    LoginManager.getInstance().logOut();

                    startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                    finish();
                } else if(menuItem.getItemId() == R.id.add_a_doctor) {
                    startActivity(new Intent(BaseActivity.this, AddDoctorActivity.class));
                    mNavigationView.getMenu().findItem(menuItem.getItemId()).setChecked(false);
                    //FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    //fragmentTransaction.replace(R.id.containerView, new AddDoctor()).addToBackStack(null).commit();
                } else if(menuItem.getItemId() == R.id.search) {
                    Preference.setValue(BaseActivity.this, "SEARCH_REDIRECT", "true");
                    tabFragment.tabLayout.getTabAt(1).select();
                } else if(menuItem.getItemId() == R.id.edit_profile) {
                    tabFragment.tabLayout.getTabAt(0).select();
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.primary_layout, new ProfileFragment()).addToBackStack("profile").commit();
                } else if(menuItem.getItemId() == R.id.add_a_clinic) {
                    startActivity(new Intent(BaseActivity.this, AddClinicActivity.class));
                } else if(menuItem.getItemId() == R.id.add_a_lab) {
                    startActivity(new Intent(BaseActivity.this, AddLabActivity.class));
                } else if(menuItem.getItemId() == R.id.top_ten_doctors) {
                    tabFragment.tabLayout.getTabAt(2).select();
                } else if(menuItem.getItemId() == R.id.bookmarks) {
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    Preference.setValue(BaseActivity.this, "USER_BOOKMARKS", "true"); //USER_BOOKMARKS
                    tabFragment.tabLayout.getTabAt(1).select();
                } else if (menuItem.getItemId() == R.id.add_a_hospital) {
                    startActivity(new Intent(BaseActivity.this, AddHospitalActivity.class));
                } else if (menuItem.getItemId() == R.id.share) {
                    //social share top doctors
                    String name = "Top Doctors";
                    String message = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.";
                    String address = "123 Eccles Old Road, New Salford Road, East";
                    String phone = "+(971) 55 12345678";
                    String email = "info@topdoctors.com";
                    String website = "http://www.topdoctors.com";
                    String appUsrl = "";

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Name: " + name + "\n" + message + "\n" + "Address: "
                            + address + "\n" + "Phone: " + phone + "\n" + "Email" + email + "\n" + "Website: "
                            + website + "\n" + "Download the app: " + appUsrl
                    );
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.social_text)));
                }

                return false;
            }

        });

        //set login username and address
        View hView = mNavigationView.getHeaderView(0);
        TextView newuser = (TextView) hView.findViewById(R.id.login_user_name);
        TextView navAddress = (TextView) hView.findViewById(R.id.login_user_address);
        Intent sender = getIntent();
        Bundle extras = sender.getExtras();
        String currentUser = Preference.getValue(BaseActivity.this, "PREF_FNAME", "");
        String currentAddress = Preference.getValue(BaseActivity.this, "PREF_ADDRESS", "");
        newuser.setText(currentUser);
        navAddress.setText(currentAddress);

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_a_clinic) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/ExoMedium.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private Boolean exit = false;

    private Boolean isPrimary = false;

    @Override
    public void onBackPressed() {
        mFragmentManager = getSupportFragmentManager();
        int fragmentBackStack = mFragmentManager.getBackStackEntryCount();
        DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        if(mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if(fragmentBackStack == 0) {
                if(exit) {
                    finish();
                } else {
                    tabFragment.tabLayout.getTabAt(0).select();
                    if(isPrimary) {
                        Toast.makeText(this, getResources().getString(R.string.app_exit_message),
                                Toast.LENGTH_SHORT).show();
                        exit = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                exit = false;
                                isPrimary = false;
                            }
                        }, 3 * 1000);
                    } else {
                        isPrimary = true;
                    }
                }
            } else {
                super.onBackPressed();
                isPrimary = false;
            }
        }
    }
}
