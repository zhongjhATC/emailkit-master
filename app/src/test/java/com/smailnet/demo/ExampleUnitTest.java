package com.smailnet.demo;

import com.smailnet.emailkit.Draft;
import com.smailnet.emailkit.EmailKit;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);

        EmailKit.Config config = new EmailKit.Config();
        config.setMailType(EmailKit.MailType.GMAIL);
        // 配置发件人邮件服务器参数
        config.setAccount("a254191389@gmail.com") // 发件人邮箱
                .setPassword("pycguvwemomwjosu"); // 密码或授权码

        // 设置一封草稿邮件
        Draft draft = new Draft()
                .setNickname("Device") // 发件人昵称
                .setTo("a254191389@gmail.com") // 收件人邮箱
                .setSubject("Device") // 邮件主题
                .setText("Device"); //邮件正文

        EmailKit.useSMTPService(config)
                .send(draft, new EmailKit.GetSendCallback() {
                    @Override
                    public void onSuccess() {
                        // 数据库修改发送成功
                    }

                    @Override
                    public void onFailure(String errMsg) {
                    }
                }, null);

    }
}