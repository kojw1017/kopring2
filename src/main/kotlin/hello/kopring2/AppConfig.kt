package hello.kopring2

import hello.kopring2.discount.FixDiscountPolicy
import hello.kopring2.member.MemberServiceImpl
import hello.kopring2.member.MemoryMemberRepo
import hello.kopring2.order.OrderServiceImpl

class AppConfig {
    fun memberService() = MemberServiceImpl(MemoryMemberRepo())
    fun orderService() = OrderServiceImpl(MemoryMemberRepo(), FixDiscountPolicy())
}