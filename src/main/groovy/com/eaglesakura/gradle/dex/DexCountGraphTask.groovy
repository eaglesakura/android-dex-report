package com.eaglesakura.gradle.dex

import com.eaglesakura.gradle.dex.model.DexCountModel
import com.eaglesakura.json.JSON
import com.eaglesakura.tool.log.Logger
import com.eaglesakura.util.Util
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


public class DexCountGraphTask extends DefaultTask {
    File source;
    List<Attention> attentions = new ArrayList<>();

    /**
     * どの程度のメソッドを使い切ったら警告を出すか
     */
    def worningLevel = 0.9;

    def width = 600;
    def height = 300;

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
    private def buildUrl() {
        int methods = rootModel.methods;

        def values = "";
        def names = "";

        Logger.out("All Methods :: ${rootModel.methods}")
        Logger.pushIndent()
        for (def att : attentions) {
            Logger.out("name(${att.getScreenName()}) methods(${att.methods})")
            names += (names.empty ? "" : "|") + URLEncoder.encode(att.getScreenName(), "UTF-8");
            values += (values.empty ? "" : ",") + att.methods;
            methods -= att.methods;
        }

        values += ",${methods}"
        names += "|Others"
        Logger.popIndent()

        String url = "http://chart.apis.google.com/chart?cht=p&chs=${width}x${height}&chl=${names}&chd=t:${values}";

        Logger.out("URL :: ${url}");
        return url;
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
        buildUrl();
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
            return Util.isEmpty(mAlias) ? packageName : mAlias;
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
