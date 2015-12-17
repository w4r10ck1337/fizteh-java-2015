package ru.fizteh.fivt.students.w4r10ck1337.miniorm;

import ru.fizteh.fivt.students.w4r10ck1337.miniorm.annotations.Column;
import ru.fizteh.fivt.students.w4r10ck1337.miniorm.annotations.PrimaryKey;
import ru.fizteh.fivt.students.w4r10ck1337.miniorm.annotations.Table;


@Table
public class TestClass {

    @PrimaryKey
    @Column
    public int key;

    @Column
    public String value1;

    @Column
    public double value2;

    public TestClass(int key, String s, double d) {
        this.key = key;
        this.value1 = s;
        this.value2 = d;
    }

    public String toString() {
        return "{" + Integer.valueOf(key).toString() + "," + value1 + "," + Double.valueOf(value2).toString() + "}";
    }
}
