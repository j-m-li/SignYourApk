package com.cod5.signyourapk;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.apksigner.ApkSignerTool;
import com.cod5.signyourapk.databinding.ActivityMainBinding;

import org.conscrypt.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 189;

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

        /*TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());*/
        binding.sampleText.setText("Sign you APK");
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickMe(v);
            }
        });
        hasWriteStoragePermission();
        askAllFilesPermission();
        listDownloadsFiles();
    }

    private void listDownloadsFiles()
    {
        File d = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File[] lst = d.listFiles();
        int i  = 0;
        for(File f: lst) {
            if (f.getName().endsWith(".apk")) {
                RadioButton r;
                r = new RadioButton(this);
                r.setText(f.getAbsolutePath());
                binding.radio.addView(r, i);
                i++;
            }
        }

    }
    /* print result of permission request */
    public void onRequestPermissionsResult(int requestCode,
                                            String[] permissions,
                                            int [] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean askAllFilesPermission() {
        if (Build.VERSION.SDK_INT >= 30) {
            if (hasAllFilesPermission()) {
                return true;
            }
            startActivity(
                    new Intent(
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            Uri.parse("package:" + getPackageName())
                    )
            );
        }
        return true;
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private boolean hasAllFilesPermission()  {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return Environment.isExternalStorageManager();
        }
        return true;
    }

    private boolean hasWriteStoragePermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE
                );
                return false;
            }
        }
        return true;

    }
    public void onClickMe(View v) {

        if (binding.radio.getChildCount() < 1) {
            listDownloadsFiles();
            Toast.makeText(MainActivity.this, "please restart app.", Toast.LENGTH_LONG).show();
            return;
        } else {
            binding.sampleText.setText("Start...");
            Toast.makeText(MainActivity.this, "Start signing", Toast.LENGTH_LONG).show();
        }
        try {
            InputStream in = null;
            OutputStream out = null;
            AssetManager assetManager = getAssets();
            in = assetManager.open("cert.bks");
            out = new FileOutputStream(this.getApplicationInfo().dataDir.toString() + "/cert.bks");
            copyFile(in, out);
            int id = binding.radio.getCheckedRadioButtonId();
            if (id >= 0) {
                RadioButton rdb = binding.radio.findViewById(id);
                ApkSignerTool.main(new String[]{"sign", "--min-sdk-version", "16", "--ks",
                        this.getApplicationInfo().dataDir.toString() + "/cert.bks",
                        "--ks-pass", "pass:12345678", rdb.getText().toString()});
                binding.sampleText.setText("APK signed !");
                Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            binding.sampleText.setText(e.getCause().toString());
        }
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