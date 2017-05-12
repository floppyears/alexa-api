package edu.oregonstate.mist.alexa

import edu.oregonstate.mist.api.Application
import edu.oregonstate.mist.api.Configuration
import io.dropwizard.setup.Environment

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

        environment.jersey().register(new AlexaResource())
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
