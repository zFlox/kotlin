// SKIP_TXT
// !DIAGNOSTICS: -UNUSED_PARAMETER
// !LANGUAGE: +NewInference

interface Inv<T>

fun <E> Inv<E>.foo(
    handler: () -> ((command: E) -> Unit)
) {}

fun bar(x: Int) {}
fun bar(x: String) {}

fun main(x: Inv<Int>) {
    x.foo<Int> {
        if (x.hashCode() == 0) return@foo <!UNRESOLVED_REFERENCE!>::bar<!>

        ::bar
    }

    x.foo {
        if (x.hashCode() == 0) return@foo <!UNRESOLVED_REFERENCE!>::bar<!>

        ::bar
    }
}
