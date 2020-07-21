package com.smailnet.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.smailnet.demo.BaseActivity;
import com.smailnet.demo.EmailApplication;
import com.smailnet.demo.R;
import com.smailnet.demo.controls.Controls;
import com.smailnet.emailkit.Draft;
import com.smailnet.emailkit.EmailKit;
import com.smailnet.microkv.MicroKV;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        EmailKit.Config config = new EmailKit.Config();
//        config.setMailType(EmailKit.MailType.GMAIL);
//        // 配置发件人邮件服务器参数
//        config.setAccount("a254191389@gmail.com") // 发件人邮箱
//                .setPassword("pycguvwemomwjosu"); // 密码或授权码
//
//        // 设置一封草稿邮件
//        Draft draft = new Draft()
//                .setNickname("Device") // 发件人昵称
//                .setTo("a254191389@gmail.com") // 收件人邮箱
//                .setSubject("Device") // 邮件主题
//                .setText("Device"); //邮件正文
//
//        EmailKit.useSMTPService(config)
//                .send(draft, new EmailKit.GetSendCallback() {
//                    @Override
//                    public void onSuccess() {
//                        // 数据库修改发送成功
//                    }
//
//                    @Override
//                    public void onFailure(String errMsg) {
//                    }
//                }, null);

    }

    @Override
    protected void initView() {
        findViewById(R.id.activity_main_edit_btn)
                .setOnClickListener(v -> startActivity(new Intent(this, SendActivity.class)));

        findViewById(R.id.activity_main_settings_btn)
                .setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        listView = findViewById(R.id.activity_main_list);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String folderName = parent.getAdapter().getItem(position).toString();
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra("folderName", folderName);
            startActivity(intent);
        });
    }

    @Override
    protected void initData() {
        MicroKV kv = MicroKV.customize("config", true);
        if (kv.containsKV("folder_list")) {
            List<String> list = new ArrayList<>(kv.getStringSet("folder_list"));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);
        } else {
            EmailKit.useIMAPService(EmailApplication.getConfig())
                    .getDefaultFolderList(new EmailKit.GetFolderListCallback() {
                        @Override
                        public void onSuccess(List<String> folderList) {
                            kv.setKV("folder_list", new HashSet<>(folderList)).save();
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                                    android.R.layout.simple_list_item_1, folderList);
                            listView.setAdapter(adapter);
                        }

                        @Override
                        public void onFailure(String errMsg) {
                            Controls.toast(errMsg);
                        }
                    });
        }
    }
}
