package shop.chobitok.modnyi.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.chobitok.modnyi.entity.NpAccount;
import shop.chobitok.modnyi.service.PropsService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/npAccount")
public class NpAccountController {

    private PropsService propsService;

    public NpAccountController(PropsService propsService) {
        this.propsService = propsService;
    }

    @GetMapping
    public List<NpAccount> getAll() {
        return propsService.getAll();
    }

}
