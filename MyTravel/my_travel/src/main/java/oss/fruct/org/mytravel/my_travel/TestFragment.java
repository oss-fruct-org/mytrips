package oss.fruct.org.mytravel.my_travel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import oss.fruct.org.mytravel.my_travel.ContentFragment;
import oss.fruct.org.mytravel.my_travel.R;

public class TestFragment extends ContentFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.test_fragment, container, true);

        Button toogle = (Button) v.findViewById(R.id.toggle);
        toogle.setOnClickListener( new OnClickListener() {

            public void onClick(View arg0) {
                toggleMenu();
            }
        });
        return v;
    }
}