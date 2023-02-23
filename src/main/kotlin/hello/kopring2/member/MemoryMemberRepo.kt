package hello.kopring2.member

class MemoryMemberRepo: MemberRepo{
    companion object{
        val store = hashMapOf<Long, Member>()
    }
    override fun save(member: Member) {
        store[member.id] = member
    }
    override fun findById(memberId: Long): Member = store[memberId] ?: error("멤버가 없습니다")
}