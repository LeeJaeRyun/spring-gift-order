package gift.item.controller;

import gift.item.dto.*;
import gift.item.service.ItemService;
import gift.item.service.OptionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ItemController {

    private final ItemService itemService;
    private final OptionService optionService;

    public ItemController(ItemService itemService, OptionService optionService) {
        this.itemService = itemService;
        this.optionService = optionService;
    }

    //상품 생성
    @PostMapping
    public ResponseEntity<ItemDto> addItem(
            @RequestBody @Valid CreateItemDto dto
    ) {
        ItemDto itemDto = itemService.saveItem(dto);
        return ResponseEntity.ok(itemDto);
    }

    //상품 전체 조회 (페이지네이션 적용)
    @GetMapping
    public ResponseEntity<Page<ItemDto>> findAllItems(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        Page<ItemDto> items = itemService.findAllItems(pageable);
        return ResponseEntity.ok(items);
    }

    //특정 상품 조회
    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> findItem(
            @PathVariable("id") Long id
    ) {
        ItemDto item = itemService.findItem(id);
        return ResponseEntity.ok(item);
    }

    //상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable("id") Long id
    ) {
        itemService.deleteItem(id);
        return ResponseEntity.ok().build();
    }

    //상품 수정
    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateItemDto dto
    ) {
        itemService.updateItem(id, dto);
        return ResponseEntity.noContent().build();
    }

    //상품 옵션 조회
    @PostMapping("{productId}/options")
    public ResponseEntity<List<OptionResponseDto>> getItemOptions(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(optionService.findOptionsByItemId(productId));
    }



}
