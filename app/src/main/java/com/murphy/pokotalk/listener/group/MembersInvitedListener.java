package com.murphy.pokotalk.listener.group;

import android.util.Log;

import com.murphy.pokotalk.Constants;
import com.murphy.pokotalk.data.DataCollection;
import com.murphy.pokotalk.data.group.Group;
import com.murphy.pokotalk.data.user.Stranger;
import com.murphy.pokotalk.data.user.StrangerList;
import com.murphy.pokotalk.data.user.User;
import com.murphy.pokotalk.data.user.UserList;
import com.murphy.pokotalk.parser.PokoParser;
import com.murphy.pokotalk.server.PokoServer;
import com.murphy.pokotalk.server.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MembersInvitedListener extends PokoServer.PokoListener {
    @Override
    public String getEventName() {
        return Constants.membersInvitedName;
    }

    @Override
    public void callSuccess(Status status, Object... args) {
        JSONObject jsonObject = (JSONObject) args[0];

        try {
            int groupId = jsonObject.getInt("groupId");
            JSONArray jsonMembers = jsonObject.getJSONArray("members");
            Group group = DataCollection.getInstance().getGroupList().getItemByKey(groupId);

            /* Group should exist */
            if (group == null) {
                Log.e("POKO ERROR", "Member cannot enter group since there is no such group");
                return;
            }

            DataCollection collection = DataCollection.getInstance();
            StrangerList strangerList = collection.getStrangerList();
            UserList memberList = group.getMembers();
            for (int i = 0; i < jsonMembers.length(); i++) {
                JSONObject jsonMember = jsonMembers.getJSONObject(i);
                Stranger invitedUser = PokoParser.parseStranger(jsonMember);
                User existingUser = collection.getUserById(invitedUser.getUserId());
                /* If the user exists just add to member list */
                if (existingUser != null) {
                    memberList.updateItem(existingUser);
                    return;
                } else {
                    /* Add Stranger to Stranger and member list */
                    memberList.updateItem(invitedUser);
                    strangerList.updateItem(invitedUser);
                }
            }
        } catch (JSONException e) {
            Log.e("POKO ERROR", "Bad invited member json data");

        }
    }

    @Override
    public void callError(Status status, Object... args) {
        Log.e("POKO ERROR", "Failed to get invited member");
    }
}
