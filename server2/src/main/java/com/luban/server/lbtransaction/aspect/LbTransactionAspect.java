package com.luban.server.lbtransaction.aspect;

import com.luban.server.lbtransaction.annotation.Lbtransactional;
import com.luban.server.lbtransaction.transactional.LbTransaction;
import com.luban.server.lbtransaction.transactional.LbTransactionManager;
import com.luban.server.lbtransaction.transactional.TransactionType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class LbTransactionAspect implements Ordered {


    @Around("@annotation(com.luban.server.lbtransaction.annotation.Lbtransactional)")
    public void invoke(ProceedingJoinPoint point) {
        // 打印出这个注解所对应的方法
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Lbtransactional lbAnnotation = method.getAnnotation(Lbtransactional.class);

        String groupId = "";
        if (lbAnnotation.isStart()) {
            groupId = LbTransactionManager.createLbTransactionGroup();
        } else {
            groupId = LbTransactionManager.getCurrentGroupId();
        }

        LbTransaction lbTransaction = LbTransactionManager.createLbTransaction(groupId);

        try {
            // spring会开启mysql事务
            point.proceed();
            LbTransactionManager.addLbTransaction(lbTransaction, lbAnnotation.isEnd(), TransactionType.commit);
        } catch (Exception e) {
            LbTransactionManager.addLbTransaction(lbTransaction, lbAnnotation.isEnd(), TransactionType.rollback);
            e.printStackTrace();
        } catch (Throwable throwable) {
            LbTransactionManager.addLbTransaction(lbTransaction, lbAnnotation.isEnd(), TransactionType.rollback);
            throwable.printStackTrace();
        }
    }


    @Override
    public int getOrder() {
        return 10000;
    }
}
