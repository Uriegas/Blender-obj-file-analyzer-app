package com.upv.pm_2022.iti_27849_u1_uriegas_ibarra_jesus_eduardo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int READ_ACCESS = 100;
    Button btn_loader;
    TextView output;
    ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set instance
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Before starting app, request READ permission
        this.permissionRequest(READ_ACCESS, Manifest.permission.READ_EXTERNAL_STORAGE);

        output = findViewById(R.id.output);
        btn_loader = findViewById(R.id.btn);

        // Result Launcher initialization, fund in StackOverflow
        resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result){
                    // Initialize result data
                    Intent data = result.getData();
                    if (data != null) {
                        File file = new File(data.getData().getPath());
                        if(file.getPath().endsWith("obj")){ // If it is an *.obj file
                            try {
                                HashMap<String, Integer> results = analyzeObj(file);
                                output.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                output.setText(results.toString());
                                Toast.makeText(getApplicationContext(), String.valueOf(results.size()),
                                        Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(getBaseContext(), e.getLocalizedMessage(),
                                        Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getBaseContext(),"Please select an obj file",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        });
        // On click open file explorer and eventually analyze file
        btn_loader.setOnClickListener( new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE },1);
                else
                    openObj();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (    requestCode == 1 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED    )
            openObj();
        else
            Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
    }

    /**
     * Intent to open a file
     */
    private void openObj() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Android works with MIME types not file extensions and/or regex
        resultLauncher.launch(intent);
    }

    /**
     * Function to ask for permission to the user
     * @param requestId
     * @param name
     * @return true if permission was granted, false otherwise
     */
    private boolean permissionRequest(int requestId, String name) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int permission = ActivityCompat.checkSelfPermission(this, name);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{name},requestId);
                return false;
            }
        }
        return true;
    }

    /**
     * Analyze the number of occurrences of each data type in a *.obj file.
     * @param file a buffered reader of the file to analyze
     * @return a map where key is the object type (vector, vertex, face, etc)
     *         and the value is the total number of occurrences of the object type.
     */
    public HashMap<String, Integer> analyzeObj(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        HashMap<String, Integer> count_analysis = new HashMap<String, Integer>() {
            /**
             * Any time this method is call one entry is updated or added when it is not present.
             * @param key
             */
            public void add(String key){
                if (this.get(key) == null)
                    this.put(key, 1);
                else
                    this.put(key, this.get(key)+1);
            }
            @Override
            public String toString(){
                String s = "";
                int total = 0;
                for (Map.Entry<String, Integer> entry : this.entrySet() ) {
                    s += entry.getKey() + " \t\t=>\t" + entry.getValue() + '\n';
                    total += entry.getValue();
                }
                s += "total" + "\t\t=>\t" + total;
                return s;
            }
        };
        String line = "";
        while( (line = br.readLine()) != null){
            if(line.startsWith("v "))
                count_analysis.put("vector",
                        count_analysis.get("vector") == null ? 1 : count_analysis.get("vector") +1);
            else if(line.startsWith("f "))
                count_analysis.put("face",
                        count_analysis.get("face") == null ? 1 : count_analysis.get("face") + 1);
            else if(line.startsWith("vn "))
                count_analysis.put("normal vector", count_analysis.get("normal vector") == null ? 1
                                                    : count_analysis.get("normal vector") + 1);
            else if(line.startsWith("vt "))
                count_analysis.put("vector texture", count_analysis.get("vector texture") == null ?1
                                                    : count_analysis.get("vector texture") + 1);
            else if(line.startsWith("vp "))
                count_analysis.put("space vertex", count_analysis.get("space vertex") == null ? 1
                                                    : count_analysis.get("space vertex") + 1);
            else
                continue;
        }
        br.close();
        return count_analysis;
    }
}