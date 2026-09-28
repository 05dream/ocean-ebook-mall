package com.edu.wikipro.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.wikipro.entity.WxUser;
import com.edu.wikipro.mapper.WxUserMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@Service
public class WxUserDetailsService implements UserDetailsService {

    @Resource
    private WxUserMapper wxUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<WxUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        WxUser user = wxUserMapper.selectOne(wrapper);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        List<GrantedAuthority> auths = Collections.singletonList(new SimpleGrantedAuthority("user"));
        return new User(user.getUsername(), user.getPassword(), auths);
    }
}