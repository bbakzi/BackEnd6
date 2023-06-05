package com.sparta.ourportfolio.project.service;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.common.exception.GlobalException;
import com.sparta.ourportfolio.common.utils.S3Service;
import com.sparta.ourportfolio.project.dto.ProjectRequestDto;
import com.sparta.ourportfolio.project.dto.ProjectResponseDto;
import com.sparta.ourportfolio.project.entity.Project;
import com.sparta.ourportfolio.project.repository.FileRepository;
import com.sparta.ourportfolio.project.repository.ProjectRepository;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class GetProjectServiceTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ProjectService projectService;

    @DisplayName("프로젝트 상세 조회")
    @Test
    void getProject() throws IOException {
        // given
        User user1 = createUser(1L, "test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        userRepository.save(user1);

        ProjectRequestDto projectRequestDto1 = createProjectRequestDto("1", "2", "3", "4", "5");

        // 이미지 파일을 생성하여 리스트에 추가
        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile imageFile1 = new MockMultipartFile("image1", "test1.jpg", "image/jpeg", "Test Image".getBytes());
        MockMultipartFile imageFile2 = new MockMultipartFile("image2", "test2.jpg", "image/jpeg", "Test Image".getBytes());
        images.add(imageFile1);
        images.add(imageFile2);

        Project project = new Project(projectRequestDto1, user1);
        project.setImageFile(s3Service.fileFactory(images, project));
        Project savedProject = projectRepository.save(project);

        // when
        ResponseDto<ProjectResponseDto> projectResponse = projectService.getProject(savedProject.getId());

        // then
        assertThat(projectResponse)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "상세 조회 성공");
    }

    @DisplayName("잘못된 프로젝트 id로 조회했을 시 예외를 반환한다.")
    @Test
    void getProjectWithWrongId() {
        // when // then
        assertThatThrownBy(() -> projectService.getProject(1L))
                .isInstanceOf(GlobalException.class)
                .hasMessage("프로젝트가 존재하지 않습니다.");
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

    private ProjectRequestDto createProjectRequestDto(String title, String term, String people, String position, String description) {
        return ProjectRequestDto.builder()
                .title(title)
                .term(term)
                .people(people)
                .position(position)
                .description(description)
                .build();
    }

}