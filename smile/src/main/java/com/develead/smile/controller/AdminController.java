package com.develead.smile.controller;
import com.develead.smile.domain.Customer;
import com.develead.smile.domain.ServiceItem;
import com.develead.smile.service.CustomerService;
import com.develead.smile.service.ServiceItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller @RequestMapping("/admin") @RequiredArgsConstructor
public class AdminController {
    private final CustomerService customerService;
    private final ServiceItemService serviceItemService;

    @GetMapping public String adminHome() { return "admin/dashboard"; }

    // Customer CRUD
    // ... (고객 CRUD 로직은 이전과 동일)

    // Service Item CRUD
    @GetMapping("/service-items")
    public String listServiceItems(Model model) {
        List<ServiceItem> serviceItems = serviceItemService.findAll();
        List<ServiceItem> activeItems = serviceItems.stream()
                .filter(item -> item.getDeleted() == 'N')  // char 비교
                .collect(Collectors.toList());

        // 디버그용 로그 추가
        System.out.println("Active items count: " + activeItems.size());
        //activeItems.forEach(item ->
                //System.out.println("Item: " + item.getServiceCode() + " - " + item.getServiceName())
        //);

        model.addAttribute("serviceItems", activeItems);
        return "admin/service-items";
    }

    @GetMapping("/service-items/new")
    public String showNewServiceItemForm(Model model) {
        model.addAttribute("serviceItem", new ServiceItem());
        return "admin/service-item-form";
    }

    @PostMapping("/service-items")
    public String createServiceItem(@Valid @ModelAttribute ServiceItem serviceItem, BindingResult result, RedirectAttributes attrs) {
        if (result.hasErrors()) {
            return "admin/service-item-form";
        }
        serviceItemService.create(serviceItem);
        attrs.addFlashAttribute("successMessage", "진료 항목이 성공적으로 등록되었습니다.");
        return "redirect:/admin/service-items";
    }

    @GetMapping("/service-items/edit/{id}")
    public String showEditServiceItemForm(@PathVariable Integer id, Model model) {
        ServiceItem item = serviceItemService.findById(id).orElseThrow();

        System.out.println(item);

        model.addAttribute("serviceItem", item);
        return "admin/service-item-form";
    }

    @PostMapping("/service-items/{id}")
    public String updateServiceItem(@PathVariable Integer id, @Valid @ModelAttribute ServiceItem serviceItem, BindingResult result, RedirectAttributes attrs) {
        if (result.hasErrors()) {
            // id를 모델에 다시 추가해야 폼 액션이 올바르게 생성됩니다.
            serviceItem.setService_item_id(id);
            return "admin/service-item-form";
        }
        serviceItem.setService_item_id(id);
        serviceItemService.update(serviceItem);
        attrs.addFlashAttribute("successMessage", "진료 항목이 성공적으로 수정되었습니다.");
        return "redirect:/admin/service-items";
    }
}
