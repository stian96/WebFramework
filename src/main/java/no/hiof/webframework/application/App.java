package no.hiof.webframework.application;
import no.hiof.webframework.application.frontend.HtmlPages;
import no.hiof.webframework.application.logging.Logger;
import no.hiof.webframework.application.tools.HtmlParser;
import no.hiof.webframework.controllers.Controller;
import no.hiof.webframework.application.routes.Route;
import no.hiof.webframework.exceptions.ChatMethodException;
import no.hiof.webframework.repository.UserDb;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Server;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * The App class is the core class of the application, responsible for managing
 * the web server and handling HTTP requests. It uses the Singleton pattern to
 * ensure that only one instance of the class exists at any given time.
 * <p>
 * The class maintains a collection of routes and associated HTTP methods, and
 * provides methods for adding custom or pre-built HTML pages and responses to
 * specific routes. It also allows a custom controller to be added for handling
 * dynamic content, and pre-defined Chatroom applications.
 * <p>
 * Example usage of a simple scenario:
 * <pre>
 * App app = App.create();
 * app.addRoute("login", HttpMethod.GET);
 * HtmlFactory factory = new HtmlFactory();
 * app.addHtmlPage(factory.createLoginPage(), "Login title");
 * app.run();
 * </pre>
 *
 * @author Stian Rusvik
 */
public class App {

    private static App instance = null;
    private final Map<String, Route> routeMap = new LinkedHashMap<>();
    private final Map<String, HtmlPages> htmlPageMap = new LinkedHashMap<>();
    private String customPage, applicationTitle;

    private Chatroom chatroom;

    private Controller controller;
    private UserDb dbUser;

    private String response;
    /**
     * The 'pageCounter' is used to control how many
     * pages that the server needs to handle.
     */
    protected int pageCounter;

    protected App() {}

    /**
     * Method used to create only one instance of the App-class.
     * @return The application object.
     */
    public static App create() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    /**
     * Adds a new route to the application.
     * @param endpoint URI value of the URL.
     * @param httpMethod Method to be used (e.g. GET, POST, PUT etc.)
     * @throws NullPointerException If one of the parameters is null.
     */
    public void addRoute(String endpoint, HttpMethod httpMethod) throws NullPointerException {
        if (endpoint != null && httpMethod != null)
        {
            Route route = new Route(endpoint, httpMethod);
            routeMap.put(endpoint, route);
        }
        else
            throw new NullPointerException("Parameters cannot be null in addRoute method!");
    }

    /**
     * Adds a ready-made html page to the specified route,
     * where all the html and css is pre-built.
     * @param htmlPage InputStream of the html page. You
     * can get this from the HtmlFactory.
     * @param title Sets the title of the page.
     */
    public void addHtmlPage(InputStream htmlPage, String title)  {
        HtmlPages page = new HtmlPages();
        page.setHtmlPage(htmlPage);
        page.setTitle(title);

        String pageTitle = HtmlParser.getTitleFromHtmlPage(htmlPage);
        htmlPageMap.put(pageTitle, page);
    }

    /**
     * Adds a custom html page to the application.
     * @param page The html page as a String.
     * Can get this from the build() method in the
     * HtmlPageBuilder class.
     */
    public void addCustomHtmlPage(String page) {
        String title = HtmlParser.readCustomHtmlPage(page);
        setCustomPage(page);
        htmlPageMap.put(title, null);
    }

    /**
     * Adds a custom html page that is connected to
     * a user database.
     * @param page The html page as a String.
     * @param user User database from 'UserDb' class.
     */
    public void addCustomHtmlPage(String page, UserDb user) {
        String title = HtmlParser.readCustomHtmlPage(page);
        setCustomPage(page);
        dbUser = user;
        htmlPageMap.put(title, null);
    }

    /**
     * Adds a response as a String to a page with no html content.
     * @param response The response to be delivered on the page.
     */
    public void addResponseToPage(String response) {
        setResponse(response);
        htmlPageMap.put(response, null);
    }

    /**
     * Adds a controller to the server.
     * @param controller The controller to be passed.
     */
    public void addController(Controller controller) {
        if (controller != null)
            this.controller = controller;
        else
            throw new NullPointerException("Controller cant be null!");
    }

    /**
     * Adds a chatroom to the application instance. If the chat method
     * is not set in the chatroom object, a ChatMethod exception is thrown.
     * @param room The chatroom object to be added to the application.
     * @throws ChatMethodException If the chat method is not set in the chatroom.
     */
    public void addChatRoom(Chatroom room) throws ChatMethodException {
        SpringServlet servlet = SpringServlet.getServlet();
        if (room.getMethod() != null) {
            servlet.setChatMethod(room.getMethod());
            this.chatroom = room;
            checkAndSetChatroomFields(servlet, room);
        }
        else {
            throw new ChatMethodException("Set chat method before passing Chatroom to App class.");
        }
    }

    private void checkAndSetChatroomFields(SpringServlet servlet, Chatroom room) {

        if (room.getTimeStamp()) {
            servlet.setTimeStamp(room.getTimeStamp());
        }
        if (room.getTitle() != null) {
            servlet.setTitle(room.getTitle());
        }
        if (room.getDeleteMessage() != null) {
            servlet.setDeleteButton(room.getDeleteMessage());
        }
    }

    /**
     * Starts and run the application by initializing a server and a chatroom
     * (if one is provided) on a separate thread. The method also turns off the logger
     * before initializing the server.
     */
    public void run() {
        Logger.turnLoggerOFF();

        ServerHandler server = constructorHandler();
        if (chatroom != null) {
            chatroom = Chatroom.create();
            new Thread(chatroom::startChatRoom).start();
            server.initializeHandler(new Server(8080), this);
        }
        else {
            server.initializeHandler(new Server(8080), this);
        }
    }

    private ServerHandler constructorHandler() {
        if (applicationTitle != null && controller == null)
            return new ServerHandler(applicationTitle);

        else if (controller != null && applicationTitle == null)
            return new ServerHandler(controller);

        else if (controller == null)
            return new ServerHandler();

        else
            return new ServerHandler(applicationTitle, controller);
    }

    /**
     * Increments the pageCounter by 1 when called.
     */
    protected void incrementPageCounter() { pageCounter++; }

    /**
     * Sets the custom page variable in the App class.
     * @param content The content to be set (a string value).
     */
    protected void setCustomPage(String content) {
        customPage = content;
    }

    /**
     * Getter used to retrieve the route map collection.
     * @return A 'LinkedHashMap' containing Strings and Route-objects.
     */
    protected Map<String, Route> getRouteMap() {
        return routeMap;
    }

    /**
     * Getter used to retrieve the html-page map collection.
     * @return The 'LinkedHashMap' containing Strings and HtmlPages.
     */
    public Map<String, HtmlPages> getHtmlPageMap() {
        return htmlPageMap;
    }

    private void setResponse(String content) {
        this.response = content;
    }

    /**
     * Getter used to retrieve a response for pages without html content.
     * @return The response as a String.
     */
    protected String getResponse() {
        return response;
    }

    /**
     * Getter used to retrieve the content of a 'custom' html page.
     * @return The custom page as a String.
     */
    protected String getCustomPage() {
        return customPage;
    }

    /**
     * Setter used to set the title of the application.
     * @param title A String value representing the title.
     */
    public void setApplicationTitle(String title) {
        this.applicationTitle = title;
    }

    /**
     * Getter used to retrieve the user-database.
     * @return A UserDb object.
     */
    protected UserDb getDbUser() {
        return dbUser;
    }

    /**
     * Returns the instance of the App object.
     * @return The instance to be returned.
     */
    protected App getInstance() {
        return instance;
    }

    protected Controller getController() { return controller; }

    protected int getPageCounter() { return pageCounter; }
}

