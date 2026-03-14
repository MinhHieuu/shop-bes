package com.beeshop.sd44.repository;

import com.beeshop.sd44.dto.response.ProductSale;
import com.beeshop.sd44.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface OrderRepo extends JpaRepository<Order, UUID> {
	List<Order> findByUserId(UUID userId);
	List<Order> findByCustomerId(UUID customerId);
	Optional<Order> findByCustomerIdAndId(UUID customerId, UUID orderId);

	List<Order> findAllByOrderByCreatedAtDesc();

	@Query("SELECT o FROM Order o WHERE " +
	       "(:status IS NULL OR o.status = :status) AND " +
	       "(:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus) AND " +
	       "(:type IS NULL OR o.type = :type) AND " +
	       "(:paymentMethod IS NULL OR o.paymentMethod = :paymentMethod) AND " +
	       "(:fromDate IS NULL OR o.createdAt >= :fromDate) AND " +
	       "(:toDate IS NULL OR o.createdAt <= :toDate) " +
	       "ORDER BY o.createdAt DESC")
	List<Order> findOrdersByFilter(
		@Param("status") Integer status,
		@Param("paymentStatus") Integer paymentStatus,
		@Param("type") Integer type,
		@Param("paymentMethod") String paymentMethod,
		@Param("fromDate") Date fromDate,
		@Param("toDate") Date toDate
	);

	@Query(value = """
		Select BIN_TO_UUID(sp.id) as id, sp.ten, SUM(hdt.so_luong ) as tong
		  FROM san_pham sp
		  JOIN san_pham_chi_tiet st ON st.san_pham_id = sp.id
		  JOIN hoa_don_chi_tiet hdt ON hdt.san_pham_chi_tiet_id  = st.id 
		  Where sp.trang_thai = 1 AND  (:productId IS NULL OR sp.id = UUID_TO_BIN(:productId))
		  GROUP BY sp.id, sp.ten
		  ORDER BY tong desc
		  LIMIT 5
""", nativeQuery = true)
	List<ProductSale> getListSaler(String productId);

	@Query(value = """
		Select BIN_TO_UUID(st.id ) as Id, st.ten, SUM(hdt.so_luong ) as tong
         		  FROM san_pham sp
         		  JOIN san_pham_chi_tiet st ON st.san_pham_id = sp.id
         		  JOIN hoa_don_chi_tiet hdt ON hdt.san_pham_chi_tiet_id  = st.id
         		  Where sp.trang_thai = 1 AND (:productId IS NULL OR sp.id = UUID_TO_BIN(:productId))
         		  GROUP BY st.id, st.ten
         		  ORDER BY tong desc
         		  LIMIT 5
""", nativeQuery = true)
	List<ProductSale> getListSalerByProductId(String productId);
}
