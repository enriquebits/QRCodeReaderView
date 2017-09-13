package com.example.qr_readerexample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class DecoderActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback, OnQRCodeReadListener {

  private static final int MY_PERMISSION_REQUEST_CAMERA = 0;

  private static final String[] DUMMY_CREDENTIALS = new String[]{
          "juan@somedomain.com.com:123456", "paco@somedomain.com:987654"
  };

  private final String MSG_INVALID_QR = "Código QR inválido";

  private final String MSG_SUCCESSFULL_LOGIN = "Login exitoso";

  private final String TAG = "DECODER_ACTIVITY";

  private ViewGroup mainLayout;

  private TextView resultTextView;
  private QRCodeReaderView qrCodeReaderView;
  private CheckBox flashlightCheckBox;
  private CheckBox enableDecodingCheckBox;
  private PointsOverlayView pointsOverlayView;

  private SharedPreferences sharedPreferences;
  private Editor editor;

  private String uEmail;
  private String uPassword;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_decoder);

    mainLayout = (ViewGroup) findViewById(R.id.main_layout);

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED) {
      initQRCodeReaderView();
    } else {
      requestCameraPermission();
    }
    // Sólo para demostración se asume que ya hay un usuario registrado en el dispositivo y sus datos se almacenan en SharedPreferences
    sharedPreferences = getApplicationContext().getSharedPreferences("DB", 0);
    editor = sharedPreferences.edit();
    int randomIndex = (int) Math.round( Math.random() );
    String credential = DUMMY_CREDENTIALS[randomIndex];
    Log.d(TAG, "DUMMY CREDENTIAL " + credential);
    String[] pieces = credential.split(":");
    editor.putString("Email", pieces[0]);
    editor.putString("Password", pieces[1]);
    editor.commit();
  }

  @Override protected void onResume() {
    super.onResume();

    if (qrCodeReaderView != null) {
      qrCodeReaderView.startCamera();
    }
  }

  @Override protected void onPause() {
    super.onPause();

    if (qrCodeReaderView != null) {
      qrCodeReaderView.stopCamera();
    }
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
      return;
    }

    if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
      initQRCodeReaderView();
    } else {
      Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT)
          .show();
    }
  }

  // Called when a QR is decoded
  // "text" : the text encoded in QR
  // "points" : points where QR control points are placed
  @Override public void onQRCodeRead(String text, PointF[] points) {
    int indexOfColon = text.indexOf(':');

    if (indexOfColon >= 0) {
      String[] textPieces = text.split(":");

      if (sharedPreferences.contains("Email"))  {
        uEmail = sharedPreferences.getString("Email", "");
      }
      if (sharedPreferences.contains("Password")) {
        uPassword = sharedPreferences.getString("Password", "");
      }

      if( textPieces[0].equals(uEmail) && textPieces[1].equals(uPassword) ) {
        //Intent mainIntent = new Intent(DecoderActivity.this, MainActivity.class);
        //mainIntent.putExtra("EXTRA_USER_EMAIL", uEmail);
        //DecoderActivity.this.startActivity(mainIntent);
        //finish();
        resultTextView.setText(MSG_SUCCESSFULL_LOGIN);
      }
      else {
        resultTextView.setText(MSG_INVALID_QR);
      }
    }
    else {
      resultTextView.setText(MSG_INVALID_QR);
    }
    pointsOverlayView.setPoints(points);
  }

  private void requestCameraPermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
      Snackbar.make(mainLayout, "Camera access is required to display the camera preview.",
          Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
        @Override public void onClick(View view) {
          ActivityCompat.requestPermissions(DecoderActivity.this, new String[] {
              Manifest.permission.CAMERA
          }, MY_PERMISSION_REQUEST_CAMERA);
        }
      }).show();
    } else {
      Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.",
          Snackbar.LENGTH_SHORT).show();
      ActivityCompat.requestPermissions(this, new String[] {
          Manifest.permission.CAMERA
      }, MY_PERMISSION_REQUEST_CAMERA);
    }
  }

  private void initQRCodeReaderView() {
    View content = getLayoutInflater().inflate(R.layout.content_decoder, mainLayout, true);

    qrCodeReaderView = (QRCodeReaderView) content.findViewById(R.id.qrdecoderview);
    resultTextView = (TextView) content.findViewById(R.id.result_text_view);
    flashlightCheckBox = (CheckBox) content.findViewById(R.id.flashlight_checkbox);
    enableDecodingCheckBox = (CheckBox) content.findViewById(R.id.enable_decoding_checkbox);
    pointsOverlayView = (PointsOverlayView) content.findViewById(R.id.points_overlay_view);

    qrCodeReaderView.setAutofocusInterval(2000L);
    qrCodeReaderView.setOnQRCodeReadListener(this);
    qrCodeReaderView.setBackCamera();
    flashlightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        qrCodeReaderView.setTorchEnabled(isChecked);
      }
    });
    enableDecodingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        qrCodeReaderView.setQRDecodingEnabled(isChecked);
      }
    });
    qrCodeReaderView.startCamera();
  }
}