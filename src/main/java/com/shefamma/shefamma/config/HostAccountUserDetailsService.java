package com.shefamma.shefamma.config;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.shefamma.shefamma.config.HostAccountEntityUserDetails;
import com.shefamma.shefamma.entities.HostAccountEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class HostAccountUserDetailsService implements UserDetailsService {

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        HostAccountEntity hostAccountEntity = dynamoDBMapper.load(HostAccountEntity.class, email);
        if (hostAccountEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new HostAccountEntityUserDetails(hostAccountEntity);
    }
}
