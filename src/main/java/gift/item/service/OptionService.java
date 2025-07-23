package gift.item.service;

import gift.global.exception.CustomException;
import gift.global.exception.ErrorCode;
import gift.item.dto.OptionRequestDto;
import gift.item.dto.OptionResponseDto;
import gift.item.entity.Item;
import gift.item.entity.Option;
import gift.item.repository.ItemRepository;
import gift.item.repository.OptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OptionService {
    private final OptionRepository optionRepository;
    private final ItemRepository itemRepository;

    public OptionService(OptionRepository optionRepository, ItemRepository itemRepository) {
        this.optionRepository = optionRepository;
        this.itemRepository = itemRepository;
    }

    //ItemId로 Options들 가져오는거
    @Transactional(readOnly = true)
    public List<OptionResponseDto> findOptionsByItemId(Long productId) {
        List<Option> options = optionRepository.findByItemId(productId);
        return options.stream()
                .map(OptionResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    //동일한 상품 내의 중복된 옵션 이름 검사
    public void validateDuplicateOptionName(Long productId, String optionName) {
        boolean exists = optionRepository.existsByItemIdAndName(productId, optionName);
        if (exists) {
            throw new CustomException(ErrorCode.OPTION_NAME_DUPLICATE);
        }
    }

    @Transactional
    public void addOption(Long productId, OptionRequestDto dto) {
        validateDuplicateOptionName(productId, dto.name());

        Item item = itemRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        Option option = new Option(dto.name(), dto.quantity(), item);
        optionRepository.save(option);
    }
}
