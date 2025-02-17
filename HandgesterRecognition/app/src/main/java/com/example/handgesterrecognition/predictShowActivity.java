package com.example.handgesterrecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.Activity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

//prediction result 1st page
public class predictShowActivity extends AppCompatActivity {

    //code for load a picture from gallery
    private final int LOADING_RESULT_CODE = 100;
    Button btn_load,btn_displayF,btn_featureResult,btn_predict;
    Bitmap bitmap = null;
    ImageView imv; // show load picture
    private Uri mImgeFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.menu_predict);
        setContentView(R.layout.activity_predict_show);

        //find a button by finding its ID in xml File
        btn_load = findViewById(R.id.button9);
        btn_displayF = findViewById(R.id.button7);
        btn_featureResult = findViewById(R.id.button15);
        btn_predict = findViewById(R.id.button8);
        imv = findViewById(R.id.imageView5);

        //method activate after click a button from btn_load
        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //make a intent variable to select picture from gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //call a method to start gallery
                startActivityForResult(intent, LOADING_RESULT_CODE);
            }
        });

        //method activate after click a button from btn_displayF
        btn_displayF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check that picture is selected, ready to send to another Activity
                if(bitmap != null) {
                    //make a intent variable to redirect to Displayfeature page
                    Intent intent = new Intent(predictShowActivity.this, DisplayFeatureActivity.class);
                    //make a intent for passing a bitmap to Displayfeature page
                    intent.putExtra("BitmapImage", bitmap);
                    //call a method to start intent
                    startActivity(intent);

                }else{
                    //toast massage when picture is not already picked
                    Toast.makeText(getApplicationContext(),"Please select picture", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_featureResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //always pass to another activity because in the next page can load a picture
                if(true) {
                    //make a intent variable to redirect to featureExtraction page
                    Intent intent = new Intent(predictShowActivity.this, featureExtractionActivity.class);
                    startActivity(intent);

                }else{

                    Toast.makeText(getApplicationContext(),"Please select picture", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //always pass to another activity because in the next page can load a picture
                if(true) {
                    //make a intent variable to redirect to featureExtraction page
                    Intent intent = new Intent(predictShowActivity.this, PredictionActivity.class);
                    startActivity(intent);

                }else{

                    Toast.makeText(getApplicationContext(),"Please select picture", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //onActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == LOADING_RESULT_CODE && data != null){
                Uri imgUri = data.getData();

                try{
                    InputStream inputStream = getContentResolver().openInputStream(imgUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    imv.setImageBitmap(bitmap);
                    imv.setBackgroundColor(Color.parseColor("#80FFFFFF"));
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
            }
        }
    }

}
