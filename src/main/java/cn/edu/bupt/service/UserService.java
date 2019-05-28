package cn.edu.bupt.service;

import cn.edu.bupt.bean.po.EntityMark;
import cn.edu.bupt.bean.po.RelationMark;
import cn.edu.bupt.bean.po.User;
import cn.edu.bupt.bean.po.VerifyStatement;
import cn.edu.bupt.bean.vo.EntityListVo;
import cn.edu.bupt.bean.vo.RelationListVo;
import cn.edu.bupt.repository.EntityMarkRepo;
import cn.edu.bupt.repository.RelationMarkRepo;
import cn.edu.bupt.repository.UserRepo;
import cn.edu.bupt.repository.VerStateRepo;
import cn.edu.bupt.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepo;

    private final VerStateRepo verStateRepo;

    private final RelationMarkRepo relationMarkRepo;

    private final EntityMarkRepo entityMarkRepo;

    @Autowired
    public UserService(UserRepo userRepo, VerStateRepo verStateRepo, RelationMarkRepo relationMarkRepo, EntityMarkRepo entityMarkRepo) {
        this.userRepo = userRepo;
        this.verStateRepo = verStateRepo;
        this.relationMarkRepo = relationMarkRepo;
        this.entityMarkRepo = entityMarkRepo;
    }

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
        String encryptedPsd;
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
    public List<EntityListVo> listEntities(long userId, int passed) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (!userOptional.isPresent()) return null;
        List<VerifyStatement> stats = verStateRepo.findByVerUser(userOptional.get());
        List<EntityMark> marks = entityMarkRepo.findByPassedAndStatementIn(passed, stats);
        List<EntityListVo> vos = new ArrayList<>();
        for (EntityMark mark : marks) {
            vos.add(new EntityListVo(mark));
        }
        return vos;
    }

    @Transactional
    public List<RelationListVo> listRelations(long userId, int passed) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (!userOptional.isPresent()) return null;
        List<VerifyStatement> stats = verStateRepo.findByVerUser(userOptional.get());
        List<RelationMark> marks = relationMarkRepo.findByPassedAndStatementIn(passed, stats);
        List<RelationListVo> vos = new ArrayList<>();
        for (RelationMark mark : marks) {
            vos.add(new RelationListVo(mark));
        }
        return vos;
    }

}
