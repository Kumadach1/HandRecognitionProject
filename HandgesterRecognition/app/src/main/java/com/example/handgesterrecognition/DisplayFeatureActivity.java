package com.example.handgesterrecognition;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//Display feature page (pre-processing)
public class DisplayFeatureActivity extends AppCompatActivity {

    ImageView imageView;
    Bitmap imageBitmap=null, grayBitmap, binaryBitmap, denoiseBitmap, contourBitmap;
    Button btn_gallery, btn_rgb, btn_gray, btn_binary, btn_denoise, btn_contour;
    Mat sampledImgMat;
    Uri imageUri;


    final int REQUEST_CODE_GALLERRY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_feature);
        setTitle(R.string.menu_predict);

        //get the bitmap from predictShowActivity
        Intent intent = getIntent();
        imageBitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");

        //check the passing bitmap from predictShowActivity
        if(imageBitmap != null) {
            Toast.makeText(getApplicationContext(),"pass a picture successfully", Toast.LENGTH_SHORT).show();
        }

        imageView  = findViewById(R.id.imageView13);
        btn_rgb = findViewById(R.id.button10);
        btn_gray = findViewById(R.id.button11);
        btn_binary = findViewById(R.id.button12);
        btn_denoise = findViewById(R.id.button13);
        btn_contour = findViewById(R.id.button14);


        if(OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"OpenCV loaded successfully", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Could not load openCV", Toast.LENGTH_SHORT).show();
        }

        btn_rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(imageBitmap);
            }
        });

        btn_gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToGray(v);
                //displayImage(grayBitmap);
            }
        });

        btn_binary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToBinaryImage(v);
                //displayImage(binaryBitmap);
            }
        });

        btn_denoise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToDenoiseImage(v);
                //displayImage(denoiseBitmap);
            }
        });

        btn_contour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToContourImage(v);
                //displayImage(contourBitmap);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Open image file from gallery
        if(requestCode == REQUEST_CODE_GALLERRY && resultCode == RESULT_OK && data!=null){

            //Setting imageUri from intent
            imageUri = data.getData();
            //Get path of imageUri
            String path = getPath(imageUri);

            //Load image (Mat) from Uri
            sampledImgMat = loadImage(path);
            imageBitmap = convertMatToImageRGB(sampledImgMat);

//            //Read image from imageUri
//            try {
//                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//            }catch(IOException e){
//                e.printStackTrace();
//            }
//            BitmapFactory.Options o = new BitmapFactory.Options();
//            o.inDither = false;
//            o.inSampleSize = 4;

            imageView.setImageBitmap(imageBitmap);
        }
    }

    private String getPath(Uri uri){
        if(uri == null){
            return null;
        }else{
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null){
                int col_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();

                return cursor.getString(col_index);
            }
        }
        return uri.getPath();
    }

    private Mat loadImage(String path){
        Mat originImage = Imgcodecs.imread(path); //Image will be in BGR format
        Mat rgbImg = new Mat();

        //Convert BGR to RGB
        Imgproc.cvtColor(originImage,rgbImg, Imgproc.COLOR_BGR2RGB);

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        int mobile_width = (int) size.x;
        int mobile_height = (int) size.y;

        sampledImgMat = new Mat();
        double downSampleRatio = calculateSubSimpleSize(rgbImg, mobile_width, mobile_height);

        Imgproc.resize(rgbImg,sampledImgMat, new Size(), downSampleRatio, downSampleRatio, Imgproc.INTER_AREA);

        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            switch (orientation){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    sampledImgMat = sampledImgMat.t();
                    Core.flip(sampledImgMat, sampledImgMat,1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    sampledImgMat = sampledImgMat.t();
                    Core.flip(sampledImgMat, sampledImgMat,0);
                    break;
            }
        }catch(IOException e) {
            e.printStackTrace();
        }

        return sampledImgMat;
    }

    private double calculateSubSimpleSize(Mat src, int mobile_width, int mobile_height) {
        final int width = src.width();
        final int height = src.height();
        double inSampleSize = 1;

        if (height > mobile_height || width > mobile_width){
            //Calculate the ratio
            final double heightRatio = (double)mobile_height / (double)height;
            final double widthRatio = (double)mobile_width / (double)width;

            inSampleSize = heightRatio < widthRatio ? height : width;
        }
        return inSampleSize;
    }

    private Bitmap convertMatToImageRGB(Mat mat){
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(),mat.rows(),Bitmap.Config.RGB_565);
        //Convert mat to bitmap
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    private void convertToGray(View v){
        //Read image from imageUri
//        try {
//            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//        }catch(IOException e){
//            e.printStackTrace();
//        }

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither = false;
        o.inSampleSize = 4;

        //Read size of Mat
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();

        //Create Mat
        Mat Rgba = new Mat();       //Input
        Mat grayMat = new Mat();    //Output

        //Convert Bitmap into Mat
        Utils.bitmapToMat(imageBitmap, Rgba);

        //Image Processing
        Imgproc.cvtColor(Rgba, grayMat, Imgproc.COLOR_BGR2GRAY);

        //Convert Mat into bitmap value
        //Create grayBitmap
        grayBitmap = Bitmap.createBitmap(width, height,Bitmap.Config.RGB_565);
        Utils.matToBitmap(grayMat, grayBitmap);

        imageView.setImageBitmap(grayBitmap);
    }

    private void convertToBinaryImage(View v) {
        //Read image from imageUri
//        try {
//            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//        }catch(IOException e){
//            e.printStackTrace();
//        }

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither = false;
        o.inSampleSize = 4;

        //Read size of Mat
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();

        //Create Mat
        Mat Rgba = new Mat();       //Input

        //Convert Bitmap into Mat
        Utils.bitmapToMat(imageBitmap, Rgba);

        //Image Processing
        Imgproc.cvtColor(Rgba, Rgba, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(Rgba, Rgba, new Size(7,7),0);
        Imgproc.threshold(Rgba, Rgba, 50,255.0,Imgproc.THRESH_BINARY);

        //Convert Mat into bitmap value
        //Create binaryBitmap
        binaryBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(Rgba,binaryBitmap);

        imageView.setImageBitmap(binaryBitmap);
    }

    private void convertToDenoiseImage(View v){
        //Read size of Mat
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();

        //Create Mat
        Mat Rgba = new Mat();

        //Convert Bitmap into Mat
        Utils.bitmapToMat(imageBitmap, Rgba);

        //Image Processing
        Imgproc.cvtColor(Rgba, Rgba, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(Rgba, Rgba, new Size(7,7),0);
        Imgproc.threshold(Rgba, Rgba, 50,255.0,Imgproc.THRESH_BINARY);

        Mat kernalErode = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(7,7));
        Mat kernalDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5));
        Imgproc.erode(Rgba, Rgba, kernalErode);
        Imgproc.dilate(Rgba, Rgba, kernalDilate);
        Imgproc.dilate(Rgba, Rgba, kernalDilate);

        //Convert Mat into bitmap value
        //Create binaryBitmap
        denoiseBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(Rgba,denoiseBitmap);

        imageView.setImageBitmap(denoiseBitmap);
    }

    private void convertToContourImage(View v){
        //Read image to Bitmap, then convert to Mat
        //Read size of Mat
        int width = imageBitmap.getWidth();
        int height = imageBitmap.getHeight();

        //Create Mat
        Mat Rgba = new Mat();

        //Convert Bitmap into Mat
        Utils.bitmapToMat(imageBitmap, Rgba);

        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        Mat hierarchy = new Mat();
        Mat contours = new Mat();

        //Create A list to store all the contours
        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();

        //Image Processing
        Imgproc.cvtColor(Rgba, Rgba, Imgproc.COLOR_BGR2GRAY);
        //Thresholding
        Imgproc.threshold(Rgba, Rgba, 50,255.0,Imgproc.THRESH_BINARY);
        //Morphology
        Mat kernalErode = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(7,7));
        Mat kernalDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5));
        Imgproc.erode(Rgba, Rgba, kernalErode);
        Imgproc.dilate(Rgba, Rgba, kernalDilate);
        Imgproc.dilate(Rgba, Rgba, kernalDilate);

        //Process Canny edge and store in cannyEdge
        Imgproc.Canny(Rgba, cannyEdges, 10, 100);

        //Finding contours
        Imgproc.findContours(cannyEdges, contourList, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        //Create a Mat for store contour
        contours.create(cannyEdges.rows(), cannyEdges.cols(), CvType.CV_8UC3);

        //Draw contour
        Imgproc.drawContours(contours, contourList, -1, new Scalar(255,255,255), -1);

        //Converting Mat back to Bitmap
        contourBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(contours, contourBitmap);

        imageView.setImageBitmap(contourBitmap);
    }

    private void saveImageProcessed(Bitmap imgBitmap, String fileNameOut){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        String fileName = fileNameOut + "_"  + formatter.format(now) + ".jpg" ;

        FileOutputStream outStream;
        try{
            //Get a public path on the device storage for saving the file
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            //Create directory for saving the image
            File saveDir = new File(path + "/Demo/");

            //If the directory is not existed
            if(!saveDir.exists()){
                saveDir.mkdir();
            }

            //Create the image file within the directory
            File fileDir = new File(saveDir, fileName);     //Create the file

            //Write into the image file by the Bitmap content
            outStream = new FileOutputStream(fileDir);
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            MediaScannerConnection.scanFile(this.getApplicationContext(),
                    new String[]{fileDir.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
            //Close the output stream
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
