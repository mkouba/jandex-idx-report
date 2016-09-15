/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mkouba.jandex;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;
import org.junit.Test;

public class JandexIndexReportTest {

    @Test
    public void testGenerate() throws IOException {
        Indexer indexer = new Indexer();
        addClass(Foo.class, indexer);
        addClass(Bar.class, indexer);
        Index idx = indexer.complete();
        String output = new JandexIndexReport().generate(idx, "foo");
        Files.write(new File("target/output.html").toPath(), output.getBytes(Charset.forName("UTF-8")));
        assertTrue(output.contains("Index: foo"));
        assertTrue(output.contains("Total classes found: 2"));
        assertTrue(output.contains("com.github.mkouba.jandex.Foo"));
        assertTrue(output.contains("com.github.mkouba.jandex.Bar"));
        assertTrue(output.contains("@java.lang.Deprecated on <i>void dontUseMe()</i>"));
    }

    private void addClass(Class<?> clazz, Indexer indexer) throws IOException {
        indexer.index(getClass().getClassLoader().getResourceAsStream(clazz.getName().replaceAll("\\.", "/") + ".class"));
    }

}
