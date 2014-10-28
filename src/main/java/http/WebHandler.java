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

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class WebHandler implements HttpHandler {

    private final Logger LOG = Logger.getLogger("WebHandler");

    private final Map<String, Path> map = new ConcurrentHashMap<>();

    public WebHandler() {
        final ClassLoader loader = getClass().getClassLoader();
        Optional.ofNullable(loader.getResource("web/index.html"))
                .map(u -> {
                    try {
                        return u.toURI();
                    } catch (URISyntaxException e) {
                        throw new URISyntaxRuntimeException(e);
                    }})
                .map(u -> {
                    Path path = Paths.get(u);
                    map.put("/", path);
                    return path.getParent();})
                .map(p -> {
                    try {
                        return Files.walkFileTree(p, new Visitor(p));
                    } catch (IOException e) {
                        throw new URISyntaxRuntimeException(e);
                    }})
                .orElseThrow(() -> new URISyntaxRuntimeException("initialization failed"));

    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        URI uri = ex.getRequestURI();
        LOG.info("uri [" + uri + "]");
        LOG.info("remote address [" + ex.getRemoteAddress() + "]");
        ex.getRequestHeaders().forEach((h, v) -> LOG.info("header - " + h + ":[" + v + "]"));
        LOG.info("method [" + ex.getRequestMethod() + "]");
        LOG.info("request body ---");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ex.getRequestBody()))) {
            reader.lines()
                    .forEach(LOG::info);
        }
        String file = uri.toASCIIString();
        if (!map.containsKey(file)) {
            final String response = "404 ノットファウンド";
            byte[] bytes = response.getBytes("UTF-8");
            ex.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
            ex.sendResponseHeaders(200, bytes.length);
            ex.getResponseBody().write(bytes);
        } else {
            Path path = map.get(file);
            byte[] bytes = Files.readAllBytes(path);
            ex.getResponseHeaders().add("Content-Type", ContentType.getContentType(file));
            ex.sendResponseHeaders(200, bytes.length);
            ex.getResponseBody().write(bytes);
        }
        ex.getResponseBody().flush();
        ex.getResponseBody().close();
    }

    private class Visitor extends SimpleFileVisitor<Path> {

        private final Path parent;

        private Visitor(Path parent) {
            this.parent = parent;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Path relative = parent.relativize(file);
            Optional.of(relative.toString())
                    .filter(p -> !p.contains("/src/"))
                    .filter(p -> !p.endsWith("bower.json"))
                    .filter(p -> !p.endsWith("LICENSE"))
                    .ifPresent(p -> map.put("/" + p, file));
            return FileVisitResult.CONTINUE;
        }
    }

    enum ContentType {
        Html("html", "text/html"),
        ICO("ico", "image/x-image"),
        JS("js", "application/javascript"),
        CSS("css", "text/css");

        static final Map<String, String> map = new HashMap<>();

        private final String extension;
        private final String contentType;

        ContentType(String extension, String contentType) {
            this.extension = extension;
            this.contentType = contentType;
        }

        static {
            Stream.of(values()).forEach(c -> map.put(c.extension, c.contentType));
        }

        static String getContentType(String fileName) {
            if (fileName.equals("/")) {
                return "text/html; charset=UTF-8";
            }
            return map.keySet().stream()
                    .filter(fileName::endsWith)
                    .map(s -> map.get(s) + "; charset=UTF-8")
                    .findAny()
                    .orElse("text/plain; charset=UTF-8");
        }
    }
}
