package cn.edu.bupt.bean.vo;

import cn.edu.bupt.bean.po.EntityMark;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.swing.text.html.parser.Entity;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class EntityListVo {

    private long totalCount;
    private int pageNo;
    private List<EntityHistory> data;

    @Data
    public static class EntityHistory{
        private long id;
        private String content;
        private int passed;
        private int reviewed;
        private Date verDate;
        private long statId;
        private String pdfUrl;
        private int pdfNo;

        public EntityHistory(EntityMark mark) {
            BeanUtils.copyProperties(mark, this);
            this.statId = mark.getStatement().getId();
            this.pdfUrl = mark.getStatement().getPdfUrl();
            this.pdfNo = mark.getStatement().getPdfNo();
        }
    }

}
