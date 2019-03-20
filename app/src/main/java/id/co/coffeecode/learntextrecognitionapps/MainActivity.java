package id.co.coffeecode.learntextrecognitionapps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    Button btnCaptureImage;
    ImageView ivPreview;
    TextView tvResult;

    String cameraPermission[];

    private static final int IMAGE_PICK_CAMErA_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCaptureImage = findViewById(R.id.btnCaptureImage);
        ivPreview = findViewById(R.id.ivPreview);
        tvResult = findViewById(R.id.tvResult);

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera();
            }
        });
    }

    private void showCamera() {
        if (!checkCameraPermission()){
            requestCameraPermission();
        }else {
            pickCamera();
        }
    }

    private void pickCamera() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentCamera, IMAGE_PICK_CAMErA_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, 200);
    }

    private boolean checkCameraPermission() {
        boolean resultCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        return resultCamera;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_CAMErA_CODE){
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }
        }

        //get croped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                //set image to preview
                ivPreview.setImageURI(resultUri);

                BitmapDrawable bitmapDrawable = (BitmapDrawable) ivPreview.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();

                processTextRecognition(bitmap);
            }
        }
    }

    private void processTextRecognition(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        Log.d("coba", "hasil : "+firebaseVisionText.getText());
                        String result = firebaseVisionText.getText();
                        tvResult.setText(result);

                        processIdCard(firebaseVisionText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void processIdCard(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
        StringBuilder result = new StringBuilder();
        for (FirebaseVisionText.TextBlock block : blocks){
            result.append(block.getText());
        }

        String resultElement;
        String finalResult = result.toString().trim();
        for (int i = 0; i < blocks.size(); i++){
            List<FirebaseVisionText.Line> lines =blocks.get(i).getLines();
//            Log.d("coba", "hasil blocks : "+blocks.get(i).getText());
            for (int j = 0; j < lines.size(); j++){
                List<FirebaseVisionText.Element> elements =lines.get(j).getElements();
//                Log.d("coba", "hasil lines : "+lines.get(j).getText());
                resultElement = lines.get(j).getText();
                String resulElementFinal = resultElement.trim();
                if (Patterns.EMAIL_ADDRESS.matcher(resulElementFinal).matches()){
                    Log.d("coba", "email : "+resulElementFinal);
                }
                if (Patterns.PHONE.matcher(resulElementFinal).matches()){
                    Log.d("coba", "phone : "+resulElementFinal);
                }
                if (Patterns.DOMAIN_NAME.matcher(resulElementFinal).matches()){
                    Log.d("coba", "domain : "+resulElementFinal);
                }
                for (int k = 0; k < elements.size(); k++){
//                    Log.d("coba", "hasil elements : "+elements.get(k).getText());
                }
            }
        }
    }

    private void checkEmail(){

    }
}
