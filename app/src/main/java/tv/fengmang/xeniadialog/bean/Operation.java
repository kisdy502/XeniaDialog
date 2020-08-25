package tv.fengmang.xeniadialog.bean;

import java.util.ArrayList;
import java.util.List;

public class Operation {

    private String operationCmd;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperationCmd() {
        return operationCmd;
    }

    public void setOperationCmd(String operationCmd) {
        this.operationCmd = operationCmd;
    }

    public final static String CMD_MKDIR = "mkdir -p %s";
    public final static String CMD_MkFILE = "touch %s";
    public final static String CMD_COPY = "cp -Rf %s %s";
    public final static String CMD_MV = "mv %s %s";
    public final static String CMD_DELETE = "rm -rf %s";

    public static List<Operation> initData() {
        List<Operation> list=new ArrayList<>();
        Operation operationNewFile=new Operation();
        operationNewFile.setName("新建文件");

        Operation operationNewDir=new Operation();
        operationNewDir.setName("新建文件夹");

        Operation operationMove=new Operation();
        operationMove.setName("剪切");

        Operation operationCopy=new Operation();
        operationCopy.setName("复制");

        Operation operationPaste=new Operation();
        operationPaste.setName("粘贴");

        Operation operationDelete=new Operation();
        operationDelete.setName("删除");

        Operation operationLookUp=new Operation();
        operationLookUp.setName("查看文件");


        list.add(operationNewFile);
        list.add(operationNewDir);
        list.add(operationMove);
        list.add(operationCopy);
        list.add(operationPaste);
        list.add(operationDelete);
        list.add(operationLookUp);

        return list;
    }
}
