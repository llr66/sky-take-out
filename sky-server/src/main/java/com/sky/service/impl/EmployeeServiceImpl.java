package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;


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
        password = DigestUtils.md5DigestAsHex(password.getBytes());
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

    /**
     * 新增员工
     *
     * @param employeeDTO
     * @return
     */
    @Override
    public boolean save(EmployeeDTO employeeDTO) {

        //先转换为Employee实体类再进行存储
        //解析响应层传递过来的EmployeeDTO类
        String username = employeeDTO.getUsername();
        String name = employeeDTO.getName();
        String phone = employeeDTO.getPhone();
        String sex = employeeDTO.getSex();
        String idNumber = employeeDTO.getIdNumber();
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime updateTime = LocalDateTime.now();


        // 获取之前拦截器处存入本线程存储空间的登录用户id
        Long createUser = BaseContext.getCurrentId();
        //因为是第一次创建所以更新用户和创建用户应该是同一个人
        Long updateUser = createUser;

        //构建employee对象,将数据封装存放
        Employee employee = Employee.builder()
                .id(null)
                .username(username)
                .name(name)
                //将经过mdp5加密后的密码封装到实体类中去
                .password(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()))
                .phone(phone)
                .sex(sex)
                .idNumber(idNumber)
                //设置账号的状态,默认正常状态1表示正常,0表示异常
                .status(StatusConstant.ENABLE)
                .createTime(createTime)
                .updateTime(updateTime)
                //设置之前在jwt令牌中获取的创建人和修改人的id
                .createUser(createUser)
                .updateUser(updateUser).build();

        log.info("存储的用户为:", employee);
        //调用mapper层存储数据
        employeeMapper.save(employee);
        return true;
    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //select * from employee limit 0,10
        //开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());

        //调用mapper层返回分页的员工数据,并且使用分页插件对象Page进行接收,泛型就写接收的实体类
        Page<Employee> page= employeeMapper.pageQuery(employeePageQueryDTO);

        //从这个分页插件page类去获取PageResult属性值:页面数量
        Long total=page.getTotal();
        List<Employee> records=page.getResult();

        //将数据封装为PageResult,并返回给controller层
        return new PageResult(total,records);
    }

    /**
     * 启用、禁用员工账号
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        //将数据封装进Employee后再执行交给数据库更新,这样能提高代码1的复用性
        Employee employee = Employee.builder()
                .id(id)
                .status(status).build();
        employeeMapper.update(employee);

    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee=employeeMapper.getById(id);
        //设置密码屏蔽,防止密码外泄
        employee.setPassword("****");
        return employee;
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    @Override
    public void updata(EmployeeDTO employeeDTO) {
        Employee employee=new Employee();

        //进行对象属性拷贝转换
        BeanUtils.copyProperties(employeeDTO,employee);

        //更新更新时间和更新用户
        employee.setUpdateTime(LocalDateTime.now());

        //使用线程空间工具类获取使用用户的id
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.update(employee);

    }
}
