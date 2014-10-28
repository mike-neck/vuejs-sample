/*
 * Copyright 2014Shinya Mochida
 * <p>
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package http;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class WebServer implements Runnable, AutoCloseable {

    private static final Logger LOG = Logger.getLogger("WebServer");

    private static final ExecutorService EXE = Executors.newFixedThreadPool(3);

    private static final CountDownLatch LATCH = new CountDownLatch(1);

    private HttpServer server;

    public static void main(String[] args) {
        try (WebServer webServer = new WebServer()) {
            EXE.submit(webServer);
            EXE.submit(LambdaHelper.run(() -> {
                LOG.info("to stop server hit enter");
                while (true) {
                    System.in.read();
                    LATCH.countDown();
                    break;
                }
            }));
            LATCH.await();
        } catch (Exception ignore) {
        } finally {
            EXE.shutdown();
        }
    }

    @Override
    public void run() {
        InetSocketAddress address = new InetSocketAddress("localhost", 8540);
        try {
            server = HttpServer.create(address, 8540);
            server.createContext("/", new WebHandler());
            server.start();
        } catch (IOException | URISyntaxRuntimeException e) {
            LATCH.countDown();
        }
    }

    @Override
    public void close() throws Exception {
        server.stop(0);
    }
}
