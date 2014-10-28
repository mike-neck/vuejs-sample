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

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class VisitorTest {

    private final ClassLoader loader = getClass().getClassLoader();

    @Test
    public void testPath() {
        URL resource = loader.getResource("web/index.html");
        assertThat(resource, is(notNullValue()));
    }

    @Test
    public void uri() {
        URI uri = Optional.ofNullable(loader.getResource("web/index.html"))
                .map(u -> {
                    try {
                        return u.toURI();
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(RuntimeException::new);
        assertThat(uri, is(notNullValue()));
    }

    @Test
    public void toPath() {
        Path path = Optional.ofNullable(loader.getResource("web/index.html"))
                .map(u -> {
                    try {
                        return u.toURI();
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(Paths::get)
                .orElseThrow(RuntimeException::new);
        assertThat(path.toFile().getName().endsWith("index.html"), is(true));
    }

    @Test
    public void parent() {
        Path parent = Optional.ofNullable(loader.getResource("web/index.html"))
                .map(u -> {
                    try {
                        return u.toURI();
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(Paths::get)
                .map(Path::getParent)
                .orElseThrow(RuntimeException::new);
        assertThat(Files.exists(parent), is(true));
    }

    @Test
    public void relative() {
        Optional<Path> optional = Optional.ofNullable(loader.getResource("web/index.html"))
                .map(u -> {
                    try {
                        return u.toURI();
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(Paths::get);
        Path index = optional.orElseThrow(RuntimeException::new);
        Path parent = optional.map(Path::getParent)
                .orElseThrow(RuntimeException::new);
        Path relative = parent.relativize(index);
        assertThat(relative, is(Paths.get("index.html")));
    }
}
