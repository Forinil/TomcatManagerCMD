package konrad.tools.tomcat;

import com.sun.net.httpserver.BasicAuthenticator;
import konrad.tools.tomcat.utils.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);
    private ConfigUtil configUtil;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public App() {
        configUtil = ConfigUtil.getInstance();
    }

    public void run() {
        logger.info("run");
        String username = configUtil.getProperty("tomcat.user");
        String password = configUtil.getProperty("tomcat.password");
        String hostname = configUtil.getProperty("tomcat.host");
        String port = configUtil.getProperty("tomcat.port");
        String prefix = configUtil.getProperty("tomcat.prefix");
        String commandList = configUtil.getProperty("tomcat.commands");

        Authenticator.setDefault(new BasicAuthenticator(username, password));

        String[] commands = commandList.split(",");

        for(String command: commands) {
            getResponse(hostname, port, prefix, command);
        }
    }

    private void getResponse(String hostname, String port, String prefix, String command) {
        if (logger.isDebugEnabled()) {
            logger.debug("getResponse: {} {} {} {}", hostname, port, prefix, command);
        } else {
            logger.info("getResponse");
        }

        URL url = null;
        try {
            String connectionUrl = String.format("http://%s:%s/%s/%s", hostname, port, prefix, command);
            logger.info("Connecting to: {}", connectionUrl);
            url = new URL(connectionUrl);
        } catch (MalformedURLException e) {
            logger.error("URl error", e);
        }
        try {
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            InputStream response = connection.getInputStream();
            try (Scanner scanner = new Scanner(response)) {
                String responseBody = scanner.useDelimiter("\\A").next();
                System.out.println(responseBody);
                logger.info("Response from server: {}", responseBody);
            }
        } catch (IOException e) {
            logger.error("Connection error", e);
        }
    }

    private class BasicAuthenticator extends Authenticator {
        private Logger logger = LoggerFactory.getLogger(this.getClass());
        private String username;
        private String password;

        public BasicAuthenticator(String username, String password) {
            logger.debug("BasicAuthenticator: {} {}", username, password);
            this.username = username;
            this.password = password;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            logger.info("getPasswordAuthentication");
            return new PasswordAuthentication (username, password.toCharArray());
        }
    }
}
