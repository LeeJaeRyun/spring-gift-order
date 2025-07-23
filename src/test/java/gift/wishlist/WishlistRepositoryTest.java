package gift.wishlist;

import gift.item.entity.Item;
import gift.item.repository.ItemRepository;
import gift.member.entity.Member;
import gift.member.repository.MemberRepository;
import gift.wishlist.entity.Wishlist;
import gift.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class WishlistRepositoryTest {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("멤버와 아이템으로 위시리스트 존재 여부 확인")
    void existsByMemberAndItem() {
        // given
        Member member = memberRepository.save(new Member("testuser@example.com", "password1234"));
        Item item = itemRepository.save(new Item("testItem", 1000, "testUrl"));
        wishlistRepository.save(new Wishlist(member, item));

        // when
        boolean exists = wishlistRepository.existsByMemberAndItem(member, item);

        // then
        assertThat(exists).isTrue();

        // then2 위시리스트 없는 경우
        Member otherMember = memberRepository.save(new Member("otheruser@example.com", "password456"));
        boolean notExists = wishlistRepository.existsByMemberAndItem(otherMember, item);
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("한 멤버의 위시리스트를 한 번에 다 가져오는 조회 테스트")
    void findByMember() {
        // given
        Member member = memberRepository.save(new Member("findmember@example.com", "password789"));
        Item item1 = itemRepository.save(new Item("item1", 500, "url1"));
        Item item2 = itemRepository.save(new Item("item2", 1500, "url2"));

        Wishlist wishlist1 = new Wishlist(member, item1);
        Wishlist wishlist2 = new Wishlist(member, item2);

        wishlistRepository.save(wishlist1);
        wishlistRepository.save(wishlist2);

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Wishlist> wishlistList = wishlistRepository.findByMember(member, pageable);

        // then
        assertThat(wishlistList).hasSize(2)
                .extracting(w -> w.getItem().getName())
                .containsExactlyInAnyOrder("item1", "item2");
    }

    @Test
    @DisplayName("한 멤버의 위시리스트를 페이지네이션해서 조회하는 테스트")
    void findByMemberWithPagination() {
        // given
        Member member = memberRepository.save(new Member("pageablemember@example.com", "password123"));
        Item item1 = itemRepository.save(new Item("item1", 500, "url1"));
        Item item2 = itemRepository.save(new Item("item2", 1500, "url2"));

        Wishlist wishlist1 = new Wishlist(member, item1);
        Wishlist wishlist2 = new Wishlist(member, item2);

        wishlistRepository.save(wishlist1);
        wishlistRepository.save(wishlist2);

        // 페이지 크기 1, 첫 번째 페이지 요청
        Pageable firstPageable = PageRequest.of(0, 1);

        // when
        Page<Wishlist> firstPage = wishlistRepository.findByMember(member, firstPageable);

        // then
        // 첫 페이지에 포함된 데이터 개수가 1개인지 확인 (페이지 사이즈가 1이기 때문)
        assertThat(firstPage.getContent()).hasSize(1);
        // 전체 위시리스트 데이터 개수가 2개인지 확인
        assertThat(firstPage.getTotalElements()).isEqualTo(2);
        // 총 페이지 수가 2인지 확인 (데이터 2개, 페이지당 1개씩이므로)
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        // 첫 페이지에 나온 아이템 이름이 "item1" 또는 "item2" 중 하나인지 확인
        assertThat(firstPage.getContent().get(0).getItem().getName())
                .isIn("item1", "item2");

        // 페이지 크기 1, 두 번째 페이지 요청
        Pageable secondPageable = PageRequest.of(1, 1);

        // when
        Page<Wishlist> secondPage = wishlistRepository.findByMember(member, secondPageable);

        // then
        assertThat(secondPage.getContent()).hasSize(1);
        assertThat(secondPage.getTotalElements()).isEqualTo(2);
        assertThat(secondPage.getTotalPages()).isEqualTo(2);
        assertThat(secondPage.getContent().get(0).getItem().getName())
                .isIn("item1", "item2");

        // 첫 번째 페이지와 두 번째 페이지에 나온 모든 아이템 이름을 합침
        List<String> allItems = Stream.concat(
                        firstPage.getContent().stream(),
                        secondPage.getContent().stream()
                )
                .map(w -> w.getItem().getName())
                .collect(Collectors.toList());

        assertThat(allItems).containsExactlyInAnyOrder("item1", "item2");
    }



    @Test
    @DisplayName("멤버와 아이템으로 위시리스트 삭제")
    void deleteByMemberAndItem() {
        // given
        Member member = new Member("test@example.com", "password123");
        memberRepository.save(member);

        Item item = new Item("TestItem", 1000, "http://image.com/test.jpg");
        itemRepository.save(item);

        Wishlist wishlist = new Wishlist(member, item);
        wishlistRepository.save(wishlist);

        // when
        wishlistRepository.deleteByMemberAndItem(member, item);

        // then
        boolean exists = wishlistRepository.existsByMemberAndItem(member, item);
        assertThat(exists).isFalse();
    }

}
