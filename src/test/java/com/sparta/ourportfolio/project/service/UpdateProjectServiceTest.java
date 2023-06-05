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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class UpdateProjectServiceTest {

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

    @DisplayName("프로젝트 수정")
    @Test
    @Transactional
    void updateProject() throws IOException {
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
        project = projectRepository.save(project);

        // 해당되는 전체 이미지 삭제
        fileRepository.deleteByProjectId(project.getId());

        ProjectRequestDto newProjectRequestDto = createProjectRequestDto("5", "4", "3", "2", "1");

        List<MultipartFile> newImages = new ArrayList<>();
        MockMultipartFile imageFile3 = new MockMultipartFile("image3", "test3.jpg", "image/jpeg", "Test Image".getBytes());
        MockMultipartFile imageFile4 = new MockMultipartFile("image4", "test4.jpg", "image/jpeg", "Test Image".getBytes());
        newImages.add(imageFile3);
        newImages.add(imageFile4);


        project.setImageFile(s3Service.fileFactory(newImages, project));
        project.updateProject(newProjectRequestDto);

        // when
        ResponseDto<ProjectResponseDto> projectResponse = projectService.updateProject(project.getId(), newProjectRequestDto, newImages, user1);

        // then
        assertThat(projectResponse)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "프로젝트 수정 완료");
    }

    @DisplayName("잘못된 프로젝트 id로 수정했을 시 예외를 반환한다.")
    @Test
    @Transactional
    void updateProjectWithWrongId() throws Exception {
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
        project = projectRepository.save(project);

        // 해당되는 전체 이미지 삭제
        fileRepository.deleteByProjectId(project.getId());

        ProjectRequestDto newProjectRequestDto = createProjectRequestDto("5", "4", "3", "2", "1");

        List<MultipartFile> newImages = new ArrayList<>();
        MockMultipartFile imageFile3 = new MockMultipartFile("image3", "test3.jpg", "image/jpeg", "Test Image".getBytes());
        MockMultipartFile imageFile4 = new MockMultipartFile("image4", "test4.jpg", "image/jpeg", "Test Image".getBytes());
        newImages.add(imageFile3);
        newImages.add(imageFile4);


        project.setImageFile(s3Service.fileFactory(newImages, project));
        project.updateProject(newProjectRequestDto);

        // when // then
        assertThatThrownBy(() -> projectService.updateProject(2L, newProjectRequestDto, newImages, user1))
                .isInstanceOf(GlobalException.class)
                .hasMessage("프로젝트가 존재하지 않습니다.");
    }

    @DisplayName("프로젝트 user id와 현재 로그인한 user id가 다를 시 예외를 반환한다.")
    @Test
    @Transactional
    void test() throws Exception {
        // given
        User user1 = createUser(1L, "test4567@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test4567", false);
        User user2 = createUser(2L, "test1234@example.com", "$2a$10$pJA9gZGQrnVlMFZJtEn0ge9qzECZ5E6vsoprz0RDBdrI6WxIicWXK", "test1234", false);
        userRepository.save(user1);
        userRepository.save(user2);

        ProjectRequestDto projectRequestDto1 = createProjectRequestDto("1", "2", "3", "4", "5");

        // 이미지 파일을 생성하여 리스트에 추가
        List<MultipartFile> images = new ArrayList<>();
        MockMultipartFile imageFile1 = new MockMultipartFile("image1", "test1.jpg", "image/jpeg", "Test Image".getBytes());
        MockMultipartFile imageFile2 = new MockMultipartFile("image2", "test2.jpg", "image/jpeg", "Test Image".getBytes());
        images.add(imageFile1);
        images.add(imageFile2);

        Project project = new Project(projectRequestDto1, user1);
        project.setImageFile(s3Service.fileFactory(images, project));
        project = projectRepository.save(project);

        // 해당되는 전체 이미지 삭제
        fileRepository.deleteByProjectId(project.getId());

        ProjectRequestDto newProjectRequestDto = createProjectRequestDto("5", "4", "3", "2", "1");

        List<MultipartFile> newImages = new ArrayList<>();
        MockMultipartFile imageFile3 = new MockMultipartFile("image3", "test3.jpg", "image/jpeg", "Test Image".getBytes());
        MockMultipartFile imageFile4 = new MockMultipartFile("image4", "test4.jpg", "image/jpeg", "Test Image".getBytes());
        newImages.add(imageFile3);
        newImages.add(imageFile4);


        project.setImageFile(s3Service.fileFactory(newImages, project));
        project.updateProject(newProjectRequestDto);

        // when // then
        assertThatThrownBy(() -> projectService.updateProject(1L, newProjectRequestDto, newImages, user2))
                .isInstanceOf(GlobalException.class)
                .hasMessage("권한이 없습니다.");
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