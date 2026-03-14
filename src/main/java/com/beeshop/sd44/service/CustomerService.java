package com.beeshop.sd44.service;

import com.beeshop.sd44.entity.Customer;
import com.beeshop.sd44.entity.User;
import com.beeshop.sd44.repository.CustomerRepo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepo customerRepo;

    public CustomerService(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    /**
     * Tạo Customer liên kết với User (dùng khi đăng ký hoặc admin tạo user có role=user)
     */
    public Customer createCustomerForUser(User user) {
        Customer customer = new Customer();
        customer.setTen(user.getName());
        customer.setSdt(user.getPhone());
        customer.setDiaChi(user.getAddress());
        customer.setNgayTao(new Date());
        customer.setUser(user);
        return customerRepo.save(customer);
    }

    /**
     * Lấy Customer theo userId
     */
    public Customer getByUserId(UUID userId) {
        Optional<Customer> customer = customerRepo.findByUserId(userId);
        return customer.orElse(null);
    }

    /**
     * Tìm khách hàng theo SDT, nếu chưa tồn tại thì tạo mới.
     * Dùng khi employee/admin tạo đơn tại quầy với số điện thoại khách.
     */
    public Customer findOrCreateBySdt(String sdt) {
        if (sdt == null || sdt.isBlank()) {
            return null;
        }
        return customerRepo.findBySdt(sdt).orElseGet(() -> {
            Customer customer = new Customer();
            customer.setSdt(sdt);
            customer.setTen("Khách lẻ - " + sdt);
            customer.setNgayTao(new Date());
            return customerRepo.save(customer);
        });
    }
}
