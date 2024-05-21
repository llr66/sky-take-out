package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    //注入请求的http数据,以便获取携带的JWT令牌
    @Autowired
    private HttpServletRequest httpServletRequest;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对

        //对密码进行MD5加密转换
        password=DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public boolean save(EmployeeDTO employeeDTO) {
        //数据库中已有该用户
        if(employeeMapper.getByUsername(employeeDTO.getUsername())!=null){
            return false;
        }
        //先转换为Employee实体类再进行存储
        else {

            //解析响应层传递过来的EmployeeDTO类
            String username = employeeDTO.getUsername();
            String name = employeeDTO.getName();
            String phone = employeeDTO.getPhone();
            String sex = employeeDTO.getSex();
            String idNumber = employeeDTO.getIdNumber();
            LocalDateTime createTime = LocalDateTime.now();
            LocalDateTime updateTime = LocalDateTime.now();

            //解析密钥 ,并获取用户id
            String JWT = httpServletRequest.getHeader("token");
            Claims claims = JwtUtil.parseJWT("llrjava", JWT);
            Long createUser = (Long) claims.get("empId");
            Long updateUser=createUser;
            Employee employee= Employee.builder()
                    .id(null)
                    .username(username)
                    .name(name)
                    .password(PasswordConstant.DEFAULT_PASSWORD)
                    .status(1)
                    .createTime(createTime)
                    .updateTime(updateTime)
                    .createUser(createUser)
                    .updateUser(updateUser).build();

            log.info("存储的用户为:",employee);
            //调用mapper层存储数据
            employeeMapper.save(employee);
            return true;
        }
    }
}
