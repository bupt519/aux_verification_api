package cn.edu.bupt.controller;

import cn.edu.bupt.bean.po.EntityMark;
import cn.edu.bupt.bean.po.RelationMark;
import cn.edu.bupt.bean.po.StmtEntities;
import cn.edu.bupt.bean.po.VerifyStatement;
import cn.edu.bupt.repository.RelationMarkRepo;
import cn.edu.bupt.service.VerService;
import cn.edu.bupt.service.EntitiesService;
import cn.edu.bupt.util.ResponseResult;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import cn.edu.bupt.service.RelationService;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("resources")
@Slf4j
public class ResourceController {

    private final VerService verService;

    private final EntitiesService entitiesService;

    private final RelationService relationService;

    @Autowired
    public ResourceController(VerService verService, EntitiesService entitiesService, RelationService relationService) {
        this.verService = verService;
        this.entitiesService = entitiesService;
        this.relationService = relationService;
    }

    //@GetMapping("pdf/{pdfName}")
    public ResponseEntity<FileSystemResource> getPdf(@PathVariable String pdfName){
        //已放弃，这部分内容改为由前端来完成
        HttpHeaders headers = new HttpHeaders();

        headers.add("Cache-Control", "max-age=0");
        headers.add("Content-Disposition", "inline; filename=" + pdfName);
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));

        try {
            pdfName += ".pdf";
            File pdfFile = new File(this.getClass().getResource("/pdf/" + pdfName).getFile());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(pdfFile.length())
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(new FileSystemResource(pdfFile));
        }
        catch (NullPointerException e){
            log.debug("file not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("test/testEntityContent/{entityId}")
    public ResponseEntity<ResponseResult<List<String>>> testEntityContent(@PathVariable int entityId){
        //从数据库中读取一个实体，并将标注文本转化为 无标签/全标签的结果，返回
        log.info("---------GET testEntityContent---------------------");
        List<String> resList = new ArrayList<>();
        for(int i = 0; i< 10; i ++){
            EntityMark entityMark = this.verService.getEntity(entityId + i);
            resList.add(entityMark.getContent());
            resList.add(entityMark.getNonTagContent());
            resList.add(entityMark.getFullTagContent());
            resList.add(entityMark.recoverTagContent(entityMark.getFullTagContent()));
        }
        return ResponseEntity.ok(ResponseResult.of("success", resList));
    }

    @GetMapping("test/testStmtEntity/{entityId}")
    public ResponseEntity<ResponseResult<List<StmtEntities>>> testStmtEntity(@PathVariable int entityId){
        //从数据库中读取一个实体，并将标注文本转化为 无标签/全标签的结果，返回
        log.info("---------GET testStmtEntity---------------------");
        List<StmtEntities> resList = new ArrayList<>();
        EntityMark entityMark = this.entitiesService.getEntity(entityId);
        resList = this.entitiesService.dealWithEntitiesModify(entityMark);

        return ResponseEntity.ok(ResponseResult.of("success", resList));
    }

    @GetMapping("test/testRelationEntity/{relationId}")
    public ResponseEntity<ResponseResult<String>> testRelationEntity(@PathVariable int relationId){
        //从数据库中读取一个关系，并将标注文本转化为 无标签/全标签的结果，返回a
        log.info("---------GET testRelationEntity---------------------");
        Pair<Boolean, String> checkRes = new Pair<>(true, "关系标注解析成功！");
        RelationMark relationMark = this.relationService.getRelationMark(relationId);
        checkRes = this.relationService.checkEntityExistence(relationMark);

        return ResponseEntity.ok(ResponseResult.of("success", checkRes.getValue()));
    }

    @GetMapping("processData/fillEntities/{statementId}")
    public ResponseEntity<ResponseResult<String>> testRelationEntity(@PathVariable long statementId){
        //从数据库中自statementId开始的所有 句子的实体/关系标注，解析并保存实体结果，解析并填充关系映射
        log.info("---------GET fillEntities---------------------");
        Pair<Boolean, String> checkRes = new Pair<>(true, "关系标注解析成功！");
        List<VerifyStatement> statements = this.verService.getStatements(statementId); // 获取id>= statementId的所有statement
        //List<VerifyStatement> statements = this.verService.getStatementsBetween(statementId, statementId + 1); // 获取id>= statementId的所有statement
        for(VerifyStatement statement: statements){
            EntityMark entityMark = statement.getEntityMark();
            this.entitiesService.dealWithEntitiesModify(entityMark); // 将这个实体标注数据的实体导入库
            List<RelationMark> relationMarks = this.verService.getRelationMarksByStatement(statement);
            for(RelationMark relationMark :relationMarks){
                try{
                    checkRes = this.relationService.checkEntityExistAndSav(relationMark);
                    if(!checkRes.getKey()){
                        log.info(String.format("关系id=%d 数据存在问题，原因：%s", relationMark.getId(), checkRes.getValue()));
                    }
                }catch (Exception e){
                    log.info(String.format("关系id=%d 数据存在问题，原因：数据异常（实体没有标全或者实体-关系数据文本对不上", relationMark.getId()));
                }
            }
        }
        return ResponseEntity.ok(ResponseResult.of("success", null));
    }
}
