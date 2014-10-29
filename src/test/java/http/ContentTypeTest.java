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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ContentTypeTest {

    @Test
    public void html() {
        String contentType = WebHandler.ContentType.getContentType("index.html");
        assertThat(contentType, is("text/html; charset=UTF-8"));
    }

    @Test
    public void js() {
        String contentType = WebHandler.ContentType.getContentType("bower_components/vue/dist/vue.js");
        assertThat(contentType, is("application/javascript; charset=UTF-8"));
    }

    @Test
    public void css() {
        String contentType = WebHandler.ContentType.getContentType("css/bootstrap.min.css");
        assertThat(contentType, is("text/css; charset=UTF-8"));
    }

    @Test
    public void ttf() {
        String contentType = WebHandler.ContentType.getContentType("fonts/glyphicons-halfilings-regular.ttf");
        assertThat(contentType, is("application/x-font-ttf"));
    }
}
