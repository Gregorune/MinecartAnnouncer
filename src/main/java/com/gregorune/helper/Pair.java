package com.gregorune.helper;

public class Pair<A, B> {
    public final A Key;
    public final B Value;

    public Pair(A key, B value) {
        this.Key = key;
        this.Value = value;
    }
}