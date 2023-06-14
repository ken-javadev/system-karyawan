package com.system.karyawan.repository;

import com.system.karyawan.models.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDBRepository extends JpaRepository<FileDB, String> {

}