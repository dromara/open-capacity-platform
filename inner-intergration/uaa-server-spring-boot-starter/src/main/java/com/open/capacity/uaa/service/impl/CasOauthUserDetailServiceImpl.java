package  com.open.capacity.uaa.service.impl;

import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.open.capacity.uaa.service.ICasOauthUserDetailService;

import lombok.AllArgsConstructor;


/**
 * @author owen
 * @date 2018/8/5
 * 根据ticket加载用户服务实现
 */
@AllArgsConstructor
public class CasOauthUserDetailServiceImpl implements  ICasOauthUserDetailService {

    private UserDetailsService userDetailsService ;
	@Override
	public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
		return userDetailsService.loadUserByUsername(token.getName());
	} 

}
