package com.eaglesakura.gradle.dex.model;

import java.util.ArrayList;
import java.util.List;

public class DexCountModel {
    public String name;
    public int methods;
    public int fields;

    public List<DexCountModel> children = new ArrayList<>();
}
