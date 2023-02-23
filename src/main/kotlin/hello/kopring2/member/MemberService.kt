package hello.kopring2.member

interface MemberService {
    fun join(member: Member)
    fun findMember(memberId: Long): Member
}