/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package test.com.sun.javafx.application;

import com.sun.javafx.PlatformUtil;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import junit.framework.AssertionFailedError;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static test.util.Util.TIMEOUT;

/**
 * Unit tests for Platform runLater.
 */
public class HostServicesTest {

    // Used to launch the application before running any test
    private static final CountDownLatch launchLatch = new CountDownLatch(1);

    // Singleton Application instance
    private static MyApp myApp;

    // Application class. An instance is created and initialized before running
    // the first test, and it lives through the execution of all tests.
    public static class MyApp extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            HostServicesTest.myApp = this;
            Platform.setImplicitExit(false);
            launchLatch.countDown();
        }
    }

    @BeforeClass
    public static void setupOnce() {
        // Start the Application
        new Thread(() -> Application.launch(MyApp.class, (String[]) null)).start();

        try {
            if (!launchLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
                throw new AssertionFailedError("Timeout waiting for Application to launch");
            }
        } catch (InterruptedException ex) {
            AssertionFailedError err = new AssertionFailedError("Unexpected exception");
            err.initCause(ex);
            throw err;
        }
    }

    @AfterClass
    public static void teardownOnce() {
        Platform.exit();
    }

    @Test
    public void testCodeBase() {
        final HostServices hs = myApp.getHostServices();
        assertNotNull(hs);
        String cbStr = hs.getCodeBase();
        assertNotNull(cbStr);
        assertTrue(cbStr.isEmpty());
    }

    @Test
    public void testDocumentBase() {
        final HostServices hs = myApp.getHostServices();
        assertNotNull(hs);
        String dbStr = hs.getDocumentBase();
        assertNotNull(dbStr);
        String userDir = System.getProperty("user.dir");
        userDir = userDir.replace("\\", "/");
        System.err.println("userDir = " + userDir);
        if (!userDir.startsWith("/")) {
            userDir = "/" + userDir;
        }
        String testDocBase = "file:" + userDir + "/";
        assertTrue(dbStr.equals(testDocBase));
    }

    @Test
    public void testWebContext() {
        final HostServices hs = myApp.getHostServices();
        assertNotNull(hs);
        assertNull(hs.getWebContext());
    }
}
