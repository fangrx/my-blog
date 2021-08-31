package com.nonelonely.modules.system.service.impl;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
@Service
public class InitService {
    @Autowired
    private EntityManager entityManager;
    @Transactional
    public void initSettings(Resource resource) {
        Session session = entityManager.unwrap(Session.class);
        EncodedResource er = new EncodedResource(resource, "utf-8");
        session.doWork(connection -> ScriptUtils.executeSqlScript(connection, er));
    }
}
