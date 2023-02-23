package hello.kopring2.member

interface MemberRepo {
    fun save(member: Member)
    fun findById(memberId: Long): Member
}