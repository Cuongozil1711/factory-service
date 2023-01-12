package vn.clmart.manager_service.api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.EmployeeDto;
import vn.clmart.manager_service.dto.UserLoginDto;
import vn.clmart.manager_service.service.UserService;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserApi {
    private final UserService userService;

    @PostMapping("/login")
    protected @ResponseBody
    ResponseEntity<Object> login(
            @RequestBody UserLoginDto userLoginDto,
            HttpServletRequest request
    ) {
        try {
            return new ResponseEntity<>(userService.authenticateUserHandler(userLoginDto, request), HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(e,  HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NullPointerException ex) {
            return new ResponseEntity<>(ex,  HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/create")
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody EmployeeDto employeeDto
    ) {
        try {
            return new ResponseEntity<>(userService.createEmployee(employeeDto, cid, uid), HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/update")
    protected @ResponseBody
    ResponseEntity<Object> update(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody EmployeeDto employeeDto
    ) {
        try {
            return new ResponseEntity<>(userService.updateEmployeee(employeeDto, cid, uid), HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/checkToken", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<Object> checkToken(){
        try {
            Authentication authen = SecurityContextHolder.getContext().getAuthentication();
            if (null != authen && !authen.getPrincipal().equals("anonymousUser")) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
            }
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/employee/search")
    protected @ResponseBody
    ResponseEntity<Object> search(
            @RequestHeader Long cid,
            @RequestHeader String uid
            , Pageable pageable) {
        try {
            return new ResponseEntity<>(userService.search(cid, pageable), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/delete/{id}")
    protected @ResponseBody
    ResponseEntity<Object> delete(
            @RequestHeader Long cid,
            @RequestHeader String uid
            ,@PathVariable(value = "id") Long id ) {
        try {
            userService.deleteEmployee(cid, uid, id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/getByUser")
    protected @ResponseBody
    ResponseEntity<Object> getByUser(
            @RequestHeader Long cid,
            @RequestHeader String uid)
     {
        try {
            return new ResponseEntity<>(userService.getByUid(cid, uid),HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
