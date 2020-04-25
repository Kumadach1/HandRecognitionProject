package com.example.handgesterrecognition.ui.Capture;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.handgesterrecognition.R;
import com.example.handgesterrecognition.ui.home.HomeViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.lang.Object.*;

public class CaptureFragment extends Fragment {

    private HomeViewModel CaptureViewModel;

    private final int CAMERA_RESULT_CODE = 100;
    OutputStream outputStream;

    private Button cmdCapture ;
    private ImageView imvOutput ;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CaptureViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_capture, container, false);

        //TextView tvtest = root.findViewById(R.id.textView7);


        cmdCapture = (Button) root.findViewById(R.id.cmdCapture);
        imvOutput = (ImageView) root.findViewById(R.id.imvOutput);

        cmdCapture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {

                    //tvtest.setText("testing text ok!!");

                    Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(it.resolveActivity(getActivity().getPackageManager())!= null){
                        startActivityForResult(it, CAMERA_RESULT_CODE);;
                    }

                }else{
                    if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                        Toast.makeText(getContext(),"Cannot use camera", Toast.LENGTH_LONG).show();
                    }
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_RESULT_CODE);
                }
            }
        });

        return root;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == CAMERA_RESULT_CODE){
                Bundle bd = data.getExtras();
                Bitmap bmp = (Bitmap) bd.get("data");
                imvOutput.setImageBitmap(bmp);
                imvOutput.setBackgroundColor(Color.parseColor("#80FFFFFF"));
                try {
                    storeImageToExternal();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults){
        if(requestCode == CAMERA_RESULT_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(it.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(it,CAMERA_RESULT_CODE);
                }
            }else{
                Toast.makeText(getContext(),"Cannot use camera", Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permission, grantResults);
        }
    }

    private void storeImageToExternal() throws FileNotFoundException {

        //Store image into external storage
        BitmapDrawable drawable = (BitmapDrawable) imvOutput.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        try{
        File filepath = Environment.getExternalStorageDirectory();
        File dir = new File(filepath.getAbsolutePath()+"/Demo");
        if (!dir.exists()){
            dir.mkdir();
        }
        //Create an image file name
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        Date now = new Date();
        String fileName = formatter.format(now) + ".jpg" ;
        //File file = new File(dir,System.currentTimeMillis()+".jpg");
        File fileDir = new File(dir,fileName);
        /**
        try{
            outputStream = new FileOutputStream(file);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        Toast.makeText(getActivity().getApplicationContext(),"Image Save to Internal",Toast.LENGTH_SHORT).show();
        try{
            outputStream.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
        try{
            outputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    **/

        //Write into the image file by the Bitmap content
        outputStream = new FileOutputStream(fileDir);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        MediaScannerConnection.scanFile(this.getContext(),
                new String[]{fileDir.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });

    }catch(Exception e){
        e.printStackTrace();
    }
    }
}
