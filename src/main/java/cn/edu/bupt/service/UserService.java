package cn.edu.bupt.service;

import cn.edu.bupt.bean.po.EntityMark;
import cn.edu.bupt.bean.po.RelationMark;
import cn.edu.bupt.bean.po.User;
import cn.edu.bupt.bean.po.VerifyStatement;
import cn.edu.bupt.repository.EntityMarkRepo;
import cn.edu.bupt.repository.RelationMarkRepo;
import cn.edu.bupt.repository.UserRepo;
import cn.edu.bupt.repository.VerStateRepo;
import cn.edu.bupt.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.security.provider.MD5;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private VerStateRepo verStateRepo;

    @Autowired
    private RelationMarkRepo relationMarkRepo;

    @Autowired
    private EntityMarkRepo entityMarkRepo;

    @Transactional
    public User addUser(User user) {
        Optional<User> userOptional = userRepo.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            return null;
        }
        String encryptedPsd;
        try {
            encryptedPsd = Md5Util.generate(user.getPassword());
        } catch (NoSuchAlgorithmException e) {
            encryptedPsd = user.getPassword();
        }
        user.setPassword(encryptedPsd);
        user = userRepo.save(user);
        return user;
    }

    @Transactional
    public boolean verifyUser(User user) {
        Optional<User> userOptional = userRepo.findByEmail(user.getEmail());
        if (!userOptional.isPresent()) {
            return false;
        }
        String encryptedPsd = null;
        try {
            encryptedPsd = Md5Util.generate(user.getPassword());
        } catch (NoSuchAlgorithmException e) {
            encryptedPsd = user.getPassword();
        }
        return encryptedPsd.equals(userOptional.get().getPassword());
    }

    @Transactional
    public User getUser(String email) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        return userOptional.orElse(null);
    }

    @Transactional
    public List<EntityMark> listEntities(long userId, int passed) {
        User user = userRepo.getOne(userId);
        List<VerifyStatement> stats = verStateRepo.findByVerUser(user);
        return entityMarkRepo.findByPassedAndStatementIn(passed, stats);
    }

    @Transactional
    public List<RelationMark> listRelations(long userId, int passed){
        User user = userRepo.getOne(userId);
        List<VerifyStatement> stats = verStateRepo.findByVerUser(user);
        return relationMarkRepo.findByPassedAndStatementIn(passed, stats);
    }

}
