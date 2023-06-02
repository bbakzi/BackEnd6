package com.sparta.ourportfolio.user.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DeleteUserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("회원탈퇴 soft")
    @Test
    void softDelete() {
        // given
        User user1 = createUser(1L, "test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user1);

        // when
        ResponseDto<HttpStatus> deleteUserResponse = userService.deleteUser(user1.getId(), user1);

        // then
        assertThat(deleteUserResponse)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "회원 탈퇴 성공!");
    }

    @DisplayName("회원탈퇴 hard")
    @Test
    void hardDelete() {
        // given
        User user2 = createUser(2L, "test1234@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test1234", false);
        userRepository.save(user2);

        // when
        ResponseDto<HttpStatus> hardDeleteUserResponse = userService.deleteUserHard(user2.getId(), user2);

        // then
        assertThat(hardDeleteUserResponse)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "영구 삭제");
    }

    private User createUser(Long id, String email, String password, String nickname, boolean isDeleted) {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .nickname(nickname)
                .isDeleted(isDeleted)
                .build();
    }

}