package com.shopme.controller;

import com.shopme.dto.request.UserRequest;
import com.shopme.dto.response.ApiResponse;
import com.shopme.dto.response.UserResponse;
import com.shopme.exception.AppException;
import com.shopme.exception.ErrorCode;
import com.shopme.export.UserExporter.UserCsvExporter;
import com.shopme.export.UserExporter.UserExcelExporter;
import com.shopme.mapper.UserMapper;
import com.shopme.repository.UserRepository;
import com.shopme.service.UserService;
import com.shopme.util.FileUploadUtil;
import com.shopme.util.PageInfo;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/users")
@Valid
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/all")
    public ApiResponse<List<UserResponse>> getAllUsers(){
        var users = userService.getAll();

        return ApiResponse.<List<UserResponse>>builder()
                .status("SUCCESS")
                .data(users)
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getUsers(
            @RequestParam(name = "page", defaultValue = "1")Integer num,
            @RequestParam(name = "size", defaultValue = UserService.USERS_PER_PAGE)Integer size,
            @RequestParam(name = "sortField", required = false) String sortField,
            @RequestParam(name = "sortDir", defaultValue = "ASC") String sortDir,
            @RequestParam(name = "keyword", required = false) String keyword)
    {


        var page = userService.listByPage(num, size, sortField, sortDir, keyword);
        var userList = page.getContent();

        PageInfo pageInfo = PageInfo.builder()
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .size(page.getSize())
                .number(page.getNumber()+1)
                .build();

        return ApiResponse.<List<UserResponse>>builder()
                .status("SUCCESS")
                .data(userList.stream().map(userMapper::toUserResponse).toList())
                .pageDetails(pageInfo)
                .build();

    }

    @GetMapping("/my-info")
    public ApiResponse<UserResponse> getMyInfo(@AuthenticationPrincipal Jwt jwt){
        String username = jwt.getSubject();
       var user = userService.getUserByUsername(username);

       return ApiResponse.<UserResponse>builder()
               .status("SUCCESS")
               .data(user)
               .build();
    }

    @PostMapping
    public ApiResponse<UserResponse> createNewUser(@Valid @ModelAttribute UserRequest request,
                                                   @RequestParam("image")MultipartFile multipartFile) throws IOException {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();

        if(userService.isDuplicateEmail(request.getEmail())){
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if(userService.isDuplicateUsername(request.getUsername())){
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        var user = userService.createUser(request);

        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            request.setAvatar(fileName);
            String uploadDir = "upload-avatar/" + user.getId();
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }

        apiResponse.setStatus("SUCCESS");
        apiResponse.setData(user);
        return apiResponse;
    }


    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable("id") Integer id){
        return ApiResponse.<UserResponse>builder()
                .status("SUCCESS")
                .data(userService.getUserById(id))
                .build();
    }

    @GetMapping("/export/csv")
    public void exportToCSV(HttpServletResponse response){
        var listUser = userService.getAll();
        UserCsvExporter csvExporter = new UserCsvExporter();
        csvExporter.export(listUser, response);
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        var listUser = userService.getAll();
        UserExcelExporter excelExporter = new UserExcelExporter();
        excelExporter.export(listUser, response);
    }

    @PutMapping("/edit/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable("id") Integer id,
                                                @Valid @ModelAttribute UserRequest request,
                                                @RequestParam("image") MultipartFile multipartFile) throws IOException {

        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            request.setAvatar(fileName);
            String uploadDir = "upload-avatar/" + id;
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        }

        return userService.update(request, id);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteUser(@PathVariable("id") Integer id){
        ApiResponse<String> apiResponse = new ApiResponse<>();
        if(userService.deleteUser(id)){
            apiResponse.setStatus("SUCCESS");
            apiResponse.setData("Deleted user with ID: "+ id );
        } else {
            throw new AppException(ErrorCode.USERID_NOT_FOUND);
        }
        return apiResponse;
    }

    @PutMapping("/status/{id}")
    public ApiResponse<String> changeStatusEnabled(@PathVariable("id") Integer id,
                                                   @RequestParam("enabled") boolean enabled){

       return userService.changeEnabledStatusUser(id, enabled);
    }


    @PostMapping("/save")
    public ApiResponse<UserResponse> createRandom(@Valid @RequestBody UserRequest request){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        System.out.println();
        if(userService.isDuplicateEmail(request.getEmail())){
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if(userService.isDuplicateUsername(request.getUsername())){
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        var user = userService.createUser(request);
        apiResponse.setStatus("SUCCESS");
        apiResponse.setData(user);
        return apiResponse;
    }


}
