/*
 * Copyright 2020 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mikeneck.graalvm.config.task;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

public interface FileInput {

    Reader newReader(Charset charset) throws IOException;

    @NotNull
    static List<FileInput> from(@NotNull String resourceGroupName, @NotNull Iterable<File> files) {
        LoggerFactory.getLogger(FileInput.class).info("input files: {}", files);
        return StreamSupport
                .stream(files.spliterator(), false)
                .map(file -> FileInput.from(resourceGroupName, file))
                .collect(Collectors.toList());
    }

    @NotNull
    static FileInput from(@NotNull String resourceGroupName, @NotNull File file) {
        return from(resourceGroupName, file.toPath());
    }

    @NotNull
    static FileInput from(@NotNull String resourceGroupName, @NotNull Path file) {
        return new FileInputImpl(resourceGroupName, file);
    }
}
