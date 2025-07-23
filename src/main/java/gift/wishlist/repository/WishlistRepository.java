package gift.wishlist.repository;

import gift.member.entity.Member;
import gift.item.entity.Item;
import gift.wishlist.entity.Wishlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    Page<Wishlist> findByMember(Member member, Pageable pageable);

    boolean existsByMemberAndItem(Member member, Item item);

    void deleteByMemberAndItem(Member member, Item item);
}