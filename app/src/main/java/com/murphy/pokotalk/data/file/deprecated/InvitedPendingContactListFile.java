package com.murphy.pokotalk.data.file.deprecated;

import com.murphy.pokotalk.Constants;
import com.murphy.pokotalk.data.DataCollection;
import com.murphy.pokotalk.data.user.PendingContactPokoList;

public class InvitedPendingContactListFile extends PendingContactListFile{
    @Override
    public String getFileName() {
        return Constants.invitedPendingContactFile;
    }

    @Override
    public PendingContactPokoList getPendingContactList() {
        return DataCollection.getInstance().getInvitedContactList();
    }
}
