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

// made by 오자현
public class AdminCreateQR extends AppCompatActivity {

    private ImageView iv;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firestore 인스턴스를 가져옵니다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_create_qr_page);

        // QR 코드를 표시할 ImageView를 초기화합니다.
        iv = findViewById(R.id.qrcode);

        // Firestore에서 lastProductCode 값을 읽어와서 QR 코드를 생성합니다.
        db.collection("counters").document("productCounter")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 문서가 존재하면, lastProductCode 필드의 값을 읽어옵니다.
                        Long lastProductCode = documentSnapshot.getLong("lastProductCode");
                        if (lastProductCode != null) {
                            // Number 타입의 값을 문자열로 변환하여 QR 코드 데이터로 사용합니다.
                            String text = "productCode:" + String.valueOf(lastProductCode);

                            // QR 코드를 생성하고 ImageView에 표시합니다.
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

    // QR코드를 화면에 출력하는 메서드 (오자현)
    private void generateAndDisplayQRCode(String text) {
        // MultiFormatWriter 객체를 생성합니다. 이 객체는 다양한 포맷의 바코드를 생성할 수 있습니다.
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            // 입력 텍스트를 QR 코드로 인코딩하여 BitMatrix 객체를 생성합니다.
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

            // BarcodeEncoder 객체를 사용하여 BitMatrix 객체를 Bitmap 객체로 변환합니다.
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            // 생성된 QR 코드를 ImageView에 설정하여 화면에 표시합니다.
            iv.setImageBitmap(bitmap); // ImageView에 QR 코드 표시

            // 생성된 QR 코드를 갤러리에 저장합니다.
            saveQRCodeToGallery(bitmap); // QR 코드 저장
        } catch (WriterException e) {
            // QR 코드 생성 중 오류가 발생하면 스택 트레이스를 출력합니다.
            e.printStackTrace();
        }
    }

    // 생성된 QR 코드를 갤러리에 저장하는 메서드 (오자현)
    private void saveQRCodeToGallery(Bitmap bitmap) {
        // ContentValues 객체를 생성하고, 이미지의 메타데이터를 설정합니다.
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "QRCode_" + System.currentTimeMillis());
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "YourAppName");

        // 이미지 파일을 저장할 URI를 가져옵니다.
        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try {
            if (imageUri != null) {
                // OutputStream을 열고, Bitmap 객체를 PNG 형식으로 압축하여 저장합니다.
                OutputStream outputStream = getContentResolver().openOutputStream(imageUri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                Objects.requireNonNull(outputStream);
                outputStream.close();// OutputStream을 닫습니다.
                Toast.makeText(this, "QR Code saved to Gallery", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace(); // 예외가 발생하면 스택 트레이스를 출력합니다.

        }
    }


}