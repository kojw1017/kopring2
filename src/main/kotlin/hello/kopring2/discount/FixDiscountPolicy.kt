package hello.kopring2.discount

import hello.kopring2.member.Grade
import hello.kopring2.member.Member

class FixDiscountPolicy(private val disCountFixAmount:Int = 1000): DisCountPolicy {
    override fun discount(member: Member, price: Int): Int {
        return when(member.grade){
            Grade.BASIC -> 0
            Grade.VIP -> disCountFixAmount
        }
    }
}