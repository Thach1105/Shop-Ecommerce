package com.shopme.service;


import com.shopme.dto.request.UserRequest;
import com.shopme.dto.response.ApiResponse;
import com.shopme.dto.response.UserResponse;
import com.shopme.entity.User;
import com.shopme.exception.AppException;
import com.shopme.exception.ErrorCode;
import com.shopme.mapper.UserMapper;
import com.shopme.repository.RoleRepository;
import com.shopme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleService roleService;

    public static final String USERS_PER_PAGE = "10";

    private void encodePassword(User user){
        if(user.getPassword().length() < 8)
            throw new AppException(ErrorCode.INVALID_PASSWORD);

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }
    
    public List<UserResponse> getAll(){
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse createUser(UserRequest request){
        User user = userMapper.toUser(request);
        encodePassword(user);
        user.setRoles(roleService.getAllRoleById(request.getRoles()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getUserById(Integer id){
        var user = userRepository.findById(id);

        return userMapper.toUserResponse(
                user.orElseThrow(() ->
                        new AppException(ErrorCode.USERID_NOT_FOUND))
        );
    }

    public UserResponse getUserByUsername(String username){
        var user = userRepository.findByUsername(username);
        if(user == null) throw new AppException(ErrorCode.USERNAME_NOT_FOUND);

        return userMapper.toUserResponse(user);
    }

    public ApiResponse<UserResponse> update(UserRequest request, Integer id){
        //tìm user trong cơ sở dữ liệu
        var prevUser = userRepository.findById(id).orElseThrow();

        //kiểm tra xem email có bị thay đổi không
        if(isDuplicateEmail(request.getEmail()) && !request.getEmail().equals(prevUser.getEmail())){
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        //mapper từ request thành user
        User user = userMapper.toUser(request);
        user.setId(id);

        if(request.getPassword().isEmpty()){
            //nếu mật khẩu không bị thay đổi thì gắn lại mật khẩu cũ
            user.setPassword(prevUser.getPassword());
        } else {
            //nếu mật khẩu bị thay đổi thì mã hóa mật khẩu mới
            encodePassword(user);
        }

        //nếu khi cập nhật avatar là một chuối null thì giữ nguyên avatar cũ
        if(request.getAvatar() == null){
            user.setAvatar(prevUser.getAvatar());
        }
        // cập nhật list role mới
        user.setRoles(roleService.getAllRoleById(request.getRoles()));

        return ApiResponse.<UserResponse>builder()
                .status("SUCCESS")
                .data(userMapper.toUserResponse(userRepository.save(user)))
                .build();

    }

    public boolean deleteUser(Integer id){
        if(!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }

    public ApiResponse<String> changeEnabledStatusUser(Integer id, boolean enabled){
        userRepository.updateEnabledStatus(id, enabled);
        String statusEnabled = enabled ? "enable" : "disable";
        return ApiResponse.<String>builder()
                .status("SUCCESS")
                .data(String.format("User with ID: %d is %s", id, statusEnabled))
                .build();
    }

    public boolean isDuplicateEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public boolean isDuplicateUsername(String username){
        return  userRepository.existsByUsername(username);
    }

    public Page<User> listByPage(int pageNumber, int pageSize, String sortField, String sortDir, String keyword){

        Sort sort = sortField != null ? Sort.by(Sort.Direction.fromString(sortDir), sortField) : Sort.unsorted();
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize, sort);

        if(keyword != null && !keyword.isEmpty()){
            return userRepository.findUserByKeyword(keyword, pageable);
        } else {
            return userRepository.findAll(pageable);
        }

    }
}
