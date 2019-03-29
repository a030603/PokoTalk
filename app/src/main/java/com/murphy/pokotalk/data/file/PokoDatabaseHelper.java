package com.murphy.pokotalk.data.file;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.murphy.pokotalk.data.file.json.Serializer;
import com.murphy.pokotalk.data.file.schema.ContactsSchema;
import com.murphy.pokotalk.data.file.schema.GroupsSchema;
import com.murphy.pokotalk.data.file.schema.MessagesSchema;
import com.murphy.pokotalk.data.group.Group;
import com.murphy.pokotalk.data.group.MessageList;
import com.murphy.pokotalk.data.group.PokoMessage;
import com.murphy.pokotalk.data.user.User;
import com.murphy.pokotalk.data.user.UserList;

public class PokoDatabaseHelper {
    public static void insertOrUpdateGroupData(SQLiteDatabase db, Group group) {
        // Insert or update group data
        UserList memberList = group.getMembers();
        MessageList messageList = group.getMessageList();

        // Insert or update group data
        ContentValues groupValues = Serializer.obtainGroupValues(group);
        PokoDatabaseHelper.insertOrUpdateGroupData(db, groupValues);

        // Delete all member data
        PokoDatabaseHelper.deleteAllGroupMemberData(db, group);

        // Insert or update all member data
        for (User member : memberList.getList()) {
            ContentValues userValues = Serializer.obtainUserValues(member);
            ContentValues memberValues = Serializer.obtainGroupMemberValues(group, member);
            //Log.v("POKO", "UPDATE GROUP MEMBER DATA " + member.getNickname() + ", " + member.getUserId());
            PokoDatabaseHelper.insertOrUpdateUserData(db, member, userValues);
            PokoDatabaseHelper.insertOrIgnoreGroupMemberData(db, memberValues);
        }

        // Insert all message data
        for (PokoMessage message : messageList.getList()) {
            ContentValues messageValues = Serializer.obtainMessageValues(group, message);
            PokoDatabaseHelper.insertOrIgnoreMessageData(db, messageValues);
        }
    }
    public static long insertOrUpdateUserData(SQLiteDatabase db, User user, ContentValues values) {
        long count = insertOrIgnore(db, PokoDatabaseQuery.insertUserData, values);
        if (count <= 0) {
            return updateUserData(db, user, values);
        } else {
            return count;
        }
    }

    public static long insertOrUpdateContactData(SQLiteDatabase db, ContentValues values) {
        return insertOrReplace(db, PokoDatabaseQuery.insertContactData, values);
    }

    public static long insertOrUpdateGroupData(SQLiteDatabase db, ContentValues values) {
        return insertOrReplace(db, PokoDatabaseQuery.insertGroupData, values);
    }

    public static long insertOrIgnoreGroupMemberData(SQLiteDatabase db, ContentValues values) {
        return insertOrIgnore(db, PokoDatabaseQuery.insertGroupMemberData, values);
    }

    public static long insertOrIgnoreMessageData(SQLiteDatabase db, ContentValues values) {
        return insertOrIgnore(db, PokoDatabaseQuery.insertMessageData, values);
    }

    // args : userId
    public static long updateUserData(SQLiteDatabase db, User user, ContentValues values) {
        String[] selectionValues = {Integer.toString(user.getUserId())};
        return update(db, PokoDatabaseQuery.updateUserData, values, selectionValues);
    }

    // args : groupId
    public static long updateContactChatDataNull(SQLiteDatabase db, String[] args) {
        ContentValues values = new ContentValues();
        values.put(ContactsSchema.Entry.GROUP_CHAT_ID, "NULL");
        return update(db, PokoDatabaseQuery.updateContactChatDataNull, values, args);
    }

    // args : userId
    public static long updateContactChatData(SQLiteDatabase db, Group group, String[] args) {
        ContentValues values = new ContentValues();
        values.put(ContactsSchema.Entry.GROUP_CHAT_ID, group.getGroupId());
        return update(db, PokoDatabaseQuery.updateContactChatData, values, args);
    }

    // Update ack field of group
    public static long updateGroupAck(SQLiteDatabase db, Group group) {
        ContentValues values = new ContentValues();
        values.put(GroupsSchema.Entry.ACK, Integer.toString(group.getAck()));

        // Create selection args for where clause.
        String[] selectionArgs = {Integer.toString(group.getGroupId())};

        return update(db, PokoDatabaseQuery.updateGroup, values, selectionArgs);
    }

    // Updates nbNewMessage field.
    public static long updateGroupNbNewMessage(SQLiteDatabase db, Group group) {
        ContentValues values = new ContentValues();
        values.put(GroupsSchema.Entry.NB_NEW_MESSAGES, Integer.toString(group.getNbNewMessages()));

        // Create selection args for where clause.
        String[] selectionArgs = {Integer.toString(group.getGroupId())};

        return update(db, PokoDatabaseQuery.updateGroup, values, selectionArgs);
    }

    // Update special contents of message
    public static long updateSpacialContentsOfMessage(SQLiteDatabase db,
                                                      Group group, PokoMessage message) {
        ContentValues values = new ContentValues();
        values.put(MessagesSchema.Entry.SPECIAL_CONTENTS, message.getSpecialContent());

        // Create selection args for where clause.
        String[] selectionArgs = {Integer.toString(group.getGroupId()),
                Integer.toString(message.getMessageId())};

        return update(db, PokoDatabaseQuery.updateMessageByMessageId, values, selectionArgs);
    }

    // Update nbNotRead fields of message of group to 'nbNotRead'
    public static long updateMessageAck(SQLiteDatabase db,
                                        Group group, int messageId, int nbNotRead) {
        ContentValues values = new ContentValues();
        values.put(MessagesSchema.Entry.NB_NOT_READ, Integer.toString(nbNotRead));

        // Create selection args for where clause.
        String[] selectionArgs = {Integer.toString(group.getGroupId()),
                Integer.toString(messageId)};

        return update(db, PokoDatabaseQuery.updateMessageByMessageId, values, selectionArgs);
    }

    // Decrements nbNotRead field of messages of group from ackStart to ackEnd
    public static long decrementMessageAck(SQLiteDatabase db,
                                           Group group, int ackStart, int ackEnd) {
        // Create update attribute and data for set clause.
        ContentValues values = new ContentValues();
        values.put(MessagesSchema.Entry.NB_NOT_READ, MessagesSchema.Entry.NB_NOT_READ + " - 1");

        // Create selection args for where clause.
        String[] selectionArgs = {Integer.toString(group.getGroupId()),
                                Integer.toString(ackStart),
                                Integer.toString(ackEnd)};

        return update(db, PokoDatabaseQuery.updateMessageInMessageIdRange, values, selectionArgs);
    }

    public static long deleteAllContactData(SQLiteDatabase db) {
        return delete(db, PokoDatabaseQuery.deleteAllContactData, null);
    }

    public static long deleteAllPendingContactData(SQLiteDatabase db) {
        return delete(db, PokoDatabaseQuery.deleteAllPendingContactData, null);
    }

    public static long deleteAllGroupData(SQLiteDatabase db) {
        return delete(db, PokoDatabaseQuery.deleteAllGroupData, null);
    }

    public static long deleteAllGroupMemberData(SQLiteDatabase db, Group group) {
        String[] selectionArgs = {Integer.toString(group.getGroupId())};
        return delete(db, PokoDatabaseQuery.deleteAllGroupMemberData, selectionArgs);
    }

    // Deletes contact or pending contact by userId
    public static long deleteContactData(SQLiteDatabase db, String[] args) {
        return delete(db, PokoDatabaseQuery.deleteContactData, args);
    }

    // Deletes group by groupId, all members and message data are removed by cascade option
    public static long deleteGroupData(SQLiteDatabase db, String[] args) {
        return delete(db, PokoDatabaseQuery.deleteGroupData, args);
    }

    // Deletes group member by userId, messages of removed member still persists.
    // SelectionArgs -> 0: groupId
    //                  1: userId
    public static long deleteGroupMemberData(SQLiteDatabase db, String[] args) {
        return delete(db, PokoDatabaseQuery.deleteGroupMemberData, args);
    }

    public static Cursor readSessionData(SQLiteDatabase db) {
        return query(db, PokoDatabaseQuery.readSessionData, null);
    }

    public static Cursor readAllUserData(SQLiteDatabase db) {
        return db.rawQuery(PokoDatabaseQuery.readAllUserData, null);
    }

    public static Cursor readGroupData(SQLiteDatabase db) {
        return query(db, PokoDatabaseQuery.readGroupData, null);
    }

    public static Cursor readGroupMemberData(SQLiteDatabase db, int groupId) {
        String[] selectionArgs = {Integer.toString(groupId)};

        return query(db, PokoDatabaseQuery.readGroupMemberData, selectionArgs);
    }

    public static Cursor readMessageDataFromBack(SQLiteDatabase db,
                                                 int groupId, int offset, int num) {
        PokoDatabaseQuery query = PokoDatabaseQuery.readMessageDataFromBack;
        String[] selectionArgs = {Integer.toString(groupId)};
        String limitOffset = Integer.toString(offset) + ", " + Integer.toString(num);

        return db.query(query.table, query.projection, query.selection, selectionArgs,
                null, null, query.sortOrder, limitOffset);
    }

    public static Cursor readMessageDataFromBackByMessageId(SQLiteDatabase db,
                                                      int groupId, int messageId, int num) {
        PokoDatabaseQuery query = PokoDatabaseQuery.readMessageDataFromBackByMessageId;
        String[] selectionArgs = {Integer.toString(groupId),
                                Integer.toString(messageId)};
        String limitOffset = Integer.toString(num);

        return db.query(query.table, query.projection, query.selection, selectionArgs,
                null, null, query.sortOrder, limitOffset);
    }

    public static Cursor query(SQLiteDatabase db, PokoDatabaseQuery query, String[] selectionArgs) {
        Cursor cursor = db.query(query.table, query.projection, query.selection, selectionArgs,
                null, null, query.sortOrder, query.limitOffset);

        return cursor;
    }

    public static long insert(SQLiteDatabase db, PokoDatabaseQuery query, ContentValues values) {
        long result = db.insert(query.table, null, values);

        return result;
    }

    public static long insertOrReplace(SQLiteDatabase db, PokoDatabaseQuery query, ContentValues values) {
        long result = db.insertWithOnConflict(query.table,
                null, values, SQLiteDatabase.CONFLICT_REPLACE);

        return result;
    }

    public static long insertOrIgnore(SQLiteDatabase db, PokoDatabaseQuery query, ContentValues values) {
        long result = db.insertWithOnConflict(query.table,
                null, values, SQLiteDatabase.CONFLICT_IGNORE);

        return result;
    }

    public static long update(SQLiteDatabase db, PokoDatabaseQuery query, ContentValues contentValues,
                              String[] selectionArgs) {
        long result = db.update(query.table, contentValues, query.selection, selectionArgs);

        return result;
    }

    public static long delete(SQLiteDatabase db, PokoDatabaseQuery query, String[] selectionArgs) {
        long result = db.delete(query.table, query.selection, selectionArgs);

        return result;
    }
}