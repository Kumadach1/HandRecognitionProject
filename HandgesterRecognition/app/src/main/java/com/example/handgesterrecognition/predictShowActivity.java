package com.example.handgesterrecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.Activity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

//prediction result 1st page
public class predictShowActivity extends AppCompatActivity {

    private final int LOADING_RESULT_CODE = 100;
    Button btn_load,btn_displayF;
    Bitmap bitmap = null;
    ImageView imv; // show load picture
    private Uri mImgeFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.menu_predict);
        setContentView(R.layout.activity_predict_show);

        btn_load = findViewById(R.id.button9);
        btn_displayF = findViewById(R.id.button7);
        imv = findViewById(R.id.imageView5);

        btn_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //start intent to select picture from gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, LOADING_RESULT_CODE);
            }
        });

        btn_displayF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check that picture is selected, ready to send to another Activity
                if(bitmap != null) {
                    Intent intent = new Intent(predictShowActivity.this, DisplayFeatureActivity.class);
                    intent.putExtra("BitmapImage", bitmap);
                    startActivity(intent);

                }else{
                    Toast.makeText(getApplicationContext(),"Please select picture", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == LOADING_RESULT_CODE){
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
