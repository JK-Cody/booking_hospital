package com.hospital.book.repository;

import model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {

    //获取Mongo数据库的对象
    Hospital getHospitalByHoscode(String hoscode);   //方法名需按照规范起名

    List<Hospital> findHospitalByHosnameLike(String hosname);
}
