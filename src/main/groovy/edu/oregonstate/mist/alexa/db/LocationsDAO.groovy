package edu.oregonstate.mist.alexa.db

import groovy.transform.InheritConstructors

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@InheritConstructors
class LocationsDAO extends ApiDAO {
    public static final NO_OPEN_RESTAURANTS = "Sorry. There are no open restaurants."

    String getOpenRestaurants() {
        def urlPrefix = alexaConfiguration['locationsUrl'].toString()
        String url = urlPrefix + "?type=dining&isOpen=true"
        def jsonApiObject = requests.get(url)

        // check if it's NO_RESULTS a 404 or empty body
        if (!jsonApiObject || !jsonApiObject['data']) {
            return NO_OPEN_RESTAURANTS
        }

        def restaurantList = []
        jsonApiObject['data'].each {
            restaurantList += it['attributes']['name']
        }

        "The following restaurants are open: " + joinList(restaurantList)
    }

    String getNearbyRestaurants(double withinDistance) {
        //def withinDistance = 1 //This is in miles.
        def deviceLongitude = alexaConfiguration['deviceLongitude']
        def deviceLatitude = alexaConfiguration['deviceLatitude']

        def urlPrefix = alexaConfiguration['locationsUrl'].toString()
        String url = urlPrefix + "?type=dining"
        url += "&distance=$withinDistance"
        url += "&lon=$deviceLongitude&lat=$deviceLatitude"
        url += "&campus=corvallis"
        url += "&distanceUnit=miles"

        def jsonApiObject = requests.get(url)

        // check if it's NO_RESULTS a 404 or empty body
        if (!jsonApiObject) {
            return NO_OPEN_RESTAURANTS
        }

        if (!jsonApiObject['data']) {
            return NO_OPEN_RESTAURANTS
        }

        def nearbyList = []
        jsonApiObject['data'].each {
            nearbyList += it['attributes']['name']
        }

        "The following restaurants are nearby: " + joinList(nearbyList)
    }

    /**
     * Returns a string in the form of "The locationQuery is open today from x to y"
     *
     * @param locationQuery
     * @return
     */
    String openHoursForLocation(def slots) {
        String query = slots["location"]["value"].toString()
        String queryEncoded = URLEncoder.encode(query, "UTF-8")
        String locationQuery = query

        def notOpenToday = "Sorry, ${locationQuery} is not open today."
        def couldntFindIt = "Sorry, I couldn't find: ${locationQuery}"

        def urlPrefix = alexaConfiguration['locationsUrl'].toString()
        String url = urlPrefix + "?q=" + queryEncoded
        def jsonApiObject = requests.get(url)

        // check if it's NO_RESULTS a 404 or empty body
        if (!jsonApiObject || !jsonApiObject['data']) {
            println "DEBUG: Couldn't find the result at all"
            return couldntFindIt
        }

        // @todo: handle multiple results

        // Check if we have open hours at all
        def attributes = jsonApiObject['data'][0]['attributes']
        def doesntHaveHours = !attributes['openHours']
        if (doesntHaveHours) {
            println "DEBUG: The location doesn't have any hours"
            return notOpenToday
        }

        // Check if we have open hours for today
        LocalDate now = LocalDate.now()
        String dayOfWeek = now.getDayOfWeek().getValue() - 1
        def todayHours = attributes['openHours'][dayOfWeek]
        if (!todayHours) {
            return notOpenToday
        }

        //@todo: convert hours to human values!
        def start = getHumanTime(todayHours[0]['start'].toString())
        def end = getHumanTime(todayHours[0]['end'].toString())
        "${locationQuery} is open today from ${ start} to ${end}."
    }

    static String getHumanTime(String datetime) {
        ZonedDateTime result = ZonedDateTime.parse(datetime, DateTimeFormatter.ISO_DATE_TIME)
        LocalDateTime localDateTime = result.toLocalDateTime()
        println "localDate: " + localDateTime

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm")
        localDateTime.format(formatter)
    }
}
