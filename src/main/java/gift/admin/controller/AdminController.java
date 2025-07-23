package gift.admin.controller;

import gift.item.dto.CreateItemDto;
import gift.item.dto.ItemDto;
import gift.item.dto.OptionRequestDto;
import gift.item.dto.UpdateItemDto;
import gift.item.service.ItemService;
import gift.item.service.OptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    private final ItemService itemService;
    private final OptionService optionService;

    public AdminController(ItemService itemService, OptionService optionService) {
        this.itemService = itemService;
        this.optionService = optionService;
    }

    //상품 전체 목록 조회 페이지
    @GetMapping("/admin/products")
    public String getAllProducts(
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            Model model) {
        Page<ItemDto> page = itemService.findAllItems(pageable);
        // 현재 페이지 상품 리스트
        model.addAttribute("products", page.getContent());
        // 페이지 정보 전체 전달
        model.addAttribute("page", page);
        return "list";
    }

    //특정 상품 조회 (단건 조회) 페이지
    @GetMapping("/admin/products/{id}")
    public String getProductById(@PathVariable Long id, Model model) {
        model.addAttribute("product", itemService.findItem(id));
        model.addAttribute("options", optionService.findOptionsByItemId(id));
        return "detail";
    }

    //상품 등록 페이지
    @GetMapping("/admin/products/create")
    public String createProduct(Model model) {
        model.addAttribute("product", new CreateItemDto("", 0, ""));
        return "create";
    }

    //상품 등록하기 기능
    @PostMapping("/admin/products/create")
    public String createProduct(CreateItemDto dto) {
        itemService.saveItem(dto);
        return "redirect:/admin/products";
    }

    //상품 삭제하기 기능
    @DeleteMapping("/admin/products/{id}")
    public String deleteProduct(@PathVariable Long id) {
        itemService.deleteItem(id);
        return "redirect:/admin/products";
    }
    
    //상품 수정 페이지
    @GetMapping("/admin/products/edit/{id}")
    public String updateProduct(@PathVariable Long id, Model model) {
        model.addAttribute("product", itemService.findItem(id));
        return "update";
    }

    //상품 수정 기능
    @PutMapping("/admin/products/edit/{id}")
    public String updateProduct(@PathVariable Long id, UpdateItemDto dto) {
        itemService.updateItem(id, dto);
        return "redirect:/admin/products";
    }

    //옵션 추가 기능
    @PostMapping("/admin/products/{productId}/options/create")
    public String createOption(
            @PathVariable Long productId,
            @ModelAttribute OptionRequestDto optionRequestDto
    ) {
        optionService.addOption(productId, optionRequestDto);
        return "redirect:/admin/products/" + productId;
    }

    //옵션 추가하는 페이지
    @GetMapping("/admin/products/{productId}/options/create")
    public String showCreateOptionForm(@PathVariable Long productId, Model model) {
        model.addAttribute("productId", productId);
        model.addAttribute("optionRequestDto", new OptionRequestDto("", 1));
        return "option-create";
    }

}
