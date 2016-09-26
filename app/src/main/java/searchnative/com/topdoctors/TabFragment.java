package searchnative.com.topdoctors;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class TabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3 ;
    int tabPosition = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x =  inflater.inflate(R.layout.tab_layout,null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        /*Tab change event listener*/
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition();

                switch (tabPosition) {
                    case 0 :
                        tabLayout.getTabAt(0).setIcon(R.mipmap.near_by);
                        tabLayout.getTabAt(1).setIcon(R.mipmap.search);
                        tabLayout.getTabAt(2).setIcon(R.mipmap.top_10_doctor_bottom_1);
                        break;
                    case 1 :
                        tabLayout.getTabAt(0).setIcon(R.mipmap.near_by_activated);
                        tabLayout.getTabAt(1).setIcon(R.mipmap.search_bottom);
                        tabLayout.getTabAt(2).setIcon(R.mipmap.top_10_doctor_bottom_1);
                        break;
                    case 2 :
                        tabLayout.getTabAt(0).setIcon(R.mipmap.near_by_activated);
                        tabLayout.getTabAt(1).setIcon(R.mipmap.search);
                        tabLayout.getTabAt(2).setIcon(R.mipmap.top_10_doctor_bottom);
                        break;
                    case 3:
                        tabLayout.getTabAt(0).setIcon(R.mipmap.near_by_activated);
                        tabLayout.getTabAt(1).setIcon(R.mipmap.search);
                        tabLayout.getTabAt(2).setIcon(R.mipmap.top_10_doctor_bottom_1);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    switch (i){
                        case 0 :
                            tabLayout.getTabAt(0).setIcon(R.mipmap.near_by);
                        case 1 :
                            tabLayout.getTabAt(1).setIcon(R.mipmap.search);
                        case 2 :
                            tabLayout.getTabAt(2).setIcon(R.mipmap.top_10_doctor_bottom_1);
                    }
                }
            }
        });

        return x;

    }

    class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return new PrimaryFragment();
                case 1 : return new SearchFragment();
                case 2 : return new UpdatesFragment();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

}