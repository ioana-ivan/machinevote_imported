
//To not repeat the RowMapper every time we want to find records in the repository.
package com.mdv.services;

import org.springframework.jdbc.core.RowMapper;

import com.mdv.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User>{
	
	    @Override
	    public User mapRow(ResultSet rs, int rowNum) throws SQLException {

	        User user = new User();
            user.setId(rs.getString("id")); 
            user.setName(rs.getString("nom")); 
            user.setFirstName(rs.getString("prenom"));
            user.setId(rs.getString("CarteID")); 
            return user;
	        

	    }
	}



