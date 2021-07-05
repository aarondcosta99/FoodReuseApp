package com.example.authenticatorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class Donate extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText Address,Details;
    Button submit;
    TextView FullName;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    String tokenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);
        FullName = findViewById(R.id.fName);
        Address  = findViewById(R.id.address);
        Details  = findViewById(R.id.details);
        submit   = findViewById(R.id.btnSubmit);
        fAuth    = FirebaseAuth.getInstance();
        fStore   = FirebaseFirestore.getInstance();

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<String> task) {
                tokenId=task.getResult();
            }
        });

        userId = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                FullName.setText(documentSnapshot.getString("fName"));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btnSubmit) {
                    alertDialog();
                }
            }
            private void alertDialog(){
                AlertDialog.Builder dialog=new AlertDialog.Builder(Donate.this);
                dialog.setMessage("Please be specific and only mention the details of the Food that you will be donating. Do not give away any personal data as all information entered can be viewed by all the other users of the app");
                dialog.setTitle("Alert");
                dialog.setPositiveButton("I Agree",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String name = FullName.getText().toString();
                                String address = Address.getText().toString();
                                String details = Details.getText().toString();
                                if (TextUtils.isEmpty(name)){
                                    FullName.setError("Name is Required");
                                    return;
                                }
                                if (TextUtils.isEmpty(address)){
                                    Address.setError("Address is Required");
                                    return;
                                }

                                if (TextUtils.isEmpty(details)){
                                    Details.setError("Details is Required");
                                    return;
                                }

                                String userID = fAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fStore.collection("DonationDetails").document();
                                Map<String,Object> user = new HashMap<>();
                                user.put("fName",name);
                                user.put("Address",address);
                                user.put("Details",details);
                                user.put("time", FieldValue.serverTimestamp());
                                user.put("UserId",userID);
                                user.put("TokenId",tokenId);
                                documentReference.set(user, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Donate.this,"Details Submitted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Donate.this,"Details not Submitted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                DocumentReference documentReference2 = FirebaseFirestore.getInstance().collection("dd").document(userID);
                                Map<String,Object> user2 = new HashMap<>();
                                user2.put("fName",name);
                                user2.put("UserId",userID);
                                documentReference2.set(user2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "dd added "+ fAuth.getCurrentUser().getUid());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "dd failed: "+ e.toString());
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                Toast.makeText(getApplicationContext(),"You will now be redirected to the Home Page",Toast.LENGTH_LONG).show();
                            }
                        });
                dialog.setNegativeButton("Go Back",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog=dialog.create();
                alertDialog.show();
            }
        });
    }
}
