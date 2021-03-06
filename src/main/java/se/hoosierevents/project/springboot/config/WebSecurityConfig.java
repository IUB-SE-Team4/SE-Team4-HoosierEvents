package se.hoosierevents.project.springboot.config;



import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInController;
import org.springframework.web.bind.annotation.RequestMapping;

import se.hoosierevents.project.springboot.controller.FacebookConnectionSignup;
import se.hoosierevents.project.springboot.controller.FacebookSignInAdapter;
import se.hoosierevents.project.springboot.controller.SuccessfulLoginHandler;

@Configuration
@EnableWebSecurity

public  class WebSecurityConfig  extends WebSecurityConfigurerAdapter {


    @Autowired
    private SuccessfulLoginHandler successHandler;

    @Autowired
    private DataSource dataSource;

    //@Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;

    //@Autowired
    private UsersConnectionRepository usersConnectionRepository;

    //@Autowired
    private FacebookConnectionSignup facebookConnectionSignup;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    @RequestMapping("/login")
    protected void configure(HttpSecurity http) throws Exception {
        http
        		.headers().frameOptions().disable()
        		.and()
                .authorizeRequests()
                //  .antMatchers("/resources/**").permitAll().anyRequest().permitAll()
                .antMatchers("/css/**", "/js/**", "/script/**", "/ws/**", "/images/**").permitAll()
                .antMatchers("/signup/me").permitAll()
                .antMatchers("/forgotPass").permitAll()
                .antMatchers("//signin/**").permitAll()
                .antMatchers("/forgotPassword").permitAll()
                .antMatchers("/validEmail").permitAll()
                .antMatchers("/forgotPassworderror").permitAll()
                .antMatchers("/signup").permitAll()
                .antMatchers("/sign").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/forgotPassword.html").permitAll()
                .antMatchers("/sendCode").permitAll()
                .antMatchers("/map").permitAll()
                .antMatchers("/getauth").permitAll()
                .antMatchers("/getuser").permitAll()
                .antMatchers("/eventpage").permitAll()
                .antMatchers("/getImage").permitAll()
                .antMatchers("/getAllEvents").permitAll()
                .antMatchers("/getEvent").permitAll()
                .antMatchers("/userExists").permitAll()
                .antMatchers("/forgotPass").permitAll()
                .antMatchers("/checkifexists").permitAll()
                .antMatchers("/search").permitAll()
                .antMatchers("/getEventsbyCategory").permitAll()
                .antMatchers("/getEventsbyLocation").permitAll()
                .antMatchers("/getEventbyName").permitAll()
                .antMatchers("/getCategories").permitAll()
                .antMatchers("/getEventsforFirstSearch").permitAll()
                .antMatchers("/googleuserexists").permitAll()
                .antMatchers("/googleusernotexists").permitAll()
                
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/managers").hasRole("MANAGERS")
                .antMatchers("/employees").hasRole("EMPLOYEES")
                .anyRequest().fullyAuthenticated()
                .and()
                .formLogin().successHandler(successHandler)
                .loginPage("/login").permitAll()

                .usernameParameter("email")
                .passwordParameter("user_password");

    }

    //@Bean
    public ProviderSignInController providerSignInController() {
        ((InMemoryUsersConnectionRepository) usersConnectionRepository)
                .setConnectionSignUp(facebookConnectionSignup);

        return new ProviderSignInController(
                connectionFactoryLocator,
                usersConnectionRepository,
                new FacebookSignInAdapter());
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .jdbcAuthentication()
                .dataSource(dataSource)

                .passwordEncoder(bCryptPasswordEncoder)
                .usersByUsernameQuery("select email,user_password,active from user_details where email=?")
                .authoritiesByUsernameQuery("select email,user_type from user_details where email=?");
    }




}