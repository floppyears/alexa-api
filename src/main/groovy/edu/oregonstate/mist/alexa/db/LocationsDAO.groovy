package edu.oregonstate.mist.alexa.db

import groovy.transform.InheritConstructors

@InheritConstructors
class LocationsDAO extends ApiDAO {
    String getOpenRestaurants() {
        "hackathon 2017"
    }

    String getNearbyRestaurants() {
        "hackathon 2017"
    }
}
