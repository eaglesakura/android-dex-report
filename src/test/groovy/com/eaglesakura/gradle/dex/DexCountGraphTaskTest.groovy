package com.eaglesakura.gradle.dex

import org.gradle.testfixtures.ProjectBuilder

public class DexCountGraphTaskTest extends GroovyTestCase {

    def getTestFile(String fileName) throws IOException {
        def result = new File("src/test/assets/${fileName}").absoluteFile;
        if (result.file) {
            return result;
        } else {
            throw new FileNotFoundException("${result.absolutePath} :: not found");
        }
    }

    void test_データ出力00() {
        def project = ProjectBuilder.builder().build();
        def task = (DexReportTask) project.task("testTask", type: DexReportTask);

        task.source = getTestFile("sample00.json");
        task.deadMethodNum = (int)(0xFFFF * 0.5)
        task.attentions += task.newAattention("com.eaglesakura.andriders").alias("App");
        task.attentions += task.newAattention("android").alias("Framework");

        task.execute();
    }
}