package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class password_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_password_layout);
        Button confirm_button;
        Button cancel_button;
        confirm_button = (Button) this.findViewById(R.id.dialog_confirm_button);
        cancel_button=(Button) this.findViewById(R.id.dialog_cancel_button);
        final TextView password_dialog =  (TextView) this.findViewById(R.id.password_dialog);
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                String password;
                password=password_dialog.getText().toString();
                if((password.length())==0)
                {
                    Toast error_toast = Toast.makeText(password_Activity.this, "password can't be null", Toast.LENGTH_LONG);
                    error_toast.setGravity(Gravity.CENTER, 0, 0);
                    error_toast.show();
                }
                else {
                    Intent intent = new Intent();
                    intent.putExtra("password", password);

                    setResult(2, intent);

                    finish();
                }
            }
         });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub


                    Intent intent = new Intent();
                    intent.putExtra("password", "");
                    setResult(1, intent);
                    finish();

            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();

        Intent intent = new Intent();
        intent.putExtra("password", "");
        setResult(1, intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent();
        intent.putExtra("password", "");
        setResult(1, intent);
        finish();
    }
}
