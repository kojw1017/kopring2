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
}