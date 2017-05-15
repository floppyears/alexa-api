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

        def openTerms = []
        jsonApiObject['data'].each {
            openTerms += it['attributes']['description']
        }

        "The open terms are: " + joinList(openTerms)
    }
}
