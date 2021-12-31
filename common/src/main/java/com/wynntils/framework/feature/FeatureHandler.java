package com.wynntils.framework.feature;

import java.util.*;

public class FeatureHandler {
    private static final Set<Feature> features = new HashSet<>();

    public static void registerFeature(Feature feature) {
        features.add(feature);
    }

    public static void unregisterFeature(Feature feature) {
        features.remove(feature);
    }

    public static void initalizeFeatures() {
        features.forEach(Feature::onEnable);
    }
}