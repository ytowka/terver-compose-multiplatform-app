package calulator

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

object Calculator {

    suspend fun calculatePlacementsNoReps(n: Int, k: Int): BigInteger{
        return factorial(n-k, n);
    }

    suspend fun calculatePlacementsWithReps(n: Int, k: Int): BigInteger{
        return (1..k).fold(BigInteger.ONE){ acc: BigInteger, i: Int ->
            coroutineContext.ensureActive()
            acc * BigInteger.fromInt(n)
        }
    }

    suspend fun calculatePermutation(n: Int): BigInteger{
        return factorial(n);
    }

    suspend fun calculatePermutationWithReps(ns: List<Int>): BigInteger{
        var result = BigInteger.ONE
        var n = factorial(ns.sum())
        for(i in ns){
            n /= factorial(i)
        }
        return n;
    }

    suspend fun combinations(n: Int, k: Int): BigInteger = binomialCoef(n, k)
    suspend fun combinationsWithReps(n: Int, k: Int): BigInteger = binomialCoef(n+k-1, k)

    suspend fun urnAll(n: Int, m: Int, k: Int): Double{
        return combinations(m, k).doubleValue() / combinations(n, k).doubleValue()
    }

    suspend fun urnPartial(n: Int, m: Int, k: Int, r: Int): Double{
        return combinations(m, r).doubleValue() * combinations(n-m, k-r).doubleValue() / combinations(n, k).doubleValue()
    }

    suspend private fun factorial(n: Int): BigInteger{
        return (1..n).fold(BigInteger.ONE){ acc: BigInteger, i: Int ->
            coroutineContext.ensureActive()
            acc * BigInteger.fromInt(i)
        }
    }

    private suspend fun factorial(a: Int, b: Int): BigInteger{
        return (a+1..b).fold(BigInteger.ONE){ acc: BigInteger, i: Int ->
            coroutineContext.ensureActive()
            acc * BigInteger.fromInt(i)
        }
    }

    private suspend fun binomialCoef(n: Int, k: Int): BigInteger{
        return factorial(k, n) / factorial(n-k)
    }
}