package cn.edu.bupt.bean.po;

public enum VerifyResult {

    ACCEPT, // 没有修改，直接通过
    MODIFY_ACCEPT, // 修改后通过
    DENIED, // 没有修改，直接拒绝
    MODIFY_DENIED // 修改后拒绝

}
