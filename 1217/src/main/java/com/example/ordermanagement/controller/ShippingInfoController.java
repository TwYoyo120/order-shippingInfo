package com.example.ordermanagement.controller;

import com.example.ordermanagement.model.ShippingInfo;
import com.example.ordermanagement.repository.ShippingInfoRepository;
import com.example.ordermanagement.service.ShippingInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shipping")
public class ShippingInfoController {

    @Autowired
    private ShippingInfoService shippingInfoService;
    
    @Autowired
    private ShippingInfoRepository shippingInfoRepository;

    /**
     * 返回物流追蹤頁面
     * 對應前端模板：resources/templates/adminShippingTracking.html
     */
    @GetMapping("/shipping-tracking")
    public String adminShippingTracking() {
        return "adminShippingTracking";
    }

    /**
     * 查詢物流信息
     *
     * @param orderId 訂單 ID (可選)
     * @param status 物流狀態 (可選)
     * @return 包含訂單和物流信息的結果列表
     */
    @ResponseBody
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getOrderShippingInfo(
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String trackingNumber) {
        try {
            System.out.println("查詢參數 - orderId: " + orderId + ", status: " + status + ", trackingNumber: " + trackingNumber);

            List<Map<String, Object>> results = shippingInfoService.getOrderShippingInfo(orderId, status, trackingNumber);

            if (results == null || results.isEmpty()) {
                System.out.println("查詢結果: 無匹配數據");
                return ResponseEntity.ok(Collections.emptyList()); // 返回空列表
            }

            System.out.println("查詢結果數量: " + results.size());
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("查詢過程中發生異常: " + e.getMessage());
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }


    /**
     * 根據 ID 查詢物流信息
     *
     * @param id 物流信息 ID
     * @return 物流信息對象 (JSON 格式)
     */
    @ResponseBody
    @GetMapping("/{id}")
    public ResponseEntity<?> getShippingInfoById(@PathVariable Long id) {
        return shippingInfoService.getShippingInfoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 更新物流信息，支持圖片上傳
     *
     * @param id 更新的物流信息 ID
     * @param updatedInfo 更新後的物流信息對象
     * @param imageFile 圖片文件 (可選)
     * @return 更新結果
     */
    @ResponseBody
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShippingInfo(
            @PathVariable Long id,
            @RequestPart("shippingInfo") ShippingInfo updatedInfo,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            boolean updated = shippingInfoService.updateShippingInfo(id, updatedInfo, imageFile);
            return updated ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("更新失敗: " + e.getMessage());
        }
    }

    @ResponseBody
    @GetMapping("/tracking-number")
    public ResponseEntity<List<ShippingInfo>> getShippingInfoByTrackingNumber(
            @RequestParam String trackingNumber) {
        System.out.println("接收到的物流追蹤號: " + trackingNumber); // 輸出接收的參數
        List<ShippingInfo> shippingInfos = shippingInfoRepository.findByShippingInfoTrackingNumber(trackingNumber);
        if (shippingInfos.isEmpty()) {
            System.out.println("未找到相關物流資訊"); // 訊息提示
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(shippingInfos);
    }


    

    @GetMapping(value = "/image/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, String>> getImage(@PathVariable Long id) {
        return shippingInfoService.getShippingInfoById(id)
                .filter(info -> info.getShippingInfoImage() != null)
                .map(info -> {
                    String base64Image = Base64.getEncoder().encodeToString(info.getShippingInfoImage());
                    Map<String, String> response = new HashMap<>();
                    response.put("image", "data:image/png;base64," + base64Image);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    
}

