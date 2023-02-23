package hello.kopring2.member


class MemberServiceImpl(
    private val memberRepository: MemberRepo = MemoryMemberRepo()
): MemberService {
    override fun join(member: Member) {
        memberRepository.save(member)
    }

    override fun findMember(memberId: Long): Member = memberRepository.findById(memberId)
}