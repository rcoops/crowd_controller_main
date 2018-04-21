package me.cooper.rick.crowdcontrollerserver.service

import me.cooper.rick.crowdcontrollerapi.dto.group.CreateGroupDto
import me.cooper.rick.crowdcontrollerapi.dto.user.UserDto
import me.cooper.rick.crowdcontrollerserver.controller.WebSocketController
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.GroupNotFoundException
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.UserInGroupException
import me.cooper.rick.crowdcontrollerserver.controller.error.exception.UserNotFoundException
import me.cooper.rick.crowdcontrollerserver.persistence.model.Group
import me.cooper.rick.crowdcontrollerserver.persistence.model.Role
import me.cooper.rick.crowdcontrollerserver.persistence.model.User
import me.cooper.rick.crowdcontrollerserver.persistence.repository.GroupRepository
import me.cooper.rick.crowdcontrollerserver.persistence.repository.RoleRepository
import me.cooper.rick.crowdcontrollerserver.persistence.repository.UserRepository
import me.cooper.rick.crowdcontrollerserver.testutil.buildTestGroup
import me.cooper.rick.crowdcontrollerserver.testutil.buildTestUserList
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class GroupServiceTest {

    @InjectMocks
    private lateinit var groupService: GroupServiceImpl

    @Mock
    private lateinit var userRepository: UserRepository
    @Mock
    private lateinit var roleRepository: RoleRepository
    @Mock
    private lateinit var groupRepository: GroupRepository
    @Mock
    private lateinit var locationResolverService: LocationResolverService
    @Mock
    private lateinit var webSocketController: WebSocketController

    private lateinit var testGroup: Group
    private lateinit var testGroupedAdmin: User

    @Rule
    @JvmField
    val thrown = ExpectedException.none()

    @Before
    fun setup() {
        testGroup = buildTestGroup(TEST_GROUP_ID, TEST_ADMIN_ID)
        setupTestGroupedAdmin()

        // group repository mocks
        doReturn(testGroup).`when`(groupRepository).findOne(TEST_GROUP_ID)
        doReturn(null).`when`(groupRepository).findOne(TEST_NON_EXISTENT_GROUP_ID)

        // role repository mocks
        doReturn(listOf(Role(1L, "ROLE_GROUP_ADMIN")))
                .`when`(roleRepository).findAllByNameIn(listOf("ROLE_GROUP_ADMIN"))

        // user repository mocks
//        doReturn(testGroup.admin).`when`(userRepository).findOne(TEST_ADMIN_ID)
        doReturn(testGroupedAdmin).`when`(userRepository).findOne(testGroupedAdmin.id)
        doReturn(null).`when`(userRepository).findOne(TEST_NON_EXISTENT_ADMIN_ID)
    }

    @Test
    fun testGroup() {
        // When the service is called to return a group (dto)
        val groupDto = groupService.group(TEST_GROUP_ID)

        // Then the group dto retrieved has the correct details
        assertEquals(groupDto.id, testGroup.id)
        assertEquals(groupDto.adminId, testGroup.admin!!.id)
    }

    @Test(expected = GroupNotFoundException::class)
    fun testGroupNotFoundExceptionOnNonExistingGroupId() {
        // When the service is called to return a group (dto) for a non existent id
        groupService.group(TEST_NON_EXISTENT_GROUP_ID)

        // Then a GroupNotFoundException is thrown
    }

    @Test
    fun testGroups() {
        // Given the group repository is mocked to return a list of 2 test groups
        val testGroup2 = buildTestGroup(TEST_GROUP_ID + 1, TEST_ADMIN_ID + 1)
        val allGroups = listOf(testGroup, testGroup2)
        doReturn(allGroups).`when`(groupRepository).findAll()

        // When the service is called to return dto representations of 'all' groups
        val groupDtos = groupService.groups()

        // Then the correct number of dtos are retrieved
        assertEquals(groupDtos.size, allGroups.size)
        // And their details are as expected
        assertEquals(groupDtos[0].id, testGroup.id)
        assertEquals(groupDtos[0].adminId, testGroup.admin!!.id)
        assertEquals(groupDtos[1].id, testGroup2.id)
        assertEquals(groupDtos[1].adminId, testGroup2.admin!!.id)
    }

    @Test
    fun testCreate() {
        // Given a list of members
        val members = buildTestUserList(5)
        // And one is designated to be the group admin
        val admin = members[0]
        val mockGroup = Group.fromUsers(admin, members).copy(id = 1L)
        // And repostories are mocked to return expected results
        doReturn(admin).`when`(userRepository).findOne(admin.id)
        doReturn(members).`when`(userRepository).findAllWithIdIn(members.map(User::id).toSet())
        doReturn(admin).`when`(userRepository)
                .saveAndFlush(any(User::class.java))
        doReturn(mockGroup).`when`(groupRepository).save(any(Group::class.java))

        // When the service is called to create a group
        val groupDto = groupService.create(CreateGroupDto(admin.id, members.map(User::toGroupMemberDto)))

        // Then the returned dto has matching id, admin id and number of members
        assertEquals(groupDto.id, mockGroup.id)
        assertEquals(groupDto.adminId, mockGroup.admin!!.id)
        assertEquals(groupDto.members.size, mockGroup.members.size)
    }

    @Test
    fun testCreateForGroupedMembers() {
        // Given a list of members that are intended to be added to a group
        val unGroupedMembers = buildTestUserList(2)
        // And some are grouped
        val groupedMembers = buildTestUserList(2, 3, testGroup)
        val members = groupedMembers + unGroupedMembers
        // And the user repository is mocked to return the members when their ids are supplied
        doReturn(members).`when`(userRepository).findAllWithIdIn(members.map(User::id).toSet())
        // And the admin of the group is set
        val admin = unGroupedMembers[0]
        doReturn(admin).`when`(userRepository).findOne(admin.id)

        // A UserInGroupException is expected
        thrown.expect(UserInGroupException::class.java)
        thrown.expectMessage("Users: [${groupedMembers.map(User::toDto).mapToName()}] already belong to a group")

        // When the service attempts to create a group including the ids of these grouped members
        groupService.create(CreateGroupDto(admin.id, members.map(User::toGroupMemberDto)))
    }

    @Test
    fun testCreateForNonExistentAdminId() {
        thrown.expect(UserNotFoundException::class.java)
        thrown.expectMessage("User with identifier: $TEST_NON_EXISTENT_ADMIN_ID not found")
        // When the service is called to create a group using a non existent admin id
        groupService.create(CreateGroupDto(TEST_NON_EXISTENT_ADMIN_ID))
    }

    @Test
    fun testCreateForAlreadyGroupedAdmin() {
        thrown.expect(UserInGroupException::class.java)
        thrown.expectMessage("User ${testGroupedAdmin.username} already belongs to a different group")

        groupService.create(CreateGroupDto(testGroupedAdmin.id))
    }

    private fun setupTestGroupedAdmin() {
        testGroupedAdmin = User(
                id = TEST_GROUPED_ADMIN_ID,
                username = TEST_GROUPED_ADMIN_USERNAME,
                group = testGroup
        )
    }

    companion object {
        private const val TEST_GROUP_ID = 1L
        private const val TEST_NON_EXISTENT_GROUP_ID = -1L
        private const val TEST_NON_EXISTENT_ADMIN_ID = -1L
        private const val TEST_ADMIN_ID = 2L
        private const val TEST_GROUPED_ADMIN_USERNAME = "admin"
        private const val TEST_GROUPED_ADMIN_ID = 666L
        fun List<UserDto>.mapToName() = map(UserDto::username).joinToString(", ")
    }

}
