package vn.clmart.manager_service.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import org.json.JSONObject;

@Service
@Transactional
public class NotificationService {



    public String sendNotification(String jsonData, String token) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httppost = new HttpPost("https://fcm.googleapis.com/fcm/send");
            StringEntity entity = new StringEntity(jsonData, "UTF8");
            httppost.setEntity(entity);
            httppost.setHeader("authorization",
                    "key=AAAAUW5zUM4:APA91bFXa5I8sKSbaoRflQ3zdtkIAwxPk_Fmc4U4OYswM5OHTxpggcRW1KiAWHVZFkmgJO-JocVX1P85hgkWAF3LfTBd22POfOtR1SIaSJDnUi4ot3bJc5fXmetzvICJtvp3bxHMjFUP");
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json;charset=UTF-8");
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                result = org.apache.http.util.EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(result);
                result = (String) jsonObject.get("success");

            } catch (Exception ex) {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        return result;
    }

    public String sendNotificationToUser(String jsonData) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httppost = new HttpPost("https://fcm.googleapis.com/fcm/send");
            StringEntity entity = new StringEntity(jsonData, "UTF8");
            httppost.setEntity(entity);
            httppost.setHeader("authorization",
                    "key=AAAAUW5zUM4:APA91bFXa5I8sKSbaoRflQ3zdtkIAwxPk_Fmc4U4OYswM5OHTxpggcRW1KiAWHVZFkmgJO-JocVX1P85hgkWAF3LfTBd22POfOtR1SIaSJDnUi4ot3bJc5fXmetzvICJtvp3bxHMjFUP");
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json;charset=UTF-8");
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                result = org.apache.http.util.EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(result);
                result = (String) jsonObject.get("success");

            } catch (Exception ex) {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        return result;
    }
}
