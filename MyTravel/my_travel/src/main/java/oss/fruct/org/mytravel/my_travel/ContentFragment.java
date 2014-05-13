package oss.fruct.org.mytravel.my_travel;

import android.support.v4.app.Fragment;
import other.SideMenuListener;

/**
 * Created by Admin on 13.05.2014.
 */
public class ContentFragment extends Fragment {

    protected void startFragment(Fragment fragment) {
        ((SideMenuListener) getActivity()).startFragment(fragment);
    }

    protected boolean toggleMenu() {
        return ((SideMenuListener) getActivity()).toggleMenu();
    }
}
