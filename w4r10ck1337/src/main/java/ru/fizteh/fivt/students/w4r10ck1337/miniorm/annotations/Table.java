package ru.fizteh.fivt.students.w4r10ck1337.miniorm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface Table {
    String name() default "";
}
