package com.coolrunnings.imagematcher;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import org.opencv.android.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends ActionBarActivity {
    public static final int SUBSAMPLING_FACTOR = 4;
    public static final int WIDTH=2448;
    public static final int HEIGHT=3264;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        if (!OpenCVLoader.initDebug()) {
            Log.e("OPENCV","fail");
        }
        else
            Log.e("OPENCV","WIN!");//THIS NOW WORKS
        ImageView im = ((ImageView) findViewById(R.id.imgView));
        im.setImageBitmap(doImages());
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


   private Bitmap doImages() {
            FeatureDetector orb = FeatureDetector.create(FeatureDetector.ORB);
            Mat large=new Mat();
            try {
                large = Utils.loadResource(this,R.drawable.key);
                Log.e("ImageMatch", "ImageLoaded");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
       Log.e("ImageMatch", "MatrixSetup");
            MatOfKeyPoint kp =new MatOfKeyPoint();
       orb.detect(large,kp);
       Features2d.drawKeypoints(large,kp,large);
       Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
       Bitmap bmp = Bitmap.createBitmap(large.width(), large.height(), conf); // this creates a MUTABLE bitmap
       Utils.matToBitmap(large,bmp);
       Log.e("ImageMatch",Integer.toString(bmp.getByteCount()));
       return bmp;
//TODO: Implement matching!
        }


        /**
         *
         * A placeholder fragment containing a simple view.
         */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
