package cn.edu.bupt.controller;

import cn.edu.bupt.bean.jo.UserInfoUpdateParam;
import cn.edu.bupt.bean.po.User;
import cn.edu.bupt.bean.vo.*;
import cn.edu.bupt.constant.OauthConsts;
import cn.edu.bupt.constant.ParamConsts;
import cn.edu.bupt.service.AdminService;
import cn.edu.bupt.util.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("api/admin")
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("entities")
    public ResponseResult<EntityListVo> reviewedEntities(@RequestBody Map<String, Object> params,
                                                         HttpSession httpSession) {
        log.info("-------------Post admin/entities ------------------------------");
        /*获取所有由当前用户审核过（中）的实体标注数据*/
        Identity identity = (Identity) httpSession.getAttribute(OauthConsts.KEY_IDENTITY);
        int pageNo = (int) params.get(ParamConsts.pageNo);
        int pageSize = (int) params.get(ParamConsts.pageSize);
        EntityListVo marks = adminService.listEntities(pageNo, pageSize);
        return ResponseResult.of("success", marks);
    }

    @PostMapping("relations")
    public ResponseResult<RelationListVo> reviewedRelations(@RequestBody Map<String, Object> params,
                                                            HttpSession session) {
        log.info("-------------Post admin/relations ------------------------------");
        /*获取所有由当前用户审核过（中）的关系标注数据*/
        Identity identity = (Identity) session.getAttribute(OauthConsts.KEY_IDENTITY);
        int pageNo = (int) params.get(ParamConsts.pageNo);
        int pageSize = (int) params.get(ParamConsts.pageSize);
//        int interPassed = passed ? 1 : 0;
        RelationListVo marks = adminService.listRelations(pageNo, pageSize);
        return ResponseResult.of("success", marks);
    }

    @GetMapping("entities/count")
    public ResponseResult<DataStatisticsVo> countEntities(HttpSession session) {
        log.info("-------------Get admin/entities/count ------------------------------");
        /*查询当前标注数据的计数状态*/
        DataStatisticsVo counts = adminService.countEntities();
        return ResponseResult.of("success", counts);
    }

    @GetMapping("relations/count")
    public ResponseResult<DataStatisticsVo> countRelations(HttpSession session) {
        log.info("-------------Get admin/relations/count ------------------------------");
        /*查询当前标注数据的计数状态*/
        DataStatisticsVo counts = adminService.countRelations();
        return ResponseResult.of("success", counts);
    }
}
