package cn.edu.bupt.service;

import cn.edu.bupt.bean.po.*;
import cn.edu.bupt.bean.vo.EntityListVo;
import cn.edu.bupt.bean.vo.RelationListVo;
import cn.edu.bupt.bean.jo.UserInfoUpdateParam;
import cn.edu.bupt.repository.*;
import cn.edu.bupt.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
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

    private final RelaReflectRepo relaReflectRepo;

    @Autowired
    public UserService(UserRepo userRepo, VerStateRepo verStateRepo, RelationMarkRepo relationMarkRepo, EntityMarkRepo entityMarkRepo, RelaReflectRepo relaReflectRepo) {
        this.userRepo = userRepo;
        this.verStateRepo = verStateRepo;
        this.relationMarkRepo = relationMarkRepo;
        this.entityMarkRepo = entityMarkRepo;
        this.relaReflectRepo = relaReflectRepo;
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
        user.setName("数据审核员");
        user.setAvatar("/avatar.2.jpg");
        user.setRole("admin");
        user = userRepo.save(user);
        return user;
    }

    @Transactional
    public boolean updateUser(User user, UserInfoUpdateParam updateInfo){
        String newName = updateInfo.getName();
        user.setName(newName);
        try {
            userRepo.save(user);
            return true;
        }catch (Exception e){
            return false;
        }
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
    public EntityListVo listEntities(long userId, /*int passed, */int pageNo, int pageSize) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (!userOptional.isPresent()) return null;
        List<VerifyStatement> stats = verStateRepo.findByVerUser(userOptional.get());
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.Direction.DESC, "verDate");
        Page<EntityMark> pageResult = entityMarkRepo.findByStatementIn(stats, pageable);
        List<EntityListVo.EntityHistory> entities = new ArrayList<>();
        for (EntityMark mark : pageResult.getContent()) {
            entities.add(new EntityListVo.EntityHistory(mark));
        }
        return new EntityListVo(pageResult.getTotalElements(), pageNo, entities);
    }

    @Transactional
    public RelationListVo listRelations(long userId, /*int passed,*/ int pageNo, int pageSize) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (!userOptional.isPresent()) return null;
        List<VerifyStatement> stats = verStateRepo.findByVerUser(userOptional.get());
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.Direction.DESC, "verDate");
        Page<RelationMark> pageResult = relationMarkRepo.findByStatementIn(/*passed,*/ stats, pageable);
        List<RelationReflect> reflects = relaReflectRepo.findAll();
        List<RelationListVo.Reflect> reflectsVos = new ArrayList<>();
        for (RelationReflect reflect : reflects) {
            RelationListVo.Reflect reflectVo = new RelationListVo.Reflect(reflect.getId(), reflect.getRName());
            reflectsVos.add(reflectVo);
        }
        List<RelationListVo.RelationHistory> relations = new ArrayList<>();
        for (RelationMark mark : pageResult) {
            relations.add(new RelationListVo.RelationHistory(mark, reflectsVos));
        }
        return new RelationListVo(pageNo, pageResult.getNumberOfElements(), relations);
    }

}
