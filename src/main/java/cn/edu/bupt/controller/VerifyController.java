package cn.edu.bupt.controller;

import cn.edu.bupt.bean.jo.EntityParam;
import cn.edu.bupt.bean.jo.RelationParam;
import cn.edu.bupt.bean.vo.VerMarksVo;
import cn.edu.bupt.constant.OauthConsts;
import cn.edu.bupt.service.VerService;
import cn.edu.bupt.util.ResponseResult;
import cn.edu.bupt.util.ResultTypeEnum;
import cn.edu.bupt.util.token.Identity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("api/ver")
public class VerifyController {

    private final VerService verService;

    @Autowired
    public VerifyController(VerService verService) {
        this.verService = verService;
    }

    @PutMapping("entity")
    public ResponseResult<String> dealEntity(@RequestBody EntityParam param, HttpSession session) {
        Identity identity = (Identity) session.getAttribute(OauthConsts.KEY_IDENTITY);
        if (StringUtils.isEmpty(param.getContent()) || param.getId() <= 0L) {
            return ResponseResult.error("审批失败");
        }
        int passed = param.isPassed() ? 0 : 1;
        return verService.dealWithEntity(identity.getId(), param.getId(), param.getStatId(), param.getContent(), passed);
    }

    @PutMapping("relation")
    public ResponseResult<String> dealRelation(@RequestBody RelationParam param, HttpSession session) {
        Identity identity = (Identity) session.getAttribute(OauthConsts.KEY_IDENTITY);
        if (StringUtils.isEmpty(param.getContent()) || param.getId() <= 0L || param.getRelationId() <= 0L) {
            return ResponseResult.error("审批失败");
        }
        int passed = param.isPassed() ? 0 : 1;
        return verService.dealWithRelation(identity.getId(), param.getId(), param.getStatId(),
                param.getContent(), passed, param.getRelationId());
    }

    @GetMapping("next")
    public ResponseResult<VerMarksVo> nextVerData(HttpSession session) {
        Identity identity = (Identity) session.getAttribute(OauthConsts.KEY_IDENTITY);
        VerMarksVo result = verService.nextUnViewedStatement(identity.getId());
        if (result == null) return ResponseResult.error(ResultTypeEnum.SERVICE_ERROR, "没有需要审核的文本");
        else return ResponseResult.success(result);
    }

}
