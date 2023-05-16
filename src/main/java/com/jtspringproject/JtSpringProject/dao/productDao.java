package com.jtspringproject.JtSpringProject.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jtspringproject.JtSpringProject.models.Product;

@Repository
public class productDao {
	@Autowired
    private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sf) {
        this.sessionFactory = sf;
    }
	
	@Transactional
	public List<Product> getProducts(){
		return this.sessionFactory.getCurrentSession().createQuery("from PRODUCT").list();
	}
	
	@Transactional
	public Product addProduct(Product product) {
		this.sessionFactory.getCurrentSession().saveOrUpdate(product);
		return product;
	}
}
