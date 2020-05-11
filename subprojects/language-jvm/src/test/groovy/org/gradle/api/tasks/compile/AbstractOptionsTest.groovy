/*
 * Copyright 2009 the original author or authors.
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
package org.gradle.api.tasks.compile


import org.gradle.util.TestUtil
import spock.lang.Ignore
import spock.lang.Specification

public class AbstractOptionsTest extends Specification {
    def "options map contains all properties with non-null values"() {
        expect:
        options.optionMap() == map

        where:
        options                                                                 | map
        new TestOptions()                                                       | [stringProp: "initial value"]
        new TestOptions(intProp: 42, stringProp: "new value", objectProp: [21]) | [intProp: 42, stringProp: "new value", objectProp: [21]]
        new TestOptions(stringProp: null)                                       | [:]
    }

    def "property names can be mapped"() {
        def options = new NameMappingOptions(intProp: 42, stringProp: "new value", objectProp: [21])

        expect:
        options.optionMap() == [intProp2: 42, stringProp: "new value", objectProp2: [21]]
    }

    def "property values can be mapped"() {
        def options = new ValueMappingOptions(intProp: 42, stringProp: "new value", objectProp: [21])

        expect:
        options.optionMap() == [intProp: 42, stringProp: "new valuenew value", objectProp: [21]]
    }

    def "option class can extend another option class"() {
        def options = new DeeplyInheritedOptions(intProp: 42, stringProp: "new value", objectProp: [21], deepProp: "deep")

        expect:
        options.optionMap() == [intProp: 42, stringProp: "new value", objectProp: [21], deepProp: "deep"]
    }

    @Ignore
    def "skips properties declared in decorated class generated by instantiator"() {
        def options = TestUtil.instantiatorFactory().decorateLenient().newInstance(TestOptions)
        options.intProp = 42
        options.stringProp = "new value"
        options.objectProp = [21]

        expect:
        options.optionMap() == [intProp: 42, stringProp: "new value", objectProp: [21]]
    }

    static class TestOptions extends AbstractOptions {
        Integer intProp
        String stringProp = "initial value"
        Object objectProp
    }

    static class NameMappingOptions extends AbstractOptions {
        Integer intProp
        String stringProp = "initial value"
        Object objectProp

        @Override
        protected String getAntPropertyName(String fieldName) {
            if (fieldName == "intProp") {
                return "intProp2"
            }
            if (fieldName == "objectProp") {
                return "objectProp2"
            }
            return fieldName
        }
    }

    static class ValueMappingOptions extends AbstractOptions {
        Integer intProp
        String stringProp = "initial value"
        Object objectProp

        @Override
        protected Object getAntPropertyValue(String fieldName, Object value) {
            if (fieldName == "stringProp") {
                return stringProp * 2
            }
            return value
        }
    }

    static class DeeplyInheritedOptions extends TestOptions {
      String deepProp
    }

    @SuppressWarnings("ClassName")
    static class TestOptions_Decorated extends TestOptions {
        String decoratedProp
    }
}

