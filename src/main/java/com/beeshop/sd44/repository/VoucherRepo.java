package com.beeshop.sd44.repository;

import com.beeshop.sd44.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VoucherRepo extends JpaRepository<Voucher, UUID> {

    Optional<Voucher> findByMa(String ma);

    @Query("SELECT v FROM Voucher v WHERE " +
           "(:trangThai IS NULL OR v.trangThai = :trangThai) " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(v.ma) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.ten) LIKE LOWER(CONCAT('%', :keyword, '%')))" +
            "AND v.ngayKetThuc >= now()"
    )
    List<Voucher> searchVouchers(@Param("keyword") String keyword, @Param("trangThai") Integer trangThai);
}
