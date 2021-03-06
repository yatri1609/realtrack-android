package com.hackforchange.backend.reminders;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hackforchange.backend.GlobalDatabaseHelper;
import com.hackforchange.models.reminders.Reminders;
import com.hackforchange.reminderalarms.NotificationReceiver;

/*
 * DAO object to update/delete/add reminders
 */
public class RemindersDAO {
    private GlobalDatabaseHelper opener;
    private SQLiteDatabase readDatabase;
    private SQLiteDatabase writeDatabase;

    public RemindersDAO(Context context) {
        this.opener = GlobalDatabaseHelper.getInstance(context);
        this.readDatabase = opener.getReadableDatabase();
        this.writeDatabase = opener.getWritableDatabase();
        closeDB();
    }

    private void openDB() {
        if (!readDatabase.isOpen()) {
            readDatabase = opener.getReadableDatabase();
        }
        if (!writeDatabase.isOpen()) {
            writeDatabase = opener.getWritableDatabase();
        }
    }

    private void closeDB() {
        if (readDatabase.isOpen()) {
            readDatabase.close();
        }
        if (writeDatabase.isOpen()) {
            writeDatabase.close();
        }
    }

    public ArrayList<Reminders> getAllReminders() {
        openDB();
        ArrayList<Reminders> output = null;
        String[] columnsToRead = new String[3];
        columnsToRead[0] = Reminders.COLUMN_ID;
        columnsToRead[1] = Reminders.COLUMN_ACTIVITYID;
        columnsToRead[2] = Reminders.COLUMN_REMINDTIME;
        Cursor returnData = readDatabase.query(Reminders.REMINDERS_TABLE, columnsToRead, null, null, null, null, null);
        output = extractReminders(returnData);
        closeDB();
        return output;
    }

    public ArrayList<Reminders> getAllRemindersForActivityId(int activityid) {
        openDB();
        ArrayList<Reminders> output = null;
        String[] columnsToRead = new String[3];
        columnsToRead[0] = Reminders.COLUMN_ID;
        columnsToRead[1] = Reminders.COLUMN_ACTIVITYID;
        columnsToRead[2] = Reminders.COLUMN_REMINDTIME;
        String whereClause = Reminders.COLUMN_ACTIVITYID + '=' + activityid;
        String orderbyClause = Reminders.COLUMN_REMINDTIME + " asc"; // order in ascending order of remind time
        Cursor returnData = readDatabase.query(Reminders.REMINDERS_TABLE, columnsToRead,
            whereClause, null, null, null, orderbyClause);
        output = extractReminders(returnData);
        closeDB();
        return output;
    }

    private ArrayList<Reminders> extractReminders(Cursor returnData) {
        // The output ArrayList is initialized
        ArrayList<Reminders> output = new ArrayList<Reminders>();
        // Move the counter to the first item in the return data
        returnData.moveToFirst();
        int count = 0;
        // While there are still values in the return data
        while (!returnData.isAfterLast()) {
            // Add the new Reminders to the ArrayList
            Reminders r = new Reminders();
            r.setId(Integer.parseInt(returnData.getString(0)));
            r.setActivityid(Integer.parseInt(returnData.getString(1)));
            r.setRemindTime(returnData.getLong(2));
            output.add(count, r);
            // Advance the Cursor
            returnData.moveToNext();
            // Advance the counter
            count++;
        }
        // Return the ArrayList
        return output;
    }

    public Reminders getReminderWithId(int id) {
        openDB();
        String[] columnsToRead = new String[2];
        columnsToRead[0] = Reminders.COLUMN_ACTIVITYID;
        columnsToRead[1] = Reminders.COLUMN_REMINDTIME;

        String whereClause = Reminders.COLUMN_ID + '=' + id;
        Cursor returnData = readDatabase.query(Reminders.REMINDERS_TABLE, columnsToRead,
            whereClause, null, null, null, null);
        returnData.moveToFirst();
        Reminders r = new Reminders();
        r.setId(id);
        r.setActivityid(returnData.getInt(0));
        r.setRemindTime(returnData.getLong(1));
        closeDB();
        // Return the constructed Reminders object
        return r;
    }

    public void addReminders(Reminders reminder, Context context) {
        openDB();
        ContentValues newValue = new ContentValues(2);
        newValue.put(Reminders.COLUMN_ACTIVITYID, reminder.getActivityid());
        newValue.put(Reminders.COLUMN_REMINDTIME, reminder.getRemindTime());
        // Insert the item into the database
        writeDatabase.insert(Reminders.REMINDERS_TABLE, null, newValue);
        closeDB();
        createOrUpdateAlarms(context);
    }

    public void updateReminders(Reminders reminder, Context context) {
        openDB();
        ContentValues newValue = new ContentValues(2);
        newValue.put(Reminders.COLUMN_ACTIVITYID, reminder.getActivityid());
        newValue.put(Reminders.COLUMN_REMINDTIME, reminder.getRemindTime());
        String whereClause = Reminders.COLUMN_ID + '=' + reminder.getId();
        // Update the item into the database
        writeDatabase.update(Reminders.REMINDERS_TABLE, newValue, whereClause, null);
        closeDB();
        createOrUpdateAlarms(context);
    }

    public int deleteReminders(int id, Context context) {
        openDB();
        String whereClause = Reminders.COLUMN_ID + '=' + id;
        // Return the total number of rows removed
        int numItemsDeleted = writeDatabase.delete(Reminders.REMINDERS_TABLE, whereClause, null);
        closeDB();
        createOrUpdateAlarms(context);
        return numItemsDeleted;
    }

    private void createOrUpdateAlarms(Context context) {
        // Schedule an alarm for the notifications when the reminder is first created
        // this action is eventually performed in NotificationService
        // The same code is also invoked on a BOOT_COMPLETED event because the NotificationService
        // does not remember the alarms set before a reboot
        NotificationReceiver.scheduleAlarm(context);
    }
}
