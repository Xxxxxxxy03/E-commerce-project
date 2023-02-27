package com.offcn;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

import java.io.IOException;

public class TestFastDfsTest {
    public static void main(String[] args) throws MyException, IOException {
        //1.加载配置文件
        ClientGlobal.init("E:\\ujiuye\\The fourth phase\\dongyimai-parent\\dongyimai-service\\dongyimai-file-service\\src\\main\\resources\\fdfs_client.conf");

        //2.创建一个Tracker client
        TrackerClient trackerClient = new TrackerClient();

        //3.使用tracker client连接到tracker server
        TrackerServer trackerServer = trackerClient.getConnection();

        //4.创建一个连接到存储服务器的客户端对象
        StorageClient storageClient = new StorageClient(trackerServer,null);

        //5.准备上传本地文件到存储服务器
        String[] files = storageClient.upload_file("E:\\background\\test.jpg", "jpg",null);
        for (String fileName : files) {
            System.out.println(fileName);
        }

    }
}
