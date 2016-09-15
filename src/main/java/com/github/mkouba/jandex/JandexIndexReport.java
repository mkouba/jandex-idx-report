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

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.jboss.jandex.Index;
import org.jboss.jandex.IndexReader;
import org.trimou.engine.MustacheEngine;
import org.trimou.engine.MustacheEngineBuilder;
import org.trimou.engine.interpolation.ThrowingExceptionMissingValueHandler;
import org.trimou.engine.locator.ClassPathTemplateLocator;
import org.trimou.handlebars.HelpersBuilder;

/**
 *
 * @author Martin Kouba
 */
public class JandexIndexReport {

    public static void main(String[] args) {

        if (args.length != 2) {
            throw new IllegalArgumentException("You must specify two arguments - index file path and output file path: " + args);
        }

        File idxFile = new File(args[0]);

        if (!idxFile.canRead()) {
            throw new IllegalArgumentException("Cannot read index file: " + idxFile);
        }

        try (FileInputStream input = new FileInputStream(idxFile)) {
            IndexReader reader = new IndexReader(input);
            Index idx = reader.read();
            String output = new JandexIndexReport().generate(idx, idxFile.toString());
            Files.write(new File(args[1]).toPath(), output.getBytes(Charset.forName("UTF-8")));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    String generate(Index idx, String idxDescription) {
        MustacheEngine engine = MustacheEngineBuilder.newBuilder()
                .addTemplateLocator(ClassPathTemplateLocator.builder().setSuffix("html").build())
                .registerHelpers(HelpersBuilder.extra().build())
                .setMissingValueHandler(new ThrowingExceptionMissingValueHandler())
                .build();
        Map<String, Object> data = new HashMap<>();
        data.put("description", idxDescription);
        data.put("idx", idx);
        data.put("generatedAt", LocalDateTime.now());
        return engine.getMustache("jandex-idx-report").render(data);
    }

}
