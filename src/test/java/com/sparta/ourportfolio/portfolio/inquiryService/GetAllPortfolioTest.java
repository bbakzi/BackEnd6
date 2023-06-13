package com.sparta.ourportfolio.portfolio.inquiryService;

import com.sparta.ourportfolio.common.dto.ResponseDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioRequestDto;
import com.sparta.ourportfolio.portfolio.dto.PortfolioResponseDto;
import com.sparta.ourportfolio.portfolio.entity.Portfolio;
import com.sparta.ourportfolio.portfolio.repository.PortfolioRepository;
import com.sparta.ourportfolio.portfolio.service.PortfolioInquiryService;
import com.sparta.ourportfolio.user.entity.User;
import com.sparta.ourportfolio.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class GetAllPortfolioTest {

    @Autowired
    private PortfolioRepository portfolioRepository;
    @Autowired
    private PortfolioInquiryService portfolioInquiryService;
    @Autowired
    private UserRepository userRepository;

    @DisplayName("카테고리와 필터를 지정하지 않으면 모든 포트폴리오를 조회한다.")
    @Test
    void getPortfolio() {
        //given
        User testUser = createUser(1L, "test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto4 = createPortfolioRequestDto("title1", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Frontend", "Backend",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto5 = createPortfolioRequestDto("title2", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Graphic", "Graphic",
                projectIdList
        );
        String imageUrl = "";

        Portfolio portfolio4 = createPortfolio(4L, portfolioRequestDto4, imageUrl, testUser);
        Portfolio portfolio5 = createPortfolio(5L, portfolioRequestDto5, imageUrl, testUser);
        portfolioRepository.save(portfolio4);
        portfolioRepository.save(portfolio5);

        //when
        ResponseDto<Slice<PortfolioResponseDto>> result = portfolioInquiryService.getAllPortfolios(
                5L, 9, "", "");

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "조회 완료");

        Slice<PortfolioResponseDto> responseData = result.getData();
        List<PortfolioResponseDto> portfolioResults = responseData.getContent();
        assertThat(portfolioResults).hasSize(5);
    }

    @DisplayName("카테고리와 필터를 지정하면 해당하는 포트폴리오를 조회한다.")
    @Test
    void getPortfolioWithCategoryAndFilter() {
        //given
        User testUser = createUser(1L, "test@gmail.com", "test-password", "test", false);
        userRepository.save(testUser);
        List<Long> projectIdList = new ArrayList<>();
        PortfolioRequestDto portfolioRequestDto1 = createPortfolioRequestDto("title1", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Develop", "Backend", "Backend",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto2 = createPortfolioRequestDto("title2", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Design", "Graphic", "Graphic",
                projectIdList
        );
        PortfolioRequestDto portfolioRequestDto3 = createPortfolioRequestDto("title2", "intro",
                "techStack", "residence", "location", "010********",
                "test@email.com", "coze", "Photographer", "Wedding", "Graphic",
                projectIdList
        );
        String imageUrl = "";

        Portfolio portfolio1 = createPortfolio(1L, portfolioRequestDto1, imageUrl, testUser);
        Portfolio portfolio2 = createPortfolio(2L, portfolioRequestDto2, imageUrl, testUser);
        Portfolio portfolio3 = createPortfolio(3L, portfolioRequestDto3, imageUrl, testUser);
        portfolioRepository.save(portfolio1);
        portfolioRepository.save(portfolio2);
        portfolioRepository.save(portfolio3);

        //when
        ResponseDto<Slice<PortfolioResponseDto>> result = portfolioInquiryService.getAllPortfolios(
                3L, 9, "Develop", "Backend");

        //then
        assertThat(result)
                .extracting("statusCode", "message")
                .contains(HttpStatus.OK, "조회 완료");

        Slice<PortfolioResponseDto> responseData = result.getData();
        List<PortfolioResponseDto> portfolioResults = responseData.getContent();
        assertThat(portfolioResults).hasSize(1);

        PortfolioResponseDto expectedPortfolio = portfolioResults.get(0);
        assertThat(expectedPortfolio.getId()).isEqualTo(1L);
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

    private PortfolioRequestDto createPortfolioRequestDto(String portfolioTitle, String intro, String techStack,
                                                          String residence, String location, String telephone,
                                                          String githubId, String blogUrl, String category,
                                                          String filter, String youtubeUrl, List<Long> projectIdList) {
        return PortfolioRequestDto.builder()
                .portfolioTitle(portfolioTitle)
                .intro(intro)
                .techStack(techStack)
                .residence(residence)
                .location(location)
                .telephone(telephone)
                .githubId(githubId)
                .blogUrl(blogUrl)
                .category(category)
                .filter(filter)
                .youtubeUrl(youtubeUrl)
                .projectIdList(projectIdList)
                .build();
    }

    private Portfolio createPortfolio(Long id, PortfolioRequestDto portfolioRequestDto, String image, User user) {
        return Portfolio.builder()
                .id(id)
                .portfolioTitle(portfolioRequestDto.getPortfolioTitle())
                .intro(portfolioRequestDto.getIntro())
                .techStack(portfolioRequestDto.getTechStack())
                .residence(portfolioRequestDto.getResidence())
                .location(portfolioRequestDto.getLocation())
                .telephone(portfolioRequestDto.getTelephone())
                .githubId(portfolioRequestDto.getGithubId())
                .blogUrl(portfolioRequestDto.getBlogUrl())
                .category(portfolioRequestDto.getCategory())
                .filter(portfolioRequestDto.getFilter())
                .youtubeUrl(portfolioRequestDto.getYoutubeUrl())
                .portfolioImage(image)
                .user(user)
                .build();
    }

}