package shop.chobitok.modnyi.controller;

import org.springframework.web.bind.annotation.*;
import shop.chobitok.modnyi.entity.VariantType;
import shop.chobitok.modnyi.entity.Variants;
import shop.chobitok.modnyi.service.VariantsService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/variants")
public class VariantsController {

    private VariantsService variantsService;

    public VariantsController(VariantsService variantsService) {
        this.variantsService = variantsService;
    }

    @GetMapping("getByType")
    public List<Variants> getVariantsByType(@RequestParam VariantType variantType) {
        return variantsService.getByType(variantType);
    }
}
