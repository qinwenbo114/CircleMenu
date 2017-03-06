package com.qinwenbo.circlemenu;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.qinwenbo.circlemenulib.CircleMenu;
import com.qinwenbo.circlemenulib.MenuIcon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qinwenbo on 2017/3/6 14:13.
 */

public class TestCircleMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CircleMenu circleMenu = (CircleMenu) findViewById(R.id.testView);
        List<MenuIcon> menuIcons = new ArrayList<>();
        menuIcons.add(new MenuIcon(ContextCompat.getDrawable(this, R.drawable.manual_mode)));
        menuIcons.add(new MenuIcon(ContextCompat.getDrawable(this, R.drawable.out_mode)));
        menuIcons.add(new MenuIcon(ContextCompat.getDrawable(this, R.drawable.party_mode)));
        menuIcons.add(new MenuIcon(ContextCompat.getDrawable(this, R.drawable.program_mode)));
        circleMenu.setMenuIcons(menuIcons);
        circleMenu.setOnMenuSwitchListener(new CircleMenu.OnMenuSwitchListener() {
            @Override
            public void onMenuSwitch(int menuStatus, int currentMenuIndex) {
                Log.d("menuStatus", menuStatus+"");
                Log.d("menuIndex", currentMenuIndex+"");
            }
        });
    }
}
