package cn.edu.bupt.controller;

import cn.edu.bupt.bean.jo.EntityParam;
import cn.edu.bupt.bean.jo.RelationParam;
import cn.edu.bupt.bean.vo.Identity;
import cn.edu.bupt.bean.vo.VerMarksVo;
import cn.edu.bupt.constant.OauthConsts;
import cn.edu.bupt.constant.ParamConsts;
import cn.edu.bupt.service.VerService;
import cn.edu.bupt.util.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/ver")
@Slf4j
public class VerifyController {

    private final VerService verService;

    @Autowired
    public VerifyController(VerService verService) {
        this.verService = verService;
    }

    @PutMapping("entity")
    public ResponseEntity<ResponseResult<String>> dealEntity(@RequestBody EntityParam param, HttpSession session) {
        Identity identity = (Identity) session.getAttribute(OauthConsts.KEY_IDENTITY);
        if (StringUtils.isEmpty(param.getContent()) || param.getId() <= 0L) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseResult.of("审核失败", "审核失败"));
//            return ResponseResult.error(ResultTypeEnum.PARAM_ERROR, "审核失败", null);
        }
        ResponseResult<String> result = verService.dealWithEntity(identity.getId(), param.getId(),
                param.getStatId(), param.getContent(), param.getPassed(), param.getDescription());
        return ResponseEntity.ok(result);
    }

    @PutMapping("relation")
    public ResponseEntity<ResponseResult<String>> dealRelation(@RequestBody RelationParam param, HttpSession session) {
        Identity identity = (Identity) session.getAttribute(OauthConsts.KEY_IDENTITY);
        if (StringUtils.isEmpty(param.getContent()) || param.getId() <= 0L || param.getRelationId() <= 0L) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseResult.of("审核失败", "审核失败"));
//            return ResponseResult.error(ResultTypeEnum.PARAM_ERROR, "审批失败", null);
        }
        ResponseResult<String> result = verService.dealWithRelation(identity.getId(), param.getId(), param.getStatId(),
                param.getContent(), param.getPassed(), param.getRelationId(), param.getDescription());
        return ResponseEntity.ok(result);
    }

    @PostMapping("next")
    public ResponseEntity<ResponseResult<VerMarksVo>> nextVerData(@RequestBody Map<String, Object> params, HttpSession session) {
        Identity identity = (Identity) session.getAttribute(OauthConsts.KEY_IDENTITY);
        int pageNo = 1, pageSize = 100;
        if (params.containsKey(ParamConsts.pageNo)) {
            pageNo = Integer.parseInt(String.valueOf(params.get(ParamConsts.pageNo)));
        }
        if (params.containsKey(ParamConsts.pageSize)) {
            pageSize = Integer.parseInt(String.valueOf(params.get(ParamConsts.pageSize)));
        }
        VerMarksVo result = verService.curUnViewedStatement(identity.getId(), pageNo, pageSize);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseResult.of("没有需要审核的文本", null));
        }
//        if (result == null) return ResponseResult.error(ResultTypeEnum.SERVICE_ERROR, "没有需要审核的文本", null);
//        else return ResponseResult.success(result);
        return ResponseEntity.ok(ResponseResult.of("success", result));
    }

    @PostMapping("next/begin")
    public ResponseEntity<ResponseResult<String>> startNext(@RequestBody Map<String, Object> params, HttpSession session) {
        Identity identity = (Identity) session.getAttribute(OauthConsts.KEY_IDENTITY);
        boolean completeLast = (boolean) params.get("completeLast");
        boolean result = verService.beginNext(identity.getId(), completeLast);
        if (result) {
            return ResponseEntity.ok(ResponseResult.of("获取成功", "获取成功"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseResult.of("没有需要审核的文本", "没有需要审核的文本"));
        }
    }

    @PostMapping("opinion/prefix")
    public ResponseEntity<ResponseResult<List<String>>> prefixOpinion(@RequestBody Map<String, Object> params) {
        String prefix;
        if (params.containsKey("prefix")) {
            prefix = (String) params.get("prefix");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseResult.of("参数不存在"));
        }
        List<String> opinions = verService.beginWithPrefixOpinion(prefix);
        if (opinions == null || opinions.size() == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseResult.of("结果为空"));
        } else {
            return ResponseEntity.ok(ResponseResult.of("success", opinions));
        }
    }

}
