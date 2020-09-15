package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateNewContact extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imageView_camera;
    EditText et_addcontact_name;
    EditText et_addcontact_email;
    EditText et_addcontact_phone;
    Button button_submit;

    FirebaseStorage storage;
    StorageReference storageRef;

    Bitmap bitmap;
    Boolean isTakenPhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_contact);

        setTitle("Create New Contact");

        storage = FirebaseStorage.getInstance();

        storageRef = storage.getReference();

        imageView_camera = findViewById(R.id.imageView_camera);
        et_addcontact_name = findViewById(R.id.et_addcontact_name);
        et_addcontact_email = findViewById(R.id.et_addcontact_email);
        et_addcontact_phone = findViewById(R.id.et_addcontact_phone);
        button_submit = findViewById(R.id.button_submit);

        imageView_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected()){
                    final String name = et_addcontact_name.getText().toString().trim();
                    final String email = et_addcontact_email.getText().toString().trim();
                    final String phone = et_addcontact_phone.getText().toString().trim();
                    boolean addContact = true;
                    if(name.equals("")){
                        addContact=false;
                        et_addcontact_name.setError("Enter the Contact Name!!");
                    }
                    if(email.equals("")){
                        addContact=false;
                        et_addcontact_email.setError("Enter the Contact Email!!");
                    }
                    else if(!isEmailValid(email)){
                        et_addcontact_email.setError("Email format incorrect!!");
                        addContact=false;
                    }
                    if(phone.equals("")){
                        addContact=false;
                        et_addcontact_phone.setError("Enter the Contact Number!!");
                    }else if(phone.length()!=10){
                        addContact=false;
                        et_addcontact_phone.setError("Enter the 10 Digit Contact Number!!");
                    }
                    if(!isTakenPhoto){
                        BitmapDrawable drawable = (BitmapDrawable) imageView_camera.getDrawable();
                        bitmap = drawable.getBitmap();

                    }

                    if (addContact){

                        Random rand = new Random();
                        int rand_int1 = rand.nextInt(1000);
                        final StorageReference imageStorageRef = storageRef.child("images/"+
                                name.replaceAll("\\s+","_")+"_"+rand_int1+".jpg");
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();
                        UploadTask uploadTask = imageStorageRef.putBytes(data);

                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return imageStorageRef.getDownloadUrl();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("demo",e.toString());
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    String downloadUrl = downloadUri.toString();
                                    Intent intent = new Intent();

                                    String phoneNoFormatted = phone.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "$1-$2-$3");
                                    ContactInfo info = new ContactInfo(
                                            downloadUrl, name, email, phoneNoFormatted);

                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(ViewContacts.CONTACT_INFO, info);
                                    intent.putExtras(bundle);
                                    setResult(RESULT_OK,intent);
                                    finish();
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });
                    }
                }else{
                    Toast.makeText(CreateNewContact.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            bitmap = imageBitmap;
            imageView_camera.setImageBitmap(imageBitmap);
            isTakenPhoto = true;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }        return true;
    }
}
