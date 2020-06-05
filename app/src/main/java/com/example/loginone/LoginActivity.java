package com.example.loginone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText emailId1,password1;
    TextView createAccount;
    Button login;
    FirebaseAuth mFirebaseAuth;
    private  FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailId1=(EditText)findViewById(R.id.loginemail);
        password1=(EditText)findViewById(R.id.loginpass);
        login=(Button)findViewById(R.id.login1);
        createAccount=(TextView)findViewById(R.id.createAccount);
        mFirebaseAuth=FirebaseAuth.getInstance();
        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser=mFirebaseAuth.getCurrentUser();
                if(mFirebaseUser!=null)
                {
                    Toast.makeText(LoginActivity.this, "You are Logged in", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Please Log in", Toast.LENGTH_SHORT).show();
                }
            }
        };


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailId1.getText().toString();
                String pwd=password1.getText().toString();
                Log.i("Email",email);
                Log.i("Password",pwd);
                if(email.isEmpty() || pwd.isEmpty())
                {
                    emailId1.setError("Enter email");
                    password1.setError("Enter Password");
                }
                else if (email.isEmpty() && pwd.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Fill reqired information", Toast.LENGTH_SHORT).show();
                }

                else if (!(email.isEmpty() && pwd.isEmpty()))
                {
                    mFirebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                          if (!task.isSuccessful())
                          {
                              Toast.makeText(LoginActivity.this, "Login Error ,Please try again", Toast.LENGTH_SHORT).show();
                          }
                          else
                          {
                              Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                              startActivity(intent);
                          }
                        }
                    });
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                }

            }
        });


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
