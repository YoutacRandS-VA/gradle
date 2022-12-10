/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.execution.schema;

import org.gradle.internal.fingerprint.DirectorySensitivity;
import org.gradle.internal.fingerprint.FileNormalizer;
import org.gradle.internal.fingerprint.LineEndingSensitivity;
import org.gradle.internal.properties.InputBehavior;
import org.gradle.internal.properties.annotations.PropertyMetadata;

import java.util.function.Supplier;

public class DefaultFileInputPropertySchema extends AbstractFileInputPropertySchema implements FileInputPropertySchema {
    private final InputBehavior behavior;
    private final DirectorySensitivity directorySensitivity;
    private final LineEndingSensitivity lineEndingSensitivity;

    public DefaultFileInputPropertySchema(
        String qualifiedName,
        PropertyMetadata metadata,
        FileNormalizer normalizer,
        InputBehavior behavior,
        DirectorySensitivity directorySensitivity,
        LineEndingSensitivity lineEndingSensitivity,
        Supplier<Object> valueResolver
    ) {
        super(qualifiedName, metadata, normalizer, valueResolver);
        this.behavior = behavior;
        this.directorySensitivity = directorySensitivity;
        this.lineEndingSensitivity = lineEndingSensitivity;
    }

    @Override
    public InputBehavior getBehavior() {
        return behavior;
    }

    @Override
    public DirectorySensitivity getDirectorySensitivity() {
        return directorySensitivity;
    }

    @Override
    public LineEndingSensitivity getLineEndingSensitivity() {
        return lineEndingSensitivity;
    }
}
