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

package org.gradle.internal.schema;

import org.gradle.internal.properties.annotations.PropertyMetadata;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class AbstractUnresolvedPropertySchema extends AbstractPropertySchema {
    private final Supplier<Object> valueResolver;

    protected AbstractUnresolvedPropertySchema(String qualifiedName, PropertyMetadata metadata, boolean optional, Supplier<Object> valueResolver) {
        super(qualifiedName, metadata, optional);
        this.valueResolver = valueResolver;
    }

    @Nullable
    @Override
    public Object getValue() {
        return valueResolver.get();
    }
}
