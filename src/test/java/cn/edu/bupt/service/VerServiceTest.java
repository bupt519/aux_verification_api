package cn.edu.bupt.service;

import cn.edu.bupt.bean.po.EntityMark;
import cn.edu.bupt.bean.po.RelationMark;
import cn.edu.bupt.bean.vo.VerMarksVo;
import cn.edu.bupt.util.ResponseResult;
import cn.edu.bupt.util.ResultTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class VerServiceTest {

    @Autowired
    private VerService verService;

    @Test
    @Transactional
    @Commit
    public void testNextUnViewedStatement() {
        VerMarksVo result = verService.nextUnViewedStatement(2);
        log.info("{}", result.getEntityMarks().get(0).getContent());
        Assert.assertEquals(result.getPdfNo(), 10);
        Assert.assertEquals(result.getPdfUrl(), "f2F4XlWfNfc&list=PLkvG4EWPDB0kWpFAmX1b3fcWh6gy9HQgG&index=45");
        Assert.assertEquals(result.getEntityMarks().size(), 2);
        Assert.assertEquals(result.getRelationMarks().size(), 2);
    }

    @Test
    @Transactional
    @Commit
    public void testDealWithEntity() {
        ResponseResult<String> res = verService.dealWithEntity(2, 1, 1,
                "此外，</o>公司</no>遵循“生产一代、研发一代、" +
                "储备一代”的持续发展目标，不断拓展</o>语音技术应用领域</nf>，前瞻性、针对性地进行研发</o>", 0);
        Assert.assertEquals((long) res.getCode(), ResultTypeEnum.SERVICE_SUCCESS.getCode());

        res = verService.dealWithEntity(1, 4, 1, "1", 1);
        Assert.assertEquals((long)res.getCode(), ResultTypeEnum.SERVICE_ERROR.getCode());

        res = verService.dealWithEntity(2, 5, 1, "12", 1);
        Assert.assertEquals((long)res.getCode(), ResultTypeEnum.SERVICE_ERROR.getCode());

        res = verService.dealWithEntity(2, 4, 2, "123", 0);
        Assert.assertEquals((long)res.getCode(), ResultTypeEnum.SERVICE_ERROR.getCode());

        res = verService.dealWithEntity(2, 4, 1, "1234567", 1);
        Assert.assertEquals((long) res.getCode(), ResultTypeEnum.SERVICE_SUCCESS.getCode());
    }

    @Test
    @Transactional
    @Commit
    public void testDealWithRelation(){
        ResponseResult<String> res = verService.dealWithRelation(2, 1, 1,
                "此外，<e1>公司</e1>遵循“生产一代、研发一代、储备一代”的持续发展目标，不断拓展<e2>语音技术应用领域</e2>，前瞻性、针对性地进行研发",
                0, 20);
        Assert.assertEquals((long)res.getCode(),ResultTypeEnum.SERVICE_SUCCESS.getCode());

        res = verService.dealWithRelation(1, 4, 1, "1", 1, 20);
        Assert.assertEquals((long)res.getCode(), ResultTypeEnum.SERVICE_ERROR.getCode());

        res = verService.dealWithRelation(2, 3, 1, "12", 1, 20);
        Assert.assertEquals((long)res.getCode(), ResultTypeEnum.SERVICE_ERROR.getCode());

        res = verService.dealWithRelation(2, 4, 2, "123", 1, 20);
        Assert.assertEquals((long)res.getCode(), ResultTypeEnum.SERVICE_ERROR.getCode());

        res = verService.dealWithRelation(2, 4, 1, "1234", 1, 40);
        Assert.assertEquals((long)res.getCode(), ResultTypeEnum.SERVICE_ERROR.getCode());

        res = verService.dealWithRelation(2, 4, 1, "12345", 0, 29);
        Assert.assertEquals((long)res.getCode(), ResultTypeEnum.SERVICE_SUCCESS.getCode());
    }

    @Test
    public void testGetEntity(){
        EntityMark entity = verService.getEntity(2);
        Assert.assertEquals(entity.getPassed(), -1);
        Assert.assertEquals(entity.getReviewed(), 0);
        Assert.assertNull(entity.getVerDate());
        Assert.assertEquals(entity.getStatement().getId(), 2);
        Assert.assertEquals(entity.getContent(),
                "2006 年</nt>公司</no>实施安徽省第四期“</o>校校通</ni>”信息工程项目（合同总金额</o> 6023万元</nn>），需垫付大量资金</o>");

        entity = verService.getEntity(-1);
        Assert.assertNull(entity);
    }

    @Test
    public void testGetRelation(){
        RelationMark relation = verService.getRelationMark(2);
        assertEquals(relation.getPassed(), -1);
        assertEquals(relation.getReviewed(), 0);
        assertNull(relation.getVerDate());
        assertEquals(relation.getReflect().getId(), 29);
        assertEquals(relation.getStatement().getId(), 2);
        assertEquals(relation.getContent(),
                "2006 年<e1>公司</e1>实施<e2>安徽省第四期“校校通”信息工程项目</e2>（合同总金额 6,023万元），需垫付大量资金");

        relation = verService.getRelationMark(-1);
        assertNull(relation);
    }

}
