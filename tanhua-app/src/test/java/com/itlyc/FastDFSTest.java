package com.itlyc;

import cn.hutool.core.io.FileUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FastDFSTest {

    @Autowired
    private FastFileStorageClient client;
    @Autowired
    private FdfsWebServer webServer;

    @Test
    public void test0() throws FileNotFoundException {
        File file = new File("E://2.mp4");
        FileInputStream fis = new FileInputStream(file);
        /*
            1.文件输入流
            2.文件大小
            3.文件扩展名
            4.文件基本属性 null即可
         */
        StorePath storePath = client.uploadFile(fis, file.length(), FileUtil.extName(file), null);

        System.out.println(webServer.getWebServerUrl() + storePath.getFullPath());
    }
}
