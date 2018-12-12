package io.hychou.common;

public abstract class DataStructureTest {

    protected boolean equalsThenHashCodeEqual(Object a, Object b) {
        if ( !a.equals(b)) {
            return false;
        }
        return a.equals(b) && a.hashCode() == b.hashCode();
    }

    // equals

    abstract public void equals_givenSelf_thenTrueShouldBeFound();

    abstract public void equals_givenSameObject_thenTrueShouldBeFound();

    abstract public void equals_givenSame_thenTrueShouldBeFound();

    abstract public void equals_givenDiff_thenFalseShouldBeFound();

    abstract public void equals_givenNull_thenFalseShouldBeFound();

    abstract public void equals_givenAnotherObject_thenFalseShouldBeFound();

    // hashCode

    abstract public void hashCode_thenCorrectHashShouldBeFound();

    // toString

    abstract public void toString_thenCorrectStringShouldBeFound();
}