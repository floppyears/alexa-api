package edu.oregonstate.mist.alexa.core

class AlexaResponse {
    String version = "1.0"
    Response response
}

class Response {
    OutputSpeech outputSpeech
}

class OutputSpeech {
    String type
    String text
    String ssml
}
