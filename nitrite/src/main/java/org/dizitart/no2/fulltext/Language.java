/*
 *
 * Copyright 2017 Nitrite author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.dizitart.no2.fulltext;

import java.util.Set;

/**
 * Represents a set of stop words for a specific language.
 *
 * @author Anindya Chatterjee
 * @since 2.1.0
 */
public interface Language {

    /**
     * A set of stop words for the language.
     *
     * @return the set of stop words.
     */
    Set<String> stopWords();
}