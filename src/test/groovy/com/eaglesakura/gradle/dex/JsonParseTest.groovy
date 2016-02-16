package com.eaglesakura.gradle.dex

import com.eaglesakura.gradle.dex.model.DexCountModel
import com.eaglesakura.json.JSON

public class JsonParseTest extends GroovyTestCase {

    def getTestFile(String fileName) throws IOException {
        def result = new File("src/test/assets/${fileName}").absoluteFile;
        if (result.file) {
            return result;
        } else {
            throw new FileNotFoundException("${result.absolutePath} :: not found");
        }
    }

    public void test_サンプルのJSONをパースする() throws Exception {
        def json = getTestFile("sample00.json");

        def parsed = JSON.decode(json.text, DexCountModel.class);
        assertNotNull(parsed);
        assertNotSame(parsed.methods, 0);
        assertNotSame(parsed.fields, 0);
    }
}