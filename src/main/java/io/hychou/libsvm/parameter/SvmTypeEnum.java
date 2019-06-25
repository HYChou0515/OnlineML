package io.hychou.libsvm.parameter;

public enum SvmTypeEnum {
    C_SVC(0),
    NU_SVC(1),
    ONE_CLASS(2),
    EPSILON_SVR(3),
    NU_SVR(4);

    private final int value;

    private SvmTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
