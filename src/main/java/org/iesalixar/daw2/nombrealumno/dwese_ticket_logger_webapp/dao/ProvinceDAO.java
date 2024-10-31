package org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.dao;


import java.sql.SQLException;
import java.util.List;
import org.iesalixar.daw2.nombrealumno.dwese_ticket_logger_webapp.entity.Province;


public interface ProvinceDAO {




    List<Province> listAllProvinces() ;


    void insertProvince(Province province);


    void updateProvince(Province province);


    void deleteProvince(int id);


    Province getProvinceById(int id);


    boolean existsProvinceByCode(String code);


    boolean existsProvinceByCodeAndNotId(String code, int id);
}
