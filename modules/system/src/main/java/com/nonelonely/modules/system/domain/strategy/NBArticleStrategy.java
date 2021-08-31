package com.nonelonely.modules.system.domain.strategy;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class NBArticleStrategy implements IdentifierGenerator {

    /**
     * 使用时间戳
     *
     * @param session
     * @param object
     * @return
     * @throws HibernateException
     */
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        return System.currentTimeMillis();
    }

}
