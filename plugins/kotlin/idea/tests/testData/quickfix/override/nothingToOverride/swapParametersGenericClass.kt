// "Change function signature to 'fun f(y: S, x: List<Set<R>>)'" "true"
interface A<P,Q> {
    fun f(a: Q, b: List<Set<P>>)
}

class B<R,S> : A<R,S> {
    <caret>override fun f(x: List<Set<R>>, y: S) {}
}

// FUS_QUICKFIX_NAME: org.jetbrains.kotlin.idea.quickfix.ChangeMemberFunctionSignatureFix
// FUS_K2_QUICKFIX_NAME: org.jetbrains.kotlin.idea.k2.codeinsight.fixes.ChangeMemberFunctionSignatureFixFactory$ChangeMemberFunctionSignatureFix