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

    private final double RIGTH_BOUND_COFF = 0.75;
    private static int DURATION = 250;
    private boolean isContentShow = true;
    private int rightBound;
    private ContentScrollController menuController;
    private Rect contentHitRect = new Rect();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        content = findViewById(contentID);
        menuController = new ContentScrollController(new Scroller(getApplicationContext(), new DecelerateInterpolator(3)));

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        rightBound = (int) (displaymetrics.widthPixels * RIGTH_BOUND_COFF);

        content.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                v.getHitRect(contentHitRect);
                contentHitRect.offset(-v.getScrollX(), v.getScrollY());
                if (contentHitRect.contains((int)event.getX(), (int)event.getY())) return true;
                return v.onTouchEvent(event);
            }
        });

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
        if(isContentShow)
            menuController.openMenu(DURATION);
        else
            menuController.closeMenu(DURATION);
        return isContentShow;
    }

    private class ContentScrollController implements Runnable {
        private final Scroller scroller;
        private int lastX = 0;

        public ContentScrollController(Scroller scroller) {
            this.scroller = scroller;
        }

        public void run() {
            if (scroller.isFinished())
                return;

            final boolean more = scroller.computeScrollOffset();
            final int x = scroller.getCurrX();
            final int diff = lastX - x;

            if (diff != 0) {
                content.scrollBy(diff, 0);
                lastX = x;
            }
            if (more)
                content.post(this);
        }

        public void openMenu(int duration) {
            isContentShow = false;
            final int startX = content.getScrollX();
            final int dx = rightBound + startX;
            fling(startX, dx, duration);
        }

        public void closeMenu(int duration) {
            isContentShow = true;
            final int startX = content.getScrollX();
            final int dx = startX;
            fling(startX, dx, duration);
        }

        private void fling(int startX, int dx, int duration) {
            if (!scroller.isFinished())
                scroller.forceFinished(true);
            if (dx == 0)
                return;
            if (duration <= 0) {
                content.scrollBy(-dx, 0);
                return;
            }
            scroller.startScroll(startX, 0, dx, 0, duration);
            lastX = startX;
            content.post(this);
        }
    }

}