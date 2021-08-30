package com.openclassrooms.realestatemanager.data.local.provider

import android.content.ContentValues
import com.openclassrooms.realestatemanager.data.local.entities.SingleProperty

fun fromContentValues(cv: ContentValues): SingleProperty {
    val property = SingleProperty()
    if (cv.containsKey("id")) property.id = cv.getAsString("id")
    if (cv.containsKey("type")) property.type = cv.getAsString("type")
    if (cv.containsKey("description")) property.description = cv.getAsString("description")
    if (cv.containsKey("surface")) property.surface = cv.getAsInteger("surface")
    if (cv.containsKey("price")) property.price = cv.getAsInteger("price")
    if (cv.containsKey("rooms")) property.rooms = cv.getAsInteger("rooms")
    if (cv.containsKey("bedroom")) property.bedroom = cv.getAsInteger("bedroom")
    if (cv.containsKey("bathroom")) property.bathroom = cv.getAsInteger("bathroom")
    if (cv.containsKey("dateRegister")) property.dateRegister =
        cv.getAsString("dateRegister")
    if (cv.containsKey("dateSold")) property.dateSold = cv.getAsString("dateSold")
    if (cv.containsKey("address1")) property.address1 = cv.getAsString("address1")
    if (cv.containsKey("address2")) property.address2 = cv.getAsString("address2")
    if (cv.containsKey("city")) property.city = cv.getAsString("city")
    if (cv.containsKey("quarter")) property.quarter = cv.getAsString("quarter")
    if (cv.containsKey("postalCode")) property.postalCode = cv.getAsInteger("postalCode")
    if (cv.containsKey("location")) property.location = cv.getAsString("location")
    if (cv.containsKey("amenities")) property.amenities = cv.getAsString("amenities")
    if (cv.containsKey("agent")) property.agent = cv.getAsString("agent")
    return property
}