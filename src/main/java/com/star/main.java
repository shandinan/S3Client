package com.star;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

public class main {

    /***
     * https://docs.aws.amazon.com/zh_cn/sdk-for-java/v1/developer-guide/examples-s3-website-configuration.html
     * amazonaws API文档 地址
     * @param args
     * @throws IOException
     */

    public static void main(String[] args) throws IOException {
//创建Amazon S3对象使用明确凭证
        BasicAWSCredentials credentials = new BasicAWSCredentials("B1180C3CE3B7492A2229", "0vSHy8g0TPLVK+GuQSzMzyGtkgkAAAF147dJKkNq");
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignerOverride("S3SignerType");//凭证验证方式
        clientConfig.setProtocol(Protocol.HTTP);//访问协议
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(clientConfig)
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(//设置要用于请求的端点配置（服务端点和签名区域）
                                "obs.szunicom.com:80",//我的s3服务器
                                "")).withPathStyleAccessEnabled(false)//是否使用路径方式，是的话s3.xxx.sn/bucketname
                .build();

        System.out.println("Uploading a new object to S3 from a file\n");
        System.out.println("连接成功!");
        //枚举bucket
        List<Bucket> buckets = s3Client.listBuckets();
        for (Bucket bucket : buckets) {
            System.out.println("Bucket: " + bucket.getName());
        }
        //枚举bucket下对象
        ObjectListing objects = s3Client.listObjects("new-bucket-6d4e39ce");
        do {
            for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
                System.out.println("Object: " + objectSummary.getKey());
            }
            objects = s3Client.listNextBatchOfObjects(objects);
        } while (objects.isTruncated());

        //    SetS3Web("new-bucket-6d4e39ce","index.html",null);
        //配置当前存储桶为web类型
        // BucketWebsiteConfiguration website_config = new BucketWebsiteConfiguration("index.html");
        //  s3Client.setBucketWebsiteConfiguration("new-bucket-6d4e39ce", website_config);
        //获取当前存储桶web的列表
        BucketWebsiteConfiguration config = s3Client.getBucketWebsiteConfiguration("new-bucket-6d4e39ce");
        System.out.format("Index document: %s\n", config.getIndexDocumentSuffix());

        //删除存储桶wen配置
        // s3Client.deleteBucketWebsiteConfiguration("new-bucket-6d4e39ce");

        Statement policy = new Statement(Statement.Effect.Allow)
                .withPrincipals(Principal.AllUsers)
                .withActions(S3Actions.GetObject)
                .withResources(new Resource("arn:aws:s3:::" + "new-bucket-6d4e39ce" + "/*"));

        //获取存储桶测试
        BucketPolicy bucket_policy = s3Client.getBucketPolicy("new-bucket-6d4e39ce");
        String policy_text = bucket_policy.getPolicyText();
        System.out.println("存储桶安全策略为：" + policy_text);
        //文件上传
/*        try {
            s3Client.putObject("bucketname", "keyname", new File("your file path"));
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }*/

        //文件下载
/*       try {
            S3Object o = s3Client.getObject("new-bucket-6d4e39ce", "HK_rightback135257.mp4");
            S3ObjectInputStream s3is = o.getObjectContent();
            FileOutputStream fos = new FileOutputStream(new File("D:\\ddd.mp4"));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }*/


    }

    /***
     * 配置 S3 存储桶 为web站点形式
     * @param bucket_name
     * @param index_doc
     * @param error_doc
     */
    public static void SetS3Web(String bucket_name, String index_doc, String error_doc) {
        BucketWebsiteConfiguration website_config = null;

        if (index_doc == null) {
            website_config = new BucketWebsiteConfiguration();
        } else if (error_doc == null) {
            website_config = new BucketWebsiteConfiguration(index_doc);
        } else {
            website_config = new BucketWebsiteConfiguration(index_doc, error_doc);
        }

        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
        try {
            s3.setBucketWebsiteConfiguration(bucket_name, website_config);
        } catch (AmazonServiceException e) {
            System.out.format(
                    "Failed to set website configuration for bucket '%s'!\n",
                    bucket_name);
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }

}
