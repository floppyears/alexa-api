package edu.oregonstate.mist.alexa

import edu.oregonstate.mist.alexa.db.DirectoryDAO
import edu.oregonstate.mist.alexa.db.LocationsDAO
import edu.oregonstate.mist.alexa.db.TermsDAO
import edu.oregonstate.mist.api.Application
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.setup.Environment
import org.apache.http.client.HttpClient

/**
 * Main application class.
 */
class AlexaApplication extends Application<AlexaConfiguration> {
    /**
     * Parses command-line arguments and runs the application.
     *
     * @param configuration
     * @param environment
     */
    @Override
    public void run(AlexaConfiguration configuration, Environment environment) {
        this.setup(configuration, environment)

        HttpClient httpClient = new HttpClientBuilder(environment)
                .using(configuration.getHttpClientConfiguration())
                .build("backend-http-client")

        TermsDAO termsDAO = new TermsDAO(httpClient, configuration.alexaConfiguration)
        DirectoryDAO directoryDAO = new DirectoryDAO(httpClient, configuration.alexaConfiguration)
        LocationsDAO locationsDAO = new LocationsDAO(httpClient, configuration.alexaConfiguration)

        environment.jersey().register(new AlexaResource(termsDAO, directoryDAO, locationsDAO))
    }

    /**
     * Instantiates the application class with command-line arguments.
     *
     * @param arguments
     * @throws Exception
     */
    public static void main(String[] arguments) throws Exception {
        new AlexaApplication().run(arguments)
    }
}
