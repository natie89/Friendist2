package com.natimeital.friendist;

/**
 * Created by Moti on 23/05/2016.
 */
public class MessageObject {
    public String recievingUser, sendingUser, theMessege, thedate;
    int id;

    public MessageObject(String recievingUser, String sendingUser, String theMessege, String thedate, int id) {
        this.recievingUser = recievingUser;
        this.thedate = thedate;
        this.id = id;
        this.sendingUser = sendingUser;
        this.theMessege = theMessege;
    }

    public MessageObject() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getThedate() {
        return thedate;
    }

    public void setThedate(String thedate) {
        this.thedate = thedate;
    }

    public String getRecievingUser() {
        return recievingUser;
    }

    public void setRecievingUser(String recievingUser) {
        this.recievingUser = recievingUser;
    }

    public String getTheMessege() {
        return theMessege;
    }

    public void setTheMessege(String theMessege) {
        this.theMessege = theMessege;
    }

    public String getSendingUser() {
        return sendingUser;
    }

    public void setSendingUser(String sendingUser) {
        this.sendingUser = sendingUser;
    }

    @Override
    public String toString() {
        return "MessageObject{" +
                "recievingUser='" + recievingUser + '\'' +
                ", sendingUser='" + sendingUser + '\'' +
                ", theMessege='" + theMessege + '\'' +
                '}';
    }
}
