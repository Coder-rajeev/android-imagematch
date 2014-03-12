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
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
            FeatureDetector orbf = FeatureDetector.create(FeatureDetector.ORB);
       DescriptorExtractor orbd = DescriptorExtractor.create(DescriptorExtractor.ORB);
            Mat key=new Mat();
            Mat example=new Mat();
            try {
                key = Utils.loadResource(this,R.drawable.key);
                example = Utils.loadResource(this,R.drawable.example);
                Log.e("ImageMatch", "Images loaded");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
       MatOfKeyPoint kpkey =new MatOfKeyPoint();
       Mat desckey=new Mat();
       orbf.detect(key,kpkey);
       orbd.compute(key, kpkey, desckey);

       MatOfKeyPoint kpexample =new MatOfKeyPoint();
       Mat descexample=new Mat();
       orbf.detect(example,kpexample);
       orbd.compute(example, kpexample, descexample);

       DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
       List<MatOfDMatch> matches=new ArrayList<MatOfDMatch>();
       matcher.knnMatch(descexample, desckey, matches, 10);

       Mat out=new Mat();
       Features2d.drawMatches2(example,kpexample,key,kpkey,matches,out);
       Imgproc.resize(out, out, new Size(), 0.25, 0.25, Imgproc.INTER_AREA);
       double angle = 90;  // or 270
       Size src_sz = out.size();
       int len = out.cols()>out.rows() ? out.cols() : out.rows();
       Mat rot_mat = Imgproc.getRotationMatrix2D(new Point(len/2, len/2), angle, 1.0);
       Imgproc.warpAffine(out, out, rot_mat, new Size(out.height(),out.width()));
       Log.e("WIDTH",Integer.toString(out.width()));
       Log.e("HEIGHT",Integer.toString(out.height()));
       Bitmap.Config conf = Bitmap.Config.ARGB_8888 ; // see other conf types
       Bitmap bmp = Bitmap.createBitmap(out.width(),out.height(),conf);
       Utils.matToBitmap(out, bmp);
       return bmp;
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
