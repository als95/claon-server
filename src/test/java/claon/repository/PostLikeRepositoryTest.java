package claon.repository;

import claon.center.domain.CenterImg;
import claon.center.domain.Charge;
import claon.center.domain.ChargeElement;
import claon.center.domain.OperatingTime;
import claon.config.QueryDslTestConfig;
import claon.user.domain.User;
import claon.user.repository.BlockUserRepository;
import claon.user.repository.UserRepository;
import claon.center.domain.Center;
import claon.center.repository.CenterRepository;
import claon.post.domain.Post;
import claon.post.domain.PostLike;
import claon.post.repository.PostLikeRepository;
import claon.post.repository.PostLikeRepositorySupport;
import claon.post.repository.PostRepository;
import claon.user.domain.BlockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import({QueryDslTestConfig.class, PostLikeRepositorySupport.class})
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class PostLikeRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BlockUserRepository blockUserRepository;
    @Autowired
    private CenterRepository centerRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostLikeRepository postLikeRepository;
    @Autowired
    private PostLikeRepositorySupport postLikeRepositorySupport;

    private User user, blockUser;
    private Post post;

    @BeforeEach
    void setUp() {
        this.user = userRepository.save(User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        ));

        this.blockUser = userRepository.save(User.of(
                "block@gmail.com",
                "1264567890",
                "testBlockNickname",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        ));

        blockUserRepository.save(BlockUser.of(
                this.user,
                this.blockUser
        ));

        Center center = Center.of(
                "test",
                "test",
                "010-1234-1234",
                "https://test.com",
                "https://instagram.com/test",
                "https://youtube.com/channel/test",
                List.of(new CenterImg("img test")),
                List.of(new OperatingTime("매일", "10:00", "23:00")),
                "facilities test",
                List.of(new Charge(List.of(new ChargeElement("자유 패키지", "330,000")), "charge image")),
                "hold info img test"
        );
        centerRepository.save(center);

        this.post = postRepository.save(Post.of(
                center,
                "testContent1",
                user,
                List.of(),
                List.of()
        ));

        postLikeRepository.save(PostLike.of(
                this.user, this.post
        ));
    }

    @Test
    public void successFindByLikerAndPost() {
        // when
        Optional<PostLike> postLike = postLikeRepository.findByLikerAndPost(this.user, this.post);

        // then
        assertThat(postLike).isPresent();
    }

    @Test
    public void successCountByPost() {
        // when
        Integer countPost = postLikeRepository.countByPost(this.post);

        // then
        assertThat(countPost).isEqualTo(1);
    }

    @Test
    public void successCountByPostIdIn() {
        // given
        List<String> postIds = List.of(post.getId());

        // when
        Long count = postLikeRepository.countByPostIdIn(postIds);

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    public void successFindAllByPost() {
        // given
        String postId = this.post.getId();
        String userId = this.user.getId();

        postLikeRepository.save(PostLike.of(
                this.blockUser, this.post
        ));

        // when
        Page<PostLike> likerList = postLikeRepositorySupport.findAllByPost(postId, userId, PageRequest.of(0, 2));

        // then
        assertThat(likerList.getContent().size()).isEqualTo(1);
    }
}