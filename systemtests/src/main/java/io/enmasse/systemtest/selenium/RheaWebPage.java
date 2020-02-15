/*
 * Copyright 2018, EnMasse authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.enmasse.systemtest.selenium;


import io.enmasse.systemtest.CustomLogger;
import io.enmasse.systemtest.KeycloakCredentials;
import org.openqa.selenium.By;
import org.slf4j.Logger;

import java.io.File;

public class RheaWebPage {

    private static Logger log = CustomLogger.getLogger();
    private SeleniumProvider selenium;

    public RheaWebPage(SeleniumProvider selenium) {
        this.selenium = selenium;
    }

    public void openRheaWebPage() throws Exception {
        File file = new File("src/main/resources/rhea.html");
        String absolutePath = file.getAbsolutePath();
        log.info("Opening rhea web page");
        selenium.getDriver().get("file://" + absolutePath);
        selenium.getAngularDriver().waitForAngularRequestsToFinish();
        selenium.takeScreenShot();
    }

    public void sendReceiveMessages(String server, String address, int count, KeycloakCredentials credentials) throws Exception {
        openRheaWebPage();
        String command = String.format("connect_to_enmasse(\"wss://%s\", \"%s\", \"%s\", \"%s\", \"%s\")",
                server, address, count, credentials.getUsername(), credentials.getPassword());
        selenium.executeJavaScript(command);
    }

    public boolean checkCountMessage(int count) throws Exception {
        return selenium.getWebElements(() -> selenium.getDriver().findElements(By.tagName("div")), count).size() == count;
    }
}
