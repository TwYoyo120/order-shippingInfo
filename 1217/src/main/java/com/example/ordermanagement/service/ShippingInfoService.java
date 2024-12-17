package com.example.ordermanagement.service;

import com.example.ordermanagement.model.Order;
import com.example.ordermanagement.model.ShippingInfo;
import com.example.ordermanagement.repository.ShippingInfoRepository;
import com.example.ordermanagement.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ShippingInfoService {

	@Autowired
	private ShippingInfoRepository shippingInfoRepository;

	@Autowired
	private OrderRepository orderRepository;

	// 更新物流信息，支援圖片上傳 (圖片存儲到資料庫)
	public boolean updateShippingInfo(Long shippingInfoId, ShippingInfo updatedInfo, MultipartFile imageFile) {
		return shippingInfoRepository.findById(shippingInfoId).map(existingInfo -> {
			// 更新基本信息
			existingInfo.setShippingInfoRecipient(updatedInfo.getShippingInfoRecipient());
			existingInfo.setShippingInfoAddress(updatedInfo.getShippingInfoAddress());
			existingInfo.setShippingInfoStatus(updatedInfo.getShippingInfoStatus());
			existingInfo.setShippingInfoTrackingNumber(updatedInfo.getShippingInfoTrackingNumber());
			// 若有圖片，將圖片數據存為 BLOB
			if (imageFile != null && !imageFile.isEmpty()) {
				try {
					existingInfo.setShippingInfoImage(imageFile.getBytes());
				} catch (IOException e) {
					throw new RuntimeException("文件上傳失敗: " + e.getMessage());
				}
			}

			shippingInfoRepository.save(existingInfo);
			return true;
		}).orElse(false);
	}

	 // 根據追蹤號查詢物流信息
    public List<ShippingInfo> getShippingInfoByTrackingNumber(String trackingNumber) {
        return shippingInfoRepository.findByShippingInfoTrackingNumber(trackingNumber);
    }
	
	
	// 根據 ID 獲取物流信息
	public Optional<ShippingInfo> getShippingInfoById(Long shippingInfoId) {
		return shippingInfoRepository.findById(shippingInfoId);
	}

	// 查詢訂單與物流信息的關聯數據
	public List<Map<String, Object>> getOrderShippingInfo(Long orderId, String shippingInfoStatus, String trackingNumber) {
	    List<ShippingInfo> shippingInfos;

	    if (trackingNumber != null && !trackingNumber.isEmpty()) {
	        // 如果提供了追蹤號，直接查詢物流信息
	        shippingInfos = shippingInfoRepository.findByShippingInfoTrackingNumber(trackingNumber);
	    } else if (orderId != null) {
	        // 如果提供了訂單ID，查詢訂單相關物流信息
	        shippingInfos = shippingInfoRepository.findByOrderOrderId(orderId);
	    } else if (shippingInfoStatus != null) {
	        // 如果提供了物流狀態，查詢符合狀態的物流信息
	        shippingInfos = shippingInfoRepository.findByShippingInfoStatus(shippingInfoStatus);
	    } else {
	        // 若無篩選條件，返回所有物流信息
	        shippingInfos = shippingInfoRepository.findAll();
	    }

	    // 組裝查詢結果
	    List<Map<String, Object>> results = new ArrayList<>();
	    for (ShippingInfo shippingInfo : shippingInfos) {
	        Map<String, Object> result = new HashMap<>();
	        result.put("order", shippingInfo.getOrder());
	        result.put("shippingInfo", shippingInfo);
	        results.add(result);
	    }
	    return results;
	}

}
