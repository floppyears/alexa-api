package edu.oregonstate.mist.alexa.db

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils

class Requests {
    HttpClient httpClient
    String oauth2Url
    String oauth2Params
    private ObjectMapper mapper = new ObjectMapper()

    Requests(HttpClient httpClient, String oauth2Url, String oauth2Params) {
        this.httpClient = httpClient
        this.oauth2Url = oauth2Url
        this.oauth2Params = oauth2Params
    }

    /**
     * Makes an http GET request to the provided url. It grabs an access token and passes it
     * to the url for authentication.
     *
     * @param url
     * @return
     */
    def get(String url) {
        def accessToken = getAccessToken()

        HttpGet request = new HttpGet(url)

        // add auth header
        request.addHeader("Authorization", "Bearer " + accessToken)

        HttpResponse response = httpClient.execute(request)
        HttpEntity entity = response.getEntity()

        def entityString = EntityUtils.toString(entity)
        println entityString

        def data = this.mapper.readValue(entityString,
                new TypeReference<HashMap<String, Object>>() {
                })

        EntityUtils.consume(entity)

        data
    }

    /**
     * Talks to oauth2Url and returns an access token.
     * @return
     */
    private String getAccessToken() {
        CloseableHttpResponse response

        def data = new HashMap<>()

        try {
            HttpPost post = new HttpPost(oauth2Url)
            post.setHeader("Content-Type", "application/x-www-form-urlencoded")
            post.setHeader("Accept", "application/json")

            post.setEntity(new StringEntity(oauth2Params))

            response = httpClient.execute(post)
            HttpEntity entity = response.getEntity()
            def entityString = EntityUtils.toString(entity)

            data = this.mapper.readValue(entityString,
                    new TypeReference<HashMap<String, Object>>() {
                    })

            EntityUtils.consume(entity)
        } finally {
            response?.close()
        }

        //@todo: error handling for empty access token
        data['access_token']
    }
}
