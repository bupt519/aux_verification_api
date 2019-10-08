package cn.edu.bupt.bean.vo;

import cn.edu.bupt.bean.po.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class DataStatisticsVo {

    private int passedCount;
    private int rejectCount;
    private int remainCount;

    public DataStatisticsVo(int passedCount, int rejectCount,int remainCount){
        this.passedCount = passedCount;
        this.rejectCount = rejectCount;
        this.remainCount = remainCount;
    }
}
