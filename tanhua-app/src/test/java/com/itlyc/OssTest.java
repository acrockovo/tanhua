package com.itlyc;

import com.itlyc.autoconfig.oss.OssTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OssTest {

    @Autowired
    private OssTemplate ossTemplate;


    @Test
    public void test0() throws Exception{
        File file = new File("D:\\images\\101.jpg");
        String url = ossTemplate.upload(file.getName(), new FileInputStream(file));
        System.out.println(url);
    }
}
