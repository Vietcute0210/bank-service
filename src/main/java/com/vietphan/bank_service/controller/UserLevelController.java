package com.vietphan.bank_service.controller;
import com.vietphan.bank_service.DTO.request.UserLevelRequest;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.DTO.response.UserLevelResponse;
import com.vietphan.bank_service.service.UserLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/user-level")
@RequiredArgsConstructor
public class UserLevelController {
    private final UserLevelService userLevelService;

    @PostMapping
    public BaseResponse<UserLevelResponse> createLevel(@RequestBody UserLevelRequest request) {
        UserLevelResponse response = userLevelService.createLevel(request);
        return BaseResponse.<UserLevelResponse>builder()
                .code(1000)
                .message("Create user level successfully")
                .data(response)
                .build();
    }

    @GetMapping
    public BaseResponse<List<UserLevelResponse>> getAllLevels() {
        List<UserLevelResponse> responses = userLevelService.getAllLevels();
        return BaseResponse.<List<UserLevelResponse>>builder()
                .code(1000)
                .message("Get all user levels successfully")
                .data(responses)
                .build();
    }

    @GetMapping("/{id}")
    public BaseResponse<UserLevelResponse> getLevelById(@PathVariable Long id) {
        UserLevelResponse response = userLevelService.getLevelById(id);
        return BaseResponse.<UserLevelResponse>builder()
                .code(1000)
                .message("Get user level successfully")
                .data(response)
                .build();
    }
}