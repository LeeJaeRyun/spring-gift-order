package gift.item.repository;

import gift.item.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByItemId(Long itemId);

    boolean existsByItemIdAndName(Long productId, String optionName);
}
