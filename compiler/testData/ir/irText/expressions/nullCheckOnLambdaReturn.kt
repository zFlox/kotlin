// FILE: nullCheckOnLambdaReturn.kt
fun check(fn: () -> Any) = fn()

fun simple(): Any = J.foo()

fun test() = check { J.foo() }

// FILE: J.java
public class J {
    public static String foo() { return null; }
}