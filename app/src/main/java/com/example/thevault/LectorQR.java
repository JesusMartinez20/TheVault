package com.example.thevault;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;


public class LectorQR extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private TextView info;
    private Button limpiarBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_qr);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        info= findViewById(R.id.info);
        limpiarBTN= findViewById(R.id.limpiarBTN);
        limpiarBTN.setVisibility(View.INVISIBLE);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(LectorQR.this, result.getText(), Toast.LENGTH_SHORT).show();
                        info.setText(result.getText());
                        mCodeScanner.startPreview();
                        limpiarBTN.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info.setText("");
                mCodeScanner.startPreview();
                limpiarBTN.setVisibility(View.INVISIBLE);
            }
        });

    }

    public void limpiar(View v){
        info.setText("");
        mCodeScanner.startPreview();
        limpiarBTN.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}