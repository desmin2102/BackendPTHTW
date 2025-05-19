package com.desmin.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            InputStream serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream("chat-47f93-firebase-adminsdk-fbsvc-6d6047f3aa.json");
            if (serviceAccount == null) {
                throw new IllegalStateException("Firebase service account file not found");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://chat-47f93-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);
            logger.info("✅ FirebaseApp đã được khởi tạo thành công: {}", app.getName());
            return app;
        } catch (Exception e) {
            logger.error("❌ Lỗi khi khởi tạo Firebase: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Bean
    public FirebaseDatabase firebaseDatabase(FirebaseApp firebaseApp) {
        return FirebaseDatabase.getInstance(firebaseApp);
    }
}
