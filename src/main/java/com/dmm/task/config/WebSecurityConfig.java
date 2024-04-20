package com.dmm.task.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dmm.task.service.AccountUserDetailsService;

@Configuration // 設定を行うクラスであることを指定
@EnableWebSecurity // Spring Securityを利用することを指定
@EnableGlobalMethodSecurity(prePostEnabled = true) // 追記 メソッド認可処理を有効化
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private AccountUserDetailsService userDetailsService;

	public PasswordEncoder passwordEncoder() {
		// BCryptアルゴリズムを使用してパスワードのハッシュ化を行う
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// AuthenticationManagerBuilderに、実装したUserDetailsServiceを設定する
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
		super.configure(auth);
	}
    
	@Override
	public void configure(WebSecurity web) throws Exception {
		// 画像、JavaScript、cssは認可の対象外とする
		web.debug(false).ignoring().antMatchers("/images/**", "/js/**", "/css/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 認可の設定
		http.authorizeRequests().antMatchers("/login").permitAll() // loginFormは、全ユーザからのアクセスを許可
				.anyRequest().authenticated(); // loginForm以外は、認証を求める

		// ログイン設定
		http.formLogin() // フォーム認証の有効化
				.loginPage("/login") // ログインフォームを表示するパス
				.loginProcessingUrl("/authenticate") // フォーム認証処理のパス
				.usernameParameter("userName") // ユーザ名のリクエストパラメータ名
				.passwordParameter("password") // パスワードのリクエストパラメータ名
				.defaultSuccessUrl("/main") // 認証成功時に遷移するデフォルトのパス
				.failureUrl("/login?error=true"); // 認証失敗時に遷移するパス

		// ログアウト設定
		http.logout().logoutSuccessUrl("/login") // ログアウト成功時に遷移するパス
				.permitAll(); // 全ユーザに対して許可
	}

}