package hello.kopring2

import hello.kopring2.member.Grade
import hello.kopring2.member.Member
import hello.kopring2.member.MemberService
import hello.kopring2.member.MemberServiceImpl

fun main(){
    val memberService: MemberService = MemberServiceImpl()
    val member = Member(1L, "memberA", Grade.VIP)
    memberService.join(member)

    val findMember = memberService.findMember(1L)
    println("new member ${member.name}")
    println("fine member ${findMember?.name}")
}
