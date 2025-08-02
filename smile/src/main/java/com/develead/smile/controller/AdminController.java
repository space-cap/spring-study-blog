package com.develead.smile.controller;

import com.develead.smile.domain.Customer;
import com.develead.smile.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CustomerService customerService;

    @GetMapping
    public String adminHome() {
        return "admin/dashboard";
    }

    @GetMapping("/customers")
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.findAllCustomers());
        return "admin/customers";
    }

    @GetMapping("/customers/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "admin/customer-form";
    }

    @PostMapping("/customers")
    public String createCustomer(@ModelAttribute Customer customer) {
        customerService.saveCustomer(customer);
        return "redirect:/admin/customers";
    }

    @GetMapping("/customers/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Customer customer = customerService.findCustomerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer Id:" + id));
        model.addAttribute("customer", customer);
        return "admin/customer-form";
    }

    @PostMapping("/customers/{id}")
    public String updateCustomer(@PathVariable Integer id, @ModelAttribute Customer customer) {
        customer.setCustomer_id(id);
        customerService.saveCustomer(customer);
        return "redirect:/admin/customers";
    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return "redirect:/admin/customers";
    }
}
