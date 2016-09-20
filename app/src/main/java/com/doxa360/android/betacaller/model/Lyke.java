package com.doxa360.android.betacaller.model;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Apple on 09/01/16.
 */
@ParseClassName("Lyke")
public class Lyke extends ParseObject {
    
    public Lyke() {
    }

    public Lyke(ParseUser user, ParseFile photo, String lyke, ParseObject[] group, Date createdAt) {
        put("user", user);
        put("photo", photo);
        put("lyke", lyke);
        addAllUnique("groups", Arrays.asList(group));
        put("createdAt", createdAt);
    }

    public String getLyke() {
        return getString("lyke");
    }

    public void setLyke(String value) {
        put("lyke", value);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public void setPhoto(ParseFile value) {
        put("photo", value);
    }

    public List<String> getGroups() {
        return getList("groups");
    }

    public void setGroups(List<String> value) {
        addAllUnique("groups", value);
    }

    public List<String> getGroupNames() {
        return getList("group_names");
    }

    public void setGroupNames(List<String> value) {
        addAllUnique("group_names", value);
    }

    public long getCreatedDateTime() {
        return getCreatedAt().getTime();
    }

    public Date getCreatedDate() {
        return getDate("createdAt");
    }

    public void setCreatedDate(Date value) {
        put("createdAt", value);
    }

    public static ParseQuery<Lyke> getQuery() {
        return ParseQuery.getQuery(Lyke.class);
    }


}
