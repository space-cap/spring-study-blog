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

@Controller @RequestMapping("/admin") @RequiredArgsConstructor
public class AdminController {
    private final CustomerService customerService;
    private final ServiceItemService serviceItemService;

    @GetMapping public String adminHome() { return "admin/dashboard"; }

    // Customer CRUD
    @GetMapping("/customers")
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.findAllCustomers());
        return "admin/customers";
    }
    // ... (고객 CRUD 로직은 이전과 동일)

    // Service Item CRUD (신규 추가)
    @GetMapping("/service-items")
    public String listServiceItems(Model model) {
        model.addAttribute("serviceItems", serviceItemService.findAll());
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
        serviceItemService.save(serviceItem);
        attrs.addFlashAttribute("successMessage", "진료 항목이 성공적으로 등록되었습니다.");
        return "redirect:/admin/service-items";
    }

    @GetMapping("/service-items/edit/{id}")
    public String showEditServiceItemForm(@PathVariable Integer id, Model model) {
        ServiceItem item = serviceItemService.findById(id).orElseThrow();
        model.addAttribute("serviceItem", item);
        return "admin/service-item-form";
    }

    @PostMapping("/service-items/{id}")
    public String updateServiceItem(@PathVariable Integer id, @Valid @ModelAttribute ServiceItem serviceItem, BindingResult result, RedirectAttributes attrs) {
        if (result.hasErrors()) {
            return "admin/service-item-form";
        }
        serviceItem.setService_item_id(id);
        serviceItemService.save(serviceItem);
        attrs.addFlashAttribute("successMessage", "진료 항목이 성공적으로 수정되었습니다.");
        return "redirect:/admin/service-items";
    }
}
