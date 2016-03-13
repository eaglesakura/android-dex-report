package com.eaglesakura.gradle.dex.model;

import com.eaglesakura.util.CollectionUtil;
import com.eaglesakura.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DexCountModel {
    public String name;
    public int methods;
    public int fields;

    private DexCountModel parent;

    public List<DexCountModel> children = new ArrayList<>();

    /**
     * 対象パッケージ名を取得する
     * buildDepthを構築してある必要がある
     */
    public static String getPackageName(DexCountModel model) {
        String result = model.name;
        while (model.parent != null) {
            model = model.parent;
            result = model.name + "." + result;
        }
        return result;
    }

    public static DexCountModel findChild(DexCountModel parent, String name) {
        if (!CollectionUtil.isEmpty(parent.children)) {
            for (DexCountModel child : parent.children) {
                if (child.name.equals(name)) {
                    return child;
                }
            }
        }
        return null;
    }

    public static DexCountModel find(DexCountModel root, String packageName) {
        List<String> packs = CollectionUtil.asList(packageName.split("\\."));

        DexCountModel packRoot = findChild(root, packs.remove(0));

        while (packRoot != null && !packs.isEmpty()) {
            packRoot = findChild(packRoot, packs.remove(0));
        }

        return packRoot;
    }

    /**
     * 親子階層を構築する
     *
     * @param current 現在の階層
     */
    public static void buildDepth(DexCountModel current) {
        if (CollectionUtil.isEmpty(current.children)) {
            return;
        }

        for (DexCountModel child : current.children) {
            child.parent = current;
            buildDepth(child);
        }
    }
}
