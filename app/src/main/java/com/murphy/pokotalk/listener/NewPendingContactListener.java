package com.murphy.pokotalk.listener;

import android.util.Log;

import com.murphy.pokotalk.Constants;
import com.murphy.pokotalk.data.Contact;
import com.murphy.pokotalk.data.ContactList;
import com.murphy.pokotalk.data.DataCollection;
import com.murphy.pokotalk.parser.PokoParser;
import com.murphy.pokotalk.server.PokoServer;
import com.murphy.pokotalk.server.Status;

import org.json.JSONException;
import org.json.JSONObject;

public class NewPendingContactListener extends PokoServer.PokoListener {
    @Override
    public String getEventName() {
        return Constants.newPendingContactName;
    }

    @Override
    public void callSuccess(Status status, Object... args) {
        JSONObject data = (JSONObject) args[0];
        DataCollection collection = DataCollection.getInstance();
        ContactList invitedList = collection.getInvitedContactList();
        ContactList invitingList = collection.getInvitingContactList();
        try {
            JSONObject jsonObject = data.getJSONObject("contact");
            Contact contact = PokoParser.parsePendingContact(jsonObject);

            /* Check invited field of pending contact */
            /* 1: I was invited, 0: I invited */
            Boolean invited = PokoParser.parseContactInvitedField(jsonObject);
            if (invited)
                invitedList.updateContact(contact);
            else
                invitingList.updateContact(contact);
        } catch (JSONException e) {
            Log.e("POKO ERROR", "Bad pending contact json data");
        }
    }

    @Override
    public void callError(Status status, Object... args) {
        Log.e("POKO ERROR", "Failed to get pending contact data");
    }
}