package com.openclassrooms.realestatemanager.data.local.provider

import com.openclassrooms.realestatemanager.data.local.database.RealEstateDatabase.Companion.getInstance
import android.content.ContentProvider
import android.content.ContentValues
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import java.lang.IllegalArgumentException

class RealEstatePropertiesProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        strings: Array<String>?,
        s: String?,
        strings1: Array<String>?,
        s1: String?
    ): Cursor {
        if (context != null) {
            val cursor = getInstance(context!!)!!
                .singlePropertyDao()!!.propertyWithImagesProvider
            cursor!!.setNotificationUri(context!!.contentResolver, uri)
            return cursor
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String {
        return "vnd.android.cursor.item/$AUTHORITY/*"
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri {
        if (context != null && contentValues != null) {
            val id = getInstance(context!!)!!
                .singlePropertyDao()!!.createSingleProperty(fromContentValues(contentValues))
            if (id != 0L) {
                context!!.contentResolver.notifyChange(uri, null)
                return ContentUris.withAppendedId(uri, id)
            }
        }
        throw IllegalArgumentException("Failed to insert row into $uri")
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        // Real Estate Manager application don't provide deletion of properties, her provide only read the properties.
        return 0
    }

    override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        s: String?,
        strings: Array<String>?
    ): Int {
        if (context != null && contentValues != null) {
            val res = getInstance(context!!)!!
                .singlePropertyDao()!!.updateProperty(fromContentValues(contentValues))
            if (res != 0) {
                context!!.contentResolver.notifyChange(uri, null)
                return res
            }
        }
        throw IllegalArgumentException("Failed to update row into $uri")
    }

    companion object {
        const val AUTHORITY = "com.openclassrooms.realestatemanager.provider"
        @JvmField
        val URI_DB: Uri = Uri.parse("content://$AUTHORITY/*")
    }
}