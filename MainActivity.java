package com.example.max.apibsp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Martin on 01.03.2016.
 */
public class MainActivity extends AppCompatActivity{

    private TextView textView;

    protected void onCreate(Bundle bundle){

        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.myText);
        //   textView.setText("halllo");

    }

    public void myClickHandler(View view){

        Toast.makeText(getBaseContext(), "Verbindung wird aufgebaut", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, WetterActivity.class);
        startActivity(intent);

    }


}
