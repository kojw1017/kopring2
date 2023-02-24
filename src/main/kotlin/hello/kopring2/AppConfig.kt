package hello.kopring2

import hello.kopring2.discount.FixDiscountPolicy
import hello.kopring2.discount.RateDiscountPolicy
import hello.kopring2.member.MemberServiceImpl
import hello.kopring2.member.MemoryMemberRepo
import hello.kopring2.order.OrderServiceImpl

class AppConfig {
    fun memberService() = MemberServiceImpl(memberRepository())
    private fun memberRepository() = MemoryMemberRepo()
    fun orderService() = OrderServiceImpl(MemoryMemberRepo(), discountPolicy())
//    fun discountPolicy() = FixDiscountPolicy()
    private fun discountPolicy() = RateDiscountPolicy()
}