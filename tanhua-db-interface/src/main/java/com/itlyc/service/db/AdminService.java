package com.itlyc.service.db;

import com.itlyc.domain.db.Admin;

public interface AdminService {

    Admin findUserByName(String username);
}
