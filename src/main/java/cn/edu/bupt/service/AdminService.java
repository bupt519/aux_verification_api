package cn.edu.bupt.service;

import cn.edu.bupt.bean.jo.UserInfoUpdateParam;
import cn.edu.bupt.bean.po.*;
import cn.edu.bupt.bean.vo.EntityListVo;
import cn.edu.bupt.bean.vo.Identity;
import cn.edu.bupt.bean.vo.RelationListVo;
import cn.edu.bupt.repository.*;
import cn.edu.bupt.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService extends UserService{

    private final VerStateRepo verStateRepo;

    @Autowired
    public AdminService(UserRepo userRepo, VerStateRepo verStateRepo, RelationMarkRepo relationMarkRepo, EntityMarkRepo entityMarkRepo, RelaReflectRepo relaReflectRepo) {
        super(userRepo, verStateRepo, relationMarkRepo, entityMarkRepo, relaReflectRepo);
        this.verStateRepo = verStateRepo;
    }

    @Transactional
    public EntityListVo listEntities(int pageNo, int pageSize) {
        List<Long> statsId = verStateRepo.findIdByVerUserNotNull();
        return this.pageAbleEntities(statsId, pageNo, pageSize);
    }

    @Transactional
    public RelationListVo listRelations(int pageNo, int pageSize) {
        List<Long> statsId = verStateRepo.findIdByVerUserNotNull();
        return this.pageAbleRelations(statsId, pageNo, pageSize);
    }

}
