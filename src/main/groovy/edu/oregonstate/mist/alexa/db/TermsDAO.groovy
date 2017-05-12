package edu.oregonstate.mist.alexa.db

import groovy.transform.InheritConstructors

@InheritConstructors
class TermsDAO extends ApiDAO {
    void ping() {
        println "pong"
        println requests.get(alexaConfiguration['termsUrl'].toString())
    }
}
