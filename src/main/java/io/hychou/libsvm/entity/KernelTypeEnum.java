package io.hychou.libsvm.entity;

public enum KernelTypeEnum {
    LINEAR(0),
    POLY(1),
    RBF(2),
    SIGMOID(3),
    PRECOMPUTED(4);

    private final int value;

    private KernelTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
