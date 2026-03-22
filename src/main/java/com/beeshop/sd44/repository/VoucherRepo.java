package com.beeshop.sd44.repository;

import com.beeshop.sd44.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VoucherRepo extends JpaRepository<Voucher, UUID> {

    Optional<Voucher> findByMa(String ma);

    boolean existsByMa(String ma);

    boolean existsByMaAndIdNot(String ma, UUID id);

    @Query("""
        SELECT v FROM Voucher v
        WHERE (:keyword IS NULL OR LOWER(v.ma) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(v.ten) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:trangThai IS NULL OR v.trangThai = :trangThai)
        ORDER BY v.ngayBatDau DESC, v.ngayKetThuc DESC
    """)
    List<Voucher> searchVouchers(@Param("keyword") String keyword,
                                 @Param("trangThai") Integer trangThai);
}