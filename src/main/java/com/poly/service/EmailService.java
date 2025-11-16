package com.poly.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:NiceSport}")
    private String appName;

    /**
     * Gửi OTP qua email
     */
    public void sendOtpEmail(String toEmail, String otpCode, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Mã OTP xác thực từ " + appName);

            // Tạo nội dung HTML
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("otpCode", otpCode);
            context.setVariable("appName", appName);

            String htmlContent = getOtpEmailTemplate(userName, otpCode);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Đã gửi OTP tới email: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email OTP", e);
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }

    /**
     * Template HTML cho email OTP
     */
    private String getOtpEmailTemplate(String userName, String otpCode) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f5f5f5;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 40px auto;
                        background-color: #ffffff;
                        border-radius: 8px;
                        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                        overflow: hidden;
                    }
                    .header {
                        background: linear-gradient(135deg, #ee4d2d 0%, #ff6b35 100%);
                        padding: 30px;
                        text-align: center;
                        color: white;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 28px;
                    }
                    .content {
                        padding: 40px 30px;
                    }
                    .greeting {
                        font-size: 18px;
                        color: #333;
                        margin-bottom: 20px;
                    }
                    .otp-box {
                        background-color: #f8f9fa;
                        border: 2px dashed #ee4d2d;
                        border-radius: 8px;
                        padding: 30px;
                        text-align: center;
                        margin: 30px 0;
                    }
                    .otp-label {
                        font-size: 14px;
                        color: #666;
                        margin-bottom: 10px;
                    }
                    .otp-code {
                        font-size: 36px;
                        font-weight: bold;
                        color: #ee4d2d;
                        letter-spacing: 8px;
                        font-family: 'Courier New', monospace;
                    }
                    .warning {
                        background-color: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 15px;
                        margin: 20px 0;
                        font-size: 14px;
                        color: #856404;
                    }
                    .footer {
                        background-color: #f8f9fa;
                        padding: 20px 30px;
                        text-align: center;
                        color: #666;
                        font-size: 12px;
                        border-top: 1px solid #e0e0e0;
                    }
                    .message {
                        color: #555;
                        line-height: 1.6;
                        margin-bottom: 15px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🏃 """ + appName + """
            </h1>
                    </div>
                    <div class="content">
                        <div class="greeting">
                            Xin chào <strong>""" + userName + """
            </strong>,
                        </div>
                        <p class="message">
                            Bạn đã yêu cầu thay đổi email trên """ + appName + """
            . 
                            Vui lòng sử dụng mã OTP dưới đây để xác thực:
                        </p>
                        <div class="otp-box">
                            <div class="otp-label">MÃ OTP CỦA BẠN</div>
                            <div class="otp-code">""" + otpCode + """
            </div>
                        </div>
                        <p class="message">
                            Mã OTP này có hiệu lực trong <strong>5 phút</strong>.
                        </p>
                        <div class="warning">
                            ⚠️ <strong>Lưu ý:</strong> Không chia sẻ mã OTP này với bất kỳ ai. 
                            Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.
                        </div>
                        <p class="message">
                            Trân trọng,<br>
                            <strong>Đội ngũ """ + appName + """
            </strong>
                        </p>
                    </div>
                    <div class="footer">
                        <p>© 2025 """ + appName + """
            . All rights reserved.</p>
                        <p>Email này được gửi tự động, vui lòng không trả lời.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}