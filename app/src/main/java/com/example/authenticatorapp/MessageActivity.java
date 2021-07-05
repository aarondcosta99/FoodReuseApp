package com.example.authenticatorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authenticatorapp.Adapters.MessageAdapter;
import com.example.authenticatorapp.Model.Chat;
import com.example.authenticatorapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private static final String TAG = "MessageActivity";
    CircleImageView profile_image;
    TextView username;
    FirebaseFirestore rootRef;
    ImageView btn_send,btn_loc;
    EditText text_send;
    String userId,roomId,tokenId;
    MessageAdapter messageAdapter;
    List<Chat> mchat;
    RecyclerView recyclerView;
    ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        recyclerView             = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        username                 = findViewById(R.id.usernametv);
        text_send                = findViewById(R.id.text_send);
        btn_send                 = findViewById(R.id.btn_send);
        btn_loc                  = findViewById(R.id.sendloc);
        rootRef                  = FirebaseFirestore.getInstance();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<String> task) {
                tokenId=task.getResult();
            }
        });
        final String recipientId = getIntent().getStringExtra("recipientId");
        profile_image            = findViewById(R.id.profile_image);
        userId = FirebaseAuth.getInstance().getUid();
        assert recipientId != null;
        assert userId != null;
        if(userId.compareTo(recipientId)<0){
            roomId = userId.concat(recipientId);
        }else if(userId.compareTo(recipientId) == 0){
            Toast.makeText(MessageActivity.this,"Error you are chatting with yourself!!!", Toast.LENGTH_SHORT).show();
        }
        else{
            roomId = recipientId.concat(userId);
        }
        profile_image.setImageResource(R.drawable.ic_default);
        DocumentReference recipientReference = rootRef.collection("users").document(recipientId);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if(!msg.isEmpty()){
                    sendMessage(FirebaseAuth.getInstance().getUid(), recipientId,msg);
                }else {
                    Toast.makeText(MessageActivity.this,"You can't send empty message",Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });
        btn_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "http://maps.google.com/maps?daddr=";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
        recipientReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        User user = document.toObject(User.class);
                        username.setText(user.getfName());
                        readMessages(userId,recipientId);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    EventListener<DocumentSnapshot> eventListener = new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
            if (snapshot != null){
                String recipientId = getIntent().getStringExtra("recipientId");
                Chat chat = snapshot.toObject(Chat.class);
                if (chat != null) {
                    if (chat.getReceiver().equals(userId) && chat.getUserId().equals(recipientId)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        FirebaseFirestore.getInstance().collection("chats").document(roomId).collection("messages").document().update(hashMap);
                    }
                }
                else {
                    // this is a comment.
                }
            }
        }
    };
    private void sendMessage(final String sender, String receiver, final String message){
        final DocumentReference documentReference = rootRef.collection("chats").document(roomId).collection("messages").document();
        Map<String,Object> user = new HashMap<>();
        user.put("UserId",sender);
        user.put("receiver",receiver);
        user.put("message",message);
        user.put("time", FieldValue.serverTimestamp());
        user.put("rid",roomId);
        user.put("isseen",false);
        user.put("TokenId",tokenId);
        documentReference.set(user,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: MessageSent "+ userId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onSuccess: Error"+ userId);
            }
        });
    }
    private void readMessages(final String userId, final String recipientId){
        mchat = new ArrayList<>();
        CollectionReference collectionReference = rootRef.collection("chats").document(roomId).collection("messages");
        collectionReference.orderBy("time", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: Listen failed.", e);
                    return;
                }
                mchat.clear();
                if(documentSnapshots!=null){
                    for(QueryDocumentSnapshot queryDocumentSnapshots : documentSnapshots){
                        Chat chat = queryDocumentSnapshots.toObject(Chat.class);
                        if(chat.getReceiver().equals(recipientId)&&chat.getUserId().equals(userId)||
                        chat.getReceiver().equals(userId)&&chat.getUserId().equals(recipientId)){
                            mchat.add(chat);
                        }
                        messageAdapter = new MessageAdapter(MessageActivity.this,mchat);
                        recyclerView.setAdapter(messageAdapter);
                    }
                }
            }
        });
        FirebaseFirestore.getInstance()
                .collection("chats").document(roomId)
                .collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot results,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        String recipientId = getIntent().getStringExtra("recipientId");
                        for (QueryDocumentSnapshot doc : results) {
                            Chat chat = doc.toObject(Chat.class);
                            if (chat != null) {
                                if (chat.getReceiver().equals(userId) && chat.getUserId().equals(recipientId)){
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("isseen", true);
                                    doc.getReference().update(hashMap);
                                }
                            }
                        }
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        listenerRegistration = FirebaseFirestore.getInstance().collection("chats").document(roomId).collection("messages").document().addSnapshotListener(eventListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}