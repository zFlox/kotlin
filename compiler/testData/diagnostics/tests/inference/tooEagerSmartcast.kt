// SKIP_TXT
// !DIAGNOSTICS: -UNUSED_VARIABLE
// !LANGUAGE: +NewInference

interface OutBase<out E>
interface OutDerived<out F> : OutBase<F>

fun <X> OutBase<X>.myLast(): X = TODO()

fun <T> foo(x: OutBase<T>) {
    if (x is OutDerived<*>) {
        val l: T = x.myLast() // required T, found Cap(*). Only in NI
    }
}

interface InvBase<E>
interface InvDerived<F> : InvBase<F>

fun <X> InvBase<X>.myLastInv(): X = TODO()

fun <T> fooInv(x: InvBase<T>) {
    if (x is InvDerived<*>) {
        val l: T = x.myLastInv() // required T, found Cap(*). Only in NI
    }
}
