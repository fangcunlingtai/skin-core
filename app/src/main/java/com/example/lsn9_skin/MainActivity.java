package com.example.lsn9_skin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.lsn9_skin.fragment.MusicFragment;
import com.example.lsn9_skin.fragment.RadioFragment;
import com.example.lsn9_skin.fragment.VideoFragment;
import com.example.lsn9_skin.widget.MyTabLayout;
import com.example.skin_core.SkinManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyTabLayout tab = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        List<Fragment> list = new ArrayList<>();
        list.add(new MusicFragment());
        list.add(new VideoFragment());
        list.add(new RadioFragment());
        List<String> listTitle = new ArrayList<>();
        listTitle.add("音乐");
        listTitle.add("视频");
        listTitle.add("电台");
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),0,list,listTitle);
        viewPager.setAdapter(myFragmentPagerAdapter);
        tab.setupWithViewPager(viewPager);

        SkinManager.getInstance().updateSkin(this);

    }

    public void skinSelect(View view) {
        startActivity(new Intent(this, SkinActivity.class));
    }
}
