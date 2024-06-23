package com.cod5.signyourapk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.apksigner.ApkSignerTool;
import com.cod5.signyourapk.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'signyourapk' library on application startup.
    static {
        System.loadLibrary("signyourapk");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an APK file"), 123);
        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMe(v);
            }
        });
    }
    public void onClickMe(View v) {
        try {
            InputStream in = null;
            OutputStream out = null;
            AssetManager assetManager = getAssets();
            in = assetManager.open("cert.bks");
            out = new FileOutputStream(this.getApplicationInfo().dataDir.toString() + "/cert.bks");
            copyFile(in, out);

            ApkSignerTool.main(new String[]{"sign", "--min-sdk-version", "16", "--ks",
                    this.getApplicationInfo().dataDir.toString() + "/cert.bks",
                    "--ks-pass", "pass:12345678", binding.sampleText.getText().toString()});
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "hello", Toast.LENGTH_LONG);
    }
    // Method used by copyAssets() on purpose to copy a file.
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && resultCode == RESULT_OK) {
            binding.sampleText.setText(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    + "/app.apk");
        }
    }
    /**
     * A native method that is implemented by the 'signyourapk' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}