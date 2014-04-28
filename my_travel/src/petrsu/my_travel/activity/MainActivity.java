package petrsu.my_travel.activity;

import java.util.Locale;

import model.myMap;
import other.NotImplementedException;
import petrsu.my_travel.R;
import petrsu.my_travel.R.id;
import petrsu.my_travel.R.layout;
import petrsu.my_travel.R.menu;
import petrsu.my_travel.R.string;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);



        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
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
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }
    
    // Код для написания

    /**
     * (Скорее всего реализуется через фрагмент)
     * Показывает выпадающее меню(с кнопками) 
     * (Внутри метода нужно создать объект Intent)
     * 
     * @param view dropMenu.xml - вызов выпадающего меню 
     */
    private void showMenu(View view){
    	try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ;
    }
    
    /**
     * 
     *  вызывает диалоговое окно с сообщением
     * 
     * @param view dialog.xml для отображения диалогового окна
     * @param str строка для отображения в окне
     * @return True - подтверждение действия, False - отмена действия 
     */
    private Boolean alert(View view,String str){
    	try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
    }
    
    /**
     * 
     * Показывает меню авторизации 
     * (Внутри метода нужно создать объект Intent)
     * 
     * @param view login.xml - форма окна авторизации
     */
    private void showEnter(View view){
    	try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
    }
    
    /**
     * 
     * Показывает меню выбора региона(для оффлайн работы) 
     * (Внутри метода нужно создать объект Intent)
     * 
     * @param view region.xml - форма выбора региона
     * @return Map - выбранный регион(карта)
     */
    private myMap showRegion(View view){
    	try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    /**
     * 
     * Показывает экран с картой и полосой быстрого поиска 
     * (Внутри метода нужно создать объект Intent)
     * 
     * @param view  mainMap.xml - форма для работы с картой
     * @param map карта для отображения (мб не обязательно)
     */
    private void showMap(View view, myMap map){
    	try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
    }
    
    /**
     * 
     *  Показывает экран для выбора радиуса отображения ближайших объектов 
     *  (Внутри метода нужно создать объект Intent)
     * 
     * @param view around.xml - форма для выбора радиуса отображения точек вокруг моего положения
     */
    private void showAround(View view){
    	try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
    }
    
    /**
     * 
     * Показывает экран поиска 
     * (Внутри метода нужно создать объект Intent)
     * 
     * @param view  search.xml - форма для поиска
     */
    private void showSearch(View view){
    	try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
    }
    
    /**
     * (мб нужно возвращать точки)
     * оказывает экран для выбора сохранённых точек 
     * (Внутри метода нужно создать объект Intent)
     * 
     * @param view myPoints.xml - форма для выбора сохранённых точек
     */
    private void showMyPoints(View view){
    	try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
    }
    
    /**
     * (мб нужно возвращать маршрут(ы)) 
     * Показывает экран для выбора сохранённых маршрутов
     * (Внутри метода нужно создать объект Intent)
     * 
     * @param view myRoutes.xml - форма для выбора сохранённых маршрутов
     */
    private void showMyRoutes(View view){
    	try {
			throw new NotImplementedException("TODO");
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
    }
}
