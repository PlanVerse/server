package com.planverse.server.team.service

import com.planverse.server.common.constant.Constant
import com.planverse.server.common.constant.StatusCode
import com.planverse.server.common.exception.BaseException
import com.planverse.server.file.service.FileService
import com.planverse.server.team.dto.TeamInfoDTO
import com.planverse.server.team.dto.TeamInfoRequestDTO
import com.planverse.server.team.dto.TeamInfoUpdateRequestDTO
import com.planverse.server.team.dto.TeamMemberInfoDTO
import com.planverse.server.team.entity.TeamMemberInfoEntity
import com.planverse.server.team.mapper.TeamMemberInfoMapper
import com.planverse.server.team.repository.TeamInfoRepository
import com.planverse.server.team.repository.TeamMemberInfoRepository
import com.planverse.server.user.dto.UserInfo
import com.planverse.server.user.repository.UserInfoRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(readOnly = true)
class TeamService(
    private val fileService: FileService,
    private val userInfoRepository: UserInfoRepository,
    private val teamInfoRepository: TeamInfoRepository,
    private val teamMemberInfoRepository: TeamMemberInfoRepository,
    private val teamMemberInfoMapper: TeamMemberInfoMapper,
) {

    private fun getCreatorDTO(teamMemberId: Long): TeamMemberInfoDTO {
        return teamMemberInfoRepository.findByTeamInfoIdAndCreatorAndDeleteYn(teamMemberId, Constant.FLAG_TRUE, Constant.DEL_N).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.let { creator ->
            val creatorUserInfo = userInfoRepository.findById(creator.userInfoId).orElseThrow {
                BaseException(StatusCode.USER_NOT_FOUND)
            }

            TeamMemberInfoDTO.toDto(creator, creatorUserInfo.name)
        }
    }

    private fun getMemberDTOs(teamMemberId: Long): List<TeamMemberInfoDTO> {
        return teamMemberInfoRepository.findAllByTeamInfoIdAndCreatorAndDeleteYn(teamMemberId, Constant.FLAG_FALSE, Constant.DEL_N).orElse(emptyList()).map { member ->
            val memberUserInfo = userInfoRepository.findById(member.userInfoId).orElseThrow {
                BaseException(StatusCode.USER_NOT_FOUND)
            }

            TeamMemberInfoDTO.toDto(member, memberUserInfo.name)
        }
    }

    private fun getCreateTeamInfoDTO(userInfoId: Long, creator: TeamMemberInfoEntity): TeamInfoDTO {
        // 팀 정보 조회
        val teamInfoDTO = teamInfoRepository.findById(creator.teamInfoId).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.let { TeamInfoDTO.toDto(it) }

        val teamProfileImage = fileService.getFile(Constant.FILE_TARGET_TEAM, creator.teamInfoId)

        // 팀 생성자 정보 조회 및 변환
        val creatorDTO = getCreatorDTO(creator.teamInfoId)

        if (teamInfoDTO.private == true) {
            if (creatorDTO.userInfoId != userInfoId) {
                throw BaseException(StatusCode.TEAM_NOT_FOUND)
            }
        }

        // 팀 멤버 정보 조회 및 변환
        val memberDTOs = getMemberDTOs(creator.teamInfoId)

        // DTO 생성 및 반환
        return teamInfoDTO.apply {
            this.teamProfileImage = teamProfileImage
            this.teamCreatorInfo = creatorDTO
            this.teamMemberInfos = memberDTOs
        }
    }

    fun getTeamInfo(userInfo: UserInfo, teamId: Long): TeamInfoDTO {
        // 팀 정보 존재여부 판단
        val teamInfoDTO = teamInfoRepository.findById(teamId).orElseThrow {
            BaseException(StatusCode.TEAM_NOT_FOUND)
        }.let { TeamInfoDTO.toDto(it) }

        val teamProfileImage = fileService.getFile(Constant.FILE_TARGET_TEAM, teamId)

        // 팀 생성자 정보 조회
        val creatorDTO = getCreatorDTO(teamInfoDTO.id!!)

        // 팀 멤버리스트 :: 생성자가 아닌 멤버를 검색하므로 null throw X
        val memberDTOs = getMemberDTOs(teamInfoDTO.id!!)

        if (teamInfoDTO.private == true) {
            val creatorAndMemberUserInfoIds = arrayListOf(creatorDTO.userInfoId, memberDTOs.stream().map { it.userInfoId }.toList())

            if (!creatorAndMemberUserInfoIds.contains(userInfo.id)) {
                throw BaseException(StatusCode.TEAM_NOT_FOUND)
            }
        }

        return teamInfoDTO.apply {
            this.teamProfileImage = teamProfileImage
            this.teamCreatorInfo = creatorDTO
            this.teamMemberInfos = memberDTOs
        }
    }

    fun getTeamListCreator(userInfo: UserInfo, pageable: Pageable): Slice<TeamInfoDTO> {
        // 팀 멤버 정보를 페이지네이션으로 조회
        return teamMemberInfoRepository.findAllByUserInfoIdAndCreatorAndDeleteYn(userInfo.id!!, Constant.FLAG_TRUE, Constant.DEL_N, pageable).map { creator ->
            getCreateTeamInfoDTO(userInfo.id!!, creator)
        }
    }

    fun getTeamListMember(userInfo: UserInfo, pageable: Pageable): Slice<TeamInfoDTO> {
        // 팀 멤버 정보를 페이지네이션으로 조회
        return teamMemberInfoRepository.findAllByUserInfoIdAndCreatorAndDeleteYn(userInfo.id!!, Constant.FLAG_FALSE, Constant.DEL_N, pageable).map { member ->
            // 팀 정보 조회
            val teamInfoDTO = teamInfoRepository.findById(member.teamInfoId).orElseThrow {
                BaseException(StatusCode.TEAM_NOT_FOUND)
            }.let { TeamInfoDTO.toDto(it) }

            val teamProfileImage = fileService.getFile(Constant.FILE_TARGET_TEAM, member.teamInfoId)

            // 팀 생성자 조회
            val creatorDTO = getCreatorDTO(member.teamInfoId)

            // 팀 멤버리스트 :: 생성자가 아닌 멤버를 검색하므로 null throw X
            val memberDTOs = getMemberDTOs(member.teamInfoId)

            if (teamInfoDTO.private == true) {
                val creatorAndMemberUserInfoIds = arrayListOf(creatorDTO.userInfoId, memberDTOs.stream().map { it.userInfoId }.toList())

                if (!creatorAndMemberUserInfoIds.contains(userInfo.id)) {
                    throw BaseException(StatusCode.TEAM_NOT_FOUND)
                }
            }

            teamInfoDTO.apply {
                this.teamProfileImage = teamProfileImage
                this.teamCreatorInfo = creatorDTO
                this.teamMemberInfos = memberDTOs
            }
        }
    }

    @Transactional
    fun createTeam(userInfo: UserInfo, teamInfoRequestDTO: TeamInfoRequestDTO, multipartFile: MultipartFile?) {
        val teamId = teamInfoRepository.save(teamInfoRequestDTO.toEntity()).id

        teamMemberInfoRepository.save(TeamMemberInfoDTO.toEntity(userInfo.id!!, teamId!!, Constant.FLAG_TRUE))

        teamInfoRequestDTO.invite?.forEach { inviteEmail ->
            if (inviteEmail == userInfo.email) {
                throw BaseException(StatusCode.TEAM_CREATOR_IS_ALREADY_MEMBER)
            } else {
                userInfoRepository.findByEmailAndDeleteYn(inviteEmail, Constant.DEL_N).ifPresent { creatorUserInfo ->
                    val teamMemberInfoEntity = TeamMemberInfoDTO.toEntity(creatorUserInfo.id!!, teamId, Constant.FLAG_FALSE)
                    teamMemberInfoRepository.save(teamMemberInfoEntity)
                }
            }
        }

        if (multipartFile != null && !multipartFile.isEmpty) {
            fileService.fileSave("team", teamId, multipartFile)
        }
    }

    @Transactional
    fun modifyTeamInfo(userInfo: UserInfo, teamInfoUpdateRequestDTO: TeamInfoUpdateRequestDTO) {
        teamMemberInfoRepository.findByTeamInfoIdAndUserInfoIdAndCreatorAndDeleteYn(teamInfoUpdateRequestDTO.teamId, userInfo.id!!, Constant.FLAG_TRUE, Constant.DEL_N)
            .orElseThrow {
                BaseException(StatusCode.TEAM_NOT_FOUND)
            }.let { member ->
                teamInfoRepository.findById(member.teamInfoId).orElseThrow {
                    BaseException(StatusCode.TEAM_NOT_FOUND)
                }.let { info ->
                    if (teamInfoUpdateRequestDTO.name != null) {
                        info.name = teamInfoUpdateRequestDTO.name
                    }
                    info.description = teamInfoUpdateRequestDTO.description

                    teamInfoRepository.save(info)
                }
            }
    }

    @Transactional
    fun inviteTeamMember(userInfo: UserInfo, teamInfoUpdateRequestDTO: TeamInfoUpdateRequestDTO) {
        // 요청자의 정보가 생성자 즉 관리자의 권한일 경우에만 조회 값이 존재
        teamInfoUpdateRequestDTO.creatorUserInfoId = userInfo.id
        teamMemberInfoMapper.selectTeamMemberInfoForCreator(teamInfoUpdateRequestDTO).ifEmpty {
            throw BaseException(StatusCode.TEAM_NOT_FOUND)
        }.run {
            // 초대 멤버 정보가 없다면 동일한 것으로 판단
            if (teamInfoUpdateRequestDTO.invite != null) {
                buildList {
                    // 초대 멤버 사용자 정보 획득
                    teamInfoUpdateRequestDTO.invite.forEach {
                        val inviteUserInfo = userInfoRepository.findByEmail(it).orElseThrow {
                            BaseException(StatusCode.USER_NOT_FOUND)
                        }

                        // DB의 값이므로 존재 확인
                        add(inviteUserInfo.id!!)
                    }
                }.filter {
                    // 신규 멤버의 사용자 id에 기존 멤버 id가 포함되어있다면 제외
                    it !in this.stream().map { member -> member.userInfoId }.toList()
                }.forEach {
                    teamMemberInfoRepository.save(TeamMemberInfoDTO.toEntity(it, teamInfoUpdateRequestDTO.teamId, Constant.FLAG_FALSE))
                }
            }

            // 내보내기 멤버 정보 존재 판단
            if (teamInfoUpdateRequestDTO.excluding != null) {
                buildList {
                    // 내보내기 멤버 사용자 정보 획득
                    teamInfoUpdateRequestDTO.excluding.forEach {
                        val inviteUserInfo = userInfoRepository.findByEmail(it).orElseThrow {
                            BaseException(StatusCode.USER_NOT_FOUND)
                        }

                        // DB의 값이므로 존재 확인
                        add(inviteUserInfo.id!!)
                    }
                }.filter {
                    // 내보내기 멤버의 사용자 id에 기존 멤버 id가 포함되어있어야함
                    it in this.stream().map { member -> member.userInfoId }.toList()
                }.forEach {
                    // 팀 생성자 즉 관리자는 팀에서 내보내기가 불가능하므로 검사
                    teamMemberInfoRepository.findByTeamInfoIdAndUserInfoIdAndCreatorAndDeleteYn(teamInfoUpdateRequestDTO.teamId, it, Constant.FLAG_FALSE, Constant.DEL_N).orElseThrow {
                        BaseException(StatusCode.TEAM_CREATOR_CANNOT_EXCLUDE)
                    }.let { excludeMemberInfo ->
                        excludeMemberInfo.deleteYn = Constant.DEL_Y
                        teamMemberInfoRepository.save(excludeMemberInfo)
                    }
                }
            }
        }
    }
}