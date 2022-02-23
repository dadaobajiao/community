package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;


//@Repository("alphaHibernate")
@Repository
public class AlphaDaoHibernateImpl implements AlphaDao {
    @Override
    public String select() {
        return "Hibernate";
    }
}
