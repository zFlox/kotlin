
// FILE: Function.java

public interface Function<Param, Result> {
    Result fun(Param param);
}

// FILE: AdapterProcessor.java

public class AdapterProcessor<T, S> {
    public AdapterProcessor(Function<? super T, ? extends S> conversion) {}
}


// FILE: main.kt

import java.util.*

interface PsiMethod {
    val containingClass: PsiClass?
}

interface PsiClass

fun test() {
    // TODO: don't forget to implement preservation flexibility of java type parameters in FIR (this is the reason of error here)
    val <!UNUSED_VARIABLE!>processor<!> = AdapterProcessor<PsiMethod, PsiClass>(
        Function { method: PsiMethod? -> method?.containingClass }
    )
    val x = ArrayList<Int>()
    <!DEBUG_INFO_EXPRESSION_TYPE("java.util.ArrayList<kotlin.Int>"), UNUSED_EXPRESSION!>x<!>
}