package com.nsg.mapssample;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.nsg.nsgmapslibrary.Classes.HomeFragment;
import com.nsg.nsgmapslibrary.database.dto.EdgeDataT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SampleActivity extends Activity {
    AnimatorSet set;
    ImageView imgView;
    List<EdgeDataT> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_vessel_sample);
        // RemoveDuplicate();

    }

    public List<EdgeDataT> RemoveDuplicate() {
        list = new ArrayList<EdgeDataT>();
        list.add(new EdgeDataT("12", "lat/lng: (55.06341094600003,24.97866310200004)", "Left"));
        list.add(new EdgeDataT("12", "lat/lng: (55.06341094600003,24.97866310200004)", "Right"));
        list.add(new EdgeDataT("13", "lat/lng: (55.063536439000075,24.978772392000053)", "Right"));
        list.add(new EdgeDataT("14", "lat/lng: (55.063536439000075,24.978772392000053)", "Left"));
        list.add(new EdgeDataT("16", "lat/lng: (55.06355823200005,24.978791371000057))", "Right"));
        list.add(new EdgeDataT("17", "lat/lng: (55.06368697400006,24.978903366000054))", "Right"));
        list.add(new EdgeDataT("18", "lat/lng: (55.06368697400006,24.978903366000054))", "Right"));

        Iterator<EdgeDataT> it = list.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }

        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size() - 1; j++) {
                if (list.get(i).getPositionMarkingPoint().equals(list.get(j).getPositionMarkingPoint())) {
                    list.remove(j);
                }
            }
        }
        System.out.println(list);
        Log.e("List", "List" + list.size());
        for (int k = 0; k < list.size(); k++) {
            EdgeDataT edge = list.get(k);
            Log.e("List", "List Items -------" + edge.getPositionMarkingPoint());
            Log.e("List", "List Items -------" + edge.getGeometryText());
            Log.e("List", "List Items -------" + edge.getDistanceInVertex());
        }
        return list;
    }

    public void getKeyFromValue() {
        {
            String key = "lat/lng: (55.06736747900004,24.978870483000037)";
            Map<String, String> map = new HashMap<String, String>();
            map.put("lat/lng: (55.06575594600008,24.97747010300003)", "Take Right");
            map.put("lat/lng: (55.067103005000035,24.98001725100005)", "Take Right");
            map.put("lat/lng: (55.06615844100003,24.981042358000025)", "Take Left");
            map.put("lat/lng: (55.063687522000066,24.978500677000056)", "Take Left");
            map.put("lat/lng: (55.06613920500007,24.98096412900003)", "Take Left");
            map.put("lat/lng: (55.06412686400006,24.978079970000067)", "Take Left");
            map.put("lat/lng: (55.06475625000007,24.977475793000053)", "Take Left");
            map.put("lat/lng: (55.065207979000036,24.977075912000032)", "Take Left");
            map.put("lat/lng: (55.065207979000036,24.977075912000032)", "Take Left");
            map.put("lat/lng: (55.06736747900004,24.978870483000037)", "TakeRight");
            getKeysFromValue(map, key);
        }
    }

    public Set<Object> getKeysFromValue(Map<String, String> map, String key) {
        Set<Object> keys = new HashSet<Object>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            //if value != null
            if (entry.getKey().equals(key)) {
                keys.add(entry.getValue());
            }
        }
        return keys;
    }
}


