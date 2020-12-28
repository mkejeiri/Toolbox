package guru.sfg.brewery.config;

import org.hibernate.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class HibernateConfig implements HibernatePropertiesCustomizer {

    @Autowired
    Interceptor orderHeaderInterceptor; //declare Interceptor (OrderHeaderInterceptor,...)
                                        // for Hibernate to monitor the status changes of BeerOrder
                                        // -> see OrderHeaderInterceptor.onFlushDirty()

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.ejb.interceptor", orderHeaderInterceptor);
    }

}
