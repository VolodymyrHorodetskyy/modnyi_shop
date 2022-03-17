package shop.chobitok.modnyi.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.VariantType;
import shop.chobitok.modnyi.entity.Variants;
import shop.chobitok.modnyi.service.VariantsService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/variants")
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('EMPLOYEE')")
public class VariantsController {

    private final VariantsService variantsService;

    public VariantsController(VariantsService variantsService) {
        this.variantsService = variantsService;
    }

    @GetMapping("getByType")
    public List<Variants> getVariantsByType(@RequestParam VariantType variantType) {
        return variantsService.getByType(variantType);
    }
}
