/*
 * Copyright (c) 2017-2020. Nitrite author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dizitart.no2.common.mapper;

import org.dizitart.no2.NitriteConfig;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.NitriteId;
import org.dizitart.no2.exceptions.ObjectMappingException;

import java.util.*;

import static org.dizitart.no2.common.util.Iterables.listOf;
import static org.dizitart.no2.common.util.ObjectUtils.newInstance;

/**
 * A {@link NitriteMapper} based on {@link Mappable} implementation.
 *
 * @author Anindya Chatterjee.
 * @since 4.0
 */
public class MappableMapper implements NitriteMapper {
    private final Set<Class<?>> valueTypes;
    private final Map<Class<?>, MappableFactory<?>> mappableFactories;

    /**
     * Instantiates a new {@link MappableMapper}.
     *
     * @param valueTypes the value types
     */
    public MappableMapper(Class<?>... valueTypes) {
        this.valueTypes = new HashSet<>();
        this.mappableFactories = new HashMap<>();
        init(listOf(valueTypes));
    }

    /**
     * Converts a document to a target object of type <code>Target</code>.
     *
     * @param <Target> the type parameter
     * @param source   the source
     * @param type     the type
     * @return the target
     */
    @SuppressWarnings("unchecked")
    protected <Target> Target convertFromDocument(Document source, Class<Target> type) {
        if (source == null) {
            return null;
        }

        if (Mappable.class.isAssignableFrom(type)) {
            Target item;
            if (mappableFactories.containsKey(type)) {
                MappableFactory<?> factory = mappableFactories.get(type);
                item = (Target) factory.create();
            } else {
                item = newInstance(type, false);
            }

            if (item == null) return null;
            ((Mappable) item).read(this, source);
            return item;
        }

        throw new ObjectMappingException(type.getName() + " is not a Mappable");
    }

    /**
     * Converts an object of type <code>Source</code> to a document.
     *
     * @param <Source> the type parameter
     * @param source   the source
     * @return the document
     */
    protected <Source> Document convertToDocument(Source source) {
        if (source instanceof Mappable) {
            Mappable mappable = (Mappable) source;
            return mappable.write(this);
        }

        throw new ObjectMappingException("Object of type " + source.getClass().getName() + " is not Mappable");
    }

    /**
     * Adds a value type to ignore during mapping.
     *
     * @param valueType the value type
     */
    protected void addValueType(Class<?> valueType) {
        this.valueTypes.add(valueType);
    }

    /**
     * Register a {@link Mappable} factory to be used when converting a document to an object.
     *
     * @param <T>     the type parameter
     * @param factory the factory
     * @param type    the type
     */
    public <T> void registerMappable(MappableFactory<T> factory, Class<T> type) {
        mappableFactories.put(type, factory);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Source, Target> Target convert(Source source, Class<Target> type) {
        if (source == null) {
            return null;
        }

        if (isValue(source)) {
            return (Target) source;
        } else {
            if (Document.class.isAssignableFrom(type)) {
                if (source instanceof Document) {
                    return (Target) source;
                } else {
                    return (Target) convertToDocument(source);
                }
            } else if (source instanceof Document) {
                return convertFromDocument((Document) source, type);
            }
        }

        throw new ObjectMappingException("Can't convert object of type " + source.getClass() + " to type " + type);
    }

    @Override
    public boolean isValueType(Class<?> type) {
        if (type.isPrimitive() && type != void.class) return true;
        if (valueTypes.contains(type)) return true;
        for (Class<?> valueType : valueTypes) {
            if (valueType.isAssignableFrom(type)) return true;
        }
        return false;
    }

    @Override
    public boolean isValue(Object object) {
        return isValueType(object.getClass());
    }

    @Override
    public void initialize(NitriteConfig nitriteConfig) {

    }

    private void init(List<Class<?>> valueTypes) {
        this.valueTypes.add(Number.class);
        this.valueTypes.add(Boolean.class);
        this.valueTypes.add(Character.class);
        this.valueTypes.add(String.class);
        this.valueTypes.add(byte[].class);
        this.valueTypes.add(Enum.class);
        this.valueTypes.add(NitriteId.class);
        this.valueTypes.add(Date.class);

        if (valueTypes != null && !valueTypes.isEmpty()) {
            this.valueTypes.addAll(valueTypes);
        }
    }
}
