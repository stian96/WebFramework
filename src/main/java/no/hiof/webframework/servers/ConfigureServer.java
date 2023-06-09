package no.hiof.webframework.servers;

import no.hiof.webframework.application.logging.Logger;
import no.hiof.webframework.exceptions.EndpointException;
import no.hiof.webframework.exceptions.PortNumberException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;

/**
 * This class provides method to configure a server in a structured way using a Builder pattern.
 * It allows setting up a server with specific port number, server endpoint, adding controllers
 * and static resources.
 * <p>
 * An instance of this class can only be created through the inner Builder class, to ensure that
 * the server is correctly configured before it is used.
 * <p>
 * The methods in the inner class is chainable, which allows for a fluent interface style where
 * method calls can be chained together.
 * <p>
 * Example usage
 * <pre>
 * ConfigureServer server = new ConfigureServer.Builder()
 *     .setPortNumber(8080)
 *     .setServerEndpoint("/api")
 *     .addController(new MyController(), "/endpoint")
 *     .addStaticResources("index.html", "/path/to/resources")
 *     .build();
 * server.startServer();
 * </pre>
 *
 * @author Stian Rusvik
 */
public class ConfigureServer {
    private ServletContextHandler contextHandler = null;
    private ResourceHandler resourceHandler = null;
    private HandlerList handlerList = null;
    private Server server = null;

    // Constructor made private to prevent direct instantiation.
    private ConfigureServer() {}

    /**
     * Inner Builder class used to set various configurations for the server.
     */
    public static class Builder {
        private int portNumber = 0;
        private String serverEndpoint;
        private HttpServlet controller;
        private String controllerEndpoint;
        private String staticResourceFilename;
        private String staticResourceFolder;

        /**
         * Method used to set the port number that the server will listen on.
         * @param portNumber An arbitrary port number.
         * @return The Builder instance, for chainable method calls.
         */
        public Builder setPortNumber(int portNumber) {
            this.portNumber = portNumber;
            return this;
        }

        /**
         * Method used to set the endpoint of the server.
         * @param serverEndpoint An arbitrary endpoint.
         * @return The Builder instance, for chainable method calls.
         */
        public Builder setServerEndpoint(String serverEndpoint) {
            this.serverEndpoint = serverEndpoint;
            return this;
        }

        /**
         * Method used to add a pre-made controller to the server, and
         * define the endpoint for that controller.
         * @param controller The controller made by the developer.
         * @param controllerEndpoint The endpoint of the controller.
         * @return The Builder instance, for chainable method calls.
         */
        public Builder addController(HttpServlet controller, String controllerEndpoint) {
            this.controller = controller;
            this.controllerEndpoint = controllerEndpoint;
            return this;
        }

        /**
         * Method used to add static resources by defining the filename of the
         * static resource to be used, and the absolute path to that file.
         * @param filename Filename of the static resource to be used.
         * @param absPathToFolder Absolute path to the folder of the static resource.
         * @return The Builder instance, for chainable method calls.
         */
        public Builder addStaticResources(String filename, String absPathToFolder) {
            this.staticResourceFilename = filename;
            this.staticResourceFolder = absPathToFolder;
            return this;
        }

        /**
         * Build method used to instantiate the actual ConfigureServer object,
         * with arguments form the chained method calls.
         * @return The ConfigureServer object.
         */
        public ConfigureServer build() {
            ConfigureServer configureServer = new ConfigureServer();
            configureServer.setPortNumber(this.portNumber);
            configureServer.setServerEndpoint(this.serverEndpoint);
            configureServer.addControllerToServer(this.controller, this.controllerEndpoint);
            configureServer.addStaticResources(this.staticResourceFilename, this.staticResourceFolder);

            return configureServer;
        }
    }

    private void setPortNumber(int number)
    {
        Logger.turnLoggerOFF();
        try
        {
            if (number == 0)
                throw new Exception();
            else
            {
                System.out.println("Listening on port: http://localhost:" + number);
                server = new Server(number);
            }

        }
        catch (Exception e)
        {
            System.err.println("Exception: Set port number before calling build method.");
            stop();
        }
    }

    private void setServerEndpoint(String endpoint)
    {
        try
        {
            contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            contextHandler.setContextPath(endpoint);
        }
        catch (IllegalArgumentException i)
        {
            System.err.println("EndpointException: Set server endpoint before calling build method.");
            stop();
        }
    }

    private void addControllerToServer(HttpServlet controller, String controllerEndpoint)
    {
        try
        {
            if (contextHandler == null)
                throw new EndpointException();
            else
                contextHandler.addServlet(new ServletHolder(controller), controllerEndpoint);
        }
        catch (EndpointException e)
        {
            System.err.println("EndpointException: " + e.getMessage());
            stop();
        }
    }

    private void addStaticResources(String filename, String absPathToFolder)
    {
        resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[] {filename});
        resourceHandler.setResourceBase(absPathToFolder);
    }

    private void activateHandler()
    {
        handlerList = new HandlerList();
        handlerList.setHandlers(new Handler[] {resourceHandler, contextHandler});
    }

    /**
     * Method used to start the actual server.
     */
    public void startServer() {
        try
        {
            if (this.server == null)
            {
                throw new PortNumberException();
            }
            else if (contextHandler == null)
            {
                throw new EndpointException();
            }
            else
            {
                activateHandler();
                server.setHandler(handlerList);
                server.join();
                server.start();
            }
        }
        catch (InterruptedException i)
        {
            System.err.println("InterruptedException: " + i.getMessage());
            stop();
        }
        catch (PortNumberException p)
        {
            System.err.println("PortNumberException: " + p.getMessage());
            stop();
        }
        catch (EndpointException end)
        {
            System.err.println("EndpointException: " + end.getMessage());
            stop();
        }
        catch (Exception e)
        {
            System.err.println("Exception: Add 'static resources' before calling start server method.");
            stop();
        }
    }

    private void stop() {
        System.exit(1);
    }
}
