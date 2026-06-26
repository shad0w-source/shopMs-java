package com.example;

import java.util.ArrayList;
import java.util.List;

public class ProductEventBus {
    private static final List<ProductChangeListener> listeners =
            new ArrayList<>();

    public static void subscribe(ProductChangeListener listener) {
        listeners.add(listener);
    }

    public static void unsubscribe(ProductChangeListener listener) {
        listeners.remove(listener);
    }

    public static void notifyProductChanged() {
        for (ProductChangeListener listener : listeners) {
            listener.onProductChanged();
        }
    }
}
