package hello.kopring2.discount

import hello.kopring2.member.Grade
import hello.kopring2.member.Member
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class RateDiscountPolicyTest{
    private val discountPolicy = RateDiscountPolicy()

    @Test
    fun vip_o(){
        val member = Member(1L, "memberVip", Grade.VIP)
        val discount = discountPolicy.discount(member, 10000)
        Assertions.assertEquals(discount, 1000)
    }

    @Test
    fun vip_x(){
        val member = Member(1L, "memberVip", Grade.BASIC)
        val discount = discountPolicy.discount(member, 10000)
        Assertions.assertEquals(discount, 0)
    }
    class TTest{
        enum class A(val title: String){
            Aawd("123"),
            Aawd1("123"),
            Aawd2("123"),
            Aawd3("123"),
            Aawd4("123"),
            Aawd5("123"),
            Aawd6("123"),
            Aawd7("123"),
            Aaw8("123"),
            Aawd9("123");
            companion object{
                fun defaultList() = listOf(Aaw8, Aawd9)
            }
        }
    }
    class iteeer<T:Any>(private val first:T, val list: List<T>): Iterable<T>{
        inner class Iter:Iterator<T>{
            var cursor = 0

            override fun hasNext() = cursor < list.size + 1

            override fun next(): T {
                val curr = cursor
                cursor++
                return if(curr == 0) first else list[curr + 1]
            }

        }
        override fun iterator(): Iterator<T> = Iter()
        operator fun get(index: Int)= if(index == 0) first else list[index + 1]
    }
    @Test
    fun vip_xz(){
        val a = iteeer(TTest.A.Aawd, TTest.A.defaultList())
        a.
    }
}