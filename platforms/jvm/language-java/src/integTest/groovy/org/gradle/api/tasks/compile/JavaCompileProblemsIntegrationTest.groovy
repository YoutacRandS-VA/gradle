/*
 * Copyright 2023 the original author or authors.
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

package org.gradle.api.tasks.compile;

import org.gradle.integtests.fixtures.AbstractIntegrationSpec;

class JavaCompileProblemsIntegrationTest extends AbstractIntegrationSpec {

    def setup() {
        buildFile << """
            plugins {
                id 'java'
            }

            tasks.compileJava {
                options.compilerArgs += ["-Xlint:all"]
            }
        """
    }

    def "problem received when a single-file compilation failure happens"() {
        enableProblemsApiCheck()
        def files = [
            writeJavaCausingCompilationError("Foo")
        ]
        // Duplicate the entries, as we have two problems per file
        files.addAll(files)

        when:
        fails("compileJava")

        then:
        collectedProblems.size() == 2
        for (def problem in collectedProblems) {
            assertError(problem, files)
        }
    }

    def "problems received when a multi-file compilation failure happens"() {
        enableProblemsApiCheck()
        def files = [
            writeJavaCausingCompilationError("Foo"),
            writeJavaCausingCompilationError("Bar")
        ]
        // Duplicate the entries, as we have two problems per file
        files.addAll(files)

        when:
        fails("compileJava")

        then:
        collectedProblems.size() == 4
        for (def problem in collectedProblems) {
            assertError(problem, files)
        }
    }

    def "problem received when a single-file warning happens"() {
        enableProblemsApiCheck()
        def files = [
            writeJavaCausingCompilationWarning("Foo")
        ]
        // Duplicate the entries, as we have two problems per file
        files.addAll(files)

        when:
        def result = run("compileJava")

        then:
        collectedProblems.size() == 2
        for (def problem in collectedProblems) {
            assertWarning(problem, files)
        }
    }

    def "problems received when a multi-file warning happens"() {
        enableProblemsApiCheck()
        def files = [
            writeJavaCausingCompilationWarning("Foo"),
            writeJavaCausingCompilationWarning("Bar")
        ]
        // Duplicate the entries, as we have two problems per file
        files.addAll(files)

        when:
        def result = run("compileJava")

        then:
        collectedProblems.size() == 4
        for (def problem in collectedProblems) {
            assertWarning(problem, files)
        }
    }

    def "problems received when a multi-file compilation failure and warning happens"() {
        enableProblemsApiCheck()
        def files = [
            writeJavaCausingCompilationErrorAndWarning("Foo"),
            writeJavaCausingCompilationErrorAndWarning("Bar")
        ]
        // Duplicate the entries, as we have two problems per file
        files.addAll(files)

        when:
        def result = fails("compileJava")

        then:
        collectedProblems.size() == 8
        for (def problem in collectedProblems) {
            if (problem["severity"] == "ERROR") {
                assertError(problem, files)
            } else {
                assertWarning(problem, files)
            }
        }
    }

    def assertError(Map<String, Object> problem, List<String> possibleFiles) {
        assert problem["severity"] == "ERROR"

        def locations = problem["where"] as List<Map<String, Object>>
        assert locations.size() == 2

        def taskLocation = locations.find {
            it["type"] == "task"
        }
        assert taskLocation != null
        assert taskLocation["identityPath"]["path"] == ":compileJava"

        def fileLocation = locations.find {
            it["type"] == "file"
        }
        assert fileLocation != null
        assert possibleFiles.remove(fileLocation["path"])
        assert fileLocation["line"] != null
        assert fileLocation["column"] != null

        return true
    }

    def assertWarning(Map<String, Object> problem, List<String> possibleFiles) {
        assert problem["severity"] == "WARNING"

        def locations = problem["where"] as List<Map<String, Object>>
        assert locations.size() == 2

        def taskLocation = locations.find {
            it["type"] == "task"
        }
        assert taskLocation != null
        assert taskLocation["identityPath"]["path"] == ":compileJava"

        def fileLocation = locations.find {
            it["type"] == "file"
        }
        assert fileLocation != null
        assert possibleFiles.remove(fileLocation["path"])
        assert fileLocation["line"] != null
        assert fileLocation["column"] != null

        return true
    }

    String writeJavaCausingCompilationError(String className) {
        def file = file("src/main/java/${className}.java")
        file << """
            public class ${className} {
                public static void problemOne(String[] args) {
                    // Missing semicolon will trigger an error
                    String s = "Hello, World!"
                }

                public static void problemTwo(String[] args) {
                    // Missing semicolon will trigger an error
                    String s = "Hello, World!"
                }
            }
        """

        return file.absolutePath
    }

    String writeJavaCausingCompilationWarning(String className) {
        def file = file("src/main/java/${className}.java")
        file << """
            public class ${className} {
                public static void warningOne(String[] args) {
                    // Unnecessary cast will trigger a warning
                    String s = (String)"Hello World";
                }

                public static void warningTwo(String[] args) {
                    // Unnecessary cast will trigger a warning
                    String s = (String)"Hello World";
                }
            }
        """

        return file.absolutePath
    }

    String writeJavaCausingCompilationErrorAndWarning(String className) {
        def file = file("src/main/java/${className}.java")
        file << """
            public class ${className} {
                public static void problemOne(String[] args) {
                    // Missing semicolon will trigger an error
                    String s = "Hello, World!"
                }

                public static void problemTwo(String[] args) {
                    // Missing semicolon will trigger an error
                    String s = "Hello, World!"
                }

                public static void warningOne(String[] args) {
                    // Unnecessary cast will trigger a warning
                    String s = (String)"Hello World";
                }

                public static void warningTwo(String[] args) {
                    // Unnecessary cast will trigger a warning
                    String s = (String)"Hello World";
                }
            }
        """

        return file.absolutePath
    }

}
