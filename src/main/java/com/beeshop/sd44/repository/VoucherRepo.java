package com.beeshop.sd44.repository;

import com.beeshop.sd44.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VoucherRepo extends JpaRepository<Voucher, UUID> {
}
