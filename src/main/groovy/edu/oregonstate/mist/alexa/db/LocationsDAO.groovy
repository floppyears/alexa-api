package edu.oregonstate.mist.alexa.db

import groovy.transform.InheritConstructors

@InheritConstructors
class LocationsDAO extends ApiDAO {

    public static final NO_OPEN_RESTAURANTS = "Sorry. There are no open restaurants."

    String getOpenRestaurants() {
        def urlPrefix = alexaConfiguration['locationsUrl'].toString()
        String url = urlPrefix + "?type=dining&isOpen=true"
        def jsonApiObject = requests.get(url)

        // check if it's NO_RESULTS a 404 or empty body
        if (!jsonApiObject) {
            return NO_OPEN_RESTAURANTS
        }

        if (!jsonApiObject['data']) {
            return NO_OPEN_RESTAURANTS
        }

        //@todo: handle multiple results
        def restaurantList = []
        jsonApiObject['data'].each {
            restaurantList += it['attributes']['name']
        }

        "The following restaurants are open: " + joinList(restaurantList)
    }

    String getNearbyRestaurants() {
        "hackathon 2017"
    }
}
