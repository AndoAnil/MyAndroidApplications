package com.example.loginone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth mFirebaseAuth;
    private static final int PICK_IMAGE_REQUES=1;
    private Button mButtonChooseFile;
    private  Button mButtonUploadFile;
    private ImageView mSelectedImage;
    private EditText mEnterFileName;
    private ProgressBar mProgressBar;
    private TextView mTextViewShowImageInRecyclerVIew;
    String TAG="MainActivity";

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;

    private Uri mImageUri;
    private StorageTask mUploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Button logout=(Button)findViewById(R.id.logout);
        mButtonChooseFile=(Button)findViewById(R.id.upload_image);
        mButtonUploadFile=(Button)findViewById(R.id.uploadFile);
        mSelectedImage=(ImageView)findViewById(R.id.images);
        mEnterFileName=(EditText)findViewById(R.id.enter_file_name);
        mProgressBar=(ProgressBar)findViewById(R.id.progress_bar);
        mStorageReference= FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseReference= FirebaseDatabase.getInstance().getReference("uploads");


        mTextViewShowImageInRecyclerVIew=(TextView)findViewById(R.id.show_images_recyclerView);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        mButtonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


        mButtonUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUploadTask!=null && mUploadTask.isInProgress())
                {
                    Toast.makeText(HomeActivity.this, "Uploading............", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    uploadFile();
                }
            }
        });
    }


    private void openFileChooser()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUES && resultCode==RESULT_OK  && data!=null && data.getData()!=null)
        {
            mImageUri=data.getData();
            Picasso.get().load(mImageUri).into(mSelectedImage);
        }

    }

  private String getFileExtension(Uri uri)
  {
      ContentResolver cR=getContentResolver();
      MimeTypeMap mime=MimeTypeMap.getSingleton();
      return mime.getExtensionFromMimeType(cR.getType(uri));
  }
    private void uploadFile()
    {
        if(mImageUri!=null)
        {
            StorageReference fileRef=mStorageReference.child(System.currentTimeMillis() +"."+getFileExtension(mImageUri));
           mUploadTask= fileRef.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler=new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                 mProgressBar.setProgress(0);
                                }
                            },5000);
                            Toast.makeText(HomeActivity.this, "File is successful uploaded", Toast.LENGTH_SHORT).show();
                            Uoload uoload=new Uoload(mEnterFileName.getText().toString().trim(),taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                            String UploadId=mDatabaseReference.push().getKey();
                            mDatabaseReference.child(UploadId).setValue(uoload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                          double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                          mProgressBar.setProgress((int)progress);
                        }
                    });
        }
        else
        {
            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }
}
