package csnet.openflow.util.fileOperation;

import java.io.File;

/**
 * 数据操作底层
 * <p>
 * 封装了文件删除的基本操作， 向上提供了delete方法。
 */
public class FileDeleter {
    String fileName;
    File file;

    /**
     * 创建文件删除
     * 
     * @param fileName
     *            要删除的文件名
     */
    public FileDeleter(String fileName) {
        this.fileName = fileName;
        file = new File(fileName);
    }

    public FileDeleter(File file) {
        this.fileName = file.getPath();
        this.file = file;
    }

    /**
     * 删除文件
     */
    public void delete() {
        file.delete();
    }
}