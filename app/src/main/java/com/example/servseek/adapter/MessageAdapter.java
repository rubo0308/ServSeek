package com.example.servseek.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servseek.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private Context context;
    private OnMessageClickListener listener;

    public MessageAdapter(List<Message> messages, Context context, OnMessageClickListener listener) {
        this.messages = messages;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private TextView messageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageInput);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Message message) {
            messageTextView.setText((CharSequence) message.getTarget());
        }


        @Override
        public boolean onLongClick(View v) {
            final int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Message message = messages.get(position);
                showOptionsDialog(message);
            }
            return true;
        }

        private void showOptionsDialog(final Message message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Message Options");
            builder.setItems(new CharSequence[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            editMessage(message);
                            break;
                        case 1:
                            deleteMessage(message);
                            break;
                    }
                }
            });
            builder.create().show();
        }

        private void editMessage(final Message message) {
            // Implement editing message functionality here
        }

        private void deleteMessage(Message message) {
            int position = messages.indexOf(message);
            messages.remove(message);
            notifyItemRemoved(position);
            // Implement deletion of message functionality here
        }
    }

    public interface OnMessageClickListener {
        void onMessageClick(Message message);
    }
}
