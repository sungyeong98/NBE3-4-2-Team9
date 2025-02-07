package com.backend.domain.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.backend.domain.post.dto.PostCreateRequestDto;
import com.backend.domain.post.entity.Post;
import com.backend.domain.post.repository.PostRepository;
import com.backend.domain.user.entity.SiteUser;
import com.backend.domain.user.repository.UserRepository;
import com.backend.global.security.custom.CustomUserDetails;
import com.backend.standard.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {"/sql/init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {"/sql/delete.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PostRepository postRepository;

    @Value("${jwt.token.access-expiration}")
    private long ACCESS_EXPIRATION;

    @Test
    @DisplayName("게시글 생성 테스트")
    void testCreatePost() throws Exception {
        //init.sql에 삽입된 유저 데이터 가져오기
        SiteUser siteUser = userRepository.findByEmail("testEmail1@naver.com").get();
        //가져온 유저 데이터로 시큐리티 유저 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(siteUser);
        //액세스 토큰 발급
        String accessToken = jwtUtil.createAccessToken(customUserDetails, ACCESS_EXPIRATION);

        PostCreateRequestDto requestDto = PostCreateRequestDto.builder().subject("새로운 제목")
                .content("새로운 내용").categoryId(1L).build();

        mockMvc.perform(post("/api/v1/posts") // 게시글 생성 API 요청
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken) //헤더에 액세스 토큰 삽입
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.subject").value("새로운 제목"))
                .andExpect(jsonPath("$.data.content").value("새로운 내용")).andDo(print());
    }

    @Test
    @DisplayName("게시글 상세 조회 테스트")
    void testGetPostById() throws Exception {
        //init.sql에 삽입된 유저 데이터 가져오기
        SiteUser siteUser = userRepository.findByEmail("testEmail1@naver.com").get();
        //가져온 유저 데이터로 시큐리티 유저 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(siteUser);
        //액세스 토큰 발급
        String accessToken = jwtUtil.createAccessToken(customUserDetails, ACCESS_EXPIRATION);

        // 게시글 ID 가져오기(ex. 가장 최근 게시글 조회)
        Post post = postRepository.findAll().get(0); // 또는 ID가 작은 순으로 정렬해서 가져올 수도 있음
        Long postId = post.getPostId(); // 실제 DB에 존재하는 ID 사용

        mockMvc.perform(get("/api/v1/posts/{id}", postId).contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)) // Authorization 헤더 포함
                .andExpect(status().isOk()) // 200 응답 확인
                .andExpect(jsonPath("$.data.id").value(postId))
                .andExpect(jsonPath("$.data.subject").isNotEmpty())
                .andExpect(jsonPath("$.data.content").isNotEmpty()).andDo(print());

    }

    @Test
    @DisplayName("게시글 전체 조회 테스트")
    void testGetPosts() throws Exception {
        mockMvc.perform(get("/api/v1/posts")) // JWT 없이 요청
                .andExpect(status().isOk()) // 200 응답 확인
                .andExpect(jsonPath("$.data").exists()) // 응답 데이터가 존재하는지 확인
                .andExpect(jsonPath("$.data.content").isArray()) // content가 배열인지 확인
                .andDo(print());

    }
    @Test
    @DisplayName("게시글 삭제 - 작성자가 삭제 -> 성공")
    void testDeletePost_Success() throws Exception {
        SiteUser siteUser = userRepository.findByEmail("testEmail1@naver.com")
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        CustomUserDetails customUserDetails = new CustomUserDetails(siteUser);
        String accessToken = jwtUtil.createAccessToken(customUserDetails, ACCESS_EXPIRATION);

        Post post = postRepository.findAll().get(0);

        mockMvc.perform(delete("/api/v1/posts/{id}", post.getPostId())
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        // 🔹 삭제 후 존재 여부 확인
        boolean exists = postRepository.existsById(post.getPostId());
        assertThat(exists).isFalse();
    }
    @Test
    @DisplayName("게시글 삭제 - 작성자가 아닌 유저가 삭제 -> 실패")
    void deletePost_Forbidden() throws Exception{
    Post testPost = postRepository.findBySubject("새로운 제목")
            .orElseThrow(() -> new RuntimeException("테스트 게시글을 찾을 수 없습니다."));

    SiteUser otherUser = userRepository.findByEmail("testEmail2@naver.com")
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

    CustomUserDetails otherUserDetails = new CustomUserDetails(otherUser);
    String accessToken = jwtUtil.createAccessToken(otherUserDetails, ACCESS_EXPIRATION);

    mockMvc.perform(delete("/api/v1/posts/{id}", testPost.getPostId())
                    .header("Authorization", "Bearer " + accessToken)) // 다른 유저의 토큰 사용
            .andExpect(status().isForbidden()) // 403 응답이 나와야 함
            .andDo(print());

    }

    @Test
    @DisplayName("게시글 삭제 - 존재하지 않는 게시글 삭제 -> 실패")
    void deletePost_NotFound() throws Exception{
        Long nonExistentPostId = 9999L;

        SiteUser siteUser = userRepository.findByEmail("testEmail1@naver.com")
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        CustomUserDetails customUserDetails = new CustomUserDetails(siteUser);
        String accessToken = jwtUtil.createAccessToken(customUserDetails, ACCESS_EXPIRATION);

        mockMvc.perform(delete("/api/v1/posts/{id}", nonExistentPostId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
