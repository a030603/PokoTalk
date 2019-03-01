package com.murphy.pokotalk.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.murphy.pokotalk.R;
import com.murphy.pokotalk.data.group.PokoMessage;

public class DateChangeMessageItem extends MessageItem {
    private String content;
    private TextView messageView;

    public DateChangeMessageItem(Context context) {
        super(context);
    }

    @Override
    public void inflate() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.message_day_border, this, true);
        messageView = view.findViewById(R.id.dateText);
    }

    @Override
    public void setMessage(PokoMessage message) {
        this.message = message;
        switch(message.getMessageType()) {
            case PokoMessage.APP_DATE_MESSAGE: {
                setContent(message.getSpecialContent());
                break;
            }
            default: {
                setContent("알 수 없는 타입");
                break;
            }
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        messageView.setText(content);
    }
}
