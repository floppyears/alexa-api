package edu.oregonstate.mist.alexa.db

import groovy.transform.InheritConstructors

@InheritConstructors
class TermsDAO extends ApiDAO {
    public static final String NO_RESULTS = "Sorry, I couldn't find that"

    String getOpenTerms () {
        def jsonApiObject = requests.get(alexaConfiguration['termsUrl'].toString())

        if (!jsonApiObject) {
            return NO_RESULTS
        }

        if (!jsonApiObject['data']) {
            return NO_RESULTS
        }

        def retString = 'The open terms are '
        def terms = jsonApiObject['data']
        terms.each {
            retString += it['attributes']['description'] + ", "
        }

        //Strip last comma and replace with period.
        retString.substring(0,retString.size() - 2) + "."
    }
    void ping() {
        println "pong"
        println requests.get(alexaConfiguration['termsUrl'].toString())
    }
}
