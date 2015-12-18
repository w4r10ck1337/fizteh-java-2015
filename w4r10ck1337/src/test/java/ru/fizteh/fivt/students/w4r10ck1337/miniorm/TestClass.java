package ru.fizteh.fivt.students.w4r10ck1337.miniorm;

import org.junit.Test;
import ru.fizteh.fivt.students.w4r10ck1337.miniorm.annotations.Column;
import ru.fizteh.fivt.students.w4r10ck1337.miniorm.annotations.PrimaryKey;
import ru.fizteh.fivt.students.w4r10ck1337.miniorm.annotations.Table;


@Table
public class TestClass {

    @PrimaryKey
    @Column
    public Integer key;

    @Column
    public String value1;

    @Column
    public Double value2;

    public TestClass(){}

    public TestClass(int key, String s, double d) {
        this.key = key;
        this.value1 = s;
        this.value2 = d;
    }

    public String toString() {
        return "{" + Integer.valueOf(key).toString() + "," + value1 + "," + Double.valueOf(value2).toString() + "}";
    }

    public boolean equals(TestClass t) {
        return this.key.equals(t.key) && this.value1.equals(t.value1) && Math.abs(this.value2 - t.value2) < 0.01;
    }

    public int hashCode() {
        return 1337 * key;
    }
}
