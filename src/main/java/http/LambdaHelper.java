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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class LambdaHelper {

    private LambdaHelper() {}

    public static Runnable run(ExRunnable runnable) {
        return () -> {try {
            runnable.run();
        } catch (Exception ignore) {
        }};
    }

    public static Runnable run(ExRunnable runnable,
                               Function<Exception, RuntimeException> handler) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw handler.apply(e);
            }
        };
    }

    @FunctionalInterface
    public interface ExRunnable {
        public void run() throws Exception;
        default public ExRunnable andThen(ExRunnable next) throws Exception {
            Objects.requireNonNull(next);
            return () -> {
                run();
                next.run();
            };
        }
    }

    public static <T, R> Function<T, Optional<R>> fun(ExFunction<T, R> function) {
        return (T t) -> {
            try {
                return Optional.ofNullable(function.apply(t));
            } catch (Exception ignore) {
                return Optional.empty();
            }
        };
    }

    public static <T, R> Function<T, Optional<R>> fun(
            ExFunction<? super T, R> function,
            Function<Exception, RuntimeException> handler) {
        return (T t) -> {
            try {
                return Optional.ofNullable(function.apply(t));
            } catch (Exception e) {
                throw handler.apply(e);
            }
        };
    }

    @FunctionalInterface
    public interface ExFunction<T, R> {
        public R apply(T source) throws Exception;
        default public <Q> ExFunction<T, Q> andThen(ExFunction<R, Q> next) {
            Objects.requireNonNull(next);
            return (T t) -> next.apply(apply(t));
        }
    }
}
