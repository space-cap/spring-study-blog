package com.develead.smile.controller;
import com.develead.smile.domain.*;
import com.develead.smile.dto.BillingDto;
import com.develead.smile.dto.MedicalRecordDto;
import com.develead.smile.dto.MedicalRecordServiceDto;
import com.develead.smile.repository.CustomerRepository;
import com.develead.smile.repository.DoctorRepository;
import com.develead.smile.repository.ServiceItemRepository;
import com.develead.smile.service.*;
import com.develead.smile.service.MedicalRecordService;
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
    private final InventoryService inventoryService;
    private final MedicalRecordService medicalRecordService;
    private final BillingService billingService; // 추가
    private final CustomerRepository customerRepository; // DTO 채우기용
    private final DoctorRepository doctorRepository; // DTO 채우기용
    private final ServiceItemRepository serviceItemRepository;

    @GetMapping public String adminHome() { return "admin/dashboard"; }

    // Customer CRUD
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

    // Inventory Item CRUD (신규 추가)
    @GetMapping("/inventory-items")
    public String listInventoryItems(Model model) {
        model.addAttribute("inventoryItems", inventoryService.findAll());
        return "admin/inventory-items";
    }

    @GetMapping("/inventory-items/new")
    public String showNewInventoryItemForm(Model model) {
        model.addAttribute("inventoryItem", new InventoryItem());
        return "admin/inventory-item-form";
    }

    @PostMapping("/inventory-items")
    public String createInventoryItem(@Valid @ModelAttribute InventoryItem inventoryItem, BindingResult result, RedirectAttributes attrs) {
        if (result.hasErrors()) {
            return "admin/inventory-item-form";
        }
        inventoryService.save(inventoryItem);
        attrs.addFlashAttribute("successMessage", "재고 항목이 성공적으로 등록되었습니다.");
        return "redirect:/admin/inventory-items";
    }

    @GetMapping("/inventory-items/edit/{id}")
    public String showEditInventoryItemForm(@PathVariable Integer id, Model model) {
        InventoryItem item = inventoryService.findById(id).orElseThrow();
        model.addAttribute("inventoryItem", item);
        return "admin/inventory-item-form";
    }

    @PostMapping("/inventory-items/{id}")
    public String updateInventoryItem(@PathVariable Integer id, @Valid @ModelAttribute InventoryItem inventoryItem, BindingResult result, RedirectAttributes attrs) {
        if (result.hasErrors()) {
            inventoryItem.setItem_id(id);
            return "admin/inventory-item-form";
        }
        inventoryItem.setItem_id(id);
        inventoryService.save(inventoryItem);
        attrs.addFlashAttribute("successMessage", "재고 항목이 성공적으로 수정되었습니다.");
        return "redirect:/admin/inventory-items";
    }

    // Medical Record CRUD (신규 추가)
    @GetMapping("/medical-records")
    public String listMedicalRecords(Model model) {
        model.addAttribute("medicalRecords", medicalRecordService.findAll());
        return "admin/medical-records";
    }

    @GetMapping("/medical-records/new")
    public String showNewMedicalRecordForm(Model model) {
        model.addAttribute("medicalRecordDto", new MedicalRecordDto());
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("serviceItems", serviceItemRepository.findAll());
        return "admin/medical-record-form";
    }

    @PostMapping("/medical-records")
    public String createMedicalRecord(@ModelAttribute MedicalRecordDto dto, RedirectAttributes attrs) {
        medicalRecordService.save(dto);
        attrs.addFlashAttribute("successMessage", "진료 기록이 성공적으로 등록되었습니다.");
        return "redirect:/admin/medical-records";
    }

    @GetMapping("/medical-records/edit/{id}")
    public String showEditMedicalRecordForm(@PathVariable Integer id, Model model) {
        MedicalRecord record = medicalRecordService.findById(id).orElseThrow();

        // [수정] Entity to DTO 변환 로직 보강
        MedicalRecordDto dto = new MedicalRecordDto();
        dto.setRecord_id(record.getRecord_id());
        dto.setAppointmentId(record.getAppointment().getAppointment_id());
        dto.setCustomerId(record.getCustomer().getCustomer_id());
        dto.setDoctorId(record.getDoctor().getDoctor_id());
        dto.setTreatmentDate(record.getTreatmentDate());
        dto.setSymptoms(record.getSymptoms());

        List<MedicalRecordServiceDto> serviceDtos = record.getServices().stream()
                .map(service -> {
                    MedicalRecordServiceDto serviceDto = new MedicalRecordServiceDto();
                    serviceDto.setServiceItemId(service.getServiceItem().getService_item_id());
                    serviceDto.setServiceName(service.getServiceItem().getServiceName());
                    serviceDto.setQuantity(service.getQuantity());
                    serviceDto.setCostAtService(service.getCostAtService());
                    return serviceDto;
                }).collect(Collectors.toList());

        System.out.println("serviceDtos: " + serviceDtos.toArray().length);

        dto.setServices(serviceDtos);

        model.addAttribute("medicalRecordDto", dto);
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("doctors", doctorRepository.findAll());
        model.addAttribute("serviceItems", serviceItemRepository.findAll());
        return "admin/medical-record-form";
    }

    @PostMapping("/medical-records/{id}")
    public String updateMedicalRecord(@PathVariable Integer id, @ModelAttribute MedicalRecordDto dto, RedirectAttributes attrs) {
        dto.setRecord_id(id);

        System.out.println("dto: " + dto);

        medicalRecordService.save(dto);
        attrs.addFlashAttribute("successMessage", "진료 기록이 성공적으로 수정되었습니다.");
        return "redirect:/admin/medical-records";
    }


    // Billing CRUD (신규 추가)
    @GetMapping("/billings")
    public String listBillings(Model model) {
        model.addAttribute("billings", billingService.findAll());
        return "admin/billings";
    }

    @GetMapping("/billings/edit/{id}")
    public String showEditBillingForm(@PathVariable Integer id, Model model) {
        Billing billing = billingService.findById(id).orElseThrow();
        // Entity to DTO
        BillingDto dto = new BillingDto();
        dto.setBilling_id(billing.getBilling_id());
        dto.setMedicalRecordId(billing.getMedicalRecord().getRecord_id());
        dto.setCustomerName(billing.getMedicalRecord().getCustomer().getName());
        dto.setTotalAmount(billing.getTotalAmount());
        dto.setAmountPaid(billing.getAmountPaid());
        dto.setPaymentMethod(billing.getPaymentMethod());
        dto.setPaymentStatus(billing.getPaymentStatus());

        model.addAttribute("billingDto", dto);
        return "admin/billing-form";
    }

    @PostMapping("/billings/{id}")
    public String updateBilling(@PathVariable Integer id, @ModelAttribute BillingDto dto, RedirectAttributes attrs) {
        dto.setBilling_id(id);
        billingService.updateBilling(dto);
        attrs.addFlashAttribute("successMessage", "수납 정보가 성공적으로 수정되었습니다.");
        return "redirect:/admin/billings";
    }

}
