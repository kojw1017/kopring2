package hello.kopring2

import hello.kopring2.member.Grade
import hello.kopring2.member.Member
import hello.kopring2.member.MemberService
import hello.kopring2.order.OrderService

fun main(){
    val appConfig = AppConfig()
    val memberService: MemberService = appConfig.memberService()
    val orderService: OrderService = appConfig.orderService()

    val memberId = 1L
    val member = Member(memberId, "memberA", Grade.VIP)
    memberService.join(member)
    val order = orderService.createOrder(memberId, "itemA", 10000)
    println(order)
}
