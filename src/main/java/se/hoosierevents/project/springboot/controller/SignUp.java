package se.hoosierevents.project.springboot.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import se.hoosierevents.project.model.User;
import se.hoosierevents.project.springboot.config.UserRole;
import se.hoosierevents.project.springboot.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SignUp {

@Autowired
UserRepository userRepository;


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


//    @RequestMapping(value="/signup",method = RequestMethod.POST)
//    public String SignmeUp(@ModelAttribute("RegForm") RegForm regForm, BindingResult result) {
//        ModelAndView modelAndView = new ModelAndView();
//        if(result.hasErrors())
//        {
//            return "login";
//        }
//        System.out.println("I am here");
////        UserService users = new UserService();
////        users.create(username, password);
//
//return "hello";
//    }
@RequestMapping(value = "/signup/me")
public final ResponseEntity<String> signMeUp( String name, String email, String phone , String password,  String type_user){
    User user = new User();

    user.setName(name);
    user.setPhoneNumber(phone);
    user.setEmail(email);
    
    if(type_user.equals("user"))
        user.setActive(1);
    else
        user.setActive(0);
    
    
    System.out.println(type_user);
    if(type_user.equals("user"))
        user.setUserType(UserRole.USER_NORMAL);
    else
        user.setUserType(UserRole.USER_ORGANIZER);
    user.setPassword(bCryptPasswordEncoder.encode(password));

    userRepository.save(user);

    System.out.println(name);
    System.out.println(user.getPassword());
//        UserService users = new UserService();
//       users.create(email, password);

    return ResponseEntity.ok("Success");

}
}
