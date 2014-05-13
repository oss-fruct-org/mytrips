package oss.fruct.org.mytravel.my_travel;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Scroller;

import other.SideMenuListener;

public class MainActivity extends FragmentActivity implements SideMenuListener {
    private String[] names = { "Иван", "Марья", "Петр", "Антон", "Даша", "Борис",  "Костя", "Игорь",
            "Анна", "Денис", "Андрей", "Иван", "Марья", "Петр", "Антон", "Даша" };

    private FragmentTransaction fragmentTransaction;
    private View content;
    private int contentID = R.id.content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // заполняем список меню
        ListView menu = (ListView) findViewById(R.id.menu);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, names);
        menu.setAdapter(adapter);
    }

    public void startFragment(Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(contentID, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public boolean toggleMenu() {
        // TODO Auto-generated method stub
        return false;
    }

}
