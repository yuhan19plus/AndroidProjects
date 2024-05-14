package kr.ac.yuhan.cs.yuhan19plus.admin;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import kr.ac.yuhan.cs.yuhan19plus.R;

public class AdminCreateQR extends AppCompatActivity {

    private ImageView iv;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore 인스턴스를 가져옵니다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_create_qr_page);

        iv = findViewById(R.id.qrcode);

        // Firestore에서 lastProductCode 값을 읽어와서 QR 코드 생성
        db.collection("counters").document("productCounter")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 문서가 존재하면, lastProductCode 필드의 값을 읽어옵니다.
                        // Number 타입의 값을 문자열로 변환
                        Long lastProductCode = documentSnapshot.getLong("lastProductCode");
                        if (lastProductCode != null) {
                            String text = "productCode:" + String.valueOf(lastProductCode);

                            // QR 코드 생성 및 표시
                            generateAndDisplayQRCode(text);
                        } else {
                            // lastProductCode 필드가 null일 경우의 처리
                            Log.d("Firestore", "lastProductCode is null");
                        }
                    } else {
                        // 문서가 존재하지 않을 경우의 처리
                        Log.d("Firestore", "No such document");
                    }
                })
                .addOnFailureListener(e -> {
                    // 데이터를 읽는 데 실패했을 경우의 처리
                    Log.w("Firestore", "Error reading document", e);
                });

    }

    private void generateAndDisplayQRCode(String text) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            iv.setImageBitmap(bitmap); // ImageView에 QR 코드 표시
            saveQRCodeToGallery(bitmap); // QR 코드 저장
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void saveQRCodeToGallery(Bitmap bitmap) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "QRCode_" + System.currentTimeMillis());
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "YourAppName");

        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try {
            if (imageUri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(imageUri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                Objects.requireNonNull(outputStream);
                outputStream.close();
                Toast.makeText(this, "QR Code saved to Gallery", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}