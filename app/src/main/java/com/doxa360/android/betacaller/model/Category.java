package com.doxa360.android.betacaller.model;

import android.graphics.Bitmap;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by Apple on 12/04/16.
 */
@ParseClassName("CorporateCategory")

public class Category extends ParseObject {

    public static final String CATEGORY = "CATEGORY";
    public static final String ID = "CATEGORY_ID";

    public Category() {
    }

    public String getCategory() {
        return getString("category");
    }

    public void setCategory(String value) {
        put("category", value);
    }

    public static ParseQuery<Category> getQuery() {
        return ParseQuery.getQuery(Category.class);
    }



}