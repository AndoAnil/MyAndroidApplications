package com.example.loginone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    EditText emailId,password;
    Button signup;
    TextView login;
    FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailId=(EditText)findViewById(R.id.emailId);
        password=(EditText)findViewById(R.id.password);
        login=(TextView)findViewById(R.id.login);
        signup=(Button)findViewById(R.id.signup);
        mFirebaseAuth=FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailId.getText().toString();
                String pass=password.getText().toString();
                if(email.isEmpty() || pass.isEmpty())
                {
                    emailId.setError("Enter email");
                    password.setError("Enter Password");
                }
                else if (email.isEmpty() && pass.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "Fill reqired information", Toast.LENGTH_SHORT).show();
                }
                else if(!(email.isEmpty() && pass.isEmpty()))
                {
                    mFirebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(MainActivity.this, "Create account unsuccessful,Try again", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Something went wrong ,Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
