package com.murphy.pokotalk.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.murphy.pokotalk.Constants;
import com.murphy.pokotalk.R;
import com.murphy.pokotalk.data.Session;
import com.murphy.pokotalk.data.user.Contact;
import com.murphy.pokotalk.view.ContactItem;

public class SettingFragment extends Fragment
        implements LogoutBewareDialog.Listener {
    private Contact user;
    private Button logoutButton;
    private Button profileImageButton;
    private Button appInfoButton;
    private FrameLayout frameLayout;
    private TextView userHelloTextView;
    private ContactItem userItem;
    private Listener listener;

    public static final int ACTION_LOGOUT = 7575;

    public interface Listener {
        void onSettingAction(int action);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement listener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_layout, null, false);

        // Get user
        user = Session.getInstance().getUser();

        // Find views
        frameLayout = view.findViewById(R.id.settings_user_frame);
        logoutButton = view.findViewById(R.id.settings_logout_button);
        profileImageButton = view.findViewById(R.id.settings_profile_image_button);
        appInfoButton = view.findViewById(R.id.settings_app_info_button);
        userHelloTextView = view.findViewById(R.id.settings_user_hello);

        // Create user item
        userItem = new ContactItem(getContext());
        userItem.inflate();
        userItem.setContact(user);

        // Add user item
        frameLayout.addView(userItem);

        // Set user hello text
        String format = getContext().getString(R.string.settings_user_hello_format);
        userHelloTextView.setText(String.format(Constants.locale, format, user.getNickname()));

        // Dialog listener
        final LogoutBewareDialog.Listener listener = this;

        // Add button listeners
        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show profile image activity
                Intent intent = new Intent(getContext(), ProfileSettingActivity.class);
                startActivityForResult(intent, Constants.RequestCode.PROFILE_UPDATE.value);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    // Show beware dialog for logout
                    LogoutBewareDialog dialog = new LogoutBewareDialog();
                    dialog.setListener(listener);
                    dialog.show(activity.getSupportFragmentManager(), "logout beware");
                }
            }
        });

        appInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Now start image load test activity
                Intent intent = new Intent(getContext(), DownloadTestActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void logoutOptionSelected(int option) {
        switch (option) {
            case LogoutBewareDialog.OPTION_LOGOUT: {
                listener.onSettingAction(ACTION_LOGOUT);
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RequestCode.PROFILE_UPDATE.value) {
            Log.v("POKO", "Profile setting activity returned");
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
