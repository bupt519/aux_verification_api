package cn.edu.bupt.service;

import cn.edu.bupt.bean.po.User;
import cn.edu.bupt.util.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.NoSuchAlgorithmException;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testAddUser(){
        // success test
        User user = new User();
        user.setEmail("123@123.com");
        user.setPassword("123");
        user = userService.addUser(user);
        Assert.assertNotNull(user);

        //conflict test
        user = userService.addUser(user);
        Assert.assertNull(user);
    }

    @Test
    public void testVerifyUser(){
        User user = new User();
        user.setEmail("123@123.com");
        user.setPassword("123");
        boolean res = userService.verifyUser(user);
        Assert.assertTrue(res);

        user.setEmail("123@124.com");
        res = userService.verifyUser(user);
        Assert.assertFalse(res);

        user.setEmail("123@123.com");
        user.setPassword("124");
        res = userService.verifyUser(user);
        Assert.assertFalse(res);
    }

    @Test
    public void testGetUser() throws NoSuchAlgorithmException {
        User user1 = userService.getUser("123@123.com");
        Assert.assertNotNull(user1);
        Assert.assertEquals(user1.getEmail(), "123@123.com");
        Assert.assertEquals(user1.getPassword(), Md5Util.generate("123"));
        User user2 = userService.getUser("123@124.com");
        Assert.assertNull(user2);
    }

}
