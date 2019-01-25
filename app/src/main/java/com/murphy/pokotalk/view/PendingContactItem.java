package com.murphy.pokotalk.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.murphy.pokotalk.R;
import com.murphy.pokotalk.data.user.PendingContact;

import de.hdodenhof.circleimageview.CircleImageView;

public class PendingContactItem extends FrameLayout {
    private String nickname;
    private String email;
    private String img;
    private Context context;
    private TextView nicknameView;
    private TextView emailView;
    private CircleImageView imageView;
    private Button acceptButton;
    private Boolean invited;
    private PendingContact contact;

    public PendingContactItem(Context context) {
        super(context);
        this.context = context;
    }

    public void inflate() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pending_contact_item, this, true);
        nicknameView = (TextView) view.findViewById(R.id.nickname);
        emailView = (TextView) view.findViewById(R.id.email);
        imageView = (CircleImageView) view.findViewById(R.id.image);
        acceptButton = (Button) view.findViewById(R.id.acceptButton);
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getImg() {
        return img;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        nicknameView.setText(nickname);
    }

    public void setEmail(String email) {
        this.email = email;
        emailView.setText(email);
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Boolean getInvited() {
        return invited;
    }

    public PendingContact getContact() {
        return contact;
    }

    public void setContact(PendingContact contact) {
        this.contact = contact;
    }

    public void setInvited(Boolean invited) {
        this.invited = invited;

        if (invited)
            acceptButton.setVisibility(View.VISIBLE);
        else
            acceptButton.setVisibility(View.INVISIBLE);
    }

    public TextView getNicknameView() {
        return nicknameView;
    }

    public TextView getEmailView() {
        return emailView;
    }

    public CircleImageView getImageView() {
        return imageView;
    }

    public Button getAcceptButton() {
        return acceptButton;
    }
}
