package no.hiof.webframework.application.frontend;

import no.hiof.webframework.exceptions.HttpMethodException;
import no.hiof.webframework.interfaces.builders.IHtmlBuilder;
import org.eclipse.jetty.http.HttpMethod;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the IHtmlBuilder interface
 * and can be used to build a custom HTML page.
 * <p>
 * @author Stian Rusvik.
 */
public class HtmlPageBuilder implements IHtmlBuilder {
    private final StringBuilder content  = new StringBuilder();
    private final List<HttpMethod> httpMethods = new ArrayList<>();

    /**
     * Constructor that sets up the default code for the HTML page
     * and fills the HttpMethods list.
     */
    public HtmlPageBuilder() {
        defaultCode();
        fillHttpList();
    }

    private void fillHttpList() {
        httpMethods.add(HttpMethod.GET);
        httpMethods.add(HttpMethod.POST);
        httpMethods.add(HttpMethod.PUT);
    }

    private void defaultCode() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/static/default.html");
            assert inputStream != null;
            String fileContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            content.append(fileContent);
        }
        catch (IOException ioException) {
            System.out.println(ioException.getMessage());
        }
    }

    /**
     * Adds a header to the top of the page.
     * @param header The header as a string.
     */
    @Override
    public void addHeader(String header) {
        String headerContent = "<h1>" + header + "</h1>";
        content.replace(
                content.indexOf("<!--HEADER_PLACEHOLDER-->"),
                content.indexOf("<!--HEADER_PLACEHOLDER-->") + "<!--HEADER_PLACEHOLDER-->".length(),
                headerContent);

    }

    /**
     * Adds a navigation menubar to the page.
     * @param navElements The elements to be displayed in the menubar.
     */
    @Override
    public void addNavElements(String... navElements) {
        StringBuilder navContent = new StringBuilder();
        navContent.append("<nav>");
        navContent.append("<ul>");
        for (String element : navElements) {
            navContent.append("<li>")
                    .append("<a href='#'>")
                    .append(element).append("</a>")
                    .append("</li>");
        }
        navContent.append("</ul>");
        navContent.append("</nav>");
        content.replace(
                content.indexOf("<!--NAV_PLACEHOLDER-->"),
                content.indexOf("<!--NAV_PLACEHOLDER-->") + "<!--NAV_PLACEHOLDER-->".length(),
                navContent.toString());
    }

    /**
     * Adds a main-section to the page that contains a header and a paragraph.
     * @param header The header to be displayed in the main-section.
     * @param paragraph The paragraph in the main-section.
     */
    @Override
    public void addMainSection(String header, String paragraph) {
        String mainContent = "<h1>" + header + "</h1>" + "<p>" + paragraph + "</p>";
        content.replace(
                content.indexOf("<!--MAIN_PLACEHOLDER-->"),
                content.indexOf("<!--MAIN_PLACEHOLDER-->") + "<!--MAIN_PLACEHOLDER-->".length(),
                mainContent);
    }

    /**
     * Adds a html form in the main-section of the html-page.
     *
     * @param method     Type of Http-method (e.g. GET, POST, PUT).
     * @param formFields Fields to be displayed in the form.
     */
    @Override
    public void addForm(HttpMethod method, String... formFields) {
        StringBuilder form = new StringBuilder();

        try {
            if (!httpMethods.contains(method))
                throw new HttpMethodException("Error: http-method must be GET or POST.");

            switch (method) {
                case GET: form.append("<form method='GET'>");
                case POST: form.append("<form method='POST'>");
                case PUT: form.append("<form method='PUT'>");
            }
            for (String field : formFields) {
                form.append("<label for='").append(field).append("'>").append(field).append("</label>");
                form.append("<input type='")
                        .append(field).append("' id='")
                        .append(field).append("' name='")
                        .append(field).append("' required><br>");
            }
            form.append("<input type='submit' value='submit'>").append("</form>");
            content.replace(
                    content.indexOf("<!--FORM_PLACEHOLDER-->"),
                    content.indexOf("<!--FORM_PLACEHOLDER-->") + "<!--FORM_PLACEHOLDER-->".length(),
                    form.toString());
        }
        catch (HttpMethodException exception) {
            System.out.println(exception.getMessage());
        }
    }

    // TODO: Need to fix this method.
    // The image wont show in the html page (think its because of some server errors).

    /**
     * This method is not working at the moment.
     */
    @Override
    public void addImage(String imageUrl, String altText) {
        String image = "<img src='" + imageUrl + "' alt='" + altText + "'>";
        content.replace(
                content.indexOf("<!--IMAGE_PLACEHOLDER-->"),
                content.indexOf("<!--IMAGE_PLACEHOLDER-->") + "<!--IMAGE_PLACEHOLDER-->".length(),
                image
        );
    }

    @Override
    public void addLink(String url, String text) {
        // Will be implemented later

    }

    @Override
    public void addTable(List<List<String>> rows) {
        // Will be implemented later
    }

    /**
     * Adds a footer-section in the bottom of the page that contains contact information.
     * @param address Address of the user/company.
     * @param phoneNumber Phone number of the user/company.
     * @param email Email to the user/company.
     */
    @Override
    public void addFooterSection(String address, String phoneNumber, String email) {
        String contactInfo = "<footer>" +
                "<p>Address: " + address + "</p>" +
                "<p>Phone: " + phoneNumber + "</p>" +
                "<p>Email: " + email + "</p>" +
                "</footer>";

        content.replace(
                content.indexOf("<!--FOOTER_PLACEHOLDER-->"),
                content.indexOf("<!--FOOTER_PLACEHOLDER-->") + "<!--FOOTER_PLACEHOLDER-->".length(),
                contactInfo);
    }

    /**
     * When the page is finished, this method can be used to build the page.
     * @return The page as a String, which then should be passed as an argument
     * to the app.addCustomHtmlPage() method.
     */
    @Override
    public String build() {
        return content.toString();
    }
}
