package hello.kopring2.discount

import hello.kopring2.member.Member

interface DisCountPolicy {
    fun discount(member: Member, price: Int): Int
}