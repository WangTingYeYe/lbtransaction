package com.luban.server.lbtransaction.aspect;

import com.luban.server.lbtransaction.connection.LbConnection;
import com.luban.server.lbtransaction.transactional.LbTransactionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.sql.Connection;

@Aspect
@Component
public class LbDataSourceAspect {

    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Connection around(ProceedingJoinPoint point) throws Throwable {
        if (LbTransactionManager.getCurrent() != null) {
            return new LbConnection((Connection) point.proceed(), LbTransactionManager.getCurrent());
        } else {
            return (Connection) point.proceed();
        }
    }
}
