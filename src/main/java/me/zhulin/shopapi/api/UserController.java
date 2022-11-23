package me.zhulin.shopapi.api;

import me.zhulin.shopapi.entity.User;
import me.zhulin.shopapi.repository.UserRepository;
import me.zhulin.shopapi.security.JWT.JwtProvider;
import me.zhulin.shopapi.service.UserService;
import me.zhulin.shopapi.vo.request.LoginForm;
import me.zhulin.shopapi.vo.response.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;


@CrossOrigin
@RestController
public class UserController {

    @Autowired
    UserService userService;


    @Autowired
    JwtProvider jwtProvider;
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/api/login")
    public ResponseEntity<User> login(@RequestBody LoginForm loginForm) {
        // throws Exception if authentication failed
System.out.println(loginForm.toString());
        try {
            /*
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtProvider.generate(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findOne(userDetails.getUsername());
            return ResponseEntity.ok(new JwtResponse(jwt, user.getEmail(), user.getName(), user.getRole()));
            */
        	String dburl="com.mysql.jdbc.Driver";
        	String url="jdbc:mysql://localhost:3306/shopping";
        	
        	Class.forName(dburl);
        	Connection conn = DriverManager.getConnection(url, "root", "Optimus@7");
        	
        	String sql = "select * from users where email = '" + loginForm.getUsername() + "' and password = '" + 
        	loginForm.getPassword()+"'";
        	User user = new User();
        	
        	Statement st = conn.createStatement();
        	ResultSet rs = st.executeQuery(sql);
        	if(rs.next())
        	{
        		user.setActive(true);
        		user.setEmail(rs.getString("email"));
        		user.setRole(rs.getString("role"));
        		user.setName(rs.getString("name"));
        	}
        	return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    
    @PostMapping("/api/register")
    public ResponseEntity<User> save(@RequestBody User user) {
        try {
        	System.out.println("Inside Register Controller");
        	
        	Random random = new Random();
        	long id = Math.abs(random.nextInt());
        	user.setId(id);
        	System.out.println("User Details : " + user.toString());
        	//userRepository.save(user);
        	String dburl="com.mysql.jdbc.Driver";
        	String url="jdbc:mysql://localhost:3306/shopping";
        	
        	Class.forName(dburl);
        	Connection conn = DriverManager.getConnection(url, "root", "Optimus@7");
        	
        	String sql = "INSERT INTO users(id,"
        			+ "email,password,"
        			+ "name, phone,"
        			+ "address, active, role)"
        			+"values("+id+",'"+user.getEmail()+"','"+user.getPassword()+"','"+user.getName()+"','"
        			+user.getPhone() + "','"+user.getAddress()+"','Active','" + user.getRole()+ "')";
        	
        	Statement st = conn.createStatement();
        	st.executeUpdate(sql);
        	System.out.println("User Inserted Success");
        	return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/profile")
    public ResponseEntity<User> update(@RequestBody User user, Principal principal) {

        try {
            if (!principal.getName().equals(user.getEmail())) throw new IllegalArgumentException();
            return ResponseEntity.ok(userService.update(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/api/profile/{email}")
    public ResponseEntity<User> getProfile(@PathVariable("email") String email, Principal principal) {
        if (principal.getName().equals(email)) {
            return ResponseEntity.ok(userService.findOne(email));
        } else {
            return ResponseEntity.badRequest().build();
        }

    }
}
