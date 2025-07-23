package gift.item;

import gift.item.entity.Item;
import gift.item.entity.Option;
import gift.item.repository.ItemRepository;
import gift.item.repository.OptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.*;

@DataJpaTest
public class OptionRepositoryDynamicTest {

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ItemRepository itemRepository;

    @TestFactory
    @DisplayName("옵션 목록 조회 및 옵션 존재 여부 확인")
    Stream<DynamicTest> optionTests() {

        Item item = new Item("바지", 1000, "pants.com");
        itemRepository.save(item);

        Option option1 = new Option("레전드", 1, item);
        Option option2 = new Option("유니크", 5, item);
        optionRepository.saveAll(List.of(option1, option2));

        List<String> expectedNames = List.of("레전드", "유니크");

        Stream<DynamicTest> findTests = expectedNames.stream()
                .map(name -> dynamicTest("옵션 목록에 " + name + " 포함 여부", () -> {
                    List<Option> found = optionRepository.findByItemId(item.getId());
                    assertThat(found).extracting("name").contains(name);
                }));

        List<Object[]> existsTestData = List.of(
                new Object[]{item.getId(), "레전드", true},
                new Object[]{item.getId(), "레어", false}
        );

        Stream<DynamicTest> existsTests = existsTestData.stream()
                .map(data -> dynamicTest("옵션 존재 여부 확인: " + data[1], () -> {
                    boolean exists = optionRepository.existsByItemIdAndName((Long)data[0], (String)data[1]);
                    assertThat(exists).isEqualTo((Boolean)data[2]);
                }));

        return Stream.concat(findTests, existsTests);
    }

}
