package com.example.jezer.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Activity_chat extends AppCompatActivity {
    private Toolbar mToolbar;

    private String messageReceiverID, messageReceiverName, messageSenderID;
    private TextView username;

    private ImageButton sendMessageBtn;
    private EditText MessageInput;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        messageReceiverID = getIntent().getExtras().get("user_id").toString();
        messageReceiverName = getIntent().getExtras().get("user_name").toString();

        mToolbar = (Toolbar) findViewById(R.id.chat_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(messageReceiverName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        sendMessageBtn = (ImageButton) findViewById(R.id.chat_sendMessage_btn);
        MessageInput = (EditText) findViewById(R.id.chat_input_message);

        sendMessageBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }
    private  void sendMessage(){
        String messageText = MessageInput.getText().toString();
        if(TextUtils.isEmpty(messageText))
            Toast.makeText(this,"Input something...", Toast.LENGTH_SHORT).show();
        else{
            String messageSenderRef = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef = "Messages/" +  messageReceiverID + "/" + messageSenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID = userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderID);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID ,messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID ,messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Activity_chat.this,"Message send Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(Activity_chat.this,"Error", Toast.LENGTH_SHORT).show();
                    }
                    MessageInput.setText("");
                }
            });

        }
    }
}

