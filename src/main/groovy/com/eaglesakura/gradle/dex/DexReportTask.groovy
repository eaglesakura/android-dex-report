package com.eaglesakura.gradle.dex

import com.eaglesakura.gradle.dex.model.DexCountModel
import com.eaglesakura.json.JSON
import com.eaglesakura.tool.log.Logger
import com.eaglesakura.util.CollectionUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

public class DexReportTask extends DefaultTask {
    File source;
    List<Attention> attentions = new ArrayList<>();

    /**
     * どの程度のメソッドを使い切ったら殺すか
     */
    def deadMethodNum = (int) ((double) 0xFFFF * 0.95);

    private DexCountModel rootModel;

    /**
     * JSONから集計情報を取り出す
     */
    private void load() {
        rootModel = JSON.decode(source.text, DexCountModel.class);
        for (def att : attentions) {
            att.load(rootModel);
        }
    }

    /**
     * グラフ描画用のURLを構築する
     */
    private void reportMethods() {
        int methods = rootModel.methods;

        Logger.out("All Methods :: ${rootModel.methods}")
        Logger.pushIndent()
        for (def att : attentions) {
            Logger.out("name(${att.getScreenName()}) methods(${att.methods})")
            methods -= att.methods;
        }
        Logger.out("name(Others) methods(${methods})")
        Logger.popIndent()
        Logger.out("Method Using :: ${rootModel.methods} / ${deadMethodNum} = ${String.format("%.1f", (double) rootModel.methods / (double) deadMethodNum * 100)} %")

        if (rootModel.methods > deadMethodNum) {
            throw new Error("Method Over Using :: ${rootModel.methods} / ${deadMethodNum}");
        }
    }

    /**
     * 注視対象を生成する
     */
    def newAattention(String packageName) {
        return new Attention(packageName);
    }

    @TaskAction
    def onExecute() {
        load();
        reportMethods();
    }

    public class Attention {
        private String packageName;
        private String mAlias;
        private DexCountModel model;

        int getMethods() {
            return model != null ? model.methods : 0;
        }

        int getFields() {
            return model != null ? model.fields : 0;
        }

        String getScreenName() {
            return CollectionUtil.isEmpty(mAlias) ? packageName : mAlias;
        }

        void load(DexCountModel root) {
            this.model = DexCountModel.find(root, packageName);
        }

        private Attention(String packageName) {
            this.packageName = packageName
            this.model = model;
        }

        public Attention alias(String set) {
            mAlias = set;
            return this;
        }
    }
}
