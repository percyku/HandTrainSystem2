package com.trainsystem.upperlimb.senior.handtrainsystem2;

import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.trainsystem.upperlimb.senior.handtrainsystem.R;
import com.trainsystem.upperlimb.senior.handtrainsystem2.tabfragment.Pager;

public class RecordActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener{
    //This is our tablayout
    private TabLayout tabLayout;

    private TextView toolbar_title;
    //This is our viewPager
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //Adding toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        toolbar_title = (TextView) this.findViewById(R.id.toolbar_title);
        toolbar_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/AdobeFanHeitiStd-Bold.otf"));



        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab().setText(R.string.game1));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.game2));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.game3));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        //Creating our pager adapter
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());

        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //Adding onTabSelectedListener to swipe views
        tabLayout.addOnTabSelectedListener(this);

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
