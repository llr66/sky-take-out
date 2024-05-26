package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MemberSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面,实现公共字段填充处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 使用注解的方式指定切入点(同时使用execution去限定AOP扫描范围提高效率)
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){}

    /**
     * 使用前置通知(并指定切入点方法),在通知中进行公共字段的赋值,并通过参数获取代理方法的信息(方法名,参数值)
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段的填充...");

        //1,获取当前被拦截的方法上的数据库操作类型(注解之中的枚举的常量)

        //获取切入点方法的签名,并进行一个向下转型获取方法签名对象
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        //然后调用方法签名对象来获取方法并获取方法上的注解对象(反射)
        AutoFill autoFill=signature.getMethod().getAnnotation(AutoFill.class);
        //获取数据库操作类型
        OperationType operationType=autoFill.value();

        //2,获取到当前被拦截的方法的参数--实体对象
        Object[] args = joinPoint.getArgs();
        //防止空指针问题
        if (args==null||args.length==0){
            return;
        }
        //我们进行约定在Mapper方法中实体类参数放在第一位,方便获取
        Object entity=args[0];
        //3,准备公共字段的数据(登录用户,和时间)
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        //4,根据当前不同的操作类型,为对应的属性来通过反射来进行赋值
        //这里使用反射的原因时我们在书写方法时是不知道之前我们获取到的实体参数类型是什么,从而无法调用方法进行赋值
        if (operationType==OperationType.INSERT){
            //为4个公共字段进行赋值
            //通过反射获取实体类中重复字段设置的方法
            try {
                log.info("执行了公共字段的填充");
                Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser", Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

                //通过反射来为对象属性赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (operationType==OperationType.UPDATE) {
            //为2个公共字段进行赋值
            //通过反射获取实体类中重复字段设置的方法
            try {
                log.info("执行了公共字段的填充");
                Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);

                //通过反射来为对象属性赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
